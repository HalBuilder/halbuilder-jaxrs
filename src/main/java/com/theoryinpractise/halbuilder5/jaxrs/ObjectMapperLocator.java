package com.theoryinpractise.halbuilder5.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

class ObjectMapperLocator {

  private Providers providers;

  public ObjectMapperLocator(final Providers providers) {
    this.providers = providers;
  }

  public ObjectMapper locate(Class<?> type, MediaType mediaType) {
    ObjectMapper mapper = null;
    if (providers != null) {
      ContextResolver<ObjectMapper> resolver =
          providers.getContextResolver(ObjectMapper.class, mediaType);
      if (resolver != null) {
        mapper = resolver.getContext(type);
      }
    }
    return mapper;
  }
}
