package org.nuxeo.labs.core.service;

import org.nuxeo.ecm.core.api.Blob;

import java.util.List;

public interface GoogleVision {

    /**
     *
     * @param blob the image blob
     * @return the list of labels returned by the API
     */
    List<String> getLabels(Blob blob);

    /**
     *
     * @param blob the image blob
     * @return the text extracted by the API
     */
    String getText(Blob blob);
}
