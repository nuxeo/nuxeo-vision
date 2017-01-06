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
package org.nuxeo.vision.core.service;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.vision.core.google.GoogleProvider;
import org.nuxeo.vision.core.google.GoogleVisionDescriptor;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VisionImpl extends DefaultComponent implements Vision {

    private static final Log log = LogFactory.getLog(VisionImpl.class);

    protected static final String CONFIG_EXT_POINT = "configuration";

    protected static final String GOOGLE_EXT_POINT = "google";

    protected static final long _4MB = 4194304;

    protected static final long _8MB = 8388608;

    protected static final int MAX_BLOB_PER_REQUEST = 16;

    protected VisionDescriptor config = null;

    protected GoogleVisionDescriptor googleConfig = null;

    protected Map<String, VisionProvider> providers = new HashMap<String, VisionProvider>();

    /**
     * Component activated notification. Called when the component is activated.
     * All component dependencies are resolved at that moment. Use this method
     * to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
    }

    /**
     * Component deactivated notification. Called before a component is
     * unregistered. Use this method to do cleanup if any and free any resources
     * held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
    }

    /**
     * Application started notification. Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle
     *            context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        if (CONFIG_EXT_POINT.equals(extensionPoint)) {
            config = (VisionDescriptor) contribution;
        } else if (GOOGLE_EXT_POINT.equals(extensionPoint)) {
            providers.put("google", new GoogleProvider((GoogleVisionDescriptor) contribution));
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }

    @Override
    public VisionResponse execute(Blob blob, List<VisionFeature> features,
            int maxResults) throws IOException, GeneralSecurityException,
            IllegalStateException {

        if (blob == null) {
            throw new IllegalArgumentException("Input Blob cannot be null");
        } else if (features == null || features.size() == 0) {
            throw new IllegalArgumentException(
                    "The feature list cannot be empty or null");
        }

        List<VisionResponse> results = execute(ImmutableList.of(blob),
                features, maxResults);
        if (results.size() > 0) {
            return results.get(0);
        } else {
            throw new NuxeoException(
                    "Google vision returned empty results for "
                            + blob.getFilename());
        }
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs,
            List<VisionFeature> features, int maxResults) throws IOException,
            GeneralSecurityException, IllegalStateException {

        if (blobs == null || blobs.size() == 0) {
            throw new IllegalArgumentException(
                    "Input Blob list cannot be null or empty");
        } else if (!checkBlobs(blobs)) {
            throw new IllegalArgumentException(
                    "Too many blobs or size exceeds the API limit");
        } else if (features == null || features.size() == 0) {
            throw new IllegalArgumentException(
                    "The feature list cannot be empty or null");
        }
        // Launch provider
        if (!providers.containsKey(config.getProvider())) {
            throw new IllegalArgumentException(
                    "The provider '" + config.getProvider() + "'is unknown");
        }
        return providers.get(config.getProvider()).execute(blobs, features, maxResults);
    }

    @Override
    public String getPictureMapperChainName() {
        return config.getPictureMapperChainName();
    }

    @Override
    public String getVideoMapperChainName() {
        return config.getVideoMapperChainName();
    }

    protected boolean checkBlobs(List<Blob> blobs) throws IOException {
        if (blobs.size() > MAX_BLOB_PER_REQUEST) {
            return false;
        }
        long totalSize = 0;
        for (Blob blob : blobs) {
            long size = blob.getLength();
            if (size <= 0) {
                throw new IOException("Could not read the blob size");
            }
            if (size > _4MB) {
                return false;
            }
            totalSize += size;
            if (totalSize > _8MB) {
                return false;
            }
        }
        return true;
    }

}
