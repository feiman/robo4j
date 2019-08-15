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

import com.robo4j.socket.http.HttpHeaderFieldNames;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.HttpVersion;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.message.AbstractHttpDecoratedMessage;
import com.robo4j.socket.http.message.HttpDecoratedRequest;
import com.robo4j.socket.http.message.HttpDecoratedResponse;
import com.robo4j.socket.http.message.HttpRequestDenominator;
import com.robo4j.socket.http.message.HttpResponseDenominator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.robo4j.socket.http.util.HttpConstant.HTTP_NEW_LINE;
import static com.robo4j.socket.http.util.HttpMessageUtils.HTTP_HEADER_BODY_DELIMITER;
import static com.robo4j.socket.http.util.HttpMessageUtils.POSITION_BODY;
import static com.robo4j.socket.http.util.HttpMessageUtils.POSITION_HEADER;

/**
 * ChannelBufferUtils useful utilities to work with channel
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class ChannelBufferUtils {

	/**
	 * Helper
	 *
	 * @param message
	 *            string message
	 * @return parsed message
	 */
	private static MessageDecorationHelper extractMessageDecoratorValues(String message) {
		final MessageDecorationHelper helper = new MessageDecorationHelper();
		helper.headerAndBody = message.split(HTTP_HEADER_BODY_DELIMITER);
		helper.header = helper.headerAndBody[POSITION_HEADER].split("[" + HTTP_NEW_LINE + "]+");
		helper.firstLine = RoboHttpUtils.correctLine(helper.header[0]);
		helper.tokens = helper.firstLine.split(HttpConstant.HTTP_EMPTY_SEP);
		helper.paramArray = Arrays.copyOfRange(helper.header, 1, helper.header.length);
		return helper;
	}

	public static final Pattern RESPONSE_SPRING_PATTERN = Pattern.compile("^(\\d.\r\n)?(.*)(\r\n)?");
	/**
	 * only for simple params max with one -
	 */
	public static final Pattern HEADER_HOST_PARAMS = Pattern.compile("(^\\w+-?\\w+):(.*)");
	public static final int CHANNEL_TIMEOUT = 60000;
	public static final int INIT_BUFFER_CAPACITY = 4 * 4096;
	public static final byte CHAR_NEW_LINE = 0x0A;
	public static final byte CHAR_RETURN = 0x0D;
	public static final byte[] END_WINDOW = { CHAR_NEW_LINE, CHAR_NEW_LINE };
	public static final int BUFFER_MARK_END = -1;
	public static final int RESPONSE_JSON_GROUP = 2;

	/**
	 *
	 * @param source
	 *            buffer
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 * @return buffer
	 */
	public static ByteBuffer copy(ByteBuffer source, int start, int end) {
		ByteBuffer result = ByteBuffer.allocate(end);
		for (int i = start; i < end; i++) {
			result.put(source.get(i));
		}
		return result;
	}

	/**
	 *
	 * @param message
	 *            message
	 * @return byte buffer
	 */
	public static ByteBuffer getByteBufferByString(String message) {
		ByteBuffer result = ByteBuffer.allocate(message.length());
		result.put(message.getBytes());
		result.flip();
		return result;
	}

	/**
	 *
	 * @param array1
	 *            array 1
	 * @param array2
	 *            array 2
	 * @return byte array
	 */
	public static byte[] joinByteArrays(final byte[] array1, byte[] array2) {
		byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	/**
	 *
	 * @param array
	 *            array
	 * @param size
	 *            size
	 * @return byte array
	 */
	public static byte[] validArray(byte[] array, int size) {
		return validArray(array, 0, size);
	}

	/**
	 *
	 * @param stopWindow
	 *            byte array
	 * @param window
	 *            byte array
	 * @return is byte array window
	 */
	public static boolean isBWindow(byte[] stopWindow, byte[] window) {
		return Arrays.equals(stopWindow, window);
	}

	private static final class MessageDecorationHelper {
		private String[] headerAndBody;
		private String[] header;
		private String firstLine;
		private String[] tokens;
		private String[] paramArray;

		private MessageDecorationHelper() {
		}
	}

	public static HttpDecoratedResponse extractDecoratedResponseByStringMessage(String message) {
		final MessageDecorationHelper helper = extractMessageDecoratorValues(message);

		final String version = helper.tokens[0];
		final StatusCode statusCode = StatusCode.getByCode(Integer.valueOf(helper.tokens[1]));
		final Map<String, String> headerParams = ChannelBufferUtils.getHeaderParametersByArray(helper.paramArray);

		HttpResponseDenominator denominator = new HttpResponseDenominator(statusCode, HttpVersion.getByValue(version));
		HttpDecoratedResponse result = new HttpDecoratedResponse(headerParams, denominator);
		if (helper.headerAndBody.length > 1) {
			if (headerParams.containsKey(HttpHeaderFieldNames.CONTENT_LENGTH)) {
				result.setLength(ChannelBufferUtils.calculateMessageSize(helper.headerAndBody[POSITION_HEADER].length(),
						headerParams));
			} else {
				result.setLength(helper.headerAndBody[POSITION_BODY].length());
			}
			Matcher matcher = ChannelBufferUtils.RESPONSE_SPRING_PATTERN.matcher(helper.headerAndBody[POSITION_BODY]);
			if (matcher.find()) {
				result.addMessage(matcher.group(ChannelBufferUtils.RESPONSE_JSON_GROUP));
			}
		}

		return result;
	}

	/**
	 *
	 * @param message
	 *            message
	 * @return http decorate request
	 */
	public static HttpDecoratedRequest extractDecoratedRequestByStringMessage(String message) {
		final MessageDecorationHelper helper = extractMessageDecoratorValues(message);

		final HttpMethod method = HttpMethod.getByName(helper.tokens[HttpMessageUtils.METHOD_KEY_POSITION]);
		final String path = helper.tokens[HttpMessageUtils.URI_VALUE_POSITION];
		final String version = helper.tokens[HttpMessageUtils.VERSION_POSITION];
		final Map<String, String> headerParams = getHeaderParametersByArray(helper.paramArray);

		final HttpRequestDenominator denominator;
		if (path.contains(HttpPathUtils.DELIMITER_PATH_ATTRIBUTES)) {
			denominator = new HttpRequestDenominator(method, path.split(HttpPathUtils.REGEX_ATTRIBUTE)[0],
					HttpVersion.getByValue(version), HttpPathUtils.extractAttributesByPath(path));
		} else {
			denominator = new HttpRequestDenominator(method, path, HttpVersion.getByValue(version));
		}
		HttpDecoratedRequest result = new HttpDecoratedRequest(headerParams, denominator);

		if (headerParams.containsKey(HttpHeaderFieldNames.CONTENT_LENGTH)) {
			result.setLength(calculateMessageSize(helper.headerAndBody[POSITION_HEADER].length(), headerParams));
			if (helper.headerAndBody.length > 1) {
				result.addMessage(helper.headerAndBody[POSITION_BODY]);
			}
		}

		return result;
	}

	/**
	 * convert byte buffer to string and clean
	 * 
	 * @param buffer
	 *            incoming buffer
	 * @return string
	 */
	public static String byteBufferToString(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		while (buffer.hasRemaining()) {
			sb.append((char) buffer.get());
		}
		buffer.clear();
		return sb.toString();
	}

	/**
	 *
	 * @param result
	 *            result message
	 * @param channel
	 *            byte channel
	 * @param buffer
	 *            buffer
	 * @param readBytes
	 *            read bytes
	 * @throws IOException
	 *             exception
	 */
	static void readChannelBuffer(AbstractHttpDecoratedMessage result, ByteChannel channel, ByteBuffer buffer,
			int readBytes) throws IOException {
		final StringBuilder sbAdditional = new StringBuilder();
		int totalReadBytes = readBytes;
		if (result.getLength() != 0) {
			while (totalReadBytes < result.getLength()) {
				readBytes = channel.read(buffer);
				buffer.flip();
				ChannelBufferUtils.addToStringBuilder(sbAdditional, buffer, readBytes);

				totalReadBytes += readBytes;
				buffer.clear();
			}
			if (sbAdditional.length() > 0) {
				result.addMessage(sbAdditional.toString());
			}
		}
	}

	/**
	 *
	 * @param paramArray
	 *            params array
	 * @return map
	 */
	private static Map<String, String> getHeaderParametersByArray(String[] paramArray) {
		final Map<String, String> result = new HashMap<>();
		for (int i = 0; i < paramArray.length; i++) {

			Matcher matcher = HEADER_HOST_PARAMS.matcher(paramArray[i]);
			while (matcher.find()) {
				result.put(matcher.group(1).trim(), matcher.group(2).trim());
			}
		}
		return result;
	}

	private static Integer calculateMessageSize(int headerValue, Map<String, String> headerParams) {
		return headerValue + HTTP_HEADER_BODY_DELIMITER.length()
				+ Integer.valueOf(headerParams.get(HttpHeaderFieldNames.CONTENT_LENGTH));
	}

	/**
	 *
	 * @param sb
	 *            string builder
	 * @param buffer
	 *            buffer
	 * @param size
	 *            size
	 */
	static void addToStringBuilder(StringBuilder sb, ByteBuffer buffer, int size) {
		byte[] array = new byte[size];
		for (int i = 0; i < size; i++) {
			array[i] = buffer.get(i);
		}
		final String message = new String(array);
		sb.append(message);
	}

	/**
	 *
	 * @param array
	 *            array
	 * @param start
	 *            start position
	 * @param size
	 *            size
	 * @return byte array
	 */
	private static byte[] validArray(byte[] array, int start, int size) {
		byte[] result = new byte[size];
		System.arraycopy(result, start, array, 0, size);
		return result;
	}

}
