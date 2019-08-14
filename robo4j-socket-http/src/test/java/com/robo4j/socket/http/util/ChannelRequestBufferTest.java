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

import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.HttpVersion;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.message.HttpRequestDenominator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static com.robo4j.socket.http.util.TestUtils.getResourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class ChannelRequestBufferTest {

	@Test
	void httpDecoratedRequestByChannelReadTest() throws IOException {

		ByteChannel channel = mock(ByteChannel.class);
		when(channel.read(any())).thenReturn(-1);

		ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
		HttpDecoratedRequest request = channelRequestBuffer.getHttpDecoratedRequestByChannel(channel);

		assertNull( request);
	}

	@Test
	void httpDecoratedRequestByChannel_Get_default_path_curl_Test() throws Exception {

		final HttpRequestDenominator denominator = new HttpRequestDenominator(HttpMethod.GET, "/",
				HttpVersion.HTTP_1_1);
		final Map<String, String> header = Collections.singletonMap("Host", "localhost:9050");
		final HttpDecoratedRequest expectedHttpDecoratedRequest = new HttpDecoratedRequest(header, denominator);
		final Path path = getResourcePath("httpGetRequestDefaultPath.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
			HttpDecoratedRequest request = channelRequestBuffer.getHttpDecoratedRequestByChannel(fileChannel);

			assertNotNull(request);
			assertFalse(expectedHttpDecoratedRequest.getHeader().isEmpty());
			assertEquals(expectedHttpDecoratedRequest.getHeader(), request.getHeader());
			assertEquals(request.getHost(), "localhost");
			assertEquals(request.getPort().intValue(), 9050);
			assertEquals(expectedHttpDecoratedRequest, request);
		}

	}

	@Test
	void httpDecoratedRequestByChannel_Get_units_path_Test() throws Exception {

		final HttpRequestDenominator denominator = new HttpRequestDenominator(HttpMethod.GET, "/units/",
				HttpVersion.HTTP_1_1);
		//@formatter:off
		final Map<String, String> header = Map.of(
				"Host", "localhost:8061",
				"User-Agent", "Robo4jAgent");
		//@formatter:on
		final HttpDecoratedRequest expectedHttpDecoratedRequest = new HttpDecoratedRequest(header, denominator);

		Path path = getResourcePath("httpGetRequestNotSupportedPath.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
			HttpDecoratedRequest request = channelRequestBuffer.getHttpDecoratedRequestByChannel(fileChannel);

			assertNotNull(request);
			assertFalse(expectedHttpDecoratedRequest.getHeader().isEmpty());
			assertEquals(expectedHttpDecoratedRequest.getHeader(), request.getHeader());
			assertEquals(expectedHttpDecoratedRequest.getHost(), request.getHost());
			assertEquals(expectedHttpDecoratedRequest.getPort(), request.getPort());
			assertEquals(request.getHost(), "localhost");
			assertEquals(request.getPort().intValue(), 8061);
			assertEquals(expectedHttpDecoratedRequest, request);
		}

	}

	@Test
	void httpDecoratedRequestByChannel_Get_specific_unit_Test() throws Exception {

		final HttpRequestDenominator denominator = new HttpRequestDenominator(HttpMethod.GET, "/units/http_server",
				HttpVersion.HTTP_1_1);
		//@formatter:off
		final Map<String, String> header = Map.of(
				"Host", "127.0.0.1:8061",
				"User-Agent", "PostmanRuntime/7.15.2",
				"Accept", "*/*",
				"Cache-Control", "no-cache",
				"Postman-Token","7de85c36-5d98-452e-aa54-bdf63052e3a5",
				"Accept-Encoding", "gzip, deflate",
				"Connection","keep-alive"
				);
		//@formatter:on
		final HttpDecoratedRequest expectedHttpDecoratedRequest = new HttpDecoratedRequest(header, denominator);

		Path path = getResourcePath("httpGetRequestPathUnitsSpecific.txt");

		try (FileChannel fileChannel = FileChannel.open(path)) {
			ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
			HttpDecoratedRequest request = channelRequestBuffer.getHttpDecoratedRequestByChannel(fileChannel);

			assertNotNull(request);
			assertFalse(expectedHttpDecoratedRequest.getHeader().isEmpty());
			assertEquals(expectedHttpDecoratedRequest.getHeader(), request.getHeader());
			assertEquals(request.getHost(), "127.0.0.1");
			assertEquals(request.getPort().intValue(), 8061);
			assertEquals(expectedHttpDecoratedRequest, request);
		}

	}


}