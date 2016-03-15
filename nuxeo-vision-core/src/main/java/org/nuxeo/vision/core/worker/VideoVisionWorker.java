package org.nuxeo.vision.core.worker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.nuxeo.vision.core.service.Vision;

/**
 * Created by Michaël on 3/10/2016.
 */
public class VideoVisionWorker extends AbstractWork {

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

        openSystemSession();
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
