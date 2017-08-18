package com.theoryinpractise.halbuilder5.jaxrs;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.Support;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ObjectMapperViaContextResolverTest extends JerseyTest {

  private static final URI BASE = URI.create("http://example.org/");

  public static class Fields {
    public String alphabet = "abcdefghijklmnopqrstuvwxyz";
  }

  @Path("test")
  public static class TestResource {

    @Path("json")
    @Produces(Support.HAL_JSON)
    @GET
    public ResourceRepresentation<Fields> writeJSON() {
      return ResourceRepresentation.create(BASE.toASCIIString(), new Fields());
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
    config.register(JaxRsHalBuilderSupport.class);
    config.register(ObjectMapperContextResolver.class);
  }

  @Test
  public void shouldUseProvidedObjectMapper() {
    ResourceRepresentation<Fields> response =
        target("/test/json")
            .request(HalBuilderMediaTypes.HAL_JSON_TYPE)
            .get(ResourceRepresentation.class);
    assertThat(
        response.getContent().get().utf8(),
        is(
            equalTo(
                "{\"_links\":{\"self\":{\"href\":\"http://example.org/\"}},\"alphabet\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"}")));
  }
}
