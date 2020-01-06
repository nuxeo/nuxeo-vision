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
package org.nuxeo.vision.google.test;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.vision.core.service.Vision;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.google.GoogleVisionProvider;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.nuxeo.vision.google.test.GoogleCredentialHelper.CRED_PROP;
import static org.nuxeo.vision.google.test.GoogleCredentialHelper.areCredentialsSet;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "nuxeo-vision-core","nuxeo-vision-google",
          "nuxeo-vision-google:test-crop-hint-automation-js.xml",
 })
public class TestAutomation {

    @Inject
    CoreSession session;

    @Inject
    Vision visionService;

    @Before
    public void setCredentials() {
        Map<String, String> params = new HashMap<>();
        params.put(GoogleVisionProvider.CREDENTIAL_PATH_PARAM, System.getProperty(CRED_PROP));
        VisionProvider googleVisionProvider = new GoogleVisionProvider(params);
        visionService.getProviders().put("google",googleVisionProvider);
    }

    @Test
    public void testCropHints() throws OperationException {
        Assume.assumeTrue("Credentials not set", areCredentialsSet());
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(blob);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestChain");
        chain.add("javascript.PictureVisionDefaultMapper");
        blob = (Blob) as.run(ctx, chain);
    }

}
