package org.nuxeo.labs.vision.core.test;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.labs.vision.core.service.ComputerVision;
import org.nuxeo.labs.vision.core.service.ComputerVisionFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@org.nuxeo.runtime.test.runner.Features({ PlatformFeature.class })
@Deploy("org.nuxeo.labs.nuxeo-labs-computer-vision-core")
@LocalDeploy({
        "org.nuxeo.labs.nuxeo-labs-computer-vision-core:OSGI-INF/mock-contrib.xml",
        "org.nuxeo.labs.nuxeo-labs-computer-vision-core:OSGI-INF/disabled-listener-contrib.xml"
})
public class TestComputerVisionService {

    @Inject
    protected ComputerVision computerVision;

    @Test
    public void testLabelFeature() throws IOException, GeneralSecurityException {
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        AnnotateImageResponse results =
                computerVision.execute(blob, ImmutableList.of(ComputerVisionFeature.LABEL_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<EntityAnnotation> labels = results.getLabelAnnotations();
        assertNotNull(labels);
        assertTrue(labels.size()>0);
        System.out.print(labels);
    }

    @Test
    public void testTextFeature() throws IOException, GeneralSecurityException {
        File file = new File(getClass().getResource("/files/text.png").getPath());
        Blob blob = new FileBlob(file);
        AnnotateImageResponse results =
                computerVision.execute(blob, ImmutableList.of(ComputerVisionFeature.TEXT_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<EntityAnnotation> texts = results.getTextAnnotations();
        assertNotNull(texts);
        assertTrue(texts.size()>0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testMultipleFeatures() throws IOException, GeneralSecurityException {
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        AnnotateImageResponse results = computerVision.execute(
                blob, ImmutableList.of(ComputerVisionFeature.TEXT_DETECTION.toString(),
                        ComputerVisionFeature.LABEL_DETECTION.toString()),5);
        assertTrue(results.size()>0);
        List<EntityAnnotation> labels = results.getLabelAnnotations();
        assertNotNull(labels);
        assertTrue(labels.size()>0);
        List<EntityAnnotation> texts = results.getTextAnnotations();
        assertNotNull(texts);
        assertTrue(texts.size()>0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testMultipleBlobs() throws IOException, GeneralSecurityException {
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new FileBlob(new File(getClass().getResource("/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource("/files/text.png").getPath())));

        List<AnnotateImageResponse> results = computerVision.execute(
                blobs, ImmutableList.of(ComputerVisionFeature.TEXT_DETECTION.toString(),
                        ComputerVisionFeature.LABEL_DETECTION.toString()), 5);
        assertTrue(results.size() == 2);
    }

}
