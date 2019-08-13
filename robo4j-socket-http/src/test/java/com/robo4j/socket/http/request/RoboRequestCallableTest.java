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

package com.robo4j.socket.http.request;

import com.robo4j.RoboContext;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.CodecRegistry;
import com.robo4j.socket.http.units.ServerContext;
import com.robo4j.socket.http.util.ChannelRequestBuffer;
import com.robo4j.socket.http.util.CodeRegistryUtils;
import org.junit.jupiter.api.Test;

import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static com.robo4j.socket.http.codec.AbstractHttpMessageCodec.DEFAULT_PACKAGE;
import static com.robo4j.socket.http.util.TestUtils.getResourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RoboRequestCallableTest {


    @Test
    void notInitiatedServerContextBadRequestTest() throws Exception{

        HttpResponseProcess expectedResponse = new HttpResponseProcess(null, null, null, StatusCode.BAD_REQUEST, null);
        RoboContext roboContext = mock(RoboContext.class);
        ServerContext serverContext = mock(ServerContext.class);

        final Path path = getResourcePath("httpGetRequestDefaultPath.txt");

        try (FileChannel fileChannel = FileChannel.open(path)) {
            ChannelRequestBuffer channelRequestBuffer = new ChannelRequestBuffer();
            HttpDecoratedRequest request = channelRequestBuffer.getHttpDecoratedRequestByChannel(fileChannel);

            CodecRegistry codecRegistry = CodeRegistryUtils.getCodecRegistry(DEFAULT_PACKAGE);
            RoboRequestFactory factory = new RoboRequestFactory(codecRegistry);

            RoboRequestCallable callable = new RoboRequestCallable(roboContext, serverContext,  request, factory);
            HttpResponseProcess process = callable.call();

            assertEquals(expectedResponse, process);

        }

    }

}