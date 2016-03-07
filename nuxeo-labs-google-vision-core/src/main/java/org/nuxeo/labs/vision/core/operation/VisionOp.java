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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.labs.vision.core.service.GoogleVision;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 *
 */
@Operation(
        id= VisionOp.ID,
        category=Constants.CAT_BLOB,
        label="Call Google Vision",
        description="Call the Google Vision API for the input blob(s)")
public class VisionOp {

    public static final String ID = "VisionOp";

    private static final Log log = LogFactory.getLog(VisionOp.class);


    @Context
    protected OperationContext ctx;

    @Context
    protected GoogleVision googleVision;

    @Param(
            name = "features",
            description= "A StringList of features to request from the API. Available Features are described at https://cloud.google.com/vision/reference/rest/v1/images/annotate#Feature",
            required = true)
    protected StringList features;

    @Param(
            name = "outputVariable",
            description= "The key of the context output variable. The output variable is a list of AnnotateImageResponse objects. See https://cloud.google.com/vision/reference/rest/v1/images/annotate#AnnotateImageResponse",
            required = true)
    protected String outputVariable;

    @Param(
            name = "maxResults",
            description= "The maximum number of results per feature",
            required = true)
    protected int maxResults;

    @OperationMethod
    public Blob run(Blob blob) {
        if (blob==null) return null;
        BlobList blobs = new BlobList();
        blobs.add(blob);
        return run(blobs).get(0);
    }

    @OperationMethod
    public BlobList run(BlobList blobs) {
        List<AnnotateImageResponse> results;
        try {
            results = googleVision.execute(blobs, features, maxResults);
            ctx.put(outputVariable,results);
        } catch (IOException | GeneralSecurityException e) {
            log.warn("Call to google vision API failed",e);
        }
        return blobs;
    }
}
