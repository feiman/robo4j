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
package com.robo4j.socket.http.units.test.codec;

import com.robo4j.socket.http.units.SocketDecoder;
import com.robo4j.socket.http.units.HttpProducer;

/**
 * Simple decoder that decodes json to an array of string.
 * 
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
@HttpProducer
public class TestArrayDecoder implements SocketDecoder<String, String[]> {
	@Override
	public String[] decode(String json) {
		String withoutStart = json.replace("array:", "");
		String withoutBrackets = withoutStart.replaceAll("[\\[\\]\\{\\}]", "");
		return withoutBrackets.split(",");
	}

	@Override
	public Class<String[]> getDecodedClass() {
		return String[].class;
	}
}
