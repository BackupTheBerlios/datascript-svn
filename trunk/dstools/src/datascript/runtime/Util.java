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
package datascript.runtime;

import java.io.DataInput;
import java.io.IOException;

public class Util
{
    public static short leShort(short s)
    {
        return (short) ((s << 8) | ((s >>> 8) & 0xff));
    }

    public static int leInt(int s)
    {
        return (s << 24) | ((s & 0xff00) << 8) | ((s & 0xff0000) >>> 8)
                | (s >>> 24);
    }

    public static long leLong(long s)
    {
        return ((s & 0xff) << 56) | ((s & 0xff00) << 40)
                | ((s & 0xff0000) << 24) | ((s & 0xff000000L) << 8)
                | ((s & 0xff00000000L) >>> 8) | ((s & 0xff0000000000L) >>> 24)
                | ((s & 0xff000000000000L) >>> 40) | (s >>> 56);
    }

    public static String readZTString(DataInput in) throws IOException
    {
        StringBuffer b = new StringBuffer();
        for (;;)
        {
            byte c = in.readByte();
            if (c == 0)
            {
                return b.toString();
            }
            b.append((char) c);
        }
    }

    public static void main(String av[])
    {
        System.out.println(Long.toHexString(0x0011223344556677L));
        System.out.println(Long.toHexString(leLong(0x0011223344556677L)));
        System.out.println(Long.toHexString(0xffeeddccbbaa99f8L));
        System.out.println(Long.toHexString(leLong(0xffeeddccbbaa99f8L)));
        System.out.println(Integer.toHexString(0x00112233));
        System.out.println(Integer.toHexString(leInt(0x00112233)));
        System.out.println(Integer.toHexString(0xffeeddfa));
        System.out.println(Integer.toHexString(leInt(0xffeeddfa)));
        System.out.println(Integer.toHexString(0x0011));
        System.out.println(Integer.toHexString(leShort((short) 0x0011)));
        System.out.println(Integer.toHexString(0xffee));
        System.out.println(Integer.toHexString(leShort((short) 0xffee)));
    }
}
