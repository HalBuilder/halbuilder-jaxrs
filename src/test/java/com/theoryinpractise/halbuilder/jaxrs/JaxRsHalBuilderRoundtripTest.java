package com.theoryinpractise.halbuilder.jaxrs;

import java.net.URI;
import java.util.Arrays;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.theoryinpractise.halbuilder.api.ContentRepresentation;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

@RunWith(Parameterized.class)
public class JaxRsHalBuilderRoundtripTest extends JerseyTest {

    private static final URI BASE = URI.create("http://example.org/");
    private static final URI EXTRA = URI.create("http://example.org/extra");
    private static final String EXTRA_REL = "extra";
    private static final String PROP_KEY = "prop";
    private static final String PROP_VALUE = "some value";
    private static final String EMB_PROP_KEY = "other-prop";
    private static final String EMB_PROP_VALUE = "some other value";

    @Parameterized.Parameter(0)
    public String subPath;

    @Parameterized.Parameters
    public static Iterable<Object[]> pathsToTest() {
        return Arrays.asList(new Object[] {"json"}, new Object[] {"xml"});
    }

    @Path("test")
    public static class TestResource {
        private static RepresentationFactory factory = new StandardRepresentationFactory();

        @Path("json")
        @Produces(RepresentationFactory.HAL_JSON)
        @GET
        public Representation writeJSON() {
            return createTestRepresentation();
        }

        @Path("xml")
        @Produces(RepresentationFactory.HAL_XML)
        @GET
        public Representation writeXML() {
            return createTestRepresentation();
        }

        private static Representation createTestRepresentation() {
            return factory.newRepresentation(BASE)
                .withLink(EXTRA_REL, EXTRA)
                .withProperty(PROP_KEY, PROP_VALUE)
                .withRepresentation(EXTRA_REL,
                                    factory.newRepresentation(EXTRA)
                                    .withProperty(EMB_PROP_KEY, EMB_PROP_VALUE));
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class)
                .register(JaxRsHalBuilderSupport.class)
                .register(JaxRsHalBuilderReaderSupport.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JaxRsHalBuilderReaderSupport.class);
    }

    @Test
    public void shouldHaveProperSelfLink() {
        Assert.assertEquals(BASE.toString(),
                            readRepresentation().getResourceLink().getHref());
    }

    @Test
    public void shouldHaveProperExtraLink() {
        Assert.assertEquals(EXTRA.toString(),
                            readRepresentation().getLinkByRel(EXTRA_REL).getHref());
    }

    @Test
    public void shouldHaveProperPropertyValue() {
        Assert.assertEquals(PROP_VALUE,
                            readRepresentation().getValue(PROP_KEY));
    }

    @Test
    public void shouldHaveOneEmbeddedResource() {
        Assert.assertEquals(1,
                            readRepresentation().getResources().size());
    }

    @Test
    public void embeddedShouldHaveProperSelfLink() {
        Assert.assertEquals(EXTRA.toString(),
                            readEmbeddedRepresentation().getResourceLink().getHref());
    }

    @Test
    public void embeddedShouldHaveProperPropertyValue() {
        Assert.assertEquals(EMB_PROP_VALUE,
                            readEmbeddedRepresentation().getValue(EMB_PROP_KEY));
    }

    @Test
    public void shouldBeReturning200Code() {
        Response response = target("/test/" + subPath).request().get();
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldBeUsingProperContentType() {
        Response response = target("/test/" + subPath).request().get();
        Assert.assertEquals("application/hal+" + subPath,
                            response.getMetadata().getFirst("Content-Type"));
    }

    private ContentRepresentation readRepresentation() {
        return target("/test/" + subPath).request().get(ContentRepresentation.class);
    }

    private ReadableRepresentation readEmbeddedRepresentation() {
        return readRepresentation().getResourcesByRel(EXTRA_REL).get(0);
    }
}
