package org.pdiff;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pdiff.MetricImpl;
import org.pdiff.PerceptualDiffParameters;
import org.pdiff.RGBAImage;
import org.pdiff.Metric.ComparisonResult;

public class MetricImplTest {

    @Test
    public void yeeCompareAlpha() throws IOException {
        MetricImpl impl = new MetricImpl();

        RGBAImage imageA = new RGBAImage(new File("data/alpha1.png"));

        RGBAImage imageB = new RGBAImage(new File("data/alpha2.png"));

        ComparisonResult result = impl.yeeCompare(imageA, imageB, new PerceptualDiffParameters(), null);

        assertEquals(1278, result.pixelsFailed);
    }

    @Test
    public void yeeCompareFish() throws IOException {
        MetricImpl impl = new MetricImpl();

        RGBAImage imageA = new RGBAImage(new File("data/fish1.png"));

        RGBAImage imageB = new RGBAImage(new File("data/fish2.png"));

        ComparisonResult result = impl.yeeCompare(imageA, imageB, new PerceptualDiffParameters(), null);

        assertEquals(20109, result.pixelsFailed);
    }

    @Test
    public void yeeCompareAqsisVase() throws IOException {
        MetricImpl impl = new MetricImpl();

        RGBAImage imageA = new RGBAImage(new File("data/Aqsis_vase_ref.png"));

        RGBAImage imageB = new RGBAImage(new File("data/Aqsis_vase.png"));

        ComparisonResult result = impl.yeeCompare(imageA, imageB, new PerceptualDiffParameters(), null);

        assertEquals(104, result.pixelsFailed);
    }

}
