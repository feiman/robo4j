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

package com.robo4j.socket.http.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class RoboHttpUtilsTest {

    @Test
    void validateNullPackage(){
        assertFalse(RoboHttpUtils.validatePackages(null));
    }

    @Test
    void validateEmptyPackage(){
        assertFalse(RoboHttpUtils.validatePackages(""));
    }

    @Test
    void validateEmptyPackag22e(){
        assertTrue(RoboHttpUtils.validatePackages("something"));
    }


    @Test
    void validatePackageOneCorrect() {
        assertTrue(RoboHttpUtils.validatePackages("com.robo4j.socket.http.codec"));
    }

    @Test
    void validatePackageMoreCorrect() {
        assertTrue(RoboHttpUtils.validatePackages("com.robo4j.socket.http.codec,com.robo4j.codecs.additional"));
    }

    @Test
    void validatePackageWrongCorrect() {
        assertFalse(RoboHttpUtils.validatePackages("com.robo4j,socket.http.codec, com.robo4j.codecs.additional"));
    }
}