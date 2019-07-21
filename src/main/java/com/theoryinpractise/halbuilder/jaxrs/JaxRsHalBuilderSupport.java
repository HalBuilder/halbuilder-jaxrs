package com.theoryinpractise.halbuilder.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;

@Provider
@Produces({RepresentationFactory.HAL_XML, RepresentationFactory.HAL_JSON})
public class JaxRsHalBuilderSupport implements MessageBodyWriter {

  @Context private Providers providers;

  @Override
  public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return ReadableRepresentation.class.isAssignableFrom(aClass) && HalBuilderMediaTypes.isSupported(mediaType);
  }

  @Override
  public long getSize(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(
      Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream)
      throws IOException, WebApplicationException {
    ReadableRepresentation representation = (ReadableRepresentation) o;
    OutputStreamWriter writer = new OutputStreamWriter(outputStream, HalBuilderMediaTypes.DEFAULT_ENCODING);
    ObjectMapper mapper = new ObjectMapperLocator(providers).locate(aClass, mediaType);
    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE) && mapper != null) {
      new JsonRepresentationFactory(mapper).lookupRenderer(mediaType.toString()).write(representation, Collections.<URI>emptySet(), writer);
    } else {
      representation.toString(mediaType.toString(), writer);
    }
  }
}
