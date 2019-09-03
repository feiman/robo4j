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

import com.robo4j.socket.http.units.ServerPathConfig;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class RequestValidOperation implements RequestChainOperation{

    private final HttpResponseProcessBuilder resultBuilder;
    private final HttpRequestContext requestContext;
    private final ServerPathConfig requestPathConfig;

    RequestValidOperation(HttpResponseProcessBuilder resultBuilder, HttpRequestContext requestContext, ServerPathConfig requestPathConfig) {
        resultBuilder.addMethod(requestPathConfig.getMethod());
        resultBuilder.addPath(requestPathConfig.getPath());
        this.resultBuilder = resultBuilder;
        this.requestContext = requestContext;
        this.requestPathConfig = requestPathConfig;
    }

    @Override
    public HttpResponseProcess execute() {
        switch (requestPathConfig.getMethod()){
            case GET:
                return new RequestGetMethodOperation(resultBuilder, requestContext, requestPathConfig).execute();
            case POST:
                return new RequestPostMethodOperation(resultBuilder, requestContext, requestPathConfig).execute();
            default:
                return new RequestNotImplementedOperation(resultBuilder).execute();
        }
    }
}
