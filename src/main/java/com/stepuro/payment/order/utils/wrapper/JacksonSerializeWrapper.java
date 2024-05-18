package com.stepuro.payment.order.utils.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.type.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JacksonSerializeWrapper {

    private static final Logger log = LoggerFactory.getLogger(JacksonSerializeWrapper.class);

    private final ObjectMapper objectMapper;

    public JacksonSerializeWrapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T deserialize(String jsonString, Class<T> clazz) {
        final T object;

        try {
            object = objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации json: {}", jsonString);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> T deserialize(String jsonString, TypeReference<T> typeReference) {
        final T object;

        try {
            object = objectMapper.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации json: {}", jsonString);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> T deserialize(JsonNode jsonNode, TypeReference<T> typeReference) {
        final T object;

        try {
            object = (T) objectMapper.treeToValue(jsonNode, TypeFactory.rawClass(typeReference.getType()));
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации json: {}", jsonNode);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> T deserialize(JsonNode jsonNode, Class<T> clazz) {
        final T object;

        try {
            object = objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации json: {}", jsonNode);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        final T object;

        try {
            object = objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("Произошла ошибка при десериализации json", e);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> T deserialize(byte[] bytes, TypeReference<T> typeReference) {
        final T object;

        try {
            object = objectMapper.readValue(bytes, typeReference);
        } catch (IOException e) {
            log.error("Произошла ошибка при десериализации json", e);
            throw new SerializationException(e.getMessage(), e);
        }

        return object;
    }

    public <T> String serialize(T obj) {
        final String jsonString;

        try {
            jsonString = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при сериализации объекта: {}", obj);
            throw new SerializationException(e.getMessage(), e);
        }

        return jsonString;
    }

    public <T> JsonNode serializeToTree(T obj) {
        final JsonNode jsonNode;

        try {
            jsonNode = objectMapper.valueToTree(obj);
        } catch (IllegalArgumentException e) {
            log.error("Произошла ошибка при сериализации объекта: {}", obj);
            throw new SerializationException(e.getMessage(), e);
        }

        return jsonNode;
    }

    public <T> byte[] serializeAsBytes(T obj) {
        final byte[] jsonBytes;

        try {
            jsonBytes = objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при сериализации объекта: {}", obj);
            throw new SerializationException(e.getMessage(), e);
        }

        return jsonBytes;
    }
}