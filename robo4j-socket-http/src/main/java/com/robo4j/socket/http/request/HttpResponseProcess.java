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

import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.enums.StatusCode;

import java.util.Objects;

/**
 *  wrapper for http request result
 *  @see HttpRequestProcessor
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class HttpResponseProcess implements ChannelResponseProcess<String> {
    private String path;
    private String target;
    private HttpMethod method;
    private StatusCode code;
    private Object result;

    HttpResponseProcess(String path, String target, HttpMethod method, StatusCode code, Object result) {
        this.path = path;
        this.target = target;
        this.method = method;
        this.code = code;
        this.result = result;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public StatusCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "HttpResponseProcess{" +
                "target='" + target + '\'' +
                ", method=" + method +
                ", code=" + code +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponseProcess process = (HttpResponseProcess) o;
        return Objects.equals(path, process.path) &&
                Objects.equals(target, process.target) &&
                method == process.method &&
                code == process.code &&
                Objects.equals(result, process.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, target, method, code, result);
    }
}
