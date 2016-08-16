package com.theoryinpractise.halbuilder.jaxrs;

import com.theoryinpractise.halbuilder.api.ContentRepresentation;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ObjectMapperViaContextResolverTest extends JerseyTest {

    private static final URI BASE = URI.create("http://example.org/");
    private static RepresentationFactory factory = new JsonRepresentationFactory();

    public static class Fields {
        public String alphabet = "abcdefghijklmnopqrstuvwxyz";
    }

    @Path("test")
    public static class TestResource {

        @Path("json")
        @Produces(RepresentationFactory.HAL_JSON)
        @GET
        public Representation writeJSON() {
            return factory.newRepresentation(BASE)
                    .withFields(new Fields());
        }

    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class)
                .register(JaxRsHalBuilderSupport.class)
                .register(ObjectMapperContextResolver.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JaxRsHalBuilderReaderSupport.class);
        config.register(ObjectMapperContextResolver.class);
    }

    @Test
    public void shouldUseProvidedObjectMapper() throws IOException {
        ContentRepresentation response = target("/test/json").request(HalBuilderMediaTypes.HAL_JSON_TYPE).get(ContentRepresentation.class);
        assertThat(response.getContent(), is(equalTo("{\"_links\":{\"self\":{\"href\":\"http://example.org/\"}},\"alphabet\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"}")));
    }

}
