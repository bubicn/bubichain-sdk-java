package cn.bubi.sdk.core.utils;

import com.google.gson.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GsonUtil{

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> DateUtil.parse(json.getAsString()))
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, typeOfSrc, context) -> new JsonPrimitive(DateUtil.format(date)))
            .disableHtmlEscaping().setPrettyPrinting().create();

    public static String toJson(Object object){
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clz){
        return GSON.fromJson(json, clz);
    }


    public static class DateUtil{

        private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        static String format(Date date){
            if (date == null) {
                return null;
            }
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(formatter);
        }

        static Date parse(String dateStr){
            Instant instant = LocalDateTime.parse(dateStr, formatter).atZone(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);
        }

        public static void main(String[] args){
            for (int i = 0; i < 1000; i++) {
                new Thread(() -> {
                    String str = format(new Date());
                    System.out.println(str);
                    System.out.println(parse(str));
                }).start();
            }

        }
    }

}
