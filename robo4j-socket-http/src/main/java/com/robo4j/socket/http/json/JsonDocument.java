package com.robo4j.socket.http.json;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * representation of the read String stream
 *
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class JsonDocument {

    private final List<Object> array = new LinkedList<>();
    private final Map<String, Object> map = new LinkedHashMap<>();
    private final Type type;

    public JsonDocument(Type type) {
        this.type = type;
    }

    public void put(String key, Object value){
        map.put(key, value);
    }

    public void add(Object value){
        array.add(value);
    }

    public List<Object> getArray() {
        return array;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Type getType() {
        return type;
    }

    public boolean isArrays(){
        return type.equals(Type.ARRAY);
    }

    @Override
    public String toString() {
        return "JsonDocument{" +
                "array=" + array +
                ", map=" + map +
                ", type=" + type +
                '}';
    }

    public enum Type {
        OBJECT,
        ARRAY
    }
}
