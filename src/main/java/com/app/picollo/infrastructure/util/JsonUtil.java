package com.app.picollo.infrastructure.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    public static String objectToJson(Object content){
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T> Object jsonToObject(String content, Class<T> clazz ){
        ObjectMapper mapper = new ObjectMapper();
        Object result = null;
        try {
            result = mapper.readValue(content, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
