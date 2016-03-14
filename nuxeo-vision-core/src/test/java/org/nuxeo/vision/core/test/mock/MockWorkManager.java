package org.nuxeo.vision.core.test.mock;

import org.nuxeo.ecm.core.work.WorkManagerImpl;
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.vision.core.worker.VideoVisionWorker;

/**
 * Created by Michaël on 3/10/2016.
 */
public class MockWorkManager extends WorkManagerImpl {

    public boolean wasSchedule = false;

    public boolean isActive = true;

    @Override
    public void schedule(Work work, Scheduling scheduling, boolean afterCommit) {
        if (isActive) {
            super.schedule(work, scheduling, afterCommit);
            if (work instanceof VideoVisionWorker) wasSchedule = true;
        }
    }
}
