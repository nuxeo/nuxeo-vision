/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.vision.core.service.Vision;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionResponse;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(FeaturesRunner.class)
@org.nuxeo.runtime.test.runner.Features({ PlatformFeature.class })
@Deploy("nuxeo-vision-core")
@LocalDeploy({ "nuxeo-vision-core:OSGI-INF/mock-adapter-contrib.xml",
        "nuxeo-vision-core:OSGI-INF/disabled-listener-contrib.xml",
        "nuxeo-vision-core:OSGI-INF/mock-provider-contrib.xml"})
public class TestVisionService {

    @Inject
    protected Vision vision;

    @Test
    public void testLabelFeature() throws IOException, GeneralSecurityException {
        File file = new File(
                getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        VisionResponse result = vision.execute(blob, ImmutableList.of(
                VisionFeature.LABEL_DETECTION), 5);
        assertNotNull(result);
    }

    @Test
    public void testMultipleBlobs() throws IOException,
            GeneralSecurityException {

        List<Blob> blobs = new ArrayList<>();
        blobs.add(new FileBlob(new File(getClass().getResource(
                "/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource(
                "/files/text.png").getPath())));

        List<VisionResponse> results = vision.execute(blobs, ImmutableList.of(
                VisionFeature.TEXT_DETECTION, VisionFeature.LABEL_DETECTION), 5);
        assertTrue(results.size() == 2);
    }

}
