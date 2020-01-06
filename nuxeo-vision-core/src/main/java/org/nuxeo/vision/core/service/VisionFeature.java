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
 *     Thibaud Arguillere
 */
package org.nuxeo.vision.core.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Visionfeature is a way to centralize labels and as the first provider to be implemented was Google, we are use their
 * labels. This list is just for convenience: If Google, Amazon or any provider adds new labels or change existing one,
 * clients of the Vision service can still directly use the new labels, the service does not check if it is part of this
 * list.
 *
 * @since 8.10
 */
public enum VisionFeature {

    FACE_DETECTION("FACE_DETECTION"), LANDMARK_DETECTION("LANDMARK_DETECTION"), LOGO_DETECTION(
            "LOGO_DETECTION"), LABEL_DETECTION("LABEL_DETECTION"), TEXT_DETECTION(
                    "TEXT_DETECTION"), SAFE_SEARCH_DETECTION("SAFE_SEARCH_DETECTION"), IMAGE_PROPERTIES(
                            "IMAGE_PROPERTIES"), WEB_DETECTION("WEB_DETECTION"), CROP_HINT("CROP_HINTS");

    private final String text;

    VisionFeature(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static List<String> asStringList() {

        List<String> list = new ArrayList<String>();
        VisionFeature [] features = VisionFeature.values();

        for(VisionFeature oneFeature : features) {
            list.add(oneFeature.toString());
        }

        return list;

    }
}
