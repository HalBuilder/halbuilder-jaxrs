package com.theoryinpractise.halbuilder5.jaxrs;

import com.theoryinpractise.halbuilder5.Support;

import javax.ws.rs.core.MediaType;

/**
 * Helper class for MediaType handling common to {@link JaxRsHalBuilderReaderSupport} and {@link
 * JaxRsHalBuilderSupport}.
 */
class HalBuilderMediaTypes {
  static final MediaType HAL_JSON_TYPE = MediaType.valueOf(Support.HAL_JSON);
  static final MediaType HAL_XML_TYPE = MediaType.valueOf(Support.HAL_XML);

  /** Is the given media type supported by HalBuilder? */
  static boolean isSupported(MediaType mediaType) {
    return mediaType.isCompatible(HAL_JSON_TYPE) || mediaType.isCompatible(HAL_XML_TYPE);
  }
}
