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

package org.broadinstitute.gatk.utils.haplotype;

import htsjdk.samtools.TextCigarCodec;
import org.broadinstitute.gatk.utils.BaseTest;
import org.broadinstitute.gatk.utils.GenomeLoc;
import org.broadinstitute.gatk.utils.UnvalidatingGenomeLoc;
import org.broadinstitute.gatk.utils.Utils;
import org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils;
import htsjdk.variant.variantcontext.VariantContext;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

public class EventMapUnitTest extends BaseTest {
    private final static String CHR = "20";
    private final static String NAME = "foo";
    
    @DataProvider(name = "MyDataProvider")
         public Object[][] makeMyDataProvider() {
        List<Object[]> tests = new ArrayList<Object[]>();

        final List<String> SNP_ALLELES = Arrays.asList("A", "C");
        final List<String> INS_ALLELES = Arrays.asList("A", "ACGTGA");
        final List<String> DEL_ALLELES = Arrays.asList("ACGTA", "C");
        final List<List<String>> allAlleles = Arrays.asList(SNP_ALLELES, INS_ALLELES, DEL_ALLELES);
        for ( final int leftNotClump : Arrays.asList(-1, 3) ) {
            for ( final int middleNotClump : Arrays.asList(-1, 10, 500) ) {
                for ( final int rightNotClump : Arrays.asList(-1, 1000) ) {
                    for ( final int nClumped : Arrays.asList(3, 4) ) {
                        for ( final List<List<String>> alleles : Utils.makePermutations(allAlleles, nClumped, true)) {
                            final List<VariantContext> allVCS = new LinkedList<VariantContext>();

                            if ( leftNotClump != -1 ) allVCS.add(GATKVariantContextUtils.makeFromAlleles(NAME, CHR, leftNotClump, SNP_ALLELES));
                            if ( middleNotClump != -1 ) allVCS.add(GATKVariantContextUtils.makeFromAlleles(NAME, CHR, middleNotClump, SNP_ALLELES));
                            if ( rightNotClump != -1 ) allVCS.add(GATKVariantContextUtils.makeFromAlleles(NAME, CHR, rightNotClump, SNP_ALLELES));

                            int clumpStart = 50;
                            final List<VariantContext> vcs = new LinkedList<VariantContext>();
                            for ( final List<String> myAlleles : alleles ) {
                                final VariantContext vc = GATKVariantContextUtils.makeFromAlleles(NAME, CHR, clumpStart, myAlleles);
                                clumpStart = vc.getEnd() + 3;
                                vcs.add(vc);
                            }

                            tests.add(new Object[]{new EventMap(new LinkedList<VariantContext>(allVCS)), Collections.emptyList()});
                            allVCS.addAll(vcs);
                            tests.add(new Object[]{new EventMap(allVCS), vcs});
                        }
                    }
                }
            }
        }

        return tests.toArray(new Object[][]{});
    }

    /**
     * Example testng test using MyDataProvider
     */
    @Test(dataProvider = "MyDataProvider", enabled = true)
    public void testGetNeighborhood(final EventMap eventMap, final List<VariantContext> expectedNeighbors) {
        final VariantContext leftOfNeighors = expectedNeighbors.isEmpty() ? null : expectedNeighbors.get(0);

        for ( final VariantContext vc : eventMap.getVariantContexts() ) {
            final List<VariantContext> n = eventMap.getNeighborhood(vc, 5);
            if ( leftOfNeighors == vc )
                Assert.assertEquals(n, expectedNeighbors);
            else if ( ! expectedNeighbors.contains(vc) )
                Assert.assertEquals(n, Collections.singletonList(vc), "Should only contain the original vc but " + n);
        }
    }

