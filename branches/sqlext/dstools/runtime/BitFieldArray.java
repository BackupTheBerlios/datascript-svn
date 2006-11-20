package datascript.runtime;

import java.io.IOException;

public class BitFieldArray implements Array, SizeOf
{
    long[] data; // data is between [offset... offset+length-1]

    int offset;

    /** Number of array elements. */
    int length;

    /** Number of bits per element. */
    int numBits;
    
    
    public BitFieldArray(BitStreamReader in, int length, int numBits)
            throws IOException
    {
        if (length == -1)
        {
            throw new RuntimeException("variable length " + getClass()
                    + " not implemented");
        }
        else
        {
            this.length = length;
            this.numBits = numBits;
            data = new long[length];
            for (int i = 0; i < length; i++)
            {
                data[i] = in.readBits(numBits);
            }
            this.offset = 0;
        }
    }

    public BitFieldArray(int length, int numBits)
    {
        this(new long[length], 0, length, numBits);
    }

    public BitFieldArray(long[] data, int offset, int length, int numBits)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.numBits = numBits;
    }

    public long elementAt(int i)
    {
        return data[offset + i];
    }

    public int length()
    {
        return length;
    }

    /**
     * @TODO This may not be the exact size in bytes!
     */
    public int sizeof()
    {
        return numBits * length / 8;
    }

    public Array map(Mapping m)
    {
        BitFieldArray result = new BitFieldArray(length, numBits);
        for (int i = 0; i < length; i++)
        {
            result.data[i] = ((Long) m.map(new Long(data[offset + i])))
                    .longValue();
        }
        return result;
    }

    public Array subRange(int begin, int length)
    {
        if (begin < 0 || begin >= this.length || begin + length > this.length)
            throw new ArrayIndexOutOfBoundsException();
        return new BitFieldArray(data, offset + begin, length, numBits);
    }

    /**
     * @TODO this is incorrect. Need to implement BitStreamWriter first.
     */
    public void write(java.io.DataOutput out, CallChain cc) throws IOException
    {
        for (int i = offset; i < offset + length; i++)
        {
            out.writeLong(data[i]);
        }
    }
}
