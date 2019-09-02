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
import com.robo4j.socket.http.units.ServerPathConfig;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RequestPathOperation implements RequestChainOperation {

	private final HttpResponseProcessBuilder resultBuilder;
	private final HttpRequestContext requestContext;

	RequestPathOperation(HttpResponseProcessBuilder resultBuilder, HttpRequestContext requestContext) {
		this.resultBuilder = resultBuilder;
		this.requestContext = requestContext;
	}

	@Override
	public HttpResponseProcess execute() {
		final HttpDecoratedRequest request = requestContext.decoratedRequest();
		final ServerPathConfig requestPathConfig = request == null ? null
				: requestContext.serverContext().getPathConfig(request.getPathMethod());

		final RequestChainOperation operation;
		if (isValidRequest(request, requestPathConfig)) {
			operation = new RequestValidOperation(resultBuilder, requestContext, requestPathConfig);
		} else {
			operation = new RequestBadRequestOperation(resultBuilder);
		}
		return operation.execute();
	}

	private boolean isValidRequest(HttpDecoratedRequest request, ServerPathConfig pathConfig) {
		return pathConfig != null && request != null && request.getPathMethod() != null;
	}
}
