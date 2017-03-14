/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.vision.aws;

import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import org.nuxeo.vision.core.image.ImageProperties;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.VisionResponse;

import java.util.ArrayList;
import java.util.List;

public class AmazonRekognitionResponse implements VisionResponse {

    private DetectLabelsResult response;
    private List<TextEntity> labels = new ArrayList<TextEntity>();

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
