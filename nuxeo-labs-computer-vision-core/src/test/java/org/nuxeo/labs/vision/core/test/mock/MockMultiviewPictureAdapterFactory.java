package org.nuxeo.labs.vision.core.test.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.MULTIVIEW_PICTURE_FACET;
import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;

/**
 * Created by Michaël on 3/8/2016.
 */
public class MockMultiviewPictureAdapterFactory implements DocumentAdapterFactory {

    private static final Log log = LogFactory.getLog(MockMultiviewPictureAdapterFactory.class);

    @Override
    public Object getAdapter(DocumentModel doc, Class itf) {
        if (doc.hasFacet(PICTURE_FACET) || doc.hasFacet(MULTIVIEW_PICTURE_FACET)) {
            return new MockMultiViewPictureAdapter(doc);
        }
        return null;
    }
}