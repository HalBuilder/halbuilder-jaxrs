package com.theoryinpractise.halbuilder.jaxrs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Consumes(RepresentationFactory.HAL_JSON)
@Produces(RepresentationFactory.HAL_JSON)
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    public ObjectMapper getContext(Class<?> aClass) {
        final ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new JsonSerializer<String>() {
            @Override
            public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(s.toUpperCase());
            }

            @Override
            public Class<String> handledType() {
                return String.class;
            }
        });
        mapper.registerModule(module);
        return mapper;
    }

}
