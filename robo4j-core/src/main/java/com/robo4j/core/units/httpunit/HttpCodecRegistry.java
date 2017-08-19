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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.robo4j.core.units.httpunit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.robo4j.core.logging.SimpleLoggingUtil;
import com.robo4j.core.reflect.ReflectionScan;

/**
 * Registry for codecs.
 * 
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class HttpCodecRegistry {
	private Map<Class<?>, HttpEncoder<?>> encoders = new HashMap<>();
	private Map<Class<?>, HttpDecoder<?>> decoders = new HashMap<>();


	public HttpCodecRegistry() {
	}

	public HttpCodecRegistry(String... packages) {
		scan(Thread.currentThread().getContextClassLoader(), packages);
	}

	public void scan(ClassLoader loader, String... packages) {
		ReflectionScan scan = new ReflectionScan(loader);
		processClasses(loader, scan.scanForEntities(packages));
	}


	private void processClasses(ClassLoader loader, List<String> allClasses) {

		for (String className : allClasses) {
			try {
				Class<?> loadedClass = loader.loadClass(className);
				if (loadedClass.isAnnotationPresent(HttpProducer.class)) {
					addInstance(loadedClass);
				}
			} catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
				SimpleLoggingUtil.error(getClass(), "Failed to load encoder/decoder", e);
			}
		}
	}

	private void addInstance(Class<?> loadedClass) throws InstantiationException, IllegalAccessException {
		Object instance = loadedClass.newInstance();
		if (instance instanceof HttpEncoder) {
			HttpEncoder<?> encoder = (HttpEncoder<?>) instance;
			encoders.put(encoder.getEncodedClass(), encoder);
		}
		// Note, not "else if". People are free to implement both in the same
		// class
		if (instance instanceof HttpDecoder) {
			HttpDecoder<?> decoder = (HttpDecoder<?>) instance;
			decoders.put(decoder.getDecodedClass(), decoder);
		}
	}


	@SuppressWarnings("unchecked")
	public <T> HttpEncoder<T> getEncoder(Class<T> type) {
		return (HttpEncoder<T>) encoders.get(type);
	}

	@SuppressWarnings("unchecked")
	public <T> HttpDecoder<T> getDecoder(Class<T> type) {
		return (HttpDecoder<T>) decoders.get(type);
	}
}