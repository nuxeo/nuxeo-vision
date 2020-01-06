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
 *     Thibaud Arguillere
 */
package org.nuxeo.vision.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.Vision;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionResponse;
import org.nuxeo.vision.core.test.mock.MockVisionProvider;
import org.nuxeo.vision.core.test.mock.MockVisionResponse;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@org.nuxeo.runtime.test.runner.Features({ PlatformFeature.class })
@Deploy("nuxeo-vision-core")
@LocalDeploy({ "nuxeo-vision-core:OSGI-INF/mock-adapter-contrib.xml",
        "nuxeo-vision-core:OSGI-INF/disabled-listener-contrib.xml",
        "nuxeo-vision-core:OSGI-INF/mock-provider-contrib.xml" })
public class TestVisionService {

    @Inject
    protected Vision vision;

    @Test
    public void testLabelFeature() throws IOException, GeneralSecurityException {
        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        VisionResponse result = vision.execute(blob, Arrays.asList(VisionFeature.LABEL_DETECTION.toString()), new HashMap<>(), 5);
        assertNotNull(result);

        List<TextEntity> labels = result.getClassificationLabels();
        assertEquals(MockVisionResponse.MOCK_RESULT_SIZE, labels.size());
        assertEquals(MockVisionResponse.MOCK_TEXT, labels.get(0).getText());
    }

    @Test
    public void testMultipleBlobs() throws IOException, GeneralSecurityException {

        List<Blob> blobs = new ArrayList<>();
        blobs.add(new FileBlob(new File(getClass().getResource("/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource("/files/text.png").getPath())));

        List<VisionResponse> results = vision.execute(blobs,
                Arrays.asList(VisionFeature.TEXT_DETECTION.toString(), VisionFeature.LABEL_DETECTION.toString()), new HashMap<>(), 5);
        assertTrue(results.size() == 2);

        for (VisionResponse oneResponse : results) {
            List<TextEntity> labels = oneResponse.getClassificationLabels();
            assertEquals(MockVisionResponse.MOCK_RESULT_SIZE, labels.size());
            assertEquals(MockVisionResponse.MOCK_TEXT, labels.get(0).getText());
        }
    }

    @Test
    public void shouldFailWithWrongFeature() {

        File file = new File(getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        try {
            @SuppressWarnings("unused")
            VisionResponse result = vision.execute(blob, Arrays.asList("abc123"), new HashMap<>(),5);
            assertFalse("Chould have failed with invalid label", true);
        } catch (Exception e) {
            assertEquals(MockVisionProvider.UNSUPPORTED_FEATURE, e.getMessage());
        }

    }

}
