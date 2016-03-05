package org.nuxeo.labs.vision.core.service;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.labs.vision.core.FeatureType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface GoogleVision {

    /**
     *
     * @param blob the image blob
     * @param features the feature to request from the api
     * @param maxResults the maximum number of results per feature
     * @return a map of results returned by the API where the key is the feature name
     */
    Map<String,Object> execute(Blob blob, List<FeatureType> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     *
     * @param blobs A list of image blobs
     * @param features the feature to request from the api
     * @param maxResults the maximum number of results per feature
     * @return a list of maps of results returned by the API where the key is the feature name
     */
    List<Map<String,Object>> execute(List<Blob> blobs, List<FeatureType> features,int maxResults)
            throws IOException, GeneralSecurityException;

}
