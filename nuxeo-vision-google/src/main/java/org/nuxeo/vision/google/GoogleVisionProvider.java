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

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.CropHintsParams;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.ImageContext;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GoogleVisionProvider implements VisionProvider {

    public static final String CREDENTIAL_PATH_PARAM = "credentialFilePath";

    protected static final long BLOB_MAX_SIZE = 4 * 1024 * 1024;

    protected static final long REQUEST_MAX_SIZE = 8 * 1024 * 1024;

    protected static final int MAX_BLOB_PER_REQUEST = 16;

    public static final String GOOGLE_VISION_CROP_HINTS_PARAMS = "cropHints";

    protected Map<String, String> params;

    public GoogleVisionProvider(Map<String, String> parameters) {
        params = parameters;
    }

    /**
     * volatile on purpose to allow for the double-checked locking idiom
     */
    protected volatile ImageAnnotatorClient visionClient;

    protected ImageAnnotatorClient getVisionClient() throws IOException {
        // thread safe lazy initialization of the google vision client
        // see https://en.wikipedia.org/wiki/Double-checked_locking
        ImageAnnotatorClient result = visionClient;
        if (result == null) {
            synchronized (this) {
                result = visionClient;
                if (result == null) {
                    File file = new File(getCredentialFilePath());
                    ImageAnnotatorSettings.Builder settingsBuilder = ImageAnnotatorSettings.newBuilder();
                    if (usesServiceAccount()) {
                        settingsBuilder.setCredentialsProvider(
                                FixedCredentialsProvider.create(ServiceAccountCredentials.
                                        fromStream(new FileInputStream(file))));
                    }
                    result = visionClient = ImageAnnotatorClient.create(settingsBuilder.build());
                }
            }
        }
        return result;
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<String> features, Map<String, Object> params, int maxResults)
            throws IOException, IllegalStateException {
        // build list of requested features
        List<Feature> requestFeatures = buildFeatureList(features, maxResults);

        // build list of request
        List<AnnotateImageRequest> requests = buildRequestList(blobs, requestFeatures, params);

        BatchAnnotateImagesResponse batchResponse = getVisionClient().batchAnnotateImages(requests);

        // check response is not empty
        if (batchResponse.getResponsesList() == null) {
            throw new IllegalStateException("Google Vision returned an empty response");
        }

        List<AnnotateImageResponse> responses = batchResponse.getResponsesList();
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
    public ImageAnnotatorClient getNativeClient() {
        try {
            return getVisionClient();
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    protected String getCredentialFilePath() {
        return params.get(CREDENTIAL_PATH_PARAM);
    }

    protected boolean usesServiceAccount() {
        String path = getCredentialFilePath();
        return StringUtils.isNotEmpty(path);
    }

    protected List<Feature> buildFeatureList(List<String> features, int maxResults) {
        List<Feature> requestFeatures = new ArrayList<>();
        for (String feature : features) {
            requestFeatures.add(Feature.newBuilder().setType(Feature.Type.valueOf(feature)).setMaxResults(maxResults).build());
        }
        return requestFeatures;
    }

    protected List<AnnotateImageRequest> buildRequestList(List<Blob> blobs, List<Feature> features, Map<String,Object> params) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        for (Blob blob : blobs) {
            AnnotateImageRequest.Builder requestBuilder = AnnotateImageRequest.newBuilder()
                    .setImage(Image.newBuilder().setContent(ByteString.copyFrom(blob.getByteArray())))
                    .addAllFeatures(features);

            ImageContext.Builder contextBuilder = ImageContext.newBuilder();
            if (params.containsKey(GOOGLE_VISION_CROP_HINTS_PARAMS)) {
                CropHintsParams cropHintsParams = getCropHintParams((float[]) params.get(GOOGLE_VISION_CROP_HINTS_PARAMS));
                contextBuilder.setCropHintsParams(cropHintsParams);
            }
            requestBuilder.setImageContext(contextBuilder.build());

            requests.add(requestBuilder.build());
        }
        return requests;
    }

    public static CropHintsParams getCropHintParams(float[] crops) {
        CropHintsParams.Builder builder = CropHintsParams.newBuilder();
        for (float crop : crops) {
            builder.addAspectRatios(crop);
        }
        return builder.build();
    }

}
