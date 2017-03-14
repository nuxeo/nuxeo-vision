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

package org.nuxeo.vision.google;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.Color;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import org.nuxeo.vision.core.image.ColorInfo;
import org.nuxeo.vision.core.image.ImageProperties;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.VisionResponse;

import java.util.ArrayList;
import java.util.List;

public class GoogleVisionResponse implements VisionResponse {

    AnnotateImageResponse response;

    public GoogleVisionResponse(AnnotateImageResponse response) {
        this.response = response;
    }

    @Override
    public List<TextEntity> getClassificationLabels() {
        List<TextEntity> results = new ArrayList<>();
        if (response.getLabelAnnotations() != null) {
            results.addAll(processEntityAnnotationList(response.getLabelAnnotations()));
        }
        if (response.getLandmarkAnnotations() != null) {
            results.addAll(processEntityAnnotationList(response.getLandmarkAnnotations()));
        }
        if (response.getLogoAnnotations() != null) {
            results.addAll(processEntityAnnotationList(response.getLogoAnnotations()));
        }
        return results;
    }

    @Override
    public List<TextEntity> getOcrText() {
        List<EntityAnnotation> labels = response.getTextAnnotations();
        return processEntityAnnotationList(labels);
    }

    @Override
    public ImageProperties getImageProperties() {
        List<ColorInfo> results = new ArrayList<>();
        com.google.api.services.vision.v1.model.ImageProperties properties = response.getImagePropertiesAnnotation();
        if (properties == null) {
            return new ImageProperties(results);
        }
        DominantColorsAnnotation annotation = properties.getDominantColors();
        if (annotation == null) {
            return new ImageProperties(results);
        }
        List<com.google.api.services.vision.v1.model.ColorInfo> colors = annotation.getColors();
        if (colors == null) {
            return new ImageProperties(results);
        }

        for (com.google.api.services.vision.v1.model.ColorInfo colorInfo : colors) {
            Color color = colorInfo.getColor();

            float red = color.getRed() != null ? color.getRed() / 255 : 0;
            float blue = color.getBlue() != null ? color.getBlue() / 255 : 0;
            float green = color.getGreen() != null ? color.getGreen() / 255 : 0;

            java.awt.Color resultColor = new java.awt.Color(red, green, blue);

            results.add(new ColorInfo(resultColor, colorInfo.getPixelFraction(), colorInfo.getScore()));
        }
        return new ImageProperties(results);
    }

    @Override
    public Object getNativeObject() {
        return response;
    }

    protected List<TextEntity> processEntityAnnotationList(List<EntityAnnotation> annotations) {
        List<TextEntity> result = new ArrayList<>();
        if (annotations == null) {
            return result;
        }
        for (EntityAnnotation annotation : annotations) {
            result.add(new TextEntity(annotation.getDescription(),
                    annotation.getScore() != null ? annotation.getScore() : 0, annotation.getLocale()));
        }
        return result;
    }

}
