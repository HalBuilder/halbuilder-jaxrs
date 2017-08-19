package com.theoryinpractise.halbuilder5.jaxrs;

import javax.ws.rs.core.MediaType;

import com.theoryinpractise.halbuilder5.Support;

/**
 * Helper class for MediaType handling common to {@link JaxRsHalBuilderReaderSupport} and {@link
 * JaxRsHalBuilderSupport}.
 */
class HalBuilderMediaTypes {

  public static final MediaType HAL_JSON_TYPE = MediaType.valueOf(Support.HAL_JSON);
  public static final MediaType HAL_XML_TYPE = MediaType.valueOf(Support.HAL_XML);

  /** Is the given media type supported by HalBuilder? */
  static boolean isSupported(MediaType mediaType) {
    return mediaType.isCompatible(HAL_JSON_TYPE) || mediaType.isCompatible(HAL_XML_TYPE);
  }
}
