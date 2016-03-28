/*
* Copyright 2012-2015 Broad Institute, Inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
* THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.broadinstitute.gatk.utils;


// the imports for unit testing.


import org.broadinstitute.gatk.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MedianUnitTest extends BaseTest {

    // --------------------------------------------------------------------------------
    //
    // Provider
    //
    // --------------------------------------------------------------------------------

    private class MedianTestProvider extends TestDataProvider {
        final List<Integer> values = new ArrayList<Integer>();
        final int cap;
        final Integer expected;

        public MedianTestProvider(int expected, int cap, Integer ... values) {
            super(MedianTestProvider.class);
            this.expected = expected;
            this.cap = cap;
            this.values.addAll(Arrays.asList(values));
            this.name = String.format("values=%s expected=%d cap=%d", this.values, expected, cap);
        }
    }

    @DataProvider(name = "MedianTestProvider")
    public Object[][] makeMedianTestProvider() {
        new MedianTestProvider(1, 1000, 0, 1, 2);
        new MedianTestProvider(1, 1000, 1, 0, 1, 2);
        new MedianTestProvider(1, 1000, 0, 1, 2, 3);
        new MedianTestProvider(2, 1000, 0, 1, 2, 3, 4);
        new MedianTestProvider(2, 1000, 4, 1, 2, 3, 0);
        new MedianTestProvider(1, 1000, 1);
        new MedianTestProvider(2, 1000, 2);
        new MedianTestProvider(1, 1000, 1, 2);

        new MedianTestProvider(1, 3, 1);
        new MedianTestProvider(1, 3, 1, 2);
        new MedianTestProvider(2, 3, 1, 2, 3);
        new MedianTestProvider(2, 3, 1, 2, 3, 4);
        new MedianTestProvider(2, 3, 1, 2, 3, 4, 5);

        new MedianTestProvider(1, 3, 1);
        new MedianTestProvider(1, 3, 1, 2);
        new MedianTestProvider(2, 3, 3, 2, 1);
        new MedianTestProvider(3, 3, 4, 3, 2, 1);
        new MedianTestProvider(4, 3, 5, 4, 3, 2, 1);

        return MedianTestProvider.getTests(MedianTestProvider.class);
    }

    @Test(dataProvider = "MedianTestProvider")
    public void testBasicLikelihoods(MedianTestProvider cfg) {
        final Median<Integer> median = new Median<Integer>(cfg.cap);

        int nAdded = 0;
        for ( final int value : cfg.values )
            if ( median.add(value) )
                nAdded++;

        Assert.assertEquals(nAdded, median.size());

        Assert.assertEquals(cfg.values.isEmpty(), median.isEmpty());
        Assert.assertEquals(cfg.values.size() >= cfg.cap, median.isFull());
        Assert.assertEquals(median.getMedian(), cfg.expected, cfg.toString());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testEmptyMedian() {
        final Median<Integer> median = new Median<Integer>();
        Assert.assertTrue(median.isEmpty());
        final Integer d = 100;
        Assert.assertEquals(median.getMedian(d), d);
        median.getMedian();
    }

}