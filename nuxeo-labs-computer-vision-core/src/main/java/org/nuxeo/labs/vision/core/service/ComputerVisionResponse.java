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

package org.nuxeo.labs.vision.core.service;

import java.util.List;


public interface ComputerVisionResponse {

    /**
     *
     * @return a list of classification labels returned by the service
     */
    List<String> getClassificationLabels();

    /**
     *
     * @return a list of text string extracted by the service
     */
    List<String> getOcrText();

    /**
     *
     * @return the native object returned by the service provider
     */
    Object getNativeObject();
}
