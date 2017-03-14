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
package org.nuxeo.vision.core.service;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisionImpl extends DefaultComponent implements Vision {

    private static final Log log = LogFactory.getLog(VisionImpl.class);

    protected static final String CONFIG_EXT_POINT = "configuration";

    protected static final String PROVIDER_EXT_POINT = "provider";

    protected VisionDescriptor config = null;

    protected Map<String, VisionProvider> providers = new HashMap<>();

    /**
     * Component activated notification. Called when the component is activated. All component dependencies are resolved
     * at that moment. Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
    }

    /**
     * Component deactivated notification. Called before a component is unregistered. Use this method to do cleanup if
     * any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
    }

    /**
     * Application started notification. Called after the application started. You can do here any initialization that
     * requires a working application (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (CONFIG_EXT_POINT.equals(extensionPoint)) {
            config = (VisionDescriptor) contribution;
        } else if (PROVIDER_EXT_POINT.equals(extensionPoint)) {
            VisionProviderDescriptor desc = (VisionProviderDescriptor) contribution;
            try {
                VisionProvider provider = (VisionProvider) desc.getClassName().getConstructor(Map.class).newInstance(
                        desc.getParameters());
                providers.put(desc.getProviderName(), provider);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                throw new NuxeoException(e);
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }

    @Override
    public VisionResponse execute(Blob blob, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {
        return execute(config.getDefaultProviderName(), blob, features, maxResults);
    }

    @Override
    public List<VisionResponse> execute(List<Blob> blobs, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException, IllegalStateException {
        return execute(config.getDefaultProviderName(), blobs, features, maxResults);
    }

    // @since 9.1
    @Override
    public VisionResponse execute(String providerName, Blob blob, List<VisionFeature> features, int maxResults)
            throws IOException, GeneralSecurityException {
        if (blob == null) {
            throw new IllegalArgumentException("Input Blob cannot be null");
        } else if (features == null || features.size() == 0) {
            throw new IllegalArgumentException("The feature list cannot be empty or null");
        }

        List<VisionResponse> results = execute(providerName, ImmutableList.of(blob), features, maxResults);
        if (results.size() > 0) {
            return results.get(0);
        } else {
            throw new NuxeoException(
                    "Provider " + providerName + " vision returned empty results for " + blob.getFilename());
        }
    }

    // @since 9.1
    @Override
    public List<VisionResponse> execute(String providerName, List<Blob> blobs, List<VisionFeature> features,
            int maxResults) throws IOException, GeneralSecurityException {
        VisionProvider provider = providers.get(providerName);

        if (provider == null)
            throw new NuxeoException("Unknown provider: " + providerName);

        if (blobs == null || blobs.size() == 0) {
            throw new IllegalArgumentException("Input Blob list cannot be null or empty");
        } else if (!provider.checkBlobs(blobs)) {
            throw new IllegalArgumentException("Too many blobs or size exceeds the API limit");
        } else if (features == null || features.size() == 0) {
            throw new IllegalArgumentException("The feature list cannot be empty or null");
        }
        return provider.execute(blobs, features, maxResults);
    }

    @Override
    public String getPictureMapperChainName() {
        return config.getPictureMapperChainName();
    }

    @Override
    public String getVideoMapperChainName() {
        return config.getVideoMapperChainName();
    }

    @Override
    public String getDefaultProvider() {
        return config.getDefaultProviderName();
    }

    @Override
    public VisionProvider getProvider(String name) {
        return providers.get(name);
    }

    @Override
    public Map<String, VisionProvider> getProviders() {
        return providers;
    }

}
