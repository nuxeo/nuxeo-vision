package org.nuxeo.labs.vision.core.service;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import org.nuxeo.ecm.core.api.Blob;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleVision {

    /**
     *
     * @param blob the image blob
     * @param features the feature to request from the api
     * @param maxResults the maximum number of results per feature
     * @return a map of results returned by the API where the key is the feature name
     */
    AnnotateImageResponse execute(Blob blob, List<String> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     *
     * @param blobs A list of image blobs
     * @param features the feature to request from the api
     * @param maxResults the maximum number of results per feature
     * @return a list of maps of results returned by the API where the key is the feature name
     */
    List<AnnotateImageResponse> execute(List<Blob> blobs, List<String> features,int maxResults)
            throws IOException, GeneralSecurityException;

}
