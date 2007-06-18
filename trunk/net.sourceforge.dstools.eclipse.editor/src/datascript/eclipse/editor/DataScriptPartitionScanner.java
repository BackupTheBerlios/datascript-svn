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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;

public class DataScriptPartitionScanner extends RuleBasedPartitionScanner
{
    /** string type */
    public final static String STRING = "__ds_string";

    /** single-line comment type */
    public final static String SINGLE_LINE_COMMENT = "__ds_single_line_comment";

    /** multi-line comment type */
    public final static String MULTI_LINE_COMMENT = "__ds_multi_line_comment";

    /** javadoc comment type */
    public final static String DOC = "__ds_doc";

    /**
     * types of partitions in an ANTLR grammar
     */
    public static final String[] PARTITION_TYPES = new String[] {
            IDocument.DEFAULT_CONTENT_TYPE, STRING, SINGLE_LINE_COMMENT,
            MULTI_LINE_COMMENT, DOC };

    /**
     * Creates the partitioner and sets up the appropriate rules.
     */
    public DataScriptPartitionScanner()
    {
        IToken string = new Token(STRING);
        IToken singleLineComment = new Token(SINGLE_LINE_COMMENT);
        IToken multiLineComment = new Token(MULTI_LINE_COMMENT);
        IToken javaDoc = new Token(DOC);

        List<IRule> rules = new ArrayList<IRule>();

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\'));

        // Add special empty comment word rule
        rules.add(new WordPatternRule(new EmptyCommentDetector(), "/**/", null,
                multiLineComment));
        // Add rules for multi-line comments
        rules.add(new MultiLineRule("/**", "*/", javaDoc));
        rules.add(new MultiLineRule("/*", "*/", multiLineComment));

        // Add special empty comment word rules
        rules.add(new WordPatternRule(new EmptyCommentDetector(), "/**/", null,
                multiLineComment));
        rules.add(new WordPatternRule(new EmptyCommentDetector(), "/***/",
                null, javaDoc));
        // Add rule for single line comments
        rules.add(new EndOfLineRule("//", singleLineComment));

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
}
