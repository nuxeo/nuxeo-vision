package org.nuxeo.labs.vision.core.test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.labs.vision.core.FeatureType;
import org.nuxeo.labs.vision.core.service.GoogleVision;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("org.nuxeo.labs.nuxeo-labs-google-vision-core")
@LocalDeploy({
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/mock-picture-blobholder-contrib.xml",
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/disabled-listener-contrib.xml",
        "org.nuxeo.labs.nuxeo-labs-google-vision-core:OSGI-INF/google-vision-test-contrib.xml"
})
public class TestGoogleVision {

    @Inject
    protected GoogleVision googleVision;

    @Test
    public void testLabelFeature() throws IOException, GeneralSecurityException {
        assertNotNull(googleVision);
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        Map<String,Object> results =
                googleVision.execute(blob, ImmutableList.of(FeatureType.LABEL_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<String> labels = (List<String>) results.get(FeatureType.LABEL_DETECTION.toString());
        assertNotNull(labels);
        assertTrue(labels.size()>0);
        System.out.print(labels);
    }

    @Test
    public void testTextFeature() throws IOException, GeneralSecurityException {
        assertNotNull(googleVision);
        File file = new File(getClass().getResource("/files/text.png").getPath());
        Blob blob = new FileBlob(file);
        Map<String,Object> results =
                googleVision.execute(blob, ImmutableList.of(FeatureType.TEXT_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<String> texts = (List<String>) results.get(FeatureType.TEXT_DETECTION.toString());
        assertNotNull(texts);
        assertTrue(texts.size()>0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testMultipleFeatures() throws IOException, GeneralSecurityException {
        assertNotNull(googleVision);
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        Map<String,Object> results = googleVision.execute(
                blob, ImmutableList.of(FeatureType.TEXT_DETECTION.toString(),
                        FeatureType.LABEL_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<String> labels = (List<String>) results.get(FeatureType.LABEL_DETECTION.toString());
        assertNotNull(labels);
        assertTrue(labels.size()>0);
        List<String> texts = (List<String>) results.get(FeatureType.TEXT_DETECTION.toString());
        assertNotNull(texts);
        assertTrue(texts.size()>0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testMultipleBlobs() throws IOException, GeneralSecurityException {
        assertNotNull(googleVision);
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new FileBlob(new File(getClass().getResource("/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource("/files/text.png").getPath())));

        List<Map<String, Object>> results = googleVision.execute(
                blobs, ImmutableList.of(FeatureType.TEXT_DETECTION.toString(),
                        FeatureType.LABEL_DETECTION.toString()), 5);
        assertTrue(results.size() == 2);
    }

}
