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

import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.ServerPathConfig;

import static com.robo4j.util.Utf8Constant.UTF8_SOLIDUS;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RequestPostMethodOperation implements RequestChainOperation {

	private final HttpResponseProcessBuilder resultBuilder;
	private final RoboRequestFactory factory;
	private final ServerPathConfig pathConfig;
	private final HttpDecoratedRequest request;

	RequestPostMethodOperation(HttpResponseProcessBuilder resultBuilder, HttpRequestContext requestContext,
			ServerPathConfig pathConfig) {
		this.resultBuilder = resultBuilder;
		this.factory = requestContext.requestFactory();
		this.pathConfig = pathConfig;
		this.request = requestContext.decoratedRequest();
	}

	@Override
	public HttpResponseProcess execute() {
		resultBuilder.addTarget(pathConfig.getRoboUnit());
		if (pathConfig.getPath().equals(UTF8_SOLIDUS)) {
			resultBuilder.addCode(StatusCode.BAD_REQUEST);
		} else {
			final Object respObj = factory.processPost(pathConfig.getRoboUnit(), request.getMessage());
			if (respObj == null) {
				resultBuilder.addCode(StatusCode.BAD_REQUEST);
			} else {
				resultBuilder.addCode(StatusCode.ACCEPTED);
				resultBuilder.addResult(respObj);
			}
		}
		return resultBuilder.build();
	}
}
