package com.theoryinpractise.halbuilder.jaxrs;

import com.theoryinpractise.halbuilder.api.ContentRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({RepresentationFactory.HAL_JSON, RepresentationFactory.HAL_XML})
public class JaxRsHalBuilderReaderSupport implements MessageBodyReader<ContentRepresentation> {

    private final RepresentationFactory factory = new StandardRepresentationFactory();

    @Override
    public boolean isReadable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return ContentRepresentation.class.isAssignableFrom(aClass) && HalBuilderMediaTypes.isSupported(mediaType);
    }

    @Override
    public ContentRepresentation readFrom(Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, InputStream inputStream) throws IOException {
	return factory.readRepresentation(mediaType.toString(),
                                          new InputStreamReader(inputStream, HalBuilderMediaTypes.DEFAULT_ENCODING));
    }

}
