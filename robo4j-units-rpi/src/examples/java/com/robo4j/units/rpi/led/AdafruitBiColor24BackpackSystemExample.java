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

package com.robo4j.units.rpi.led;

import com.robo4j.RoboApplication;
import com.robo4j.RoboBuilder;
import com.robo4j.RoboContext;
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.net.LookupService;
import com.robo4j.net.LookupServiceProvider;
import com.robo4j.util.SystemUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * AdafruitBiColor24BackpackSystemExample provides an example of single
 * Robo4j system which does contain {@link Adafruit24BargraphUnit}. It is possible to send
 * a {@link LEDBackpackMessage} to the system.
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class AdafruitBiColor24BackpackSystemExample {

    public static void main(String[] args) throws Exception{
        System.out.println("... Adafruit Bargraph24 System Example ...");

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream systemIS = classLoader.getResourceAsStream("robo4jBargrap24System.xml");
        final InputStream contextIS = classLoader.getResourceAsStream("bargraph24example.xml");

        RoboBuilder builder = new RoboBuilder(systemIS);
        builder.add(contextIS);

        RoboContext system = builder.build();
        system.start();

        LookupService service = LookupServiceProvider.getDefaultLookupService();
        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleLoggingUtil.info(RoboApplication.class, SystemUtil.printStateReport(system));
        System.out.println("Press key...");
        System.in.read();
        service.stop();
        system.shutdown();
        System.out.println("Bye!");

    }
}
