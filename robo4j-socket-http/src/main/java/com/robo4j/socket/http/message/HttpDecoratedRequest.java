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
package com.robo4j.socket.http.message;

import com.robo4j.socket.http.HttpHeaderFieldNames;
import com.robo4j.socket.http.units.PathHttpMethod;
import com.robo4j.socket.http.util.RoboHttpUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.robo4j.socket.http.HttpHeaders.HOST;
import static com.robo4j.socket.http.util.HttpMessageUtils.COLON;
import static com.robo4j.socket.http.util.HttpMessageUtils.HTTP_DEFAULT_PORT;

/**
 * Inbound Http message used by Server units.
 * Message does contains all necessary information for processing the request
 *
 * message has two mutable fields host, port
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class HttpDecoratedRequest extends AbstractHttpDecoratedMessage {

	private final HttpRequestDenominator denominator;
	private String host;
	private Integer port;

	public HttpDecoratedRequest(){
		super();
		denominator = null;
	}

	public HttpDecoratedRequest(HttpRequestDenominator denominator){
		super(denominator.getVersion());
		this.denominator = denominator;
	}

	public HttpDecoratedRequest(Map<String, String> header, HttpRequestDenominator denominator) {
		super(header, denominator.getVersion());
		if(header.containsKey(HOST)){
			String[] arrayHost = header.get(HOST).split(COLON);
			host = arrayHost[0];
			port = arrayHost.length > 1 ? port = Integer.valueOf(arrayHost[1]) : HTTP_DEFAULT_PORT;

		}
		this.denominator = denominator;
	}

	@Override
	public HttpDenominator getDenominator() {
		return denominator;
	}

	public PathHttpMethod getPathMethod(){
		return denominator.getPathHttpMethod();
	}

	public Set<String> getPathAttributes(){
		return denominator == null ? Collections.emptySet() : denominator.getPathAttributes();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void addHostHeader(){
		Objects.requireNonNull(host, "host is required");
		Objects.requireNonNull(port, "port is required");
		addHeaderElement(HttpHeaderFieldNames.HOST, RoboHttpUtils.createHost(host, port));
	}

	@Override
	public String toString() {
		return "HttpDecoratedRequest{" + " denominator=" + denominator + "\' " + super.toString() + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HttpDecoratedRequest that = (HttpDecoratedRequest) o;
		return Objects.equals(denominator, that.denominator) &&
				Objects.equals(host, that.host) &&
				Objects.equals(port, that.port);
	}

	@Override
	public int hashCode() {
		return Objects.hash(denominator, host, port);
	}
}
