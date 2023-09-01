package com.addmore.crawler.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CommonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object data) {
        String result;
        try {
            result = gson.toJson(data);
        } catch (Exception e) {
            result = e.getMessage();
            log.info("this data is not a json type");
        }
        return result;
    }

    public static Map<String, Object> fromJson(String data) {
        Map<String, Object> result_data = (Map<String, Object>)gson.fromJson(data, Map.class);
        return result_data;
    }
}
