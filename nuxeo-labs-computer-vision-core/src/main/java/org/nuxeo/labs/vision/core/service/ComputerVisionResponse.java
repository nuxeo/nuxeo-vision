package org.nuxeo.labs.vision.core.service;

import java.util.List;

/**
 * Created by Michaël on 3/9/2016.
 */
public interface ComputerVisionResponse {

    /**
     *
     * @return a list of classification labels returned by the service
     */
    List<String> getClassificationLabels();

    /**
     *
     * @return a list of text string extracted by the service
     */
    List<String> getOcrText();

    /**
     *
     * @return the native object returned by the service provider
     */
    Object getNativeObject();
}
