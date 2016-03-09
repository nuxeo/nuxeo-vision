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

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.runtime.api.Framework;

/**
 * Created by Michaël on 3/7/2016.
 */

@XObject("configuration")
public class GoogleVisionDescriptor {

    public static final String ENV_VARIABLE = "NUXEO_GOOGLE_APPLICATION_CREDENTIALS";

    @XNode("appName")
    protected String appName = "Nuxeo";

    @XNode("credentialFilePath")
    protected String credentialFilePath;

    @XNode("mapperChainName")
    protected String mapperChainName = "javascript.VisionDefaultMapper";


    public String getAppName() {
        return appName;
    }

    public String getCredentialFilePath() {
        if (Framework.isTestModeSet() &&
                (credentialFilePath==null || credentialFilePath.length()==0)) {
            // Use ENV variable if running unit tests
            return System.getenv(ENV_VARIABLE);
        } else {
            return credentialFilePath;
        }
    }

    public String getMapperChainName() {
        return mapperChainName;
    }
}
