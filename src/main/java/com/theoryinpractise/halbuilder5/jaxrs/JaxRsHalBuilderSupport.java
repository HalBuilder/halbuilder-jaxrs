package com.theoryinpractise.halbuilder5.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.Support;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationReader;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

@Provider
@Produces(Support.HAL_JSON)
@Consumes(Support.HAL_JSON)
public class JaxRsHalBuilderSupport<T extends ResourceRepresentation<?>>
    implements MessageBodyWriter<T>, MessageBodyReader<T> {
  @Context private Providers providers;

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return ResourceRepresentation.class.isAssignableFrom(type)
        && HalBuilderMediaTypes.isSupported(mediaType);
  }

  @Override
  public long getSize(
      T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isReadable(
      Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return ResourceRepresentation.class.isAssignableFrom(aClass)
        && HalBuilderMediaTypes.isSupported(mediaType);
  }

  @Override
  public T readFrom(
      Class<T> aClass,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream)
      throws WebApplicationException {
    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE)) {

      JsonRepresentationReader reader =
          Optional.ofNullable(
                  providers.getContextResolver(
                      ObjectMapper.class, HalBuilderMediaTypes.HAL_JSON_TYPE))
              .map(resolver -> resolver.getContext(ObjectMapper.class))
              .map(JsonRepresentationReader::create)
              .orElseGet(JsonRepresentationReader::create);

      return (T) reader.read(new InputStreamReader(entityStream, Support.DEFAULT_ENCODING), aClass);
    } else if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_XML_TYPE)) {
      //TODO Provide a writer for HAL+XML.
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    } else {
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    }
  }

  @Override
  public void writeTo(
      T t,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws WebApplicationException {
    ResourceRepresentation<?> representation = t;

    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE)) {
      JsonRepresentationWriter writer =
          Optional.ofNullable(
                  providers.getContextResolver(
                      ObjectMapper.class, HalBuilderMediaTypes.HAL_JSON_TYPE))
              .map(resolver -> resolver.getContext(ObjectMapper.class))
              .map(JsonRepresentationWriter::create)
              .orElseGet(JsonRepresentationWriter::create);
      writer.write(representation, new OutputStreamWriter(entityStream, Support.DEFAULT_ENCODING));
    } else if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_XML_TYPE)) {
      //TODO Provide a writer for HAL+XML.
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    } else {
      throw new RuntimeException("No writer available for media type '" + mediaType + "'");
    }
  }
}
