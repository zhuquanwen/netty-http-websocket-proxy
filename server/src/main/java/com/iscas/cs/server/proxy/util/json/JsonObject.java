package com.iscas.cs.server.proxy.util.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/5/9 11:11
 * @since jdk1.8
 */
public class JsonObject implements Json {
    // 初始容量
    private int INITIAL_CAPACITY = 16;

    private Map<String, Object> data ;

    public JsonObject() {
        data = new LinkedHashMap<>(INITIAL_CAPACITY);
    }
    public JsonObject(int capacity) {
        data = new LinkedHashMap<>(capacity);
    }
    public Map<String, Object> toMap() {
        return data;
    }

    @Override
    public String toJson() {
        return data == null ? null : JsonUtils.toJson(data);
    }

    public <T> T fromJson(Class<T> tClass) {
        return JsonUtils.fromJson(toJson(), tClass);
    }

    /**
     * <p>向JsonObject中注入值</p>
     * @version 1.0
     * @since jdk1.8
     * @date 2019/5/9
     * @param key 键
     * @param value 值 支持JsonArray、JsonObject、List、Map、JavaBean
     * @throws
     * @return com.iscas.common.web.tools.json.JsonObject
     */
    public JsonObject set(String key, Object value) {
        if (value != null) {
            data.put(key, JsonUtils.convertValue(value));
        }
        return this;
    }

}
