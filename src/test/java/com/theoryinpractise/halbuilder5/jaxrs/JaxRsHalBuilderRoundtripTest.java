package com.theoryinpractise.halbuilder5.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.Support;
import io.vavr.collection.HashMap;
import okio.ByteString;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import static com.theoryinpractise.halbuilder5.Links.getHref;
import static com.theoryinpractise.halbuilder5.json.JsonRepresentationReader.readByteStringAs;

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
  public static Iterable<Object[]> pathsToTest() {
    return Arrays.asList(new Object[] {"json"}, new Object[] {"xml"});
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
          .withValue(HashMap.of(PROP_KEY, PROP_VALUE))
          .withRepresentation(
              EXTRA_REL,
              ResourceRepresentation.empty(EXTRA.toASCIIString())
                  .withValue(HashMap.of(EMB_PROP_KEY, EMB_PROP_VALUE)));
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
    Assert.assertEquals(BASE.toString(), getHref(readRepresentation().getResourceLink().get()));
  }

  @Test
  public void shouldHaveProperExtraLink() {
    Assert.assertEquals(EXTRA.toString(), getHref(readRepresentation().getResourceLink().get()));
  }

  @Test
  public void shouldHaveProperPropertyValue() {
    ResourceRepresentation<Map> map =
        readEmbeddedRepresentation().map(readByteStringAs(MAPPER, Map.class));
    Assert.assertEquals(PROP_VALUE, map.get().get(PROP_KEY));
  }

  @Test
  public void shouldHaveOneEmbeddedResource() {
    Assert.assertEquals(1, readRepresentation().getResources().size());
  }

  @Test
  public void embeddedShouldHaveProperSelfLink() {
    Assert.assertEquals(EXTRA.toString(), getHref(readRepresentation().getResourceLink().get()));
  }

  @Test
  public void embeddedShouldHaveProperPropertyValue() {
    ResourceRepresentation<Map> map =
        readEmbeddedRepresentation().map(readByteStringAs(MAPPER, Map.class));

    Assert.assertEquals(EMB_PROP_VALUE, map.get().get(EMB_PROP_KEY));
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

  private ResourceRepresentation<ByteString> readRepresentation() {
    return target("/test/" + subPath).request().get(ResourceRepresentation.class);
  }

  private ResourceRepresentation<ByteString> readEmbeddedRepresentation() {
    return (ResourceRepresentation<ByteString>)
        readRepresentation().getResourcesByRel(EXTRA_REL).head();
  }
}
