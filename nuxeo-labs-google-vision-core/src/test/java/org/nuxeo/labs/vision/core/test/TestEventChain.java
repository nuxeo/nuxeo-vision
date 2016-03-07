package org.nuxeo.labs.vision.core.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.tag.Tag;
import org.nuxeo.ecm.platform.tag.TagService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.labs.nuxeo-labs-google-vision-core",
        "org.nuxeo.ecm.platform.picture.core",
        "org.nuxeo.ecm.platform.tag"
})
@LocalDeploy({
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/mock-picture-blobholder-contrib.xml",
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/disabled-listener-contrib.xml",
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/google-vision-test-contrib.xml"
})
public class TestEventChain {

    @Inject
    CoreSession session;

    @Inject
    protected TagService tagService;

    @Test
    public void shouldCallTheChain() throws IOException, OperationException {

        DocumentModel picture = session.createDocumentModel("/", "Picture", "Picture");
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        picture.setPropertyValue("file:content", (Serializable) blob);
        picture = session.createDocument(picture);


        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(picture);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestTagChain");
        chain.add("TagImageChain");
        picture = (DocumentModel) as.run(ctx, chain);

        List<Tag> tags =
                tagService.getDocumentTags(session,picture.getId(),session.getPrincipal().getName());

        Assert.assertTrue(tags.size()>0);
    }


}
