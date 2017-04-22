/*
 * Compare Args
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

import java.io.File;
import java.io.IOException;

/*
 * Arguments to pass into the comparison function.
 *
 * NB Command line option processing not implemented because this Java port is assumed to only be of use as a library.
 */
class CompareArgs {
    final RGBAImage imageA;

    final RGBAImage imageB;

    final RGBAImage imageDifference;

    final boolean verbose;

    final boolean sumErrors;    // Print a sum of the luminance and color differences of each pixel

    final PerceptualDiffParameters params = new PerceptualDiffParameters();

    CompareArgs(String[] mainArgs) throws IOException {
        imageA = new RGBAImage(new File(mainArgs[0]));
        imageB = new RGBAImage(new File(mainArgs[1]));
        imageDifference = new RGBAImage(imageA.getWidth(), imageA.getHeight(), "image_difference");

        verbose = false;
        sumErrors = false;
    }

    void printArgs() {
        System.out.println("Field of view is " + params.fieldOfView + " degrees");
        System.out.println("Threshold pixels is " + params.thresholdPixels + " pixels");
        System.out.println("The gamma is " + params.gamma);
        System.out.println("The display's luminance is " + params.luminance + " candela per meter squared");
    }
}
