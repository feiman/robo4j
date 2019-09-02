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
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.ServerContext;
import com.robo4j.socket.http.units.ServerPathConfig;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.robo4j.util.Utf8Constant.UTF8_SOLIDUS;

/**
 * RequestGetMethodOperation handles all get requests
 * 
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class RequestGetMethodOperation implements RequestChainOperation {

	private final HttpResponseProcessBuilder resultBuilder;
	private final RoboContext roboContext;
	private final RoboRequestFactory factory;
	private final ServerContext serverContext;
	private final ServerPathConfig pathConfig;
	private final HttpDecoratedRequest request;

	RequestGetMethodOperation(HttpResponseProcessBuilder resultBuilder, HttpRequestContext requestContext,
			ServerPathConfig pathConfig) {
		this.resultBuilder = resultBuilder;
		this.roboContext = requestContext.roboContext();
		this.factory = requestContext.requestFactory();
		this.serverContext = requestContext.serverContext();
		this.pathConfig = pathConfig;
		this.request = requestContext.decoratedRequest();
	}

	@Override
	public HttpResponseProcess execute() {
		if (pathConfig.getPath().equals(UTF8_SOLIDUS)) {
			resultBuilder.addResult(factory.createRoboContextResponse(roboContext));
			resultBuilder.addCode(StatusCode.OK);
		} else {
			resultBuilder.addTarget(pathConfig.getRoboUnit());
			try {
				Object getResult = extractGetRequestResult();
				resultBuilder.addCode(StatusCode.OK);
				resultBuilder.addResult(getResult);
			} catch (InterruptedException | ExecutionException e) {
				SimpleLoggingUtil.error(getClass(), "extracting issue", e);
				resultBuilder.addCode(StatusCode.BAD_REQUEST);
			}
		}
		return resultBuilder.build();

	}

	/**
	 * Extract response from the system properties for GET
	 *
	 * @throws InterruptedException
	 *             exception
	 * @throws ExecutionException
	 *             exception
	 */
	private Object extractGetRequestResult() throws InterruptedException, ExecutionException {
		final Set<String> requestAttributes = request.getPathAttributes();
		if (requestAttributes.isEmpty()) {
			Collection<ServerPathConfig> pathConfigs = serverContext
					.getPathConfigByPath(request.getPathMethod().getPath());
			return factory.createRoboUnitResponse(pathConfig.getRoboUnit(), pathConfigs);
		} else {
			return factory.createRoboUnitAttributesResponse(pathConfig.getRoboUnit(), requestAttributes);
		}
	}
}
