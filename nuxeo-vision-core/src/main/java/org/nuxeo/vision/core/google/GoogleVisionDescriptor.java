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

package org.nuxeo.vision.core.google;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.runtime.api.Framework;

/**
 * Created by Michaël on 3/7/2016.
 */

@XObject("configuration")
public class GoogleVisionDescriptor {

    public static final String CRED_PROP = "org.nuxeo.vision.test.credential.file";

    public static final String KEY_PROP = "org.nuxeo.vision.test.credential.key";

    @XNode("appName")
    protected String appName = "Nuxeo";

    @XNode("apiKey")
    protected String apiKey;

    @XNode("credentialFilePath")
    protected String credentialFilePath;

    public String getAppName() {
        return appName;
    }

    public String getCredentialFilePath() {
        if (Framework.isTestModeSet()
                && (credentialFilePath == null || credentialFilePath.length() == 0)) {
            // Use ENV variable if running unit tests
            return System.getProperty(CRED_PROP);
        } else {
            return credentialFilePath;
        }
    }

    public String getApiKey() {
        if (Framework.isTestModeSet()
                && (apiKey == null || apiKey.length() == 0)) {
            // Use ENV variable if running unit tests
            return System.getProperty(KEY_PROP);
        } else {
            return apiKey;
        }
    }
}
