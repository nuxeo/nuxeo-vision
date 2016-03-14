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

import java.awt.*;

public class ColorInfo {


    private Color color;

    /**
     * Stores the fraction of pixels the color occupies in the image. Value in range [0, 1].
     */
    private float pixelFraction;

    /**
     * Image-specific score for this color. Value in range [0, 1].
     */
    private float score;


    public ColorInfo(Color color, float pixelFraction, float score) {
        this.color = color;
        this.pixelFraction = pixelFraction;
        this.score = score;
    }

    public Color getColor() {
        return color;
    }

    public float getPixelFraction() {
        return pixelFraction;
    }

    public float getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ColorInfo{" +
                "color=" + color +
                ", pixelFraction=" + pixelFraction +
                ", score=" + score +
                '}';
    }
}
