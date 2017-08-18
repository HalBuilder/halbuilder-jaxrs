package com.theoryinpractise.halbuilder5.jaxrs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.theoryinpractise.halbuilder5.Support;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Consumes(Support.HAL_JSON)
@Produces(Support.HAL_JSON)
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

  @Override
  public ObjectMapper getContext(Class<?> aClass) {
    final ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(
        new JsonSerializer<String>() {
          @Override
          public void serialize(
              String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
              throws IOException {
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
