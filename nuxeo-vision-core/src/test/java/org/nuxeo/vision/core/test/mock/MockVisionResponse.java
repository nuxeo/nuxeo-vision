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

package org.nuxeo.vision.core.test.mock;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.vision.core.image.ImageProperties;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.VisionResponse;

public class MockVisionResponse implements VisionResponse {

    public static final int MOCK_RESULT_SIZE = 1;

    public static final String MOCK_TEXT = "Hello World";

    @Override
    public List<TextEntity> getClassificationLabels() {
        return Arrays.asList(new TextEntity(MOCK_TEXT, 1.0f, "en"));
    }

    @Override
    public List<TextEntity> getOcrText() {
        return Arrays.asList(new TextEntity(MOCK_TEXT, 1.0f, "en"));
    }

    @Override
    public ImageProperties getImageProperties() {
        return null;
    }

    @Override
    public Object getNativeObject() {
        return null;
    }
}
