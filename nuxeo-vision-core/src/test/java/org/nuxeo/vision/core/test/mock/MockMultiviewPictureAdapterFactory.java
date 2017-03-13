/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.vision.core.test.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.MULTIVIEW_PICTURE_FACET;
import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;

public class MockMultiviewPictureAdapterFactory implements
        DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(MockMultiviewPictureAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class itf) {
        if (doc.hasFacet(PICTURE_FACET)
                || doc.hasFacet(MULTIVIEW_PICTURE_FACET)) {
            return new MockMultiViewPictureAdapter(doc);
        }
        return null;
    }
}