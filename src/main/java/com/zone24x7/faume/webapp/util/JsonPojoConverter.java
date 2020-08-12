package com.zone24x7.faume.webapp.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Map;

/**
 * Utility class for handling Json Pojo conversions
 */
public final class JsonPojoConverter {

    private static ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    /**
     * Private constructor
     */
    private JsonPojoConverter() {
        // Private constructor
    }

    /**
     * Method to convert an input json string to a given pojo of class type.
     *
     * @param inputNode the input json string
     * @param classType the class type to convert
     * @param <T>       the class type
     * @return the pojo
     * @throws IOException if an error occurs
     */
    public static <T> T toPojo(String inputNode, Class<T> classType) throws IOException {
        return mapper.readValue(inputNode, classType);
    }

    /**
     * Method to convert a pojo object to a json node.
     *
     * @param pojo the pojo object to convert
     * @param <T>  the class type
     * @return the json node
     */
    public static <T> JsonNode toJson(T pojo) {
        return mapper.convertValue(pojo, JsonNode.class);
    }

    /**
     * Method to convert a pojo to a map.
     *
     * @param pojo the pojo object to convert
     * @param <T>  <T>  the class type
     * @return the pojo
     */
    public static <T> Map<String, String> toMap(T pojo) {
        return mapper.convertValue(pojo, new TypeReference<Map<String, String>>() {});
    }

    /**
     * Method to convert string to given pojo class.
     *
     * @param inputNode         input json string
     * @param typeReference     type to convert
     * @return the pojo
     */
    public static <T> T toPojo(String inputNode, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(inputNode, typeReference);
    }
}
