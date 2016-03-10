package org.nuxeo.labs.vision.core.image;

/**
 * Created by Michaël on 3/10/2016.
 */
public class TextEntity {

    /**
     * Entity textual description, expressed in its locale language.
     */
    protected String text;

    /**
     * Overall score of the result. Range [0, 1].
     */
    protected float score;

    /**
     * The language code for the locale in which the entity text is expressed.
     */
    protected String locale;

    public TextEntity(String text, float score, String locale) {
        this.text = text;
        this.score = score;
        this.locale = locale;
    }

    public String getText() {
        return text;
    }

    public float getScore() {
        return score;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return "TextEntity{" +
                "text='" + text + '\'' +
                ", score=" + score +
                ", locale='" + locale + '\'' +
                '}';
    }
}
