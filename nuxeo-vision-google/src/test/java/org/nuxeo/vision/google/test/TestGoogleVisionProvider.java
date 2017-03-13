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
package org.nuxeo.vision.google.test;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.vision.core.image.ColorInfo;
import org.nuxeo.vision.core.image.TextEntity;
import org.nuxeo.vision.core.service.Vision;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionResponse;
import org.nuxeo.vision.google.GoogleVisionProvider;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({"nuxeo-vision-core","nuxeo-vision-google"})
public class TestGoogleVisionProvider {

    public static final String CRED_PROP = "org.nuxeo.vision.test.credential.file";
    public static final String KEY_PROP = "org.nuxeo.vision.test.credential.key";

    @Inject
    Vision visionService;

    protected GoogleVisionProvider googleVisionProvider = null;

    @Before
    public void testProviderisLoaded() {
        assertNotNull(visionService.getProvider("google"));
    }

    @Test
    public void testLabelFeature() throws IOException, GeneralSecurityException {

        File file = new File(
                getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<VisionResponse> results = getGoogleVisionProvider().execute(ImmutableList.of(blob),
                ImmutableList.of(VisionFeature.LABEL_DETECTION), 5);
        assertEquals(1,results.size());
        List<TextEntity> labels = results.get(0).getClassificationLabels();
        assertNotNull(labels);
        assertTrue(labels.size() > 0);
        System.out.print(labels);
    }

    @Test
    public void testTextFeature() throws IOException, GeneralSecurityException {

        File file = new File(
                getClass().getResource("/files/text.png").getPath());
        Blob blob = new FileBlob(file);
        List<VisionResponse> results = getGoogleVisionProvider().execute(ImmutableList.of(blob),
                ImmutableList.of(VisionFeature.TEXT_DETECTION), 5);
        assertEquals(1,results.size());
        List<TextEntity> texts = results.get(0).getOcrText();
        assertNotNull(texts);
        assertTrue(texts.size() > 0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testColorFeature() throws IOException, GeneralSecurityException {

        File file = new File(
                getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<VisionResponse> results = getGoogleVisionProvider().execute(ImmutableList.of(blob),
                ImmutableList.of(VisionFeature.IMAGE_PROPERTIES), 5);
        assertEquals(1,results.size());
        List<ColorInfo> colors = results.get(0).getImageProperties().getColors();
        assertNotNull(colors);
        assertTrue(colors.size() > 0);
        System.out.print(colors.get(0));
    }

    @Test
    public void testMultipleFeatures() throws IOException,
            GeneralSecurityException {

        File file = new File(
                getClass().getResource("/files/plane.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<VisionResponse> results = getGoogleVisionProvider().execute(ImmutableList.of(blob),
                ImmutableList.of(VisionFeature.TEXT_DETECTION, VisionFeature.LABEL_DETECTION), 5);
        assertEquals(1,results.size());
        List<TextEntity> labels = results.get(0).getClassificationLabels();
        assertNotNull(labels);
        assertTrue(labels.size() > 0);

        List<TextEntity> texts = results.get(0).getOcrText();
        assertNotNull(texts);
        assertTrue(texts.size() > 0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testAllFeatures() throws IOException, GeneralSecurityException {

        File file = new File(getClass().getResource("/files/nyc.jpg").getPath());
        Blob blob = new FileBlob(file);
        List<VisionResponse> results = getGoogleVisionProvider().execute(ImmutableList.of(blob), ImmutableList.of(
                VisionFeature.TEXT_DETECTION, VisionFeature.LABEL_DETECTION,
                VisionFeature.IMAGE_PROPERTIES, VisionFeature.FACE_DETECTION,
                VisionFeature.LOGO_DETECTION, VisionFeature.LANDMARK_DETECTION,
                VisionFeature.SAFE_SEARCH_DETECTION), 5);
        assertEquals(1,results.size());
        VisionResponse result = results.get(0);
        List<TextEntity> labels = result.getClassificationLabels();
        assertNotNull(labels);
        assertTrue(labels.size() > 0);
        System.out.print(labels);

        List<TextEntity> texts = result.getOcrText();
        assertNotNull(texts);
        assertTrue(texts.size() > 0);
        System.out.print(texts.get(0));
    }

    @Test
    public void testMultipleBlobs() throws IOException,
            GeneralSecurityException {

        List<Blob> blobs = new ArrayList<>();
        blobs.add(new FileBlob(new File(getClass().getResource(
                "/files/plane.jpg").getPath())));
        blobs.add(new FileBlob(new File(getClass().getResource(
                "/files/text.png").getPath())));

        List<VisionResponse> results = getGoogleVisionProvider().execute(blobs, ImmutableList.of(
                VisionFeature.TEXT_DETECTION, VisionFeature.LABEL_DETECTION), 5);
        assertTrue(results.size() == 2);
    }

    protected GoogleVisionProvider getGoogleVisionProvider() {
        if (googleVisionProvider!=null) return googleVisionProvider;
        Map<String,String> params = new HashMap<>();
        params.put(GoogleVisionProvider.APP_NAME_PARAM,"Nuxeo");
        params.put(GoogleVisionProvider.API_KEY_PARAM,System.getProperty(KEY_PROP));
        params.put(GoogleVisionProvider.CREDENTIAL_PATH_PARAM,System.getProperty(CRED_PROP));
        googleVisionProvider = new GoogleVisionProvider(params);
        return googleVisionProvider;
    }

}
