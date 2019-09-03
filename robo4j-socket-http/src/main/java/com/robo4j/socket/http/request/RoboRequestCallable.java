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

import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.units.ServerPathConfig;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.robo4j.util.Utf8Constant.UTF8_SOLIDUS;

/**
 * RoboRequestCallable process the incoming request
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
@Deprecated
public class RoboRequestCallable implements Callable<HttpResponseProcess> {

	private final HttpRequestContext requestContext;

	public RoboRequestCallable(final HttpRequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public HttpResponseProcess call() throws Exception {

		final HttpResponseProcessBuilder resultBuilder = HttpResponseProcessBuilder.Builder();
		final HttpDecoratedRequest decoratedRequest = requestContext.decoratedRequest();
		final ServerPathConfig requestPathConfig = decoratedRequest == null ? null
				: requestContext.serverContext().getPathConfig(decoratedRequest.getPathMethod());

		if (isValidRequest(requestPathConfig)) {
			resultBuilder.addMethod(requestPathConfig.getMethod());
			resultBuilder.addPath(requestPathConfig.getPath());
			processValidRequest(resultBuilder, requestPathConfig);
		} else {
			resultBuilder.addCode(StatusCode.BAD_REQUEST);
		}
		return resultBuilder.build();
	}

	/**
	 * Process valid request
	 * 
	 * @param resultBuilder
	 *            result builder
	 * @param requestPathConfig
	 *            requested path config
	 * @throws InterruptedException
	 *             exception
	 * @throws ExecutionException
	 *             exception
	 */
	private void processValidRequest(HttpResponseProcessBuilder resultBuilder, ServerPathConfig requestPathConfig)
			throws InterruptedException, ExecutionException {
		switch (requestPathConfig.getMethod()) {
		case GET:
			if (requestPathConfig.getPath().equals(UTF8_SOLIDUS)) {
				resultBuilder.addResult(
						requestContext.requestFactory().createRoboContextResponse(requestContext.roboContext()));
			} else {
				resultBuilder.addTarget(requestPathConfig.getRoboUnit());
				final Object getResult = extractGetRequestResult(requestPathConfig);
				resultBuilder.addResult(getResult);
			}
			resultBuilder.addCode(StatusCode.OK);
			break;
		case POST:
			resultBuilder.addTarget(requestPathConfig.getRoboUnit());
			if (requestPathConfig.getPath().equals(UTF8_SOLIDUS)) {
				resultBuilder.addCode(StatusCode.BAD_REQUEST);
			} else {
				final Object respObj = requestContext.requestFactory().processPost(requestPathConfig.getRoboUnit(),
						requestContext.decoratedRequest().getMessage());
				if (respObj == null) {
					resultBuilder.addCode(StatusCode.BAD_REQUEST);
				} else {
					resultBuilder.addCode(StatusCode.ACCEPTED);
					resultBuilder.addResult(respObj);
				}
			}
			break;
		default:
			resultBuilder.addCode(StatusCode.NOT_IMPLEMENTED);
			SimpleLoggingUtil.debug(getClass(),
					"not implemented method: " + requestContext.decoratedRequest().getPathMethod());
		}
	}

	/**
	 * Extract response from the system properties for GET
	 *
	 * @param requestPathConfig
	 *            requested path config
	 * @return result object
	 * @throws InterruptedException
	 *             exception
	 * @throws ExecutionException
	 *             exception
	 */
	private Object extractGetRequestResult(ServerPathConfig requestPathConfig)
			throws InterruptedException, ExecutionException {
		final Set<String> requestAttributes = requestContext.decoratedRequest().getPathAttributes();
		if (requestAttributes.isEmpty()) {
			Collection<ServerPathConfig> pathConfigs = requestContext.serverContext()
					.getPathConfigByPath(requestContext.decoratedRequest().getPathMethod().getPath());
			return requestContext.requestFactory().createRoboUnitResponse(requestPathConfig.getRoboUnit(), pathConfigs);
		} else {
			return requestContext.requestFactory().createRoboUnitAttributesResponse(requestPathConfig.getRoboUnit(),
					requestAttributes);
		}
	}

	private boolean isValidRequest(ServerPathConfig pathConfig) {
		return pathConfig != null && requestContext.decoratedRequest() != null
				&& requestContext.decoratedRequest().getPathMethod() != null;
	}

}
