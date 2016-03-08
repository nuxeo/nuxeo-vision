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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleVisionImpl extends DefaultComponent implements GoogleVision {

    private static final Log log = LogFactory.getLog(GoogleVisionImpl.class);

    private volatile Vision visionClient;

    protected static final String EXT_POINT = "configuration";

    protected GoogleVisionDescriptor config = null;

    /**
     * Component activated notification.
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
    }

    /**
     * Application started notification.
     * Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (EXT_POINT.equals(extensionPoint)) {
            config = (GoogleVisionDescriptor) contribution;
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }


    private Vision getVisionService() throws IOException, GeneralSecurityException{
        // thread safe lazy initialization of the google vision client
        // see https://en.wikipedia.org/wiki/Double-checked_locking
        Vision result = visionClient;
        if (result == null) {
            synchronized(this) {
                result = visionClient;
                if (result == null) {
                    File file = new File(config.getCredentialFilePath());
                    GoogleCredential credential =
                            GoogleCredential.fromStream(
                                    new FileInputStream(file)).createScoped(VisionScopes.all());
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    result = visionClient = new Vision.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                            .setApplicationName(config.getAppName())
                            .build();
                }
            }
        }
        return result;
    }


    @Override
    public AnnotateImageResponse execute(Blob blob, List<String> features,int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {

        if (blob==null) {
            throw new IllegalArgumentException("Input Blob cannot be null");
        } else if (features == null || features.size()==0) {
            throw new IllegalArgumentException("The feature list cannot be empty or null");
        }

        List<AnnotateImageResponse> results = execute(ImmutableList.of(blob),features,maxResults);
        if (results.size()>0) {
            return results.get(0);
        } else {
            throw new NuxeoException("Google vision returned empty results for "+blob.getFilename());
        }
    }


    @Override
    public List<AnnotateImageResponse> execute(List<Blob> blobs, List<String> features,int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {

        if (blobs==null || blobs.size()==0) {
            throw new IllegalArgumentException("Input Blob list cannot be null or empty");
        } else if (features == null || features.size()==0) {
            throw new IllegalArgumentException("The feature list cannot be empty or null");
        }

        //build list of requested features
        List<Feature> requestFeatures = buildFeatureList(features,maxResults);

        //build list of request
        List<AnnotateImageRequest> requests = buildRequestList(blobs,requestFeatures);

        Vision.Images.Annotate annotate;
        annotate = getVisionService().images().annotate(
                new BatchAnnotateImagesRequest().setRequests(requests));

        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);

        // execute request
        BatchAnnotateImagesResponse batchResponse;
        batchResponse = annotate.execute();

        //check response is not empty
        if (batchResponse.getResponses()==null) {
            throw new IllegalStateException("Google Vision returned an empty response");
        }

        return batchResponse.getResponses();
    }

    @Override
    public String getMapperChainName() {
        return config.getMapperChainName();
    }


    protected List<Feature> buildFeatureList(List<String> features,int maxResults) {

        List<Feature> requestFeatures = new ArrayList<>();
        for (String feature: features) {
            requestFeatures.add(new Feature().setType(feature).setMaxResults(maxResults));
        }
        return requestFeatures;
    }


    protected List<AnnotateImageRequest> buildRequestList(List<Blob> blobs, List<Feature> features)
            throws IOException{

        List<AnnotateImageRequest> requests = new ArrayList<>();
        for (Blob blob: blobs) {
            AnnotateImageRequest request =
                    new AnnotateImageRequest()
                            .setImage(new Image().encodeContent(blob.getByteArray()))
                            .setFeatures(features);
            requests.add(request);
        }
        return requests;
    }



}
