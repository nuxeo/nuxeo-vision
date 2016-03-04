package org.nuxeo.labs.vision.core;

/**
 * Created by Michaël on 3/4/2016.
 */
public enum FeatureType {

    FACE_DETECTION("FACE_DETECTION"),
    LANDMARK_DETECTION("LANDMARK_DETECTION"),
    LOGO_DETECTION("LANDMARK_DETECTION"),
    LABEL_DETECTION("LABEL_DETECTION"),
    TEXT_DETECTION("TEXT_DETECTION"),
    SAFE_SEARCH_DETECTION("AFE_SEARCH_DETECTION"),
    IMAGE_PROPERTIES("IMAGE_PROPERTIES");

    private final String text;


    FeatureType(final String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }
}
