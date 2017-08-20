package com.theoryinpractise.halbuilder5.jaxrs;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theoryinpractise.halbuilder5.Links;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.Support;

import okio.ByteString;

@RunWith(Parameterized.class)
public class JaxRsHalBuilderRoundtripTest extends JerseyTest {

  private static final URI BASE = URI.create("http://example.org/");
  private static final URI EXTRA = URI.create("http://example.org/extra");
  private static final String EXTRA_REL = "extra";
  private static final String PROP_KEY = "prop";
  private static final String PROP_VALUE = "some value";
  private static final String EMB_PROP_KEY = "other-prop";
  private static final String EMB_PROP_VALUE = "some other value";

  public static final ObjectMapper MAPPER = new ObjectMapper();

  @Parameterized.Parameter(0)
  public String subPath;

  @Parameterized.Parameters
  public static Iterable<String> pathsToTest() {
    return Arrays.asList(new String[] {"json"});
  }

  @Path("test")
  public static class TestResource {
    @Path("json")
    @Produces(Support.HAL_JSON)
    @GET
    public ResourceRepresentation<HashMap<String, String>> writeJSON() {
      return createTestRepresentation();
    }

    private static ResourceRepresentation<HashMap<String, String>> createTestRepresentation() {
      return ResourceRepresentation.empty(BASE.toASCIIString())
          .withLink(EXTRA_REL, EXTRA)
          .withValue(io.vavr.collection.HashMap.of(PROP_KEY, PROP_VALUE).toJavaMap())
          .withRepresentation(
              EXTRA_REL,
              ResourceRepresentation.empty(EXTRA.toASCIIString())
                  .withValue(
                      io.vavr.collection.HashMap.of(EMB_PROP_KEY, EMB_PROP_VALUE).toJavaMap()));
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(TestResource.class).register(JaxRsHalBuilderSupport.class);
  }

  @Override
  protected void configureClient(ClientConfig config) {
    config.register(JaxRsHalBuilderSupport.class);
  }

  @Test
  public void shouldHaveProperSelfLink() {
    Assert.assertEquals(
        BASE.toString(), Links.getHref(readRepresentation().getResourceLink().get()));
  }

  @Test
  public void shouldHaveProperExtraLink() {
    Assert.assertEquals(
        EXTRA.toString(), Links.getHref(readRepresentation().getLinkByRel(EXTRA_REL).get()));
  }

  @Test
  public void shouldHaveProperPropertyValue() throws Exception {
    // Use MAPPER directly here since JsonRepresentationReader.readByteStringAs() does not support generics.
    Map<String, String> map =
        MAPPER.readValue(
            readRepresentation().get().utf8(),
            MAPPER.getTypeFactory().constructMapType(Map.class, String.class, String.class));

    Assert.assertEquals(PROP_VALUE, map.get(PROP_KEY));
  }

  @Test
  public void shouldHaveOneEmbeddedResource() {
    Assert.assertEquals(1, readRepresentation().getResources().size());
  }

  @Test
  public void embeddedShouldHaveProperSelfLink() {
    Assert.assertEquals(
        EXTRA.toString(), Links.getHref(readEmbeddedRepresentation().getResourceLink().get()));
  }

  @Test
  public void embeddedShouldHaveProperPropertyValue() throws Exception {
    Map<String, String> map =
        MAPPER.readValue(
            readEmbeddedRepresentation().get().utf8(),
            MAPPER.getTypeFactory().constructMapType(Map.class, String.class, String.class));

    Assert.assertEquals(EMB_PROP_VALUE, map.get(EMB_PROP_KEY));
  }

  @Test
  public void shouldBeReturning200Code() {
    Response response = target("/test/" + subPath).request().get();
    Assert.assertEquals(200, response.getStatus());
  }

  @Test
  public void shouldBeUsingProperContentType() {
    Response response = target("/test/" + subPath).request().get();
    Assert.assertEquals(
        "application/hal+" + subPath, response.getMetadata().getFirst("Content-Type"));
  }

  @SuppressWarnings("unchecked")
  private ResourceRepresentation<ByteString> readRepresentation() {
    return (ResourceRepresentation<ByteString>)
        target("/test/" + subPath).request().get(ResourceRepresentation.class);
  }

  @SuppressWarnings("unchecked")
  private ResourceRepresentation<ByteString> readEmbeddedRepresentation() {
    return (ResourceRepresentation<ByteString>)
        readRepresentation().getResourcesByRel(EXTRA_REL).head();
  }
}
