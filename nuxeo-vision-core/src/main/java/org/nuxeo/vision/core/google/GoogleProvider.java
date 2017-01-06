package org.nuxeo.vision.core.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loopingz on 05/01/2017.
 */
public class GoogleProvider implements VisionProvider {

    protected  GoogleVisionDescriptor config;

    public GoogleProvider(GoogleVisionDescriptor config) {
        this.config = config;
    }

    private volatile com.google.api.services.vision.v1.Vision visionClient;

    private com.google.api.services.vision.v1.Vision getVisionService()
            throws IOException, GeneralSecurityException {
        // thread safe lazy initialization of the google vision client
        // see https://en.wikipedia.org/wiki/Double-checked_locking
        com.google.api.services.vision.v1.Vision result = visionClient;
        if (result == null) {
            synchronized (this) {
                result = visionClient;
                if (result == null) {
                    GoogleCredential credential = null;
                    if (usesServiceAccount()) {
                        File file = new File(
                                config.getCredentialFilePath());
                        credential = GoogleCredential.fromStream(
                                new FileInputStream(file)).createScoped(
                                VisionScopes.all());
                    }
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    result = visionClient = new com.google.api.services.vision.v1.Vision.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            jsonFactory, credential).setApplicationName(
                            config.getAppName()).build();
                }
            }
        }
        return result;
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults) throws IOException, GeneralSecurityException, IllegalStateException {
        // build list of requested features
        List<Feature> requestFeatures = buildFeatureList(features, maxResults);

        // build list of request
        List<AnnotateImageRequest> requests = buildRequestList(blobs,
                requestFeatures);

        com.google.api.services.vision.v1.Vision.Images.Annotate annotate;
        annotate = getVisionService().images().annotate(
                new BatchAnnotateImagesRequest().setRequests(requests));

        if (!usesServiceAccount() && usesApiKey()) {
            annotate.setKey(config.getApiKey());
        }

        // Due to a bug: requests to Vision API containing large images fail
        // when GZipped.
        annotate.setDisableGZipContent(true);

        // execute request
        BatchAnnotateImagesResponse batchResponse;
        batchResponse = annotate.execute();

        // check response is not empty
        if (batchResponse.getResponses() == null) {
            throw new IllegalStateException(
                    "Google Vision returned an empty response");
        }

        List<AnnotateImageResponse> responses = batchResponse.getResponses();
        List<VisionResponse> output = new ArrayList<>();
        for (AnnotateImageResponse response : responses) {
            output.add(new GoogleVisionResponse(response));
        }
        return output;
    }

    protected List<Feature> buildFeatureList(List<VisionFeature> features,
                                             int maxResults) {

        List<Feature> requestFeatures = new ArrayList<>();
        for (VisionFeature feature : features) {
            requestFeatures.add(new Feature().setType(feature.toString()).setMaxResults(
                    maxResults));
        }
        return requestFeatures;
    }

    protected List<AnnotateImageRequest> buildRequestList(List<Blob> blobs,
                                                          List<Feature> features) throws IOException {

        List<AnnotateImageRequest> requests = new ArrayList<>();
        for (Blob blob : blobs) {
            AnnotateImageRequest request = new AnnotateImageRequest().setImage(
                    new Image().encodeContent(blob.getByteArray())).setFeatures(
                    features);
            requests.add(request);
        }
        return requests;
    }

    protected boolean usesServiceAccount() {
        String path = config.getCredentialFilePath();
        return path != null && path.length() > 0;
    }

    protected boolean usesApiKey() {
        String key = config.getApiKey();
        return key != null && key.length() > 0;
    }
}
