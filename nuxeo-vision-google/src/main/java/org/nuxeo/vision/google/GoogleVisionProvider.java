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
 *     Remi Cattiau
 *     Thibaud Arguillere
 */

package org.nuxeo.vision.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;

public class GoogleVisionProvider implements VisionProvider {

    public static final String APP_NAME_PARAM = "appName";

    public static final String API_KEY_PARAM = "apiKey";

    public static final String CREDENTIAL_PATH_PARAM = "credentialFilePath";

    protected static final long BLOB_MAX_SIZE = 4 * 1024 * 1024;

    protected static final long REQUEST_MAX_SIZE = 8 * 1024 * 1024;

    protected static final int MAX_BLOB_PER_REQUEST = 16;

    protected Map<String, String> params;

    public GoogleVisionProvider(Map<String, String> parameters) {
        params = parameters;
    }

    /**
     * volatile on purpose to allow for the double-checked locking idiom
     */
    protected volatile com.google.api.services.vision.v1.Vision visionClient;

    protected com.google.api.services.vision.v1.Vision getVisionClient() throws IOException, GeneralSecurityException {
        // thread safe lazy initialization of the google vision client
        // see https://en.wikipedia.org/wiki/Double-checked_locking
        com.google.api.services.vision.v1.Vision result = visionClient;
        if (result == null) {
            synchronized (this) {
                result = visionClient;
                if (result == null) {
                    GoogleCredential credential = null;
                    if (usesServiceAccount()) {
                        File file = new File(getCredentialFilePath());
                        credential = GoogleCredential.fromStream(new FileInputStream(file))
                                                     .createScoped(VisionScopes.all());
                    }
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    result = visionClient = new com.google.api.services.vision.v1.Vision.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                                                                                                  .setApplicationName(
                                                                                                          getAppName())
                                                                                                  .build();
                }
            }
        }
        return result;
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<String> features, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {
        // build list of requested features
        List<Feature> requestFeatures = buildFeatureList(features, maxResults);

        // build list of request
        List<AnnotateImageRequest> requests = buildRequestList(blobs, requestFeatures);

        com.google.api.services.vision.v1.Vision.Images.Annotate annotate;
        annotate = getVisionClient().images().annotate(new BatchAnnotateImagesRequest().setRequests(requests));

        if (!usesServiceAccount() && usesApiKey()) {
            annotate.setKey(getApiKey());
        }

        // Due to a bug: requests to Vision API containing large images fail
        // when GZipped.
        annotate.setDisableGZipContent(true);

        // execute request
        BatchAnnotateImagesResponse batchResponse;
        batchResponse = annotate.execute();

        // check response is not empty
        if (batchResponse.getResponses() == null) {
            throw new IllegalStateException("Google Vision returned an empty response");
        }

        List<AnnotateImageResponse> responses = batchResponse.getResponses();
        List<VisionResponse> output = new ArrayList<>();
        for (AnnotateImageResponse response : responses) {
            output.add(new GoogleVisionResponse(response));
        }
        return output;
    }

    @Override
    public boolean checkBlobs(List<Blob> blobs) throws IOException {
        if (blobs.size() > MAX_BLOB_PER_REQUEST) {
            return false;
        }
        long totalSize = 0;
        for (Blob blob : blobs) {
            long size = blob.getLength();
            if (size <= 0) {
                throw new IOException("Could not read the blob size");
            }
            if (size > BLOB_MAX_SIZE) {
                return false;
            }
            totalSize += size;
            if (totalSize > REQUEST_MAX_SIZE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public com.google.api.services.vision.v1.Vision getNativeClient() {
        try {
            return getVisionClient();
        } catch (IOException | GeneralSecurityException e) {
            throw new NuxeoException(e);
        }
    }

    protected String getCredentialFilePath() {
        return params.get(CREDENTIAL_PATH_PARAM);
    }

    protected String getApiKey() {
        return params.get(API_KEY_PARAM);
    }

    protected String getAppName() {
        return params.get(APP_NAME_PARAM);
    }

    protected boolean usesServiceAccount() {
        String path = getCredentialFilePath();
        return StringUtils.isNotEmpty(path);
    }

    protected boolean usesApiKey() {
        String key = getApiKey();
        return StringUtils.isNotEmpty(key);
    }

    protected List<Feature> buildFeatureList(List<String> features, int maxResults) {

        List<Feature> requestFeatures = new ArrayList<>();
        for (String feature : features) {
            requestFeatures.add(new Feature().setType(feature).setMaxResults(maxResults));
        }
        return requestFeatures;
    }

    protected List<AnnotateImageRequest> buildRequestList(List<Blob> blobs, List<Feature> features) throws IOException {

        List<AnnotateImageRequest> requests = new ArrayList<>();
        for (Blob blob : blobs) {
            AnnotateImageRequest request = new AnnotateImageRequest().setImage(
                    new Image().encodeContent(blob.getByteArray())).setFeatures(features);
            requests.add(request);
        }
        return requests;
    }

}
