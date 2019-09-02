/*
 * Copyright (c) 2014, 2019, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.robo4j.socket.http.channel;

import com.robo4j.RoboContext;
import com.robo4j.socket.http.SocketException;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.request.HttpRequestContext;
import com.robo4j.socket.http.request.HttpRequestProcessor;
import com.robo4j.socket.http.request.HttpResponseProcess;
import com.robo4j.socket.http.units.ServerContext;
import com.robo4j.socket.http.util.ChannelRequestBuffer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Reading TPC/IP Socket protocol handler
 *
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class ReadSelectionKeyHandler implements SelectionKeyHandler {

	private final RoboContext context;
	private final ServerContext serverContext;
	private final Map<SelectionKey, HttpResponseProcess> outBuffers;
	private final SelectionKey key;
	private final Lock lock = new ReentrantLock();
	private final ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();

	public ReadSelectionKeyHandler(RoboContext context, ServerContext serverContext,
			Map<SelectionKey, HttpResponseProcess> outBuffers, SelectionKey key) {
		this.context = context;
		this.serverContext = serverContext;
		this.outBuffers = outBuffers;
		this.key = key;
	}

	@Override
	public SelectionKey handle() {
		final SocketChannel channel = (SocketChannel) key.channel();
		lock.lock();
		try {
			final HttpDecoratedRequest decoratedRequest = channelRequestBuffer
					.getHttpDecoratedRequestByChannel(channel);
			final HttpRequestContext httpRequestContext = new HttpRequestContext.Builder()
					.addDecoratedRequest(decoratedRequest)
					.build(context, serverContext);
//			final RoboRequestCallable callable = new RoboRequestCallable(httpRequestContext);
			final HttpRequestProcessor processor = new HttpRequestProcessor(httpRequestContext);
			final Future<HttpResponseProcess> futureResult = context.getScheduler().submit(processor);
			final HttpResponseProcess result = extractRoboResponseProcess(futureResult);
			outBuffers.put(key, result);
			registerSelectionKey(channel);
			return key;
		} catch (IOException e) {
			throw new SocketException(e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	private HttpResponseProcess extractRoboResponseProcess(Future<HttpResponseProcess> future) {
		try {
			return future.get();
		} catch (Exception e) {
			throw new SocketException("extract robo response", e);
		}
	}

	private void registerSelectionKey(SocketChannel channel) {
		try {
			channel.register(key.selector(), SelectionKey.OP_WRITE);
		} catch (Exception e) {
			throw new SocketException("register selection key", e);
		}
	}
}
