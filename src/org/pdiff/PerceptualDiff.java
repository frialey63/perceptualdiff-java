/*
 * PerceptualDiff - a program that compares two images using a perceptual metric based on the paper :
 * A perceptual metric for production testing. Journal of graphics tools, 9(4):33-40, 2004, Hector Yee
 *
 * Copyright (C) 2006-2011 Yangli Hector Yee
 * Copyright (C) 2011-2016 Steven Myint, Jeff Terrace

 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdiff;

import java.io.IOException;

/**
 * The main class for the PerceptualDiff program.
 * @author Paul Parlett
 */
public final class PerceptualDiff {

    private static final int EXIT_SUCCESS = 0;

    private static final int EXIT_FAILURE = 1;

    /**
     * The main method through which the program may be invoked.
     * @param mainArgs The program arguments
     * @throws IOException Thrown if there is an error reading or writing an image file
     */
    public static void main(String[] mainArgs) throws IOException {
        CompareArgs args = new CompareArgs(mainArgs);

        if (args.verbose) {
            args.printArgs();
        }

        long startMillis = System.currentTimeMillis();

        Metric.ComparisonResult result =
                new MetricImpl().yeeCompare(args.imageA, args.imageB, args.params, args.imageDifference);

        System.out.println("elapsedMillis = " + (System.currentTimeMillis() - startMillis));

        if (result.passed) {
            if (args.verbose) {
                System.out.println("PASS: " + result.reason);
            }
        } else {
            System.out.println("FAIL: " + result.reason);
        }

        if (args.sumErrors) {
            double normalized = result.errorSum / (args.imageA.getWidth() * args.imageA.getHeight() * 255.f);

            System.out.println(result.errorSum + " error sum");
            System.out.println(normalized + " normalzied error sum");
        }

        if (args.imageDifference != null) {
            args.imageDifference.writeToFile();
            System.err.println("Wrote difference image to " + args.imageDifference.getName());
        }

        System.exit(result.passed ? EXIT_SUCCESS : EXIT_FAILURE);
    }

    private PerceptualDiff() {
        // prevent instantiation
    }
}
