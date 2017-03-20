/*
 * (C) Copyright 2015-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Thibaud Arguillere
 */
package org.nuxeo.vision.core.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;

/**
 * A service that performs Computer Vision tasks like classification, OCR, Face Detection ...
 */
public interface Vision {

    String EVENT_IMAGE_HANDLED = "visionOnImageDone";

    String EVENT_VIDEO_HANDLED = "visionOnVideoDone";

    /**
     * @param blob the image blob
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a {@link VisionResponse} object
     */
    VisionResponse execute(Blob blob, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     * 
     * @param provider the provider to use
     * @param blob the image blob
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a {@link VisionResponse} object
     * @since 9.1
     */
    VisionResponse execute(String provider, Blob blob, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     *
     * @param provider the provider to use
     * @param blobs A list of image blobs
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a list of {@link VisionResponse} object
     * @since 9.1
     */
    List<VisionResponse> execute(String provider, List<Blob> blobs, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     * @param blobs A list of image blobs
     * @param features the feature to request from the service
     * @param maxResults the maximum number of results per feature
     * @return a list of {@link VisionResponse} object
     */
    List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException;

    /**
     * @return The name of the automation name to use for Pictures
     */
    String getPictureMapperChainName();

    /**
     * @return The name of the automation name to use for Videos
     */
    String getVideoMapperChainName();

    /**
     * @return The name of default provider
     * @since 9.1
     */
    String getDefaultProvider();

    /**
     * @return The provider object
     * @since 9.1
     */
    VisionProvider getProvider(String name);

    /**
     * @return all registered providers
     * @since 9.1
     */
    Map<String, VisionProvider> getProviders();

}
