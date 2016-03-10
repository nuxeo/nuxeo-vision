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

package org.nuxeo.labs.vision.core.google;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.nuxeo.labs.vision.core.service.ComputerVisionResponse;

import java.util.ArrayList;
import java.util.List;


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
