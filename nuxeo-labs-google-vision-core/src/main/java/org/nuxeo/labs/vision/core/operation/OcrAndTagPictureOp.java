package org.nuxeo.labs.vision.core.operation;

import com.google.common.collect.ImmutableList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.nuxeo.ecm.platform.tag.TagService;
import org.nuxeo.labs.vision.core.FeatureType;
import org.nuxeo.labs.vision.core.service.GoogleVision;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Operation(
        id= OcrAndTagPictureOp.ID,
        category=Constants.CAT_DOCUMENT,
        label="Tag & OCR Picture",
        description="Tag Picture Using the Google Vision API")
public class OcrAndTagPictureOp {

    public static final String ID = "Document.OcrAndTagPictureOp";

    private static final Log log = LogFactory.getLog(OcrAndTagPictureOp.class);

    @Context
    protected CoreSession session;

    @Context
    protected GoogleVision googleVision;

    @Context
    protected TagService tagService;

    @Param(name = "conversion", required = true)
    protected String conversion;

    @Param(name = "save", required = true)
    protected boolean save;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        if (!doc.hasFacet("Picture")) return doc;

        PictureBlobHolder holder = (PictureBlobHolder)doc.getAdapter(BlobHolder.class);
        Blob picture = holder.getBlob(conversion);

        Map<String,Object> results;
        try {
            results = googleVision.execute(
                    picture, ImmutableList.of(FeatureType.LABEL_DETECTION.toString(),
                            FeatureType.TEXT_DETECTION.toString()),5);
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Call to google vision API failed",e);
            return doc;
        }

        // Tag documents
        List<String> labels = (List<String>) results.get(FeatureType.LABEL_DETECTION.toString());
        if (labels==null) return doc;
        for (String label: labels) {
            tagService.tag(
                    session,doc.getId(),
                    label.replaceAll(" ","+"),
                    session.getPrincipal().getName());
        }

        // Get OCR text
        List<String> textItems = (List<String>) results.get(FeatureType.TEXT_DETECTION.toString());
        if (textItems==null || textItems.size()==0) return doc;
        StringBuilder text = new StringBuilder();
        for (String textItem: textItems) {
            text.append(textItem).append("\n");
        }
        doc.setPropertyValue("dc:description",text.toString());

        // Save document
        if (save) {
            doc = session.saveDocument(doc);
        }

        return doc;
    }
}
