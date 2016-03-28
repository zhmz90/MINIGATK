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

package org.broadinstitute.gatk.utils.locusiterator;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;

/**
* Created with IntelliJ IDEA.
* User: depristo
* Date: 1/5/13
* Time: 8:42 PM
* To change this template use File | Settings | File Templates.
*/
public final class LIBS_position {

    SAMRecord read;

    final int numOperators;
    int currentOperatorIndex = 0;
    int currentPositionOnOperator = 0;
    int currentReadOffset = 0;
    int currentGenomeOffset = 0;

    public boolean isBeforeDeletionStart = false;
    public boolean isBeforeDeletedBase = false;
    public boolean isAfterDeletionEnd = false;
    public boolean isAfterDeletedBase = false;
    public boolean isBeforeInsertion = false;
    public boolean isAfterInsertion = false;
    public boolean isNextToSoftClip = false;

    boolean sawMop = false;

    public LIBS_position(final SAMRecord read) {
        this.read = read;
        numOperators = read.getCigar().numCigarElements();
    }

    public int getCurrentReadOffset() {
        return Math.max(0, currentReadOffset - 1);
    }

    public int getCurrentPositionOnOperatorBase0() {
        return currentPositionOnOperator - 1;
    }

    public int getCurrentGenomeOffsetBase0() {
        return currentGenomeOffset - 1;
    }

    /**
     * Steps forward on the genome.  Returns false when done reading the read, true otherwise.
     */
    public boolean stepForwardOnGenome() {
        if ( currentOperatorIndex == numOperators )
            return false;

        CigarElement curElement = read.getCigar().getCigarElement(currentOperatorIndex);
        if ( currentPositionOnOperator >= curElement.getLength() ) {
            if ( ++currentOperatorIndex == numOperators )
                return false;

            curElement = read.getCigar().getCigarElement(currentOperatorIndex);
            currentPositionOnOperator = 0;
        }

        switch ( curElement.getOperator() ) {
            case I: // insertion w.r.t. the reference
//                if ( !sawMop )
//                    break;
            case S: // soft clip
                currentReadOffset += curElement.getLength();
            case H: // hard clip
            case P: // padding
                currentOperatorIndex++;
                return stepForwardOnGenome();

            case D: // deletion w.r.t. the reference
            case N: // reference skip (looks and gets processed just like a "deletion", just different logical meaning)
                currentPositionOnOperator++;
                currentGenomeOffset++;
                break;

            case M:
            case EQ:
            case X:
                sawMop = true;
                currentReadOffset++;
                currentPositionOnOperator++;
                currentGenomeOffset++;
                break;
            default:
                throw new IllegalStateException("No support for cigar op: " + curElement.getOperator());
        }

        final boolean isFirstOp = currentOperatorIndex == 0;
        final boolean isLastOp = currentOperatorIndex == numOperators - 1;
        final boolean isFirstBaseOfOp = currentPositionOnOperator == 1;
        final boolean isLastBaseOfOp = currentPositionOnOperator == curElement.getLength();

        isBeforeDeletionStart = isBeforeOp(read.getCigar(), currentOperatorIndex, CigarOperator.D, isLastOp, isLastBaseOfOp);
        isBeforeDeletedBase = isBeforeDeletionStart || (!isLastBaseOfOp && curElement.getOperator() == CigarOperator.D);
        isAfterDeletionEnd = isAfterOp(read.getCigar(), currentOperatorIndex, CigarOperator.D, isFirstOp, isFirstBaseOfOp);
        isAfterDeletedBase  = isAfterDeletionEnd || (!isFirstBaseOfOp && curElement.getOperator() == CigarOperator.D);
        isBeforeInsertion = isBeforeOp(read.getCigar(), currentOperatorIndex, CigarOperator.I, isLastOp, isLastBaseOfOp)
                || (!sawMop && curElement.getOperator() == CigarOperator.I);
        isAfterInsertion = isAfterOp(read.getCigar(), currentOperatorIndex, CigarOperator.I, isFirstOp, isFirstBaseOfOp);
        isNextToSoftClip = isBeforeOp(read.getCigar(), currentOperatorIndex, CigarOperator.S, isLastOp, isLastBaseOfOp)
                || isAfterOp(read.getCigar(), currentOperatorIndex, CigarOperator.S, isFirstOp, isFirstBaseOfOp);

        return true;
    }

    private static boolean isBeforeOp(final Cigar cigar,
                                      final int currentOperatorIndex,
                                      final CigarOperator op,
                                      final boolean isLastOp,
                                      final boolean isLastBaseOfOp) {
        return  !isLastOp && isLastBaseOfOp && cigar.getCigarElement(currentOperatorIndex+1).getOperator() == op;
    }

    private static boolean isAfterOp(final Cigar cigar,
                                     final int currentOperatorIndex,
                                     final CigarOperator op,
                                     final boolean isFirstOp,
                                     final boolean isFirstBaseOfOp) {
        return  !isFirstOp && isFirstBaseOfOp && cigar.getCigarElement(currentOperatorIndex-1).getOperator() == op;
    }
}
