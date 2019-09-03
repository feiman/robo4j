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
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.CodecRegistry;
import com.robo4j.socket.http.units.ServerContext;

import java.util.Objects;

import static com.robo4j.socket.http.util.RoboHttpUtils.PROPERTY_CODEC_REGISTRY;

/**
 * HttpRequestContext
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class HttpRequestContext {

    public static class Builder {

        private HttpDecoratedRequest decoratedRequest;

        public Builder addDecoratedRequest(HttpDecoratedRequest decoratedRequest){
            this.decoratedRequest = decoratedRequest;
            return this;
        }

        public HttpRequestContext build(RoboContext roboContext, ServerContext serverContext){
            Objects.requireNonNull(roboContext, "not allowed empty roboContext");
            Objects.requireNonNull(serverContext, "not allowed empty serverContext");

            final CodecRegistry codecRegistry = serverContext.getPropertySafe(CodecRegistry.class, PROPERTY_CODEC_REGISTRY);
            final RoboRequestFactory requestFactory = new RoboRequestFactory(codecRegistry);
            return new HttpRequestContext(roboContext, serverContext, requestFactory, decoratedRequest);
        }
    }

    private final RoboContext context;
    private final ServerContext serverContext;
    private final RoboRequestFactory requestFactory;
    private final HttpDecoratedRequest decoratedRequest;

    private HttpRequestContext(RoboContext context, ServerContext serverContext, RoboRequestFactory requestFactory, HttpDecoratedRequest decoratedRequest) {
        this.context = context;
        this.serverContext = serverContext;
        this.requestFactory = requestFactory;
        this.decoratedRequest = decoratedRequest;
    }

    public RoboContext roboContext(){
        return context;
    }

    public ServerContext serverContext(){
        return serverContext;
    }

    public RoboRequestFactory requestFactory(){
        return requestFactory;
    }

    public HttpDecoratedRequest decoratedRequest(){
        return decoratedRequest;
    }

}
