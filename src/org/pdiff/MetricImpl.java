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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jafama.FastMath;

/**
 * Default implementation of the Metric interface.
 * @author Paul Parlett
 */
public final class MetricImpl implements Metric {

    private static final Logger LOGGER = Logger.getLogger(MetricImpl.class.getName());

    private static int adaptation(double numOneDegreePixels) {
        double numPixels = 1.0;
        int adaptationLevel = 0;

        for (int i = 0; i < LPyramid.MAX_PYR_LEVELS; i++) {
            adaptationLevel = i;
            if (numPixels > numOneDegreePixels) {
                break;
            }
            numPixels *= 2;
        }

        return adaptationLevel;
    }

    /*
     * Computes the contrast sensitivity function (Barten SPIE 1989) given the
     * cycles per degree (cpd) and luminance (lum).
     */
    private static double csf(double cpd, double lum) {
        double a = 440.0 * FastMath.pow((1.0 + 0.7 / lum), -0.2);
        double b = 0.3 * FastMath.pow((1.0 + 100.0 / lum), 0.15);

        return a * cpd * FastMath.exp(-b * cpd) * FastMath.sqrt(1.0 + 0.06 * FastMath.exp(b * cpd));
    }

    /*
     * Visual Masking Function from Daly 1993.
     */
    private static double mask(double contrast) {
        double a = FastMath.pow(392.498 * contrast, 0.7);
        double b = FastMath.pow(0.0153 * a, 4.0);
        return FastMath.pow(1.0 + b, 0.25);
    }

    /*
     * Given the adaptation luminance, this function returns the threshold of
     * visibility in cd per m^2.
     *
     * TVI means Threshold vs Intensity function. This version comes from Ward
     * Larson Siggraph 1997.
     *
     * Returns the threshold luminance given the adaptation luminance. Units are
     * candelas per meter squared.
     */
    private static double tvi(double adaptationLuminance) {
        double logA = FastMath.log10(adaptationLuminance);

        double r;
        if (logA < -3.94) {
            r = -2.86;
        } else if (logA < -1.44) {
            r = FastMath.pow(0.405 * logA + 1.6, 2.18) - 2.86;
        } else if (logA < -0.0184) {
            r = logA - 0.395;
        } else if (logA < 1.9) {
            r = FastMath.pow(0.249 * logA + 0.65, 2.7) - 0.72;
        } else {
            r = logA - 1.255;
        }

        return FastMath.pow(10.0, r);
    }

