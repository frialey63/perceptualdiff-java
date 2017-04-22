/*
 * Perceptual Diff Parameters
 * Copyright (C) 2006-2011 Yangli Hector Yee
 * Copyright (C) 2011-2016 Steven Myint, Jeff Terrace
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdiff;

/**
 * Parameters for the Yee image comparison algorithm.
 * @author Paul Parlett
 */
public class PerceptualDiffParameters {

    /**
     * Only consider luminance; ignore chroma channels in the comparison.
     */
    boolean luminanceOnly;

    /**
     * Field of view in degrees.
     */
    double fieldOfView;

    /**
     * The gamma to convert to linear color space
     */
    double gamma;

    /**
     * Luminance
     */
    double luminance;

    /**
     *  How many pixels different to ignore.
     */
    int thresholdPixels;

    /**
     * How much color to use in the metric. 0.0 is the same as luminance_only_ = true, 1.0 means full strength.
     */
    double colorFactor;

    PerceptualDiffParameters() {
        luminanceOnly = false;
        fieldOfView = 45.0;
        gamma = 2.2;
        luminance = 100.0;
        thresholdPixels = 100;
        colorFactor = 1.0;
    }
}
