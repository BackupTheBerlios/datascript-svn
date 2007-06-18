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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public class DataScriptCodeScanner extends RuleBasedScanner
{

    /** DataScript keywords we care about */
    public static final String[] KEYWORDS = new String[] {
        "big",
        "bit",
        "bitmask",
        "condition",
        "const",
        "enum",
        "forall",
        "function",
        "if",
        "import",
        "index",
        "in",
        "is",
        "lengthof",
        "little",
        "package",
        "return",
        "sizeof",
        "sql",
        "sql_database",
        "sql_integer",
        "sql_key",
        "sql_metadata",
        "sql_pragma",
        "sql_table",
        "string",
        "subtype",
        "sum",
        "union",
        "zip"
    };

    /**
     * Create an instance of a code scanner for syntax highlighting
     * 
     * @param aColorProvider
     *            The color mapping
     */
    public DataScriptCodeScanner(DataScriptColorProvider provider)
    {
        IToken keyword = new Token(new TextAttribute(provider
                .getColor(DataScriptColorConstants.KEYWORD), null, SWT.BOLD));
        IToken other = new Token(new TextAttribute(provider
                .getColor(DataScriptColorConstants.DEFAULT)));
        IToken string = new Token(new TextAttribute(provider
                .getColor(DataScriptColorConstants.STRING)));
        IToken singleLineComment = new Token(new TextAttribute(provider
                .getColor(DataScriptColorConstants.COMMENT)));

        List<IRule> rules = new ArrayList<IRule>();

        // Add generic whitespace rule
        rules.add(new WhitespaceRule(new WhitespaceDetector()));

        // Add rule for single line comments
        rules.add(new EndOfLineRule("//", singleLineComment));

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\'));

        // Add word rule for ANTLR keywords
        WordRule wordRule = new WordRule(new WordDetector(), other);
        for (int i = 0; i < KEYWORDS.length; i++)
        {
            wordRule.addWord(KEYWORDS[i], keyword);
        }
        rules.add(wordRule);

        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        setRules(result);
    }

}
