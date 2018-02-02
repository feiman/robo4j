/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 * 
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.robo4j.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.robo4j.configuration.Configuration;
import com.robo4j.logging.SimpleLoggingUtil;

/**
 * This is a server that listens on messages, and sends them off to the
 * indicated local recipient. It is associated to RoboContext.
 * 
 * TODO: Rewrite in NIO for better thread management.
 * 
 * @author Marcus
 */
public class MessageServer {
	public final static String KEY_HOST_NAME = "hostname";
	
	private volatile int listeningPort = 0;
	private volatile String listeningHost;
	private volatile boolean running = true;
	private MessageCallback callback;
	private Configuration configuration;
	
	private class MessageHandler implements Runnable {
		private Socket socket;

		public MessageHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))) {

				if (checkMagic(objectInputStream.readShort())) {
					// Then keep reading string, byte, data triplets until dead
					while (running) {
						String id = (String) objectInputStream.readUTF();
						Object message = decodeMessage(objectInputStream);
						callback.handleMessage(id, message);
					}
				} else {
					// Init protocol. First check magic
					SimpleLoggingUtil.error(getClass(),
							"Got wrong communication magic - will shutdown communication with " + socket.getRemoteSocketAddress());
				}

			} catch (IOException e) {
				SimpleLoggingUtil.error(getClass(), "IO Exception communicating with " + socket.getRemoteSocketAddress(), e);
			} catch (ClassNotFoundException e) {
				SimpleLoggingUtil.error(getClass(), "Could not find class to deserialize message to - will stop receiving messages from "
						+ socket.getRemoteSocketAddress(), e);
			}
			SimpleLoggingUtil.info(getClass(), "Shutting down socket " + socket.toString());

		}

		private Object decodeMessage(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
			byte dataType = objectInputStream.readByte();
			switch (dataType) {
			case MessageProtocolConstants.OBJECT:
				return objectInputStream.readObject();
			case MessageProtocolConstants.MOD_UTF8:
				return objectInputStream.readUTF();
			case MessageProtocolConstants.BYTE:
				return objectInputStream.readByte();
			case MessageProtocolConstants.SHORT:
				return objectInputStream.readShort();
			case MessageProtocolConstants.FLOAT:
				return objectInputStream.readFloat();
			case MessageProtocolConstants.INT:
				return objectInputStream.readInt();
			case MessageProtocolConstants.DOUBLE:
				return objectInputStream.readDouble();
			case MessageProtocolConstants.LONG:
				return objectInputStream.readLong();
			case MessageProtocolConstants.CHAR:
				return objectInputStream.readChar();
			default:
				throw new IOException("The type with id " + dataType + " is not supported!");
			}
		}

		private boolean checkMagic(short magic) {
			return magic == MessageProtocolConstants.MAGIC;
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public MessageServer(MessageCallback callback, Configuration configuration) {
		this.callback = callback;
		this.configuration = configuration;
	}

	/**
	 * This will be blocking/running until stop is called (and perhaps for
	 * longer). Dispatch in whatever thread you feel appropriate.
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		String host = configuration.getString(KEY_HOST_NAME, null);
		InetAddress bindAddress = null;
		if (host != null) {
			bindAddress = InetAddress.getByName(host);
		}

		try (ServerSocket serverSocket = new ServerSocket(configuration.getInteger("port", 0), configuration.getInteger("backlog", 20),
				bindAddress)) {
			listeningHost = serverSocket.getInetAddress().getHostAddress();
			listeningPort = serverSocket.getLocalPort();
			ThreadGroup g = new ThreadGroup("Robo4J communication threads");
			while (running) {
				MessageHandler handler = new MessageHandler(serverSocket.accept());
				Thread t = new Thread(g, handler, "Communication [" + handler.socket.getRemoteSocketAddress() + "]");
				t.setDaemon(true);
				t.start();
			}
		}
	}

	public void stop() {
		running = false;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	/**
	 * @return the URI for the listening socket. This is the address to connect
	 *         to.
	 */
	public URI getListeningURI() {
		try {
			String host = configuration.getString(KEY_HOST_NAME, null);
			if (host != null) {
				return new URI("robo4j", "", host, listeningPort, "", "", "");
			} else {
				return new URI("robo4j", "", listeningHost, listeningPort, "", "", "");				
			}
		} catch (URISyntaxException e) {
			SimpleLoggingUtil.error(getClass(), "Could not create URI for listening URI");
			return null;
		}
	}
}
