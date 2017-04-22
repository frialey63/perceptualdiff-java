/*
 * Metric
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
 * This interface defines the Yee image comparison operation and its result type.
 * @author Paul Parlett
 */
public interface Metric {

    /**
     * This class represents the result of an image comparison by the Yee algorithm.
     * @author Paul Parlett
     *
     */
    class ComparisonResult {
        final boolean passed;
        final int pixelsFailed;
        final double errorSum;
        final String reason;
        final RGBAImage imageDifference;

        ComparisonResult(boolean passed, String reason) {
            this(passed, -1, 0.0, reason, null);
        }

        ComparisonResult(boolean passed, int pixelsFailed, double errorSum, String reason, RGBAImage imageDifference) {
            super();
            this.passed = passed;
            this.pixelsFailed = pixelsFailed;
            this.errorSum = errorSum;
            this.reason = reason;
            this.imageDifference = imageDifference;
        }
    }

    /**
     * Image comparison metric using Yee's method.
     * References: A Perceptual Metric for Production Testing, Hector Yee, Journal of Graphics Tools 2004
     *
     * @param imageA The first image to compare
     * @param imageB The second image to compare
     * @param params The parameters for the comparison algorithm
     * @param imageDifference The difference between the two images as a new image
     * @return The result of the comparison as a ComparisonResult
     */
    ComparisonResult yeeCompare(RGBAImage imageA, RGBAImage imageB, PerceptualDiffParameters params, RGBAImage imageDifference);

}
