package org.nuxeo.labs.vision.core.service;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.labs.vision.core.FeatureType;

import java.util.List;
import java.util.Map;

public interface GoogleVision {

    /**
     *
     * @param blob the image blob
     * @param features the feature to request from the api
     * @return a map of results returned by the API where the key is the feature name
     */
    Map<String,Object> execute(Blob blob, List<FeatureType> features);

}
