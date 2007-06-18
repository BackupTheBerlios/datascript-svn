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

import org.eclipse.jface.text.rules.IWordDetector;

public class WordDetector implements IWordDetector
{
    /**
     * Determines if the specified character is permissible as the first
     * character in a DataScript identifier. A character may start a DataScript identifier
     * if and only if it is one of the following:
     * <ul>
     * <li>a letter
     * <li>a connecting punctuation character ("_")
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may start a ANTLR identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetter(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart
     */
    public boolean isWordStart(char aChar)
    {
        return Character.isLetter(aChar) || aChar == '_';
    }

    /**
     * Determines if the specified character may be part of a DataScript identifier
     * as other than the first character. A character may be part of a DataScript
     * identifier if and only if it is one of the following:
     * <ul>
     * <li>a letter
     * <li>a digit
     * <li>an underscore  ("_").
     * </ul>
     * 
     * @param aChar
     *            the character to be tested.
     * @return true if the character may be part of a DataScript identifier; false
     *         otherwise.
     * @see java.lang.Character#isLetterOrDigit(char)
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart
     */
    public boolean isWordPart(char aChar)
    {
        return Character.isLetterOrDigit(aChar) || aChar == '_';
    }
}
