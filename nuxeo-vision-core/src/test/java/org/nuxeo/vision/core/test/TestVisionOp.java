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

package org.nuxeo.vision.core.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.operation.VisionOp;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionResponse;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(FeaturesRunner.class)
@org.nuxeo.runtime.test.runner.Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "nuxeo-vision-core"
})
public class TestVisionOp {

    @Inject
    CoreSession session;

    @Test
    public void testOneBlobWithTags() throws IOException, OperationException {
        File file = new File(getClass().getResource("/files/nyc.jpg").getPath());
        Blob blob = new FileBlob(file);

        StringList features = new StringList();
        features.add(VisionFeature.LABEL_DETECTION.toString());

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(blob);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestTextTagBlobOp");
        chain.add(VisionOp.ID).
                set("features",features).
                set("outputVariable","testTags").
                set("maxResults",5);
        blob = (Blob) as.run(ctx, chain);

        List<VisionResponse> resultList = (List<VisionResponse>) ctx.get("testTags");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1,resultList.size());
        VisionResponse result = resultList.get(0);
        List<TextEntity> labels = result.getClassificationLabels();
        Assert.assertNotNull(labels);
        Assert.assertTrue(labels.size()>0);
        System.out.print(labels);
    }


    @Test
    public void testMultipleBlobsWithTags() throws IOException, OperationException {
        BlobList blobs = new BlobList();
        blobs.add(new FileBlob(new File(getClass().getResource("/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource("/files/text.png").getPath())));

        StringList features = new StringList();
        features.add(VisionFeature.LABEL_DETECTION.toString());

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(blobs);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestTextTagBlobOp");
        chain.add(VisionOp.ID).
                set("features",features).
                set("outputVariable","testTags").
                set("maxResults",5);
        blobs = (BlobList) as.run(ctx, chain);

        List<VisionResponse> resultList = (List<VisionResponse>) ctx.get("testTags");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2,resultList.size());
    }


}