    @DataProvider(name = "BlockSubstitutionsData")
    public Object[][] makeBlockSubstitutionsData() {
        List<Object[]> tests = new ArrayList<Object[]>();

        for ( int size = EventMap.MIN_NUMBER_OF_EVENTS_TO_COMBINE_INTO_BLOCK_SUBSTITUTION; size < 10; size++ ) {
            final String ref = Utils.dupString("A", size);
            final String alt = Utils.dupString("C", size);
            tests.add(new Object[]{ref, alt, size + "M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1, Arrays.asList(ref, alt))});
        }

        tests.add(new Object[]{"AAAAAA", "GAGAGA", "6M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1, Arrays.asList("AAAAA", "GAGAG"))});
        tests.add(new Object[]{"AAAAAA", "GAGAGG", "6M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1, Arrays.asList("AAAAAA", "GAGAGG"))});

        for ( int len = 0; len < 10; len++ ) {
            final String s = len == 0 ? "" : Utils.dupString("A", len);
            tests.add(new Object[]{s + "AACCCCAA", s + "GAAG", len + 2 + "M4D2M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1 + len,   Arrays.asList("AACCCCAA", "GAAG"))});
            tests.add(new Object[]{s + "AAAA", s + "GACCCCAG", len + 2 + "M4I2M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1 + len, Arrays.asList("AAAA", "GACCCCAG"))});

            tests.add(new Object[]{"AACCCCAA" + s, "GAAG" + s, "2M4D" + (len + 2) + "M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1,   Arrays.asList("AACCCCAA", "GAAG"))});
            tests.add(new Object[]{"AAAA" + s, "GACCCCAG" + s, "2M4I" + (len + 2) + "M", GATKVariantContextUtils.makeFromAlleles(NAME, CHR, 1, Arrays.asList("AAAA", "GACCCCAG"))});
        }

        return tests.toArray(new Object[][]{});
    }

    /**
     * Example testng test using MyDataProvider
     */
    @Test(dataProvider = "BlockSubstitutionsData")
    public void testBlockSubstitutionsData(final String refBases, final String haplotypeBases, final String cigar, final VariantContext expectedBlock) {
        final Haplotype hap = new Haplotype(haplotypeBases.getBytes(), false, 0, TextCigarCodec.decode(cigar));
        final GenomeLoc loc = new UnvalidatingGenomeLoc(CHR, 0, 1, refBases.length());
        final EventMap ee = new EventMap(hap, refBases.getBytes(), loc, NAME);
        ee.replaceClumpedEventsWithBlockSubstitutions();
        Assert.assertEquals(ee.getNumberOfEvents(), 1);
        final VariantContext actual = ee.getVariantContexts().iterator().next();
        Assert.assertTrue(GATKVariantContextUtils.equalSites(actual, expectedBlock), "Failed with " + actual);
    }

    @DataProvider(name = "AdjacentSNPIndelTest")
    public Object[][] makeAdjacentSNPIndelTest() {
        List<Object[]> tests = new ArrayList<Object[]>();

        tests.add(new Object[]{"TT", "GCT", "1M1I1M", Arrays.asList(Arrays.asList("T", "GC"))});
        tests.add(new Object[]{"GCT", "TT", "1M1D1M", Arrays.asList(Arrays.asList("GC", "T"))});
        tests.add(new Object[]{"TT", "GCCT", "1M2I1M", Arrays.asList(Arrays.asList("T", "GCC"))});
        tests.add(new Object[]{"GCCT", "TT", "1M2D1M", Arrays.asList(Arrays.asList("GCC", "T"))});
        tests.add(new Object[]{"AAGCCT", "AATT", "3M2D1M", Arrays.asList(Arrays.asList("GCC", "T"))});
        tests.add(new Object[]{"AAGCCT", "GATT", "3M2D1M", Arrays.asList(Arrays.asList("A", "G"), Arrays.asList("GCC", "T"))});
        tests.add(new Object[]{"AAAAA", "AGACA", "5M", Arrays.asList(Arrays.asList("A", "G"), Arrays.asList("A", "C"))});

        return tests.toArray(new Object[][]{});
    }

    /**
     * Example testng test using MyDataProvider
     */
    @Test(dataProvider = "AdjacentSNPIndelTest")
    public void testAdjacentSNPIndelTest(final String refBases, final String haplotypeBases, final String cigar, final List<List<String>> expectedAlleles) {
        final Haplotype hap = new Haplotype(haplotypeBases.getBytes(), false, 0, TextCigarCodec.decode(cigar));
        final GenomeLoc loc = new UnvalidatingGenomeLoc(CHR, 0, 1, refBases.length());
        final EventMap ee = new EventMap(hap, refBases.getBytes(), loc, NAME);
        ee.replaceClumpedEventsWithBlockSubstitutions();
        Assert.assertEquals(ee.getNumberOfEvents(), expectedAlleles.size());
        final List<VariantContext> actuals = new ArrayList<VariantContext>(ee.getVariantContexts());
        for ( int i = 0; i < ee.getNumberOfEvents(); i++ ) {
            final VariantContext actual = actuals.get(i);
            Assert.assertEquals(actual.getReference().getDisplayString(), expectedAlleles.get(i).get(0));
            Assert.assertEquals(actual.getAlternateAllele(0).getDisplayString(), expectedAlleles.get(i).get(1));
        }
    }

    @DataProvider(name = "MakeBlockData")
    public Object[][] makeMakeBlockData() {
        List<Object[]> tests = new ArrayList<Object[]>();

        tests.add(new Object[]{Arrays.asList("A", "G"), Arrays.asList("AGT", "A"), Arrays.asList("AGT", "G")});
        tests.add(new Object[]{Arrays.asList("A", "G"), Arrays.asList("A", "AGT"), Arrays.asList("A", "GGT")});

        tests.add(new Object[]{Arrays.asList("AC", "A"), Arrays.asList("A", "AGT"), Arrays.asList("AC", "AGT")});
        tests.add(new Object[]{Arrays.asList("ACGTA", "A"), Arrays.asList("A", "AG"), Arrays.asList("ACGTA", "AG")});
        tests.add(new Object[]{Arrays.asList("AC", "A"), Arrays.asList("A", "AGCGT"), Arrays.asList("AC", "AGCGT")});
        tests.add(new Object[]{Arrays.asList("A", "ACGTA"), Arrays.asList("AG", "A"), Arrays.asList("AG", "ACGTA")});
        tests.add(new Object[]{Arrays.asList("A", "AC"), Arrays.asList("AGCGT", "A"), Arrays.asList("AGCGT", "AC")});

        return tests.toArray(new Object[][]{});
    }

    /**
     * Example testng test using MyDataProvider
     */
    @Test(dataProvider = "MakeBlockData", enabled = true)
    public void testGetNeighborhood(final List<String> firstAlleles, final List<String> secondAlleles, final List<String> expectedAlleles) {
        final VariantContext vc1 = GATKVariantContextUtils.makeFromAlleles("x", "20", 10, firstAlleles);
        final VariantContext vc2 = GATKVariantContextUtils.makeFromAlleles("x", "20", 10, secondAlleles);
        final VariantContext expected = GATKVariantContextUtils.makeFromAlleles("x", "20", 10, expectedAlleles);

        final EventMap eventMap = new EventMap(Collections.<VariantContext>emptyList());
        final VariantContext block = eventMap.makeBlock(vc1, vc2);

        Assert.assertEquals(block.getStart(), expected.getStart());
        Assert.assertEquals(block.getAlleles(), expected.getAlleles());
    }
}
