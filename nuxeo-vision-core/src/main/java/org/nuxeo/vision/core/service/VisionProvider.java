package org.nuxeo.vision.core.service;

import org.nuxeo.ecm.core.api.Blob;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by loopingz on 05/01/2017.
 */
public interface VisionProvider {
    List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults) throws IOException,
            GeneralSecurityException, IllegalStateException;
}
