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
package org.nuxeo.vision.core.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.video.VideoConstants;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.vision.core.worker.VideoVisionWorker;

public class VideoStoryboardChangedListener implements EventListener {

    private static final Log log = LogFactory.getLog(VideoStoryboardChangedListener.class);

    @Override
    public void handleEvent(Event event) {
        EventContext ectx = event.getContext();
        if (!(ectx instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) ectx;
        DocumentModel doc = docCtx.getSourceDocument();
        if (!doc.hasFacet(VideoConstants.HAS_STORYBOARD_FACET) || doc.isProxy()) {
            return;
        }

        if (!doc.getProperty(VideoConstants.STORYBOARD_PROPERTY).isDirty()) {
            return;
        }

        VideoVisionWorker work = new VideoVisionWorker(doc.getRepositoryName(),
                doc.getId());
        WorkManager workManager = Framework.getLocalService(WorkManager.class);
        workManager.schedule(work, WorkManager.Scheduling.IF_NOT_SCHEDULED,
                true);
    }
}
