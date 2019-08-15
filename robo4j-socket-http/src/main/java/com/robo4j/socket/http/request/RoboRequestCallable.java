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
import com.robo4j.socket.http.message.HttpRequestDenominator;
import com.robo4j.socket.http.units.ServerContext;
import com.robo4j.socket.http.units.ServerPathConfig;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.robo4j.util.Utf8Constant.UTF8_SOLIDUS;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class RoboRequestCallable implements Callable<HttpResponseProcess> {

	private final RoboContext context;
	private final ServerContext serverContext;
	private final HttpDecoratedRequest decoratedRequest;
	private final DefaultRequestFactory<?> factory;

	public RoboRequestCallable(RoboContext context, ServerContext serverContext, HttpDecoratedRequest decoratedRequest,
			DefaultRequestFactory<Object> factory) {
		Objects.requireNonNull(context, "not allowed empty context");
		Objects.requireNonNull(serverContext, "not allowed empty serverContext");
		this.context = context;
		this.serverContext = serverContext;
		this.decoratedRequest = decoratedRequest;
		this.factory = factory;
	}

	@Override
	public HttpResponseProcess call() throws Exception {

		final HttpResponseProcessBuilder resultBuilder = HttpResponseProcessBuilder.Builder();

		final ServerPathConfig requestPathConfig = decoratedRequest == null ? null :
				serverContext.getPathConfig(decoratedRequest.getPathMethod());

		if (isValidRequest(requestPathConfig)) {
			resultBuilder.setMethod(requestPathConfig.getMethod());
			resultBuilder.setPath(requestPathConfig.getPath());

			switch (requestPathConfig.getMethod()) {
			case GET:
				if (requestPathConfig.getPath().equals(UTF8_SOLIDUS)) {
					resultBuilder.setCode(StatusCode.OK);
					resultBuilder.setResult(factory.processGet(context));
				} else {
					resultBuilder.setTarget(requestPathConfig.getRoboUnit());
					final Object unitDescription;
					// the system needs to have one more worker thread to evaluate Future get
					final HttpRequestDenominator denominator = (HttpRequestDenominator) decoratedRequest
							.getDenominator();
					final Set<String> requestAttributes = denominator.getPathAttributes();

					if (requestAttributes.isEmpty()) {
						Collection<ServerPathConfig> pathConfigs = serverContext
								.getPathConfigByPath(decoratedRequest.getPathMethod().getPath());
						unitDescription = factory.processGet(requestPathConfig.getRoboUnit(), pathConfigs);
					} else {
						unitDescription = factory.processGet(requestPathConfig.getRoboUnit(), requestAttributes);
					}

					resultBuilder.setCode(StatusCode.OK);
					resultBuilder.setResult(unitDescription);
				}
				break;
			case POST:
				if (requestPathConfig.getPath().equals(UTF8_SOLIDUS)) {
					resultBuilder.setCode(StatusCode.BAD_REQUEST);
				} else {
					resultBuilder.setTarget(requestPathConfig.getRoboUnit());
					Object respObj = factory.processPost(requestPathConfig.getRoboUnit(),
							decoratedRequest.getMessage());
					if (respObj == null) {
						resultBuilder.setCode(StatusCode.BAD_REQUEST);
					} else {
						resultBuilder.setCode(StatusCode.ACCEPTED);
						resultBuilder.setResult(respObj);
					}
				}
				break;
			default:
				resultBuilder.setCode(StatusCode.BAD_REQUEST);
				SimpleLoggingUtil.debug(getClass(), "not implemented method: " + decoratedRequest.getPathMethod());
			}
		} else {
			resultBuilder.setCode(StatusCode.BAD_REQUEST);
		}
		return resultBuilder.build();
	}

	private boolean isValidRequest(ServerPathConfig pathConfig) {
		return pathConfig != null && decoratedRequest != null && decoratedRequest.getPathMethod() != null;
	}

}
