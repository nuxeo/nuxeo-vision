package org.nuxeo.labs.vision.core.test;

import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.labs.vision.core.service.GoogleVision;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("org.nuxeo.labs.nuxeo-labs-google-vision-core")
public class TestGoogleVision {

    @Inject
    protected GoogleVision googleVision;

    @Test
    public void testLabelService() {
        assertNotNull(googleVision);
        Framework.getProperties().put(
                "org.nuxeo.labs.google.credential",
                getClass().getResource("/files/credential.json").getPath());
        Framework.getProperties().put(
                "org.nuxeo.labs.google.enable","true");
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<String> labels = googleVision.getLabels(blob);
        assertTrue(labels.size()>0);
        System.out.print(labels);

    }

    @Test
    public void testTextService() {
        assertNotNull(googleVision);
        Framework.getProperties().put(
                "org.nuxeo.labs.google.credential",
                getClass().getResource("/files/credential.json").getPath());
        Framework.getProperties().put(
                "org.nuxeo.labs.google.enable","true");
        File file = new File(getClass().getResource("/files/text.png").getPath());
        Blob blob = new FileBlob(file);
        String text = googleVision.getText(blob);
        assertTrue(text.length()>0);
        assertTrue(text.startsWith("Getting Started"));
        assertTrue(text.endsWith("sponsored by Amazon.\n"));
        System.out.print(text);

    }


    @Test
    public void testDeactivatedService() {
        assertNotNull(googleVision);
        Framework.getProperties().put(
                "org.nuxeo.labs.google.credential",
                getClass().getResource("/files/credential.json").getPath());
        Framework.getProperties().put(
                "org.nuxeo.labs.google.enable","false");
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<String> labels = googleVision.getLabels(blob);
        assertTrue(labels.size()==0);

    }
}
