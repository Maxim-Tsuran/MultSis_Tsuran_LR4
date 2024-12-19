package ru.mpei.AgentDetector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class SerializatorJason {

    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public static Optional<String> serialize(Object o){

        try {
            return Optional.of(mapper.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public static <T> Optional<T> deserialize(String json, Class<T> clazz){
        try {
            T res = mapper.readValue(json, clazz);
            return Optional.of(res);
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
