/* BSD License
 *
 * Copyright (c) 2007, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
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
 *     * Neither the name of Harman/Becker Automotive Systems 
 *       nor the names of their contributors may be used to
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
package datascript.eclipse.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class DataScriptColorProvider
{
    private static final RGB RGB_DEFAULT = new RGB(0, 0, 0);

    private static final RGB RGB_KEYWORD = new RGB(127, 0, 85);

    private static final RGB RGB_STRING = new RGB(42, 0, 255);

    private static final RGB RGB_COMMENT = new RGB(63, 127, 95);

    private static final RGB RGB_DOC_COMMENT = new RGB(63, 95, 191);

    protected Map<String, Color> colorTable = new HashMap<String, Color>(10);

    protected Map<String, RGB> rgbTable = new HashMap<String, RGB>(10);

    public DataScriptColorProvider()
    {
        rgbTable.put(DataScriptColorConstants.COMMENT, RGB_COMMENT);
        rgbTable.put(DataScriptColorConstants.DOC_COMMENT, RGB_DOC_COMMENT);
        rgbTable.put(DataScriptColorConstants.STRING, RGB_STRING);
        rgbTable.put(DataScriptColorConstants.KEYWORD, RGB_KEYWORD);
        rgbTable.put(DataScriptColorConstants.DEFAULT, RGB_DEFAULT);
    }

    public void dispose()
    {
        Iterator e = colorTable.values().iterator();
        while (e.hasNext())
            ((Color) e.next()).dispose();
    }

    public Color getColor(String name)
    {
        Color color = (Color) colorTable.get(name);
        if (color == null)
        {
            RGB rgb = rgbTable.get(name);
            color = new Color(Display.getCurrent(), rgb);
            colorTable.put(name, color);
        }
        return color;
    }
}
