/*
 * RGBAImage.h
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class RGBAImage {

    private final String name;

    private final BufferedImage bufferedImage;

    private final int width;

    private final int height;

    RGBAImage(File file) throws IOException {
        super();
        this.name = file.getName();
        this.bufferedImage = ImageIO.read(file);

        if ((bufferedImage == null) || (bufferedImage.getType() != BufferedImage.TYPE_4BYTE_ABGR)) {
            throw new IOException("failed to read image from file, unsupported image format");
        }

        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
    }

    RGBAImage(int width, int height, String name) {
        super();
        this.width = width;
        this.height = height;
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.name = name;
    }

    void dumpImage() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.printf("%9d", bufferedImage.getRGB(x, y));
            }
            System.out.printf("\n");
        }
    }

    int getRed(int i) {
        /*
         * In Java "new Color(get(i)).getRed()" is equivalent to "(get(i) >> 16) & 0xFF"
         * but to achieve same comparison result as perceptualdiff we instead do
         */
        return (get(i) >> 0) & 0xFF;
    }

    int getGreen(int i) {
        /*
         * In Java "new Color(get(i)).getGreen()" is equivalent to "(get(i) >> 8) & 0xFF"
         */
        return (get(i) >> 8) & 0xFF;
    }

    int getBlue(int i) {
        /*
         * In Java "new Color(get(i)).getBlue()" is equivalent to "(get(i) >> 0) & 0xFF"
         * but to achieve same comparison result as perceptualdiff we instead do
         */
        return (get(i) >> 16) & 0xFF;
    }

    int getAlpha(int i) {
        /*
         * In Java "new Color(get(i)).getAlpha()" is equivalent to "(get(i) >> 24) & 0xFF"
         */
        return (get(i) >> 24) & 0xff;
    }

    void set(int r, int g, int b, int a, int i) {
        Color c = new Color(r, g, b, a);

        int x = i % width;
        int y = i / width;

        bufferedImage.setRGB(x, y, c.getRGB());
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int get(int x, int y) {
        return bufferedImage.getRGB(x, y);
    }

    int get(int i) {
        int x = i % width;
        int y = i / width;

        return bufferedImage.getRGB(x, y);
    }

    String getName() {
        return name;
    }

    void writeToFile() throws IOException {
        ImageIO.write(bufferedImage, "PNG", new File(name + ".PNG"));
    }

}
