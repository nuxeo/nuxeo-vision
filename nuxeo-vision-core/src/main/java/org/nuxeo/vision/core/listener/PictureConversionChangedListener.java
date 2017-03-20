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
package org.nuxeo.vision.core.listener;

import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.*;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.vision.core.service.Vision;

public class PictureConversionChangedListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(PictureConversionChangedListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            handleEvent(event);
        }
    }

    protected void handleEvent(Event event) {
        EventContext ectx = event.getContext();
        if (!(ectx instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) ectx;
        DocumentModel doc = docCtx.getSourceDocument();
        if (!doc.hasFacet(PICTURE_FACET) || doc.isProxy()) {
            return;
        }

        Vision visionService = Framework.getService(Vision.class);
        String mapperChainName = visionService.getPictureMapperChainName();

        CoreSession session = doc.getCoreSession();
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext octx = new OperationContext();
        octx.setInput(doc);
        octx.setCoreSession(session);
        OperationChain chain = new OperationChain("PictureChangedListenerChain");
        chain.add(mapperChainName);
        try {
            as.run(octx, chain);

            EventContextImpl evctx = new DocumentEventContext(session, session.getPrincipal(), doc);
            Event eventToSend = evctx.newEvent(Vision.EVENT_IMAGE_HANDLED);
            EventService eventService = Framework.getLocalService(EventService.class);
            eventService.fireEvent(eventToSend);

        } catch (OperationException e) {
            log.warn(e);
        }
    }
}
