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

import java.lang.reflect.Field;
import java.util.Vector;

public class ToString
{
    public static String print(Object obj)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Class cls = obj.getClass();
        sb.append(cls.toString());
        sb.append(": ");

        Vector fields = new Vector();
        while (cls != null)
        {
            Field[] f = cls.getDeclaredFields();
            for (int i = 0; i < f.length; i++)
            {
                f[i].setAccessible(true);
                fields.addElement(f[i]);
            }
            cls = cls.getSuperclass();
        }

        for (int i = 0; i < fields.size(); i++)
        {
            Field f = (Field) fields.elementAt(i);
            sb.append(f.getName());
            sb.append("=");
            try
            {
                Object val = f.get(obj);

                if (val != null)
                    if (val instanceof String)
                        sb.append("\"" + val + "\"");
                    else
                        sb.append(val.toString());
                else
                    sb.append("null");
            }
            catch (IllegalAccessException e)
            {
                sb.append("not accessible!?");
            }
            if (i < fields.size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
