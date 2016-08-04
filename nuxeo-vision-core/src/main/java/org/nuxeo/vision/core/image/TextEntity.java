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
 */
package org.nuxeo.vision.core.image;

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
        return "TextEntity{" + "text='" + text + '\'' + ", score=" + score
                + ", locale='" + locale + '\'' + '}';
    }
}
