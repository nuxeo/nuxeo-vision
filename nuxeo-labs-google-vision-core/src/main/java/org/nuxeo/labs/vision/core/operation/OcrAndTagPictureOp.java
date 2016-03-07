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

package org.nuxeo.labs.vision.core.operation;

import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
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

/**
 *
 */
@Operation(
        id= OcrAndTagPictureOp.ID,
        category=Constants.CAT_DOCUMENT,
        label="Tag & OCR Picture",
        description="Tag and OCR the input Picture document using the Google Vision API." +
                " Tags are stored using the Tag Service and OCR text is put in dc:description")
public class OcrAndTagPictureOp {

    public static final String ID = "Document.OcrAndTagPictureOp";

    private static final Log log = LogFactory.getLog(OcrAndTagPictureOp.class);

    @Context
    protected CoreSession session;

    @Context
    protected GoogleVision googleVision;

    @Context
    protected TagService tagService;

    @Param(
            name = "conversion",
            description = "The picture conversion to use. Default to Medium",
            required = false)
    protected String conversion;

    @Param(
            name = "save",
            description = "Set to true to save the modification made to the document within this Operation",
            required = true)
    protected boolean save;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        if (!doc.hasFacet("Picture")) return doc;

        PictureBlobHolder holder = (PictureBlobHolder)doc.getAdapter(BlobHolder.class);
        Blob picture = holder.getBlob(conversion);

        AnnotateImageResponse result;
        try {
            result = googleVision.execute(
                    picture, ImmutableList.of(FeatureType.LABEL_DETECTION.toString(),
                            FeatureType.TEXT_DETECTION.toString()),5);
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Call to google vision API failed",e);
            return doc;
        }

        // Tag documents
        List<EntityAnnotation> labels = result.getLabelAnnotations();
        if (labels==null) return doc;
        for (EntityAnnotation label: labels) {
            tagService.tag(
                    session,doc.getId(),
                    label.getDescription().replaceAll(" ","+"),
                    session.getPrincipal().getName());
        }

        // Get OCR text
        List<EntityAnnotation> textItems = result.getTextAnnotations();
        if (textItems==null || textItems.size()==0) return doc;
        StringBuilder text = new StringBuilder();
        for (EntityAnnotation textItem: textItems) {
            text.append(textItem.getDescription()).append("\n");
        }
        doc.setPropertyValue("dc:description",text.toString());

        // Save document
        if (save) {
            doc = session.saveDocument(doc);
        }

        return doc;
    }
}
