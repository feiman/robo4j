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

import com.robo4j.socket.http.HttpVersion;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedResponse;
import com.robo4j.socket.http.message.HttpResponseDenominator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;

import static com.robo4j.socket.http.util.ChannelBufferUtils.BUFFER_MARK_END;
import static com.robo4j.socket.http.util.ChannelBufferUtils.INIT_BUFFER_CAPACITY;
import static com.robo4j.socket.http.util.ChannelBufferUtils.extractDecoratedResponseByStringMessage;

/**
 * ChannelResponseBuffer
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class ChannelResponseBuffer {

	private ByteBuffer responseBuffer;

	public ChannelResponseBuffer() {
		responseBuffer = ByteBuffer.allocateDirect(INIT_BUFFER_CAPACITY);
	}

	public HttpDecoratedResponse getHttpDecoratedResponseByChannel(ByteChannel channel) throws IOException {
		final StringBuilder sbBasic = new StringBuilder();
		int readBytes = channel.read(responseBuffer);
		try {
			if (readBytes != BUFFER_MARK_END) {
				responseBuffer.flip();
				ChannelBufferUtils.addToStringBuilder(sbBasic, responseBuffer, readBytes);
				final HttpDecoratedResponse result = extractDecoratedResponseByStringMessage(sbBasic.toString());
				ChannelBufferUtils.readChannelBuffer(result, channel, responseBuffer, readBytes);
				return result;
			} else {
				return new HttpDecoratedResponse(new HashMap<>(),
						new HttpResponseDenominator(StatusCode.BAD_REQUEST, HttpVersion.HTTP_1_1));
			}
		} finally {
			responseBuffer.clear();
		}
	}

}
