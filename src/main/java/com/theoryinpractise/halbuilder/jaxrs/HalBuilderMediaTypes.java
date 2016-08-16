package com.theoryinpractise.halbuilder.jaxrs;

import com.google.common.base.Charsets;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;

/**
 * Helper class for MediaType handling common to {@link JaxRsHalBuilderReaderSupport}
 * and {@link JaxRsHalBuilderSupport}.
 */
class HalBuilderMediaTypes {
    static final MediaType HAL_JSON_TYPE = MediaType.valueOf(RepresentationFactory.HAL_JSON);
    static final MediaType HAL_XML_TYPE = MediaType.valueOf(RepresentationFactory.HAL_XML);
    static final Charset DEFAULT_ENCODING = Charsets.UTF_8;

    /**
     * Is the given media type supported by HalBuilder?
     */
    static boolean isSupported(MediaType mediaType) {
        return mediaType.isCompatible(HAL_JSON_TYPE) || mediaType.isCompatible(HAL_XML_TYPE);
    }
}
