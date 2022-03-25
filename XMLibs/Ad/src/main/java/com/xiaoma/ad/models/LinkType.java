package com.xiaoma.ad.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author KY
 * @date 2018/9/13
 */
public enum LinkType {

    /**
     * 无跳转
     */
    NONE(0),
    /**
     * H5页面跳转链接
     */
    WEB(1),
    /**
     * 内部跳转，URI协议跳转
     */
    INNER_JUMP(2);

    private int value;

    LinkType(int value) {
        this.value = value;
    }

    public static LinkType valueOf(int value) {
        switch (value) {
            case 0:
                return NONE;
            case 1:
                return WEB;
            case 2:
                return INNER_JUMP;
            default:
                return NONE;
        }
    }

    public int getValue() {
        return value;
    }

    public static class GsonAdapter implements JsonSerializer<LinkType>, JsonDeserializer<LinkType> {
        @Override
        public JsonElement serialize(LinkType linkType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(linkType.getValue());
        }

        @Override
        public LinkType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            int linkType = jsonElement.getAsInt();
            return LinkType.valueOf(linkType);
        }
    }

}
