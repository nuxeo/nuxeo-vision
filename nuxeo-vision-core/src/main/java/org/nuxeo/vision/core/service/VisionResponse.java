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
package org.nuxeo.vision.core.service;

import java.util.List;

import org.nuxeo.vision.core.image.ImageProperties;
import org.nuxeo.vision.core.image.TextEntity;

public interface VisionResponse {

    /**
     * @return a list of classification labels returned by the service
     */
    List<TextEntity> getClassificationLabels();

    /**
     * @return a list of text strings extracted by the service
     */
    List<TextEntity> getOcrText();

    /**
     * @return a list of properties
     */
    ImageProperties getImageProperties();

    /**
     * @return the native object returned by the service provider
     */
    Object getNativeObject();
}
