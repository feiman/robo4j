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
package com.robo4j.socket.http.util;

import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.socket.http.message.HttpDecoratedRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * ChannelRequestBuffer
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class ChannelRequestBuffer {

	private ByteBuffer requestBuffer;

	public ChannelRequestBuffer() {
		requestBuffer = ByteBuffer.allocateDirect(ChannelBufferUtils.INIT_BUFFER_CAPACITY);
	}

	/**
	 * read byte channel and extract {@link HttpDecoratedRequest} in case of not
	 * readable channel provide empty decorated GET request
	 * 
	 * @param channel
	 *            byte channel
	 * @return decorated request {@link HttpDecoratedRequest}
	 * @throws IOException
	 *             exception
	 */
	public HttpDecoratedRequest getHttpDecoratedRequestByChannel(ByteChannel channel) throws IOException {
		final StringBuilder sbBasic = new StringBuilder();
		int readBytes = channel.read(requestBuffer);
		try {
			if (readBytes != ChannelBufferUtils.BUFFER_MARK_END) {
				requestBuffer.flip();
				ChannelBufferUtils.addToStringBuilder(sbBasic, requestBuffer, readBytes);
				final HttpDecoratedRequest result = ChannelBufferUtils
						.extractDecoratedRequestByStringMessage(sbBasic.toString());
				ChannelBufferUtils.readChannelBuffer(result, channel, requestBuffer, readBytes);
				return result;
			} else {
				SimpleLoggingUtil.error(getClass(), "http decorated request issue: empty is provided");
				return null;
			}
		} finally {
			requestBuffer.clear();
		}

	}
}
