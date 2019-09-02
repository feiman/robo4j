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

import com.robo4j.socket.http.message.HttpDecoratedRequest;

/**
 * RequestChainHandler handles {@link HttpDecoratedRequest} and creates a
 * response
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RequestChainHandler {

    static class Builder {

        private final HttpRequestContext requestContext;

        Builder(HttpRequestContext requestContext) {
            this.requestContext = requestContext;
        }

        RequestChainHandler build(){
            HttpResponseProcessBuilder resultBuilder = HttpResponseProcessBuilder.Builder();
            RequestPathOperation head = new RequestPathOperation(resultBuilder, requestContext);
            return new RequestChainHandler(head);
        }

    }

    private final RequestChainOperation operation;

    private RequestChainHandler(final RequestChainOperation operation) {
        this.operation = operation;
    }

    HttpResponseProcess process(){
        return operation.execute();
    }
}
