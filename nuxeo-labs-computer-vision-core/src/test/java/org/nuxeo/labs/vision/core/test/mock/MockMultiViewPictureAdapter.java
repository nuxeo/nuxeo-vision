package org.nuxeo.labs.vision.core.test.mock;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.picture.api.PictureView;
import org.nuxeo.ecm.platform.picture.api.PictureViewImpl;
import org.nuxeo.ecm.platform.picture.api.adapters.MultiviewPictureAdapter;

/**
 * Created by Michaël on 3/8/2016.
 */
public class MockMultiViewPictureAdapter extends MultiviewPictureAdapter {

    private DocumentModel doc;

    public MockMultiViewPictureAdapter(DocumentModel docModel) {
        super(docModel);
        doc = docModel;
    }

    @Override
    public PictureView getView(String title) {
        PictureView view = new PictureViewImpl();
        view.setTitle(title);
        view.setBlob((Blob) doc.getPropertyValue("file:content"));
        return view;
    }
}