    @Override
    public ComparisonResult yeeCompare(
            RGBAImage imageA, RGBAImage imageB, PerceptualDiffParameters params, RGBAImage imageDifference) {

        if ((imageA.getWidth() != imageB.getWidth()) || (imageA.getHeight() != imageB.getHeight())) {
            return new ComparisonResult(false, "Image dimensions do not match");
        }

        int w = imageA.getWidth();
        int h = imageA.getHeight();
        int dim = w * h;

        if (LOGGER.isLoggable(Level.FINEST)) {
            imageA.dumpImage();
            imageB.dumpImage();
        }

        boolean identical = true;

        for (int i = 0; i < dim; i++) {
            if (imageA.get(i) != imageB.get(i)) {
                identical = false;
                break;
            }
        }

        if (identical) {
            return new ComparisonResult(true, "Images are binary identical");
        }

        /*
         *  Assuming colorspaces are in Adobe RGB (1998) convert to XYZ.
         */

        LOGGER.log(Level.INFO, "Converting RGB to XYZ");

        ColourSpace colourSpace = new ColourSpace(imageA, imageB, params.gamma, params.luminance);

        if (LOGGER.isLoggable(Level.FINEST)) {
            colourSpace.dump();
        }

        LOGGER.log(Level.INFO, "Constructing Laplacian Pyramids");

        LPyramid la = new LPyramid(colourSpace.aLum, w, h);
        LPyramid lb = new LPyramid(colourSpace.bLum, w, h);

        if (LOGGER.isLoggable(Level.FINEST)) {
            la.dump();
            lb.dump();
        }

        double numOneDegreePixels = (double) Math.toDegrees(2 * Math.tan(params.fieldOfView * Math.toRadians(0.5)));
        double pixelsPerDegree = w / numOneDegreePixels;

        LOGGER.log(Level.INFO, "Performing test");

        long startMillis = System.currentTimeMillis();

        int adaptationLevel = adaptation(numOneDegreePixels);

        double[] cpd = new double[LPyramid.MAX_PYR_LEVELS];
        cpd[0] = 0.5 * pixelsPerDegree;

        for (int i = 1; i < LPyramid.MAX_PYR_LEVELS; i++) {
            cpd[i] = 0.5 * cpd[i - 1];
        }

        double csfMax = csf(3.248, 100.0);

        // assert (LPyramid.MAX_PYR_LEVELS > 2) : "MAX_PYR_LEVELS must be greater than 2";

        double[] fFreq = new double[LPyramid.MAX_PYR_LEVELS - 2];

        for (int i = 0; i < LPyramid.MAX_PYR_LEVELS - 2; i++) {
            fFreq[i] = csfMax / csf(cpd[i], 100.0);
        }

        int pixelsFailed = 0;
        double errorSum = 0.0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;

                double adapt = Math.max((la.getValue(x, y, adaptationLevel) + lb.getValue(x, y, adaptationLevel)) * 0.5, 1e-5);

                double sumContrast = 0.0;
                double factor = 0.0;

                for (int i = 0; i < LPyramid.MAX_PYR_LEVELS - 2; i++) {
                    double n1 = Math.abs(la.getValue(x, y, i) - la.getValue(x, y, i + 1));
                    double n2 = Math.abs(lb.getValue(x, y, i) - lb.getValue(x, y, i + 1));

                    double numerator = Math.max(n1, n2);

                    double d1 = Math.abs(la.getValue(x, y, i + 2));
                    double d2 = Math.abs(lb.getValue(x, y, i + 2));

                    double denominator = Math.max(Math.max(d1, d2), 1e-5);
                    double contrast = numerator / denominator;
                    double fMask = mask(contrast * csf(cpd[i], adapt));

                    factor += contrast * fFreq[i] * fMask;
                    sumContrast += contrast;
                }

                sumContrast = Math.max(sumContrast, 1e-5);
                factor /= sumContrast;
                factor = Math.min(Math.max(factor, 1.0), 10.0);

                double delta = Math.abs(la.getValue(x, y, 0) - lb.getValue(x, y, 0));

                errorSum += delta;

                boolean pass = true;

                // Pure luminance test.
                if (delta > factor * tvi(adapt)) {
                    pass = false;
                }

                if (!params.luminanceOnly) {
                    // CIE delta E test with modifications
                    double colorScale = params.colorFactor;

                    // Ramp down the color test in scotopic regions
                    if (adapt < 10.0) {
                        // Don't do color test at all
                        colorScale = 0.0;
                    }

                    double da = colourSpace.aA[index] - colourSpace.bA[index];
                    double db = colourSpace.aB[index] - colourSpace.bB[index];
                    double deltaE = (da * da + db * db) * colorScale;

                    errorSum += deltaE;

                    if (deltaE > factor) {
                        pass = false;
                    }
                }

                if (pass) {
                    if (imageDifference != null) {
                        imageDifference.set(0, 0, 0, 255, index);
                    }
                } else {
                    pixelsFailed++;

                    if (imageDifference != null) {
                        imageDifference.set(255, 0, 0, 255, index);
                    }
                }
            }
        }

        LOGGER.log(Level.FINE, "elapsedMillis = " + (System.currentTimeMillis() - startMillis));

        String different = pixelsFailed + " pixels are different";

        boolean passed = pixelsFailed < params.thresholdPixels;

        String reason = passed ? ("Images are perceptually indistinguishable\n" + different)
                : ("Images are visibly different\n" + different);

        return new ComparisonResult(passed, pixelsFailed, errorSum, reason, imageDifference);
    }

}
