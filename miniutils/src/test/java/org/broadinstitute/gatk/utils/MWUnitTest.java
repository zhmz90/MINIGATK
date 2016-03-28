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

import org.broadinstitute.gatk.utils.BaseTest;
import org.broadinstitute.gatk.utils.collections.Pair;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: Ghost
 * Date: 3/5/11
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MWUnitTest extends BaseTest {
    @BeforeClass
    public void init() { }

    @Test
    private void testMWU() {
        logger.warn("Testing MWU");
        MannWhitneyU mwu = new MannWhitneyU();
        mwu.add(0, MannWhitneyU.USet.SET1);
        mwu.add(1,MannWhitneyU.USet.SET2);
        mwu.add(2,MannWhitneyU.USet.SET2);
        mwu.add(3,MannWhitneyU.USet.SET2);
        mwu.add(4,MannWhitneyU.USet.SET2);
        mwu.add(5,MannWhitneyU.USet.SET2);
        mwu.add(6,MannWhitneyU.USet.SET1);
        mwu.add(7,MannWhitneyU.USet.SET1);
        mwu.add(8,MannWhitneyU.USet.SET1);
        mwu.add(9,MannWhitneyU.USet.SET1);
        mwu.add(10,MannWhitneyU.USet.SET1);
        mwu.add(11,MannWhitneyU.USet.SET2);
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwu.getObservations(), MannWhitneyU.USet.SET1),25L);
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwu.getObservations(),MannWhitneyU.USet.SET2),11L);

        MannWhitneyU mwu2 = new MannWhitneyU();
        MannWhitneyU mwuNoDither = new MannWhitneyU(false);
        for ( int dp : new int[]{2,4,5,6,8} ) {
            mwu2.add(dp,MannWhitneyU.USet.SET1);
            mwuNoDither.add(dp,MannWhitneyU.USet.SET1);
        }

        for ( int dp : new int[]{1,3,7,9,10,11,12,13} ) {
            mwu2.add(dp,MannWhitneyU.USet.SET2);
            mwuNoDither.add(dp,MannWhitneyU.USet.SET2);
        }

        MannWhitneyU.ExactMode pm = MannWhitneyU.ExactMode.POINT;
        MannWhitneyU.ExactMode cm = MannWhitneyU.ExactMode.CUMULATIVE;

        // tests using the hypothesis that set 2 dominates set 1 (U value = 10)
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwu2.getObservations(),MannWhitneyU.USet.SET1),10L);
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwu2.getObservations(),MannWhitneyU.USet.SET2),30L);
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwuNoDither.getObservations(),MannWhitneyU.USet.SET1),10L);
        Assert.assertEquals(MannWhitneyU.calculateOneSidedU(mwuNoDither.getObservations(),MannWhitneyU.USet.SET2),30L);

        Pair<Integer,Integer> sizes = mwu2.getSetSizes();

        Assert.assertEquals(MannWhitneyU.calculatePUniformApproximation(sizes.first,sizes.second,10L),0.4180519701814064,1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.first,sizes.second,10L,false,pm).second,0.021756021756021756,1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePNormalApproximation(sizes.first,sizes.second,10L,false).second,0.06214143703127617,1e-14);
        logger.warn("Testing two-sided");
        Assert.assertEquals((double)mwu2.runTwoSidedTest().second,2*0.021756021756021756,1e-8);

        // tests using the hypothesis that set 1 dominates set 2 (U value = 30) -- empirical should be identical, normall approx close, uniform way off
        Assert.assertEquals(MannWhitneyU.calculatePNormalApproximation(sizes.second,sizes.first,30L,true).second,2.0*0.08216463976903321,1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePUniformApproximation(sizes.second,sizes.first,30L),0.0023473625009559074,1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,30L,false,pm).second,0.021756021756021756,1e-14); // note -- exactly same value as above
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,29L,false,cm).second,1.0-0.08547008547008,1e-14); // r does a correction, subtracting 1 from U
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,11L,false,cm).second,0.08547008547008,1e-14); // r does a correction, subtracting 1 from U
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,11L,false,cm).first,-1.36918910442,1e-2); // apache inversion set to be good only to 1e-2
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,29L,false,cm).first,1.36918910442,1e-2); // apache inversion set to be good only to 1e-2
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,29L,false,pm).first,1.2558754796642067,1e-8); // PDF should be similar
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(sizes.second,sizes.first,11L,false,pm).first,-1.2558754796642067,1e-8); // PDF should be similar
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(4,5,10L,false,pm).second,0.0952381,1e-5);
        Assert.assertEquals(MannWhitneyU.calculatePRecursively(4,5,10L,false,pm).first,0.0,1e-14);

        logger.warn("Set 1");
        Assert.assertEquals((double)mwu2.runOneSidedTest(MannWhitneyU.USet.SET1).second,0.021756021756021756,1e-8);
        logger.warn("Set 2");
        Assert.assertEquals((double)mwu2.runOneSidedTest(MannWhitneyU.USet.SET2).second,0.021756021756021756,1e-8);

        MannWhitneyU mwu3 = new MannWhitneyU();
        for ( int dp : new int[]{0,2,4} ) {
            mwu3.add(dp,MannWhitneyU.USet.SET1);
        }
        for ( int dp : new int[]{1,5,6,7,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34} ) {
            mwu3.add(dp,MannWhitneyU.USet.SET2);
        }
        long u = MannWhitneyU.calculateOneSidedU(mwu3.getObservations(),MannWhitneyU.USet.SET1);
        //logger.warn(String.format("U is: %d",u));
        Pair<Integer,Integer> nums = mwu3.getSetSizes();
        //logger.warn(String.format("Corrected p is: %.4e",MannWhitneyU.calculatePRecursivelyDoNotCheckValuesEvenThoughItIsSlow(nums.first,nums.second,u)));
        //logger.warn(String.format("Counted sequences: %d",MannWhitneyU.countSequences(nums.first, nums.second, u)));
        //logger.warn(String.format("Possible sequences: %d", (long) Arithmetic.binomial(nums.first+nums.second,nums.first)));
        //logger.warn(String.format("Ratio: %.4e",MannWhitneyU.countSequences(nums.first,nums.second,u)/Arithmetic.binomial(nums.first+nums.second,nums.first)));
        Assert.assertEquals(MannWhitneyU.calculatePRecursivelyDoNotCheckValuesEvenThoughItIsSlow(nums.first, nums.second, u), 3.665689149560116E-4, 1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePNormalApproximation(nums.first,nums.second,u,false).second,0.0032240865760884696,1e-14);
        Assert.assertEquals(MannWhitneyU.calculatePUniformApproximation(nums.first,nums.second,u),0.0026195003025784036,1e-14);

    }
}
