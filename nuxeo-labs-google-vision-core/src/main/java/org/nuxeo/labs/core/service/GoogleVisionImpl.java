package org.nuxeo.labs.core.service;

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
import org.nuxeo.runtime.api.Framework;
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

    private Vision googleVision=null;

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
        // do nothing by default. You can remove this method if not used.
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        // Add some logic here to handle contributions
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }


    private Vision getVisionService() throws IOException, GeneralSecurityException {
        boolean active = "true".equals(Framework.getProperty("org.nuxeo.labs.google.enable"));
        if (!active) throw new IllegalStateException("Google Vision Service is deactivated");

        if (googleVision!=null) return googleVision;

        String credentialPath = Framework.getProperty("org.nuxeo.labs.google.credential");
        File file = new File(credentialPath);
        GoogleCredential credential =
                GoogleCredential.fromStream(new FileInputStream(file)).createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        googleVision = new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
                .setApplicationName("test")
                .build();
        return googleVision;
    }

    @Override
    public List<String> getLabels(Blob blob) {

        byte[] data;
        try {
            data = blob.getByteArray();
        } catch (IOException e) {
            log.error(e);
            return new ArrayList<>();
        }

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("LABEL_DETECTION")
                                        .setMaxResults(5)));

        Vision vision;
        try {
            vision = getVisionService();
        } catch (IOException | GeneralSecurityException | IllegalStateException e) {
            log.error(e);
            return new ArrayList<>();
        }

        Vision.Images.Annotate annotate;
        try {
            annotate = vision.images()
                    .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        } catch (IOException e) {
            log.error(e);
            return new ArrayList<>();
        }

        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);


        BatchAnnotateImagesResponse batchResponse;
        try {
            batchResponse = annotate.execute();
        } catch (IOException e) {
            log.error(e);
            return new ArrayList<>();
        }

        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLabelAnnotations() == null) {
            return new ArrayList<>();
        }
        List<EntityAnnotation> annotations = response.getLabelAnnotations();

        List<String> results = new ArrayList<>();

        for(EntityAnnotation annotation : annotations) {
            results.add(annotation.getDescription());
        }

        return results;
    }

    @Override
    public String getText(Blob blob) {
        byte[] data;
        try {
            data = blob.getByteArray();
        } catch (IOException e) {
            log.error(e);
            return "";
        }

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("TEXT_DETECTION")
                                        .setMaxResults(5)));

        Vision vision;
        try {
            vision = getVisionService();
        } catch (IOException | GeneralSecurityException | IllegalStateException e) {
            log.error(e);
            return "";
        }

        Vision.Images.Annotate annotate = null;
        try {
            annotate = vision.images()
                    .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        } catch (IOException e) {
            log.error(e);
            return "";
        }

        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);


        BatchAnnotateImagesResponse batchResponse;
        try {
            batchResponse = annotate.execute();
        } catch (IOException e) {
            log.error(e);
            return "";
        }

        return batchResponse.getResponses().get(0).getTextAnnotations().get(0).getDescription();

    }

}
