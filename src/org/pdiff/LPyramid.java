/*
 * Laplacian Pyramid
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class LPyramid {

    private static final Logger LOGGER = Logger.getLogger(LPyramid.class.getName());

    private static List<float[]> createList(int size) {
        List<float[]> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            result.add(null);
        }

        return result;
    }

    static final int MAX_PYR_LEVELS = 8;

    private int width;

    private int height;

    // Successively blurred versions of the original image
    private List<float[]> levels = createList(MAX_PYR_LEVELS);

    LPyramid(float[] image, int width, int height) {
        this.width = width;
        this.height = height;

        long startMillis = System.currentTimeMillis();

        // Make the Laplacian pyramid by successively copying the earlier levels and blurring them
        for (int i = 0; i < MAX_PYR_LEVELS; i++) {
            if (i == 0 || width * height <= 1) {
                levels.set(i, image);
            } else {
                levels.set(i, new float[width * height]);
                convolve(levels.get(i), levels.get(i - 1));
            }
        }

        LOGGER.log(Level.FINE, "elapsedMillis = " + (System.currentTimeMillis() - startMillis));
    }

    void dump() {
        for (int level = 0; level < MAX_PYR_LEVELS; level++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    System.out.printf("%11f", getValue(x, y, level));
                }
                System.out.printf("\n");
            }
            System.out.printf("\n");
        }

        System.out.printf("\n");
    }

    double getValue(int x, int y, int level) {
        int index = x + y * width;

        assert (level < MAX_PYR_LEVELS);

        return levels.get(level)[index];
    }

    /*
     *  Convolves image b with the filter kernel and stores it in a.
     */
    private void convolve(float[] a, float[] b) {
        assert (a.length > 1);
        assert (b.length > 1);

        double[] kernel = {0.05, 0.25, 0.4, 0.25, 0.05 };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                double result = 0.0f;

                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        int nx = x + i;
                        int ny = y + j;

                        nx = Math.max(nx, -nx);
                        ny = Math.max(ny, -ny);

                        if (nx >= width) {
                            nx = 2 * width - nx - 1;
                        }

                        if (ny >= height) {
                            ny = 2 * height - ny - 1;
                        }

                        result += kernel[i + 2] * kernel[j + 2] * b[ny * width + nx];
                    }
                }

                a[index] = (float) result;
            }
        }
    }

}
