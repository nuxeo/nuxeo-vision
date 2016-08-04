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
 *     Thibaud Arguillere
 */
package org.nuxeo.vision.core.worker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.nuxeo.vision.core.service.Vision;

/**
 * Created by Michaël on 3/10/2016.
 */
public class VideoVisionWorker extends AbstractWork {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(VideoVisionWorker.class);

    public VideoVisionWorker(String repositoryName, String docId) {
        super(repositoryName + ':' + docId + ":Vision");
        setDocument(repositoryName, docId);
    }

    @Override
    public void work() {

        setProgress(Progress.PROGRESS_INDETERMINATE);
        setStatus("Extracting");

        if (!TransactionHelper.isTransactionActive()) {
            startTransaction();
        }

        initSession();
        if (!session.exists(new IdRef(docId))) {
            setStatus("Nothing to process");
            return;
        }

        DocumentModel doc = session.getDocument(new IdRef(docId));

        Vision visionService = Framework.getService(Vision.class);
        String mapperChainName = visionService.getVideoMapperChainName();

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext octx = new OperationContext();
        octx.setInput(doc);
        octx.setCoreSession(session);
        OperationChain chain = new OperationChain("VideoChangedListenerChain");
        chain.add(mapperChainName);
        try {
            doc = (DocumentModel) as.run(octx, chain);
            session.saveDocument(doc);

            EventContextImpl evctx = new DocumentEventContext(session,
                    session.getPrincipal(), doc);
            Event eventToSend = evctx.newEvent(Vision.EVENT_VIDEO_HANDLED);
            EventService eventService = Framework.getLocalService(EventService.class);
            eventService.fireEvent(eventToSend);

        } catch (OperationException e) {
            log.warn(e);
        }

        setStatus("Done");
    }

    @Override
    public String getTitle() {
        return "VideoVisionWorker";
    }
}
