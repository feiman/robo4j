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

package com.robo4j.socket.http.dto;

import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.util.ReflectUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ResponseDecoderUnitDTOTest operation tests
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
class ResponseDecoderUnitDTOTests {


    @Test
    void jsonResponseDecoderUnitDTOTest(){

        String expectedJson = "{\"id\":\"testId\",\"codec\":\"testCodec\",\"methods\":[\"GET\",\"PUT\"]," +
                "\"attributes\":[{\"id\":\"aId1\",\"type\":\"aT1\",\"value\":\"test1\"}," +
                "{\"id\":\"aId2\",\"type\":\"aT2\",\"value\":\"test2\"}]}";
        List<ResponseAttributeDTO> attributes = new ArrayList<>();
        attributes.add(new ResponseAttributeDTO("aId1", "aT1", "test1"));
        attributes.add(new ResponseAttributeDTO("aId2", "aT2", "test2"));

        ResponseDecoderUnitDTO response = new ResponseDecoderUnitDTO();
        response.setId("testId");
        response.setCodec("testCodec");
        response.setMethods(Arrays.asList(HttpMethod.GET, HttpMethod.PUT));
        response.setAttributes(attributes);

        String json = ReflectUtils.createJson(response);

        assertEquals(expectedJson, json);
    }
}