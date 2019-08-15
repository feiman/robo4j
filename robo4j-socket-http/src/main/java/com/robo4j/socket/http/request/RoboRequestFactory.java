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

import com.robo4j.AttributeDescriptor;
import com.robo4j.RoboContext;
import com.robo4j.RoboReference;
import com.robo4j.socket.http.dto.ClassGetSetDTO;
import com.robo4j.socket.http.dto.PathAttributeDTO;
import com.robo4j.socket.http.dto.ResponseAttributeDTO;
import com.robo4j.socket.http.dto.ResponseDecoderUnitDTO;
import com.robo4j.socket.http.dto.ResponseUnitDTO;
import com.robo4j.socket.http.units.CodecRegistry;
import com.robo4j.socket.http.units.HttpServerUnit;
import com.robo4j.socket.http.units.ServerPathConfig;
import com.robo4j.socket.http.units.SocketDecoder;
import com.robo4j.socket.http.util.JsonUtil;
import com.robo4j.socket.http.util.ReflectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * RoboRequestFactory intent to be responsible for Response body
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class RoboRequestFactory implements DefaultRequestFactory<Object> {
	private final CodecRegistry codecRegistry;

	public RoboRequestFactory(final CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	/**
	 * Generic robo context overview. It returns all units registered into the
	 * context including system id. The 1st position is reserved for the system
	 *
	 * @param context
	 *            robo context
	 * @return description of desired context
	 */
	@Override
	public Object createRoboContextResponse(RoboContext context) {
		final List<ResponseUnitDTO> units = new LinkedList<>();
		for (RoboReference<?> rf : context.getUnits()) {
			units.add(new ResponseUnitDTO(rf.getId(), rf.getState()));
		}
		units.add(0, new ResponseUnitDTO(context.getId(), context.getState()));
		return JsonUtil.toJsonArray(units);
	}

	@Override
	public Object createRoboUnitResponse(RoboReference<?> roboReference, Collection<ServerPathConfig> pathConfigs) throws InterruptedException, ExecutionException {
		final SocketDecoder<?, ?> decoder = codecRegistry.getDecoder(roboReference.getMessageType());

		final List<ResponseAttributeDTO> attrList = new LinkedList<>();
		for (AttributeDescriptor<?> ad : roboReference.getKnownAttributes()) {
			ResponseAttributeDTO attributeDTO = createResponseAttributeDTO(roboReference, ad);
			if (ad.getAttributeName().equals(HttpServerUnit.ATTR_PATHS)) {
				attributeDTO.setType("java.util.ArrayList");
			}
			attrList.add(attributeDTO);
		}

		if (decoder == null) {
			return JsonUtil.toJsonArrayServer(attrList);
		} else {
			final ResponseDecoderUnitDTO responseDecoderUnitDTO = new ResponseDecoderUnitDTO();
			responseDecoderUnitDTO.setId(roboReference.getId());
			responseDecoderUnitDTO.setCodec(decoder.getDecodedClass().getName());
			responseDecoderUnitDTO
					.setMethods(pathConfigs.stream().map(ServerPathConfig::getMethod).collect(Collectors.toList()));
			responseDecoderUnitDTO.setAttributes(attrList);
			return ReflectUtils.createJson(responseDecoderUnitDTO);
		}
	}

	@Override
	public Object createRoboUnitAttributesResponse(RoboReference<?> roboReference, Set<String> requestAttributes)
			throws InterruptedException, ExecutionException {
		List<PathAttributeDTO> attributes = new ArrayList<>();
		for (AttributeDescriptor<?> attr : roboReference.getKnownAttributes()) {
			if (requestAttributes.contains(attr.getAttributeName())) {
				PathAttributeDTO attribute = new PathAttributeDTO();
				String valueString = String.valueOf(roboReference.getAttribute(attr).get());
				attribute.setValue(valueString);
				attribute.setName(attr.getAttributeName());
				attributes.add(attribute);
			}
		}
		if (attributes.size() == 1) {
			Map<String, ClassGetSetDTO> responseAttributeDescriptorMap = ReflectUtils
					.getFieldsTypeMap(PathAttributeDTO.class);
			return JsonUtil.toJson(responseAttributeDescriptorMap, attributes.get(0));
		} else {
			return JsonUtil.toJsonArray(attributes);
		}
	}

	/**
	 * currently is supported POST message in JSON format
	 *
	 * example: { "value" : "move" }
	 *
	 * @param unitReference
	 *            desired unit
	 * @param message
	 *            string message
	 * @return processed object
	 */
	@Override
	public Object processPost(final RoboReference<?> unitReference, final String message) {
		final SocketDecoder<Object, ?> decoder = codecRegistry.getDecoder(unitReference.getMessageType());
		return decoder == null ? null : decoder.decode(message);
	}

	// TODO: improve
	private ResponseAttributeDTO createResponseAttributeDTO(final RoboReference<?> reference,
			final AttributeDescriptor<?> ad) throws InterruptedException, ExecutionException {
		final Object val = reference.getAttribute(ad).get();
		final ResponseAttributeDTO attributeDTO = new ResponseAttributeDTO();
		attributeDTO.setId(ad.getAttributeName());
		attributeDTO.setType(ad.getAttributeType().getTypeName());
		attributeDTO.setValue(String.valueOf(val));
		return attributeDTO;
	}

}
