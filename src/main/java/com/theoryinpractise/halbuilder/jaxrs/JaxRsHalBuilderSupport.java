package com.theoryinpractise.halbuilder.jaxrs;

import com.theoryinpractise.halbuilder.api.ReadableRepresentation;

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
public class JaxRsHalBuilderSupport implements MessageBodyWriter {

    private static final MediaType HAL_JSON_TYPE = new MediaType("application", "hal+json");

    private static final MediaType HAL_XML_TYPE = new MediaType("application", "hal+xml");

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return ReadableRepresentation.class.isAssignableFrom(aClass) && (mediaType.isCompatible(HAL_JSON_TYPE) || mediaType.isCompatible(HAL_XML_TYPE));
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
