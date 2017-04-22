package org.pdiff;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jafama.FastMath;

class ColourSpace {

    private static class XYZ {
        double x;
        double y;
        double z;
    }

    /*
     * Convert Adobe RGB (1998) with reference white D65 to XYZ.
     */
    private static XYZ adobeRgbToXyz(double r, double g, double b) {
        final XYZ result = new XYZ();

        // matrix is from http://www.brucelindbloom.com/
        result.x = r * 0.576700 + g * 0.185556 + b * 0.188212;
        result.y = r * 0.297361 + g * 0.627355 + b * 0.0752847;
        result.z = r * 0.0270328 + g * 0.0706879 + b * 0.991248;

        return result;
    }

    private static class White {
        final XYZ xyz;

        White() {
            xyz = adobeRgbToXyz(1.0, 1.0, 1.0);
        }
    }

    private static final White GLOBAL_WHITE = new White();

    private static class LAB {
        @SuppressWarnings("unused") double l;
        double a;
        double b;
    }

    private static final double EPSILON = 216.0 / 24389.0;

    private static final double KAPPA = 24389.0 / 27.0;

    /*
     * Convert XYZ to LAB.
     */
    private static LAB xyzToLab(double x, double y, double z) {
        LAB result = new LAB();

        double[] r = {x / GLOBAL_WHITE.xyz.x, y / GLOBAL_WHITE.xyz.y, z / GLOBAL_WHITE.xyz.z };

        double[] f = new double[3];

        for (int i = 0; i < 3; i++) {
            if (r[i] > EPSILON) {
                f[i] = FastMath.pow(r[i], 1.0 / 3.0);
            } else {
                f[i] = (KAPPA * r[i] + 16.0) / 116.0;
            }
        }

        result.l = 116.0 * f[1] - 16.0;
        result.a = 500.0 * (f[0] - f[1]);
        result.b = 200.0 * (f[1] - f[2]);

        return result;
    }

    private static final Logger LOGGER = Logger.getLogger(ColourSpace.class.getName());

    final float[] aLum;
    final float[] bLum;

    final float[] aA;
    final float[] bA;
    final float[] aB;
    final float[] bB;

    ColourSpace(RGBAImage imageA, RGBAImage imageB, double gamma, double luminance) {
        int w = imageA.getWidth();
        int h = imageA.getHeight();
        int dim = w * h;

        long startMillis = System.currentTimeMillis();

        aLum = new float[dim];
        bLum = new float[dim];

        aA = new float[dim];
        bA = new float[dim];
        aB = new float[dim];
        bB = new float[dim];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;

                /*
                 * perceptualdiff used to use premultiplied alphas when loading
                 * the image. This is no longer the case since the switch to
                 * FreeImage. We need to do the multiplication here now. As was
                 * the case with premultiplied alphas, differences in alphas
                 * won't be detected where the color is black.
                 */

                double aAlpha = imageA.getAlpha(i) / 255.0;
                double aColorR = FastMath.pow(imageA.getRed(i) / 255.0 * aAlpha, gamma);
                double aColorG = FastMath.pow(imageA.getGreen(i) / 255.0 * aAlpha, gamma);
                double aColorB = FastMath.pow(imageA.getBlue(i) / 255.0 * aAlpha, gamma);

                XYZ a = adobeRgbToXyz(aColorR, aColorG, aColorB);
                LAB lab = xyzToLab(a.x, a.y, a.z);

                aA[i] = (float) lab.a;
                aB[i] = (float) lab.b;

                double bAlpha = imageB.getAlpha(i) / 255.0;
                double bColorR = FastMath.pow(imageB.getRed(i) / 255.0 * bAlpha, gamma);
                double bColorG = FastMath.pow(imageB.getGreen(i) / 255.0 * bAlpha, gamma);
                double bColorB = FastMath.pow(imageB.getBlue(i) / 255.0 * bAlpha, gamma);

                XYZ b = adobeRgbToXyz(bColorR, bColorG, bColorB);
                lab = xyzToLab(b.x, b.y, b.z);

                bA[i] = (float) lab.a;
                bB[i] = (float) lab.b;

                aLum[i] = (float) (a.y * luminance);
                bLum[i] = (float) (b.y * luminance);
            }
        }

        LOGGER.log(Level.FINE, "elapsedMillis = " + (System.currentTimeMillis() - startMillis));
    }

    void dump() {
        dumpArray(aLum);
        dumpArray(bLum);
        dumpArray(aA);
        dumpArray(bA);
        dumpArray(aB);
        dumpArray(bB);
    }

    private static void dumpArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf("%11f", array[i]);

            if ((i + 1) % 10 == 0) {
                System.out.printf("\n");
            }
        }

        System.out.printf("\n");
    }
}
