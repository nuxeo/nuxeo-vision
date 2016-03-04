package org.nuxeo.labs.vision.core.operation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.platform.picture.api.adapters.PictureBlobHolder;
import org.nuxeo.labs.vision.core.service.GoogleVision;


/**
 *
 */
@Operation(id=ExtractTextOp.ID, category=Constants.CAT_DOCUMENT, label="Extract Text From Picture", description="Describe here what your operation does.")
public class ExtractTextOp {

    public static final String ID = "Document.ExtractTextOp";

    @Context
    protected CoreSession session;

    @Context
    protected GoogleVision googleVision;


    @Param(name = "conversion", required = true)
    protected String conversion;

    @Param(name = "save", required = true)
    protected boolean save;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        if (!doc.hasFacet("Picture")) return doc;
        PictureBlobHolder holder = (PictureBlobHolder)doc.getAdapter(BlobHolder.class);
        Blob picture = holder.getBlob(conversion);
        String text = googleVision.getText(picture);
        if (text!=null && text.length()>0) doc.setPropertyValue("dc:description",text);
        if (save) {
            doc = session.saveDocument(doc);
        }
        return doc;
    }
}
