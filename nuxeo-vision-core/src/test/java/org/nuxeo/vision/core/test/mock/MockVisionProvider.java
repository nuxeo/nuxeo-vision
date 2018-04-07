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

package org.nuxeo.vision.core.test.mock;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.vision.core.service.VisionFeature;
import org.nuxeo.vision.core.service.VisionProvider;
import org.nuxeo.vision.core.service.VisionResponse;

public class MockVisionProvider implements VisionProvider {

    public static final String NAME = "mock";

    public static final String UNSUPPORTED_FEATURE = "Unsupported feature";

    public MockVisionProvider(Map<String, String> parameters) {
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<String> features, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {

        if(!featuresAreSupported(features)) {
            throw new IllegalArgumentException(UNSUPPORTED_FEATURE);
        }

        List<VisionResponse> responses = new ArrayList<>();
        blobs.forEach(b -> {
            responses.add(new MockVisionResponse());
        });
        return responses;
    }

    @Override
    public boolean checkBlobs(List<Blob> blobs) throws IOException {
        return true;
    }

    @Override
    public Object getNativeClient() {
        return null;
    }

    protected boolean featuresAreSupported(List<String> features) {

        if(features == null) {
            return false;
        }

        return VisionFeature.asStringList().containsAll(features);

    }
}
