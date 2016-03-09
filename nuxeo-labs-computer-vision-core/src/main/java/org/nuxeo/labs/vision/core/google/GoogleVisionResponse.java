package org.nuxeo.labs.vision.core.google;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.nuxeo.labs.vision.core.service.ComputerVisionResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël on 3/9/2016.
 */
public class GoogleVisionResponse implements ComputerVisionResponse {

    AnnotateImageResponse response;

    public GoogleVisionResponse(AnnotateImageResponse response) {
        this.response = response;
    }

    @Override
    public List<String> getClassificationLabels() {
        List<String> results = new ArrayList<>();
        results.addAll(processEntityAnnotationList(response.getLabelAnnotations()));
        results.addAll(processEntityAnnotationList(response.getLandmarkAnnotations()));
        results.addAll(processEntityAnnotationList(response.getLogoAnnotations()));
        return results;
    }

    @Override
    public List<String> getOcrText() {
        List<EntityAnnotation> labels = response.getTextAnnotations();
        return processEntityAnnotationList(labels);
    }

    @Override
    public Object getNativeObject() {
        return response;
    }

    protected List<String> processEntityAnnotationList(List<EntityAnnotation> annotations) {
        List<String> result = new ArrayList<>();
        if (annotations==null) return result;
        for (EntityAnnotation annotation : annotations) {
            result.add(annotation.getDescription());
        }
        return result;
    }
}
