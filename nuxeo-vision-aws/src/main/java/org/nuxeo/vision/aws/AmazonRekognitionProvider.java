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
 */
package org.nuxeo.vision.aws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;
import org.nuxeo.runtime.aws.NuxeoAWSCredentialsProvider;
import org.nuxeo.runtime.aws.NuxeoAWSRegionProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsRequest;

public class AmazonRekognitionProvider implements VisionProvider {

    protected static final long BLOB_MAX_SIZE = 5 * 1024 * 1024;

    protected static final List<String> SUPPORTED_FORMAT = Arrays.asList("image/jpeg", "image/png");

    /**
     * volatile on purpose to allow for the double-checked locking idiom
     */
    protected volatile AmazonRekognition client;

    public AmazonRekognitionProvider(Map<String, String> parameters) {
       // Params not used.
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<String> features, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {
        List<VisionResponse> result = new ArrayList<>();
        for (Blob blob : blobs) {
            result.add(new AmazonRekognitionResponse(getClient().detectLabels(
                    new DetectLabelsRequest().withImage(new com.amazonaws.services.rekognition.model.Image().withBytes(
                            ByteBuffer.wrap(blob.getByteArray()))).withMaxLabels(maxResults))));
            // different API on AWS Rekognition for safe content detection
            if (features.contains(VisionFeature.SAFE_SEARCH_DETECTION.toString())) {
                result.add(new AmazonRekognitionResponse(
                        getClient().detectModerationLabels(new DetectModerationLabelsRequest().withImage(
                                new com.amazonaws.services.rekognition.model.Image().withBytes(
                                        ByteBuffer.wrap(blob.getByteArray()))))));
            }
        }
        return result;
    }

    @Override
    public boolean checkBlobs(List<Blob> blobs) throws IOException {
        for (Blob blob : blobs) {
            long size = blob.getLength();
            if (size <= 0) {
                throw new IOException("Could not read the blob size");
            }
            if (size > BLOB_MAX_SIZE) {
                return false;
            }
            if (!SUPPORTED_FORMAT.contains(blob.getMimeType())) {
                return false;
            }

        }
        return true;
    }

    @Override
    public Object getNativeClient() {
        return getClient();
    }

    protected AmazonRekognition getClient() {
        // thread safe lazy initialization of the AWS Rekognition client
        // see https://en.wikipedia.org/wiki/Double-checked_locking
        AmazonRekognition result = client;
        if (result == null) {
            synchronized (this) {
                result = client;
                if (result == null) {
                    AmazonRekognitionClientBuilder builder = AmazonRekognitionClientBuilder.standard();
                    builder.withCredentials(NuxeoAWSCredentialsProvider.getInstance());
                    builder.withRegion(NuxeoAWSRegionProvider.getInstance().getRegion());
                    result = client = builder.build();
                }
            }
        }
        return result;
    }

}
