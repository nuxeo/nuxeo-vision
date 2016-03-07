package org.nuxeo.labs.vision.core.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Created by Michaël on 3/7/2016.
 */

@XObject("configuration")
public class GoogleVisionDescriptor {

    @XNode("appName")
    protected String appName = "Nuxeo";

    @XNode("credentialFilePath")
    protected String credentialFilePath;

    public String getAppName() {
        return appName;
    }

    public String getCredentialFilePath() {
        return credentialFilePath;
    }
}
