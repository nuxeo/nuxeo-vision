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
 */

package org.nuxeo.vision.google;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
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
        if (response.getLabelAnnotationsList() != null) {
            results.addAll(processEntityAnnotationList(response.getLabelAnnotationsList()));
        }
        if (response.getLandmarkAnnotationsList() != null) {
            results.addAll(processEntityAnnotationList(response.getLandmarkAnnotationsList()));
        }
        if (response.getLogoAnnotationsList() != null) {
            results.addAll(processEntityAnnotationList(response.getLogoAnnotationsList()));
        }
        return results;
    }

    @Override
    public List<TextEntity> getOcrText() {
        List<EntityAnnotation> labels = response.getTextAnnotationsList();
        return processEntityAnnotationList(labels);
    }

    @Override
    public ImageProperties getImageProperties() {
        List<ColorInfo> results = new ArrayList<>();
        com.google.cloud.vision.v1.ImageProperties properties = response.getImagePropertiesAnnotation();
        if (properties == null) {
            return new ImageProperties(results);
        }
        DominantColorsAnnotation annotation = properties.getDominantColors();
        if (annotation == null) {
            return new ImageProperties(results);
        }
        List<com.google.cloud.vision.v1.ColorInfo> colors = annotation.getColorsList();
        if (colors == null) {
            return new ImageProperties(results);
        }

        for (com.google.cloud.vision.v1.ColorInfo colorInfo : colors) {
            com.google.type.Color color = colorInfo.getColor();
            float red = color.getRed() / 255f;
            float blue = color.getBlue() / 255f;
            float green = color.getGreen() / 255f;
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
            result.add(new TextEntity(annotation.getDescription(), annotation.getScore(), annotation.getLocale()));
        }
        return result;
    }

}
