/*
 * Copyright (C)  2016. Miroslav Kopecky
 * This RequestUnitStatusEnum.java  is part of robo4j.
 *
 *  robo4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  robo4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.core.client.enums;

/**
 * Request State used for the HTTP in
 *
 * //TODO: separate properly request status/result of the procedure status
 * 
 * @see com.robo4j.core.client.request.ProcessorResult
 *
 * @author Miro Kopecky (@miragemiko)
 * @since 13.11.2016
 */
public enum RequestUnitStatusEnum {

	// formatter:off
	STOP(0, "stop"), ACTIVE(1, "active"), STATUS(2, "status"), SETUP(3, "setup"),;
	// formatter:on

	private int code;
	private String desc;

	RequestUnitStatusEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "RequestUnitStatusEnum{" + "code=" + code + ", desc='" + desc + '\'' + '}';
	}
}