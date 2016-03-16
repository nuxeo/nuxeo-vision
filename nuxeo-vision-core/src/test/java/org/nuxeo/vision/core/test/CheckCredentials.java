/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thibaud Arguillere
 */
package org.nuxeo.vision.core.test;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.vision.core.google.GoogleVisionDescriptor;

/**
 * @since 7.10HF06
 */
public class CheckCredentials {

    protected static Boolean hasCredentials = null;

    public static boolean ok() {

        if (hasCredentials == null) {
            hasCredentials = StringUtils.isNotBlank(System.getenv(GoogleVisionDescriptor.CRED_ENV_VARIABLE))
                    && StringUtils.isNotBlank(System.getenv(GoogleVisionDescriptor.KEY_ENV_VARIABLE));
        }

        return hasCredentials.booleanValue();

    }
}
