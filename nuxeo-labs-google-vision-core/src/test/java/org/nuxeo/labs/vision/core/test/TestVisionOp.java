package org.nuxeo.labs.vision.core.test;

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
import org.nuxeo.labs.vision.core.FeatureType;
import org.nuxeo.labs.vision.core.operation.VisionOp;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.labs.nuxeo-labs-google-vision-core"
})
public class TestVisionOp {

    @Inject
    CoreSession session;

    @Test
    public void testOneBlobWithTags() throws IOException, OperationException {

        Framework.getProperties().put(
                "org.nuxeo.labs.google.credential",
                getClass().getResource("/files/credential.json").getPath());

        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);

        StringList features = new StringList();
        features.add(FeatureType.LABEL_DETECTION.toString());

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

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) ctx.get("testTags");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1,resultList.size());
        Map<String,Object> result = resultList.get(0);
        List<String> labels = (List<String>) result.get(FeatureType.LABEL_DETECTION.toString());
        Assert.assertNotNull(labels);
        Assert.assertTrue(labels.size()>0);
        System.out.print(labels);
    }


    @Test
    public void testMultipleBlobsWithTags() throws IOException, OperationException {

        Framework.getProperties().put(
                "org.nuxeo.labs.google.credential",
                getClass().getResource("/files/credential.json").getPath());

        BlobList blobs = new BlobList();
        blobs.add(new FileBlob(new File(getClass().getResource("/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource("/files/text.png").getPath())));

        StringList features = new StringList();
        features.add(FeatureType.LABEL_DETECTION.toString());

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

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) ctx.get("testTags");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2,resultList.size());
    }


}
