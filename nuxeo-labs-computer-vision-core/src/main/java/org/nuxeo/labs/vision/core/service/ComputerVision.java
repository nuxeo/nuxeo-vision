/*
 * (C) Copyright 2015-2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Vachette
 */

package org.nuxeo.labs.vision.core.service;

import org.nuxeo.ecm.core.api.Blob;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


/**
 * A service that performs Computer Vision tasks like classification, OCR, Face Detection ...
 */
public interface ComputerVision {

    /**
     *
     * @param blob the image blob
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a {@link ComputerVisionResponse} object
     */
    ComputerVisionResponse execute(Blob blob, List<ComputerVisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     *
     * @param blobs A list of image blobs
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a list of {@link ComputerVisionResponse} object
     */
    List<ComputerVisionResponse> execute(List<Blob> blobs, List<ComputerVisionFeature> features,
                                        int maxResults) throws IOException, GeneralSecurityException;


    /**
     *
     * @return The name of the automation name to use for Pictures
     */
    String getPictureMapperChainName();

    /**
     *
     * @return The name of the automation name to use for Videos
     */
    String getVideoMapperChainName();

}
