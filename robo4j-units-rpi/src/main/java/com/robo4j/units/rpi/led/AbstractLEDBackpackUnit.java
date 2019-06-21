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

import com.robo4j.ConfigurationException;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.hw.rpi.i2c.adafruitoled.LEDBackpack;
import com.robo4j.hw.rpi.i2c.adafruitoled.LEDBackpackFactory;
import com.robo4j.hw.rpi.i2c.adafruitoled.LEDBackpackType;
import com.robo4j.hw.rpi.i2c.adafruitoled.PackElement;
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.units.rpi.I2CEndPoint;
import com.robo4j.units.rpi.I2CRegistry;

import java.util.List;

import static com.robo4j.hw.rpi.i2c.adafruitoled.LEDBackpack.DEFAULT_BRIGHTNESS;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public abstract class AbstractLEDBackpackUnit<T extends LEDBackpack> extends RoboUnit<LEDBackpackMessage> {

    public AbstractLEDBackpackUnit(Class<LEDBackpackMessage> messageType, RoboContext context, String id) {
        super(messageType, context, id);
    }

    @SuppressWarnings("unchecked")
    T getBackpackDevice(LEDBackpackType type, int bus, int address) throws ConfigurationException {
        I2CEndPoint endPoint = new I2CEndPoint(bus, address);
        Object device = I2CRegistry.getI2CDeviceByEndPoint(endPoint);
        if (device == null) {
            try {
                device = LEDBackpackFactory.createDevice(bus, address, type, DEFAULT_BRIGHTNESS);
                // Note that we cannot catch hardware specific exceptions here,
                // since they will be loaded when we run as mocked.
            } catch (Exception e) {
                throw new ConfigurationException(e.getMessage());
            }
            I2CRegistry.registerI2CDevice(device, new I2CEndPoint(bus, address));
        }
        return (T) device;
    }

    void processMessage(LEDBackpack device, LEDBackpackMessage message){
        switch (message.getType()){
            case CLEAR:
                device.clear();
                break;
            case ADD:
                addElements(message.getElements());
                break;
            case DISPLAY:
                addElements(message.getElements());
                device.display();
                break;
            default:
                SimpleLoggingUtil.error(getClass(), String.format("Illegal message: %s", message));

        }
    }

    abstract void addElements(List<PackElement> elements);
}
