package com.theoryinpractise.halbuilder.jaxrs;

import javax.ws.rs.core.MediaType;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

/**
 * Helper class for MediaType handling common to {@link JaxRsHalBuilderReaderSupport}
 * and {@link JaxRsHalBuilderSupport}.
 */
class HalBuilderMediaTypes {
    private static final MediaType HAL_JSON_TYPE = MediaType.valueOf(RepresentationFactory.HAL_JSON);
    private static final MediaType HAL_XML_TYPE = MediaType.valueOf(RepresentationFactory.HAL_XML);
    static final String DEFAULT_ENCODING = "UFT-8";

    /**
     * Is the given media type supported by HalBuilder?
     */
    static boolean isSupported(MediaType mediaType) {
        return mediaType.isCompatible(HAL_JSON_TYPE) || mediaType.isCompatible(HAL_XML_TYPE);
    }
}
