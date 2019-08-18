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

import com.robo4j.socket.http.codec.SimpleCommand;

import java.io.Serializable;
import java.util.List;

/**
 * TestMessageArray holds the array of different object
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class TestMessageSimpleCommands implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SimpleCommand> commands;

    public TestMessageSimpleCommands() {
    }

    public TestMessageSimpleCommands(List<SimpleCommand> commands) {
        this.commands = commands;
    }

    public List<SimpleCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<SimpleCommand> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "TestMessageSimpleCommandArray{" +
                "commands=" + commands +
                '}';
    }
}
