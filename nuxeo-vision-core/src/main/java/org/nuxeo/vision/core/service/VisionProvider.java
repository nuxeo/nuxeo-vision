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
 *     Remi Cattiau
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
 * A vision provider is a wrapper that encapsulates calls to a given computer vision service
 * @since 9.1
 */
public interface VisionProvider {

    /**
     * @param blobs the blobs to pass to the API
     * @param features the feature to request from the provider
     * @param params parameters to pass to the provider
     * @param maxResults the maximum number of results per feature
     * @return a {@link VisionResponse} object
     */
    List<VisionResponse> execute(List<Blob> blobs, List<String> features, Map<String, Object> params, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException;

    /**
     * Verifies that the blobs size and format are supported by the provider
     *
     * @param blobs the blobs to pass to the API
     * @return a {@link VisionResponse} object
     */
    boolean checkBlobs(List<Blob> blobs) throws IOException;

    /**
     * @return the provider native client object
     */
    Object getNativeClient();

}
