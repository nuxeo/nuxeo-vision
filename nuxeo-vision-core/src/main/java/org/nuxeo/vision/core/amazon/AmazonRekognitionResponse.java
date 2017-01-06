package org.nuxeo.vision.core.amazon;

import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import org.nuxeo.vision.core.image.ImageProperties;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.VisionResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loopingz on 05/01/2017.
 */
public class AmazonRekognitionResponse implements VisionResponse {

    DetectLabelsResult response;
    List<TextEntity> labels = new ArrayList<TextEntity>();

    public AmazonRekognitionResponse(DetectLabelsResult response) {
        this.response = response;
        for (Label label : response.getLabels()) {
            labels.add(new TextEntity(label.getName(), label.getConfidence(), "en_US"));
        }
    }

    @Override
    public List<TextEntity> getClassificationLabels() {
        return labels;
    }

    @Override
    public List<TextEntity> getOcrText() {
        return new ArrayList<>();
    }

    @Override
    public ImageProperties getImageProperties() {
        return null;
    }

    @Override
    public Object getNativeObject() {
        return response;
    }
}
