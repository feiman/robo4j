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
import com.robo4j.RoboReference;
import com.robo4j.socket.http.units.ServerPathConfig;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public interface DefaultRequestFactory<ResponseType> {

	/**
	 * Get complete robo system description
	 * 
	 * @param context
	 *            robo context
	 * @return all available units
	 */
	ResponseType processGet(RoboContext context);

	/**
	 * Get detailed {@link RoboReference} description
	 * 
	 * @param roboReference
	 *            robo unit reference
	 * @param pathConfig
	 *            path to the robo unit reference
	 * @return full robo unit description with all available information
	 */
	ResponseType processGet(RoboReference<?> roboReference, Collection<ServerPathConfig> pathConfig);

	/**
	 * Get attributes of specific robo unit
	 *
	 * @param roboReference robo unit
	 * @return full robo unit description
	 */
	ResponseType processGet(RoboReference<?> roboReference, Set<String> requestAttributes) throws InterruptedException, ExecutionException;

	ResponseType processPost(RoboReference<?> unitReference, String message);
}
