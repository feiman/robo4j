/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.socket.http.message;

import com.robo4j.socket.http.util.HttpDenominator;
import com.robo4j.socket.http.util.HttpHeaderBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * General Http Message properties
 *
 * each HttpMessage may contains callback units
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public abstract class AbstractHttpMessageDescriptor {
	private final HttpHeaderBuilder headerBuilder = HttpHeaderBuilder.Build();
	private final String version;
	private int length;
	private String message;
	private List<String> callbacks = new ArrayList<>();

	AbstractHttpMessageDescriptor(String version) {
		this.version = version;
	}

	AbstractHttpMessageDescriptor(Map<String, String> header, String version) {
		this.headerBuilder.addAll(header);
		this.version = version;
	}

	public abstract HttpDenominator getDenominator();

	public Map<String, String> getHeader() {
		return headerBuilder.getMap();
	}

	public String getHeaderValue(String key) {
		return headerBuilder.getValue(key);
	}

	public void addHeaderElement(String key, String value) {
		headerBuilder.add(key, value);
	}

	public void addHeaderElements(Map<String, String> map) {
		headerBuilder.addAll(map);
	}

	public String generateHeader() {
		return headerBuilder.build();
	}

	public String getVersion() {
		return version;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMessage() {
		return message;
	}

	public void addMessage(String message) {
		this.message = this.message == null ? message : this.message.concat(message);
	}

	public void addCallbacks(List<String> callbacks){
		this.callbacks.addAll(callbacks);
	}
	public void addCallback(String callback){
		callbacks.add(callback);
	}

	public List<String> getCallbacks() {
		return callbacks;
	}

	@Override
	public String toString() {
		return "header=" + headerBuilder.getMap() + ", version='" + version + '\'' + ", length=" + length
				+ ", message='" + message + '\'' + '}';
	}
}
