/* BSD License
 *
 * Copyright (c) 2009, Harald Wellmann, Harman/Becker Automotive Systems
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
package de.berlios.datascript.conversion;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.util.Strings;

import de.berlios.datascript.dataScript.Model;

public class DataScriptValueConverter extends DefaultTerminalConverters
{
    @ValueConverter(rule = "PackagePath")
    public IValueConverter<String> convertPackagePath()
    {
        return new IValueConverter<String>()
        {
            public String toValue(String string, AbstractNode node)
            {
                EObject rootElement = node.getParent().getParent().getElement();
                if (!(rootElement instanceof Model))
                {
                    throw new ValueConverterException("Model not found", node,
                            null);
                }
                Model model = (Model) rootElement;
                String pkg = model.getPackage().getName();
                int numDots = 0;
                for (int i = 0; i < pkg.length(); i++)
                {
                    if (pkg.charAt(i) == '.')
                    {
                        numDots++;
                    }
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < numDots; i++)
                {
                    builder.append("../");
                }

                if (Strings.isEmpty(string))
                    throw new ValueConverterException(
                            "Couldn't convert empty string to int", node, null);

                String result = string.replace(".", "/") + ".ds";
                builder.append(result);
                result = builder.toString();
                return result;
            }

            public String toString(String value)
            {
                String result = value.substring(0, value.length() - 3).replace(
                        "/", ".");
                return result;
            }

        };
    }

    @ValueConverter(rule = "HEX")
    public IValueConverter<Integer> convertHexLiteral()
    {
        return new IValueConverter<Integer>()
        {
            public Integer toValue(String string, AbstractNode node)
            {
                String hexString = string.substring(2);
                int result = Integer.parseInt(hexString, 16);
                return result;
            }

            public String toString(Integer value)
            {
                String result = "0x" + Integer.toHexString(value);
                return result;
            }

        };
    }

    @ValueConverter(rule = "BINARY")
    public IValueConverter<Integer> convertBinaryLiteral()
    {
        return new IValueConverter<Integer>()
        {
            public Integer toValue(String string, AbstractNode node)
            {
                String binString = string.substring(0, string.length() - 1);
                int result = Integer.parseInt(binString, 2);
                return result;
            }

            public String toString(Integer value)
            {
                String result = Integer.toBinaryString(value) + "b";
                return result;
            }

        };
    }

}
