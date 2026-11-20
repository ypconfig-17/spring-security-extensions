/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 * Modified for this project: relocated to the project namespace.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ypconfig17.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * @author yangpan
 */


public final class JacksonUtils {

    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        mapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
    }
    /**
     * Object to json string.
     *
     * @param obj obj
     * @return json string
     * @throws  if transfer failed
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param type {@link Type} of object
     * @param <T>  General type
     * @return object
     */
    public static <T> T toObj(String json, Type type) {
        try {
            return mapper.readValue(json, mapper.constructType(type));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     */
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Json string deserialize to Jackson {@link JsonNode}.
     *
     * @param json json string
     * @return {@link JsonNode}
     */
    public static JsonNode toObj(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        mapper.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(mapper.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(mapper.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return mapper.constructType(type);
    }
}
