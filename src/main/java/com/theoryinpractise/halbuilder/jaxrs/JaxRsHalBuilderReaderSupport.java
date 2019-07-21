package com.theoryinpractise.halbuilder.jaxrs;

import com.theoryinpractise.halbuilder.api.ContentRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

@Provider
@Consumes({RepresentationFactory.HAL_JSON, RepresentationFactory.HAL_XML})
public class JaxRsHalBuilderReaderSupport implements MessageBodyReader<ContentRepresentation> {

  @Context private Providers providers;

  @Override
  public boolean isReadable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return ContentRepresentation.class.isAssignableFrom(aClass) && HalBuilderMediaTypes.isSupported(mediaType);
  }

  @Override
  public ContentRepresentation readFrom(
      Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, InputStream inputStream) throws IOException {
    if (mediaType.isCompatible(HalBuilderMediaTypes.HAL_JSON_TYPE)) {
      return new JsonRepresentationFactory(new ObjectMapperLocator(providers).locate(aClass, mediaType))
          .readRepresentation(mediaType.toString(), new InputStreamReader(inputStream, HalBuilderMediaTypes.DEFAULT_ENCODING));
    }
    return new StandardRepresentationFactory()
        .readRepresentation(mediaType.toString(), new InputStreamReader(inputStream, HalBuilderMediaTypes.DEFAULT_ENCODING));
  }
}
