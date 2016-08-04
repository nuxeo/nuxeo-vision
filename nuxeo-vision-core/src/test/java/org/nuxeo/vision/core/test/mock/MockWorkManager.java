/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
            if (work instanceof VideoVisionWorker) {
                wasSchedule = true;
            }
        }
    }
}
