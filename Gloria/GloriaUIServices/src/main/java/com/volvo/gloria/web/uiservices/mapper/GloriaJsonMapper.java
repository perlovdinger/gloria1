package com.volvo.gloria.web.uiservices.mapper;

import java.text.SimpleDateFormat;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.volvo.gloria.util.DateUtil;

/**
 * 
 * Mapper to handle the date time conversions, handle polymorphic data binding.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GloriaJsonMapper implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public GloriaJsonMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.getGloriaDateTimeFormat()));
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // @Class property should be ignored during DESERIALIZATION. The custom type resolver will still be effective otherwise.
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // set the custom type resolver to act on PageObject.gridContents[List<PageResults>], ReportFilters.
        TypeResolverBuilder<?> typeResolver = new GloriaResponseObjectTypeResolver();
        typeResolver.init(JsonTypeInfo.Id.CLASS, null);
        typeResolver.inclusion(JsonTypeInfo.As.PROPERTY);
        objectMapper.setDefaultTyping(typeResolver);
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }
}
