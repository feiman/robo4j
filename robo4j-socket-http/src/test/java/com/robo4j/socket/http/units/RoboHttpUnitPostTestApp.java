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

package com.robo4j.socket.http.units;

import com.robo4j.RoboBuilder;
import com.robo4j.RoboContext;
import com.robo4j.configuration.Configuration;
import com.robo4j.configuration.ConfigurationBuilder;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.units.test.MessageSimpleCommandsUnit;
import com.robo4j.socket.http.util.HttpPathConfigJsonBuilder;
import com.robo4j.util.SystemUtil;

import static com.robo4j.socket.http.util.RoboHttpUtils.PROPERTY_CODEC_PACKAGES;
import static com.robo4j.socket.http.util.RoboHttpUtils.PROPERTY_SOCKET_PORT;
import static com.robo4j.socket.http.util.RoboHttpUtils.PROPERTY_UNIT_PATHS_CONFIG;

/**
 * RoboHttpUnitPostTestApp is the simple stand-alone app to test POST request
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class RoboHttpUnitPostTestApp {

    private static final int SERVER_PORT = 8061;
    private static final String UNIT_ID_HTTP_CLIENT = "http_client";

    public static void main(String[] args) throws Exception {
        new RoboHttpUnitPostTestApp().systemWithHttpServerOnlyTest();
    }

    public void systemWithHttpServerOnlyTest() throws Exception {
        final HttpPathConfigJsonBuilder pathBuilder = HttpPathConfigJsonBuilder.Builder()
                .addPath(HttpServerUnit.NAME, HttpMethod.GET)
                .addPath(MessageSimpleCommandsUnit.NAME, HttpMethod.POST);

        //@formatter:off
		Configuration systemConfiguration = new ConfigurationBuilder()
				.addInteger("poolSizeScheduler", 3)
				.addInteger("poolSizeWorker", 2)
				.addInteger("poolSizeBlocking", 2)
				.build();
		RoboBuilder builder = new RoboBuilder("roboSystem1", systemConfiguration);
		//@formatter:on

        //@formatter:off
		Configuration configServer = new ConfigurationBuilder()
				.addInteger(PROPERTY_SOCKET_PORT, SERVER_PORT)
				.addString(PROPERTY_CODEC_PACKAGES, "com.robo4j.socket.http.units.test.codec")
				.addString(PROPERTY_UNIT_PATHS_CONFIG, pathBuilder.build())
				.build();
		//@formatter:on
        builder.add(HttpServerUnit.class, configServer, HttpServerUnit.NAME);
        builder.add(MessageSimpleCommandsUnit.class, MessageSimpleCommandsUnit.NAME);
        RoboContext system = builder.build();

        system.start();
        System.out.println("systemPong: State after start:");
        System.out.println(SystemUtil.printStateReport(system));
        System.out.println("Press <Enter>...");
        System.in.read();
        system.shutdown();
    }
}
