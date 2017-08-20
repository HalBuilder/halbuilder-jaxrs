package com.theoryinpractise.halbuilder5.jaxrs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.Support;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationReader;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;

@Provider
@Produces(Support.HAL_JSON)
@Consumes(Support.HAL_JSON)
public class JaxRsHalBuilderSupport
    implements MessageBodyWriter<ResourceRepresentation<?>>,
        MessageBodyReader<ResourceRepresentation<?>> {

  @Context private Providers providers;

  @Override
  public boolean isReadable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return ResourceRepresentation.class.isAssignableFrom(type)
        && mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE);
  }

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return ResourceRepresentation.class.isAssignableFrom(type)
        && mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE);
  }

  @Override
  public long getSize(
      ResourceRepresentation<?> t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType) {
    return -1;
  }

  @Override
  public ResourceRepresentation<?> readFrom(
      Class<ResourceRepresentation<?>> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream)
      throws java.io.IOException, javax.ws.rs.WebApplicationException {
    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE)) {

      JsonRepresentationReader reader =
          Optional.ofNullable(
                  providers.getContextResolver(
                      ObjectMapper.class, HalBuilderMediaTypes.HAL_JSON_TYPE))
              .map(resolver -> resolver.getContext(ObjectMapper.class))
              .map(JsonRepresentationReader::create)
              .orElseGet(JsonRepresentationReader::create);

      return reader.read(new InputStreamReader(entityStream, Support.DEFAULT_ENCODING));
    } else {
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    }
  }

  @Override
  public void writeTo(
      ResourceRepresentation<?> t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws java.io.IOException, javax.ws.rs.WebApplicationException {
    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE)) {
      JsonRepresentationWriter writer =
          Optional.ofNullable(
                  providers.getContextResolver(
                      ObjectMapper.class, HalBuilderMediaTypes.HAL_JSON_TYPE))
              .map(resolver -> resolver.getContext(ObjectMapper.class))
              .map(JsonRepresentationWriter::create)
              .orElseGet(JsonRepresentationWriter::create);

      writer.write(t, new OutputStreamWriter(entityStream, Support.DEFAULT_ENCODING));
    } else {
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    }
  }
}
