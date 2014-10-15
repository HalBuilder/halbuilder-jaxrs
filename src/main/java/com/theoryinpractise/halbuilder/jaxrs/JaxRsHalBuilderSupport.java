package com.theoryinpractise.halbuilder.jaxrs;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({RepresentationFactory.HAL_XML, RepresentationFactory.HAL_JSON})
public class JaxRsHalBuilderSupport implements MessageBodyWriter {

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return ReadableRepresentation.class.isAssignableFrom(aClass) && HalBuilderMediaTypes.isSupported(mediaType);
    }

    @Override
    public long getSize(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        ReadableRepresentation representation = (ReadableRepresentation) o;
        return representation.toString(mediaType.toString()).length();
    }

    @Override
    public void writeTo(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        ReadableRepresentation representation = (ReadableRepresentation) o;
        representation.toString(mediaType.toString(), new OutputStreamWriter(outputStream));
    }

}
