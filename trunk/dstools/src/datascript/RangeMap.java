/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package datascript;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.SortedMap;
import java.math.BigInteger;

/**
 * A subclass of treemap that maps ranges to objects. An invariant is that the
 * ranges are disjoint.
 * 
 * The map's key range can be used to express a subset of integers, (optionally)
 * associated with some values. However, ranges of arbitrary Comparable are
 * possible
 * 
 * It also supports intersection and union with another subset.
 */
public class RangeMap extends TreeMap /* <Range, Object> */
{

    static class Range implements Comparable
    {
        Comparable lower, upper; // lower <= x < upper

        public Range(Comparable lower, Comparable upper)
        {
            this.lower = lower;
            this.upper = upper;
        }

        public Comparable getLower()
        {
            return this.lower;
        }

        public Comparable getUpper()
        {
            return this.upper;
        }

        /**
         * See if this range denotes a single number. Assume bounds are
         * BigIntegers... not very clean, since rest of Range is based on
         * Comparable only
         */
        boolean isNumber()
        {
            return lower instanceof BigInteger
                    && upper instanceof BigInteger
                    && ((BigInteger) upper).subtract((BigInteger) lower)
                            .equals(BigInteger.ONE);
        }

        public int compareTo(Range that)
        {
            return this.lower.compareTo(that.lower);
        }

        public int compareTo(Object that)
        {
            return compareTo((Range) that);
        }

        public int hashCode()
        {
            return lower.hashCode();
        }

        public boolean overlapsWith(Range that)
        {
            if (this.lower.compareTo(that.lower) == 0)
                return true;

            if (this.lower.compareTo(that.lower) == -1)
            {
                return this.upper.compareTo(that.lower) == 1;
            }
            else
            {
                return that.upper.compareTo(this.lower) == 1;
            }
        }

        private static Comparable max(Comparable a, Comparable b)
        {
            return a.compareTo(b) == -1 ? b : a;
        }

        private static Comparable min(Comparable a, Comparable b)
        {
            return a.compareTo(b) == -1 ? a : b;
        }

        public Range getOverlap(Range that)
        {
            if (this.lower.compareTo(that.lower) == 0)
                return new Range(this.lower, min(this.upper, that.upper));
            if (this.lower.compareTo(that.lower) == -1)
            {
                if (this.upper.compareTo(that.lower) == 1)
                    return new Range(that.lower, this.upper);
            }
            else
            {
                if (that.upper.compareTo(this.lower) == 1)
                    return new Range(this.lower, that.upper);
            }
            return null;
        }

        public boolean equals(Range that)
        {
            return this.lower.compareTo(that.lower) == 0;
        }

        public boolean equals(Object that)
        {
            return this.equals((Range) that);
        }

        public String toString()
        {
            return "[" + lower + ", " + upper + "]";
        }
    }

    static RangeMap empty = new RangeMap();

    public RangeMap()
    {
    }

    /**
     * Create subset equal to this one range
     */
    public RangeMap(Range range, Object value)
    {
        super();
        put(range, value);
    }

    protected RangeMap(SortedMap map)
    {
        super(map);
    }

    public Object put(Object key, Object value)
    {
        if (containsKey(key))
            throw new Error("region map already contains " + get(key));
        return super.put(key, value);
    }

    /**
     * Set the values for all keys to the provided value
     */
    public void setAllValues(Object value)
    {
        Iterator thisSet = this.keySet().iterator();
        while (thisSet.hasNext())
        {
            super.put(thisSet.next(), value);
        }
    }

    /**
     * compute the symmetric difference of these maps
     * 
     * @return new RangeMap or null if intersection is nonempty
     */
    RangeMap symDifference(RangeMap that)
    {
        if (isEmpty())
            return new RangeMap(that);

        RangeMap result = new RangeMap(this);
        Iterator thatSet = that.keySet().iterator();
        while (thatSet.hasNext())
        {
            Range nRange = (Range) thatSet.next();
            if (result.containsKey(nRange))
                return null;

            // check largest range to the left of nRange
            SortedMap lmap = result.headMap(nRange);
            if (!lmap.isEmpty()
                    && ((Range) lmap.lastKey()).overlapsWith(nRange))
            {
                return null;
            }

            // check smallest range to the right of nRange
            SortedMap rmap = result.tailMap(nRange);
            if (!rmap.isEmpty()
                    && ((Range) rmap.firstKey()).overlapsWith(nRange))
            {
                return null;
            }

            result.put(nRange, that.get(nRange));
        }
        return result;
    }

    /**
     * compute the intersection of two maps.
     */
    RangeMap intersect(RangeMap that)
    {
        Iterator thisSet = this.keySet().iterator();
        Iterator thatSet = that.keySet().iterator();
        RangeMap result = new RangeMap();

        /* hmmm, is this correct? */
        outer: while (thisSet.hasNext() && thatSet.hasNext())
        {
            /* advance both */
            Range thisRange = (Range) thisSet.next();
            Range thatRange = (Range) thatSet.next();

            /* skip thatRanges until we find an overlap */
            while (!thisRange.overlapsWith(thatRange))
            {
                if (!thatSet.hasNext())
                    break outer;
                thatRange = (Range) thatSet.next();
            }
            /* now collect all overlapping thatRanges from that point */
            while (thisRange.overlapsWith(thatRange))
            {
                result.put(thisRange.getOverlap(thatRange), get(thisRange));
                if (!thatSet.hasNext())
                    break outer;
                thatRange = (Range) thatSet.next();
            }
        }
        if (result.size() == 0)
            return null;
        else
            return result;
    }

    /**
     * compute the union of two ranges.
     */
    RangeMap union(RangeMap that)
    {
        throw new Error("union not implemented");
    }

    public static void main(String[] av)
    {
        RangeMap m1 = new RangeMap();
        m1.put(new Range(new Integer(1), new Integer(2)), "1-2");
        m1.put(new Range(new Integer(3), new Integer(4)), "3-4");

        RangeMap m2 = new RangeMap();
        m2.put(new Range(new Integer(4), new Integer(5)), "4-5");

        RangeMap m3 = new RangeMap();
        m3.put(new Range(new Integer(4), new Integer(5)), "4-5");
        m3.put(new Range(new Integer(1), new Integer(3)), "1-3");

        System.out.println("m1 = " + m1);
        System.out.println("m2 = " + m2);
        System.out.println("m3 = " + m3);
        System.out.println("m1 symdiff m2 = " + m1.symDifference(m2));
        System.out.println("m2 symdiff m1 = " + m2.symDifference(m1));
        System.out.println("m1 symdiff m3 = " + m1.symDifference(m3));
        System.out.println("m3 symdiff m1 = " + m3.symDifference(m1));
        System.out.println("m3 symdiff m2 = " + m3.symDifference(m2));
        System.out.println("m2 symdiff m3 = " + m2.symDifference(m3));
        System.out.println("m1 intersect m2 = " + m1.intersect(m2));
        System.out.println("m2 intersect m1 = " + m2.intersect(m1));
        System.out.println("m1 intersect m3 = " + m1.intersect(m3));
        System.out.println("m2 intersect m3 = " + m2.intersect(m3));
    }
}
