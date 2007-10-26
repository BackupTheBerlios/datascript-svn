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


package datascript.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import antlr.Token;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.TokenStreamRecognitionException;
import antlr.collections.AST;

import datascript.antlr.DataScriptEmitter;
import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.DataScriptWalker;
import datascript.antlr.ExpressionEvaluator;
import datascript.antlr.TypeEvaluator;
import datascript.antlr.util.FileNameToken;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;

import datascript.ast.DataScriptException;
import datascript.ast.Package;
import datascript.ast.ParserException;
import datascript.ast.Scope;



public class DataScriptTool implements Parameters
{
    private static final String VERSION = "rds 0.15.15 (26 Oct 2007)";

    private static final File EXT_DIR = new File("ext/");
    private ToolContext context;
    private TokenAST rootNode = null;
    private DataScriptParser parser = null;
    private final Scope globals = new Scope();
    private final HashSet<String> allPackageFiles = new HashSet<String>();

    private final DataScriptEmitter emitter = new DataScriptEmitter();

    private Extensions rdsExtensions = null;

    /* Properties for command line parameters */
    private final Options rdsOptions = new Options();
    private CommandLine cli = null;
    private String fileName = null;
    private String srcPathName = null;
    private String outPathName = null;
    private boolean checkSyntax = false;



    private class CmdLineParser extends org.apache.commons.cli.Parser
    {
        /**
         * <p>This implementation of {@link Parser}'s abstract
         * {@link Parser#flatten(Options,String[],boolean) flatten} method
         * filters all arguments, that are defined in {@link Options}.
         * </p>
         * 
         * <p>
         * <b>Note:</b> <code>stopAtNonOption</code> is not used in this
         * <code>flatten</code> method.
         * </p>
         * 
         * @param options
         *            The command line {@link Option}
         * @param arguments
         *            The command line arguments to be parsed
         * @param stopAtNonOption
         *            Specifies whether to stop flattening when an non option is
         *            found.
         * @return The <code>arguments</code> String array.
         */
        protected String[] flatten(Options options, String[] arguments,
                boolean stopAtNonOption)
        {
            boolean nextIsArg = false;
            List<String> newArguments = new ArrayList<String>();
            for (String argument : arguments)
            {
                if (nextIsArg)
                {
                    nextIsArg = false;
                }
                else
                {
                    if (!options.hasOption(argument))
                        continue;
                    Option opt = options.getOption(argument);
                    nextIsArg = opt.hasArg();
                }
                newArguments.add(argument);
            }

            String[] a = new String[0];
            return newArguments.toArray(a);
        }
    }



    public DataScriptTool()
    {
        Token token = new FileNameToken(DataScriptParserTokenTypes.ROOT, "ROOT");
        rootNode = new TokenAST(token);
    }


    public void getOptions(org.apache.commons.cli.Options rdsOptions, String[] args)
    {
        org.apache.commons.cli.Option rdsOption;

        rdsOption = new Option("h", "help", false, "prints this help text and exit");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("c", false, "check syntax");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("ext", true, "path to the extension directory");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("out", true,
                "path to the directory in which the generated code is stored");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);

        rdsOption = new Option("src", true, "path to DataScript source files");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);
    }


    public void parseArguments(String[] args)
    {
        getOptions(rdsOptions, args);
        CmdLineParser parser = new CmdLineParser();
        try
        {
            cli = parser.parse(rdsOptions, args, true);
        }
        catch (org.apache.commons.cli.ParseException pe)
        {
            System.err.println(pe.getMessage());
        }
    }


    public boolean checkArguments()
    {
        if (cli == null)
            return false;

        checkSyntax = cli.hasOption('c');
        srcPathName = cli.getOptionValue("src");
        outPathName = cli.getOptionValue("out");
        //fileName = cli.getOptionValue("");

        if (outPathName == null || outPathName.length() == 0)
        {
            outPathName = ".";
        }
        else
        {
            int i = outPathName.length();
            while (outPathName.charAt(i - 1) == File.separatorChar)
                --i;
            if (i < outPathName.length())
                outPathName = outPathName.substring(0, i);
        }

        if (fileName == null)
            return false;
        return true;
    }


    private void prepareExtensions(String[] args) throws IOException,
            InstantiationException, IllegalAccessException
    {
        if (cli == null)
            return;

	// normalize slashes and backslashes
        fileName = new File(args[args.length-1]).getPath();

        File myExtDir = new File(cli.getOptionValue("ext", "ext/"));
        rdsExtensions = new Extensions(myExtDir);

        for (Extension extension : rdsExtensions)
        {
            extension.getOptions(rdsOptions);
        }

        CmdLineParser parser = new CmdLineParser();
        try
        {
            cli = parser.parse(rdsOptions, args, false);
        }
        catch (ParseException pe)
        {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(pe.getMessage(), rdsOptions);
        }

        for (Extension extension : rdsExtensions)
        {
            extension.setParameter(this);
        }
    }


    public void parseDatascript() throws Exception
    {
        // create tool context for information exchange between pipeline
        // components
        context = ToolContext.getInstance();
        context.setFileName(fileName);
        context.setPathName(srcPathName);

        allPackageFiles.add(fileName);
        AST unitRoot = (TokenAST) parsePackage();
        if (unitRoot != null)
        {
            rootNode.addChild(unitRoot);
            parseImportedPackages(unitRoot);
        }

        // Validate the syntax tree - this has no side effects.
        if (checkSyntax)
        {
            DataScriptWalker walker = new DataScriptWalker();
            walker.setContext(context);
            walker.root(rootNode);
            if (context.getErrorCount() != 0)
                throw new ParserException("Walker: Parser errors.");
        }

        // create name scopes and resolve references
        TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.pushScope(globals);
        typeEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("TypeEvaluator: Parser errors.");
        Package.linkAll();
        if (ToolContext.getInstance().getErrorCount() != 0)
            throw new ParserException("TypeEvaluator: Linker errors.");

        // check expression types and evaluate constant expressions
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("ExpressionEvaluator: Parser errors.");
    }


    public void emitDatascript() throws Exception
    {
        if (rootNode == null)
            return;

        if (rdsExtensions == null || rdsExtensions.size() <= 0)
        {
            System.out.println("No backends found in "
                    + EXT_DIR.getAbsolutePath() + ", nothing emitted.");
            return;
        }
        for (Extension extension : rdsExtensions)
        {
            extension.generate(emitter, rootNode);
        }
    }


    private void parseImportedPackages(AST unitNode) throws Exception
    {
        AST node = unitNode.getFirstChild();
        if (node.getType() == DataScriptParserTokenTypes.PACKAGE)
        {
            while (true)
            {
                node = node.getNextSibling();
                if (node == null
                        || node.getType() != DataScriptParserTokenTypes.IMPORT)
                    break;

                String fileName = getPackageFile(node);
                if (!allPackageFiles.contains(fileName))
                {
                    allPackageFiles.add(fileName);
                    context.setFileName(fileName);
                    AST unitRoot = (TokenAST) parsePackage();
                    if (unitRoot != null)
                    {
                        rootNode.addChild(unitRoot);
                        parseImportedPackages(unitRoot);
                    }
                }
            }
        }
    }


    public static String getPackageFile(AST node)
    {
        AST sibling = node.getFirstChild();
        String fileName = sibling.getText();
        File file = new File(fileName);
        while (true)
        {
            sibling = sibling.getNextSibling();
            if (sibling == null)
                break;

            file = new File(file, sibling.getText());
        }
        return file.getPath() + ".ds";
    }


    private AST parsePackage() throws Exception
    {
        String fileName = ToolContext.getFullName();
        System.out.println("Parsing " + fileName);

        // set up lexer, parser and token buffer
        try
        {
            FileInputStream is = new FileInputStream(fileName);
            DataScriptLexer lexer = new DataScriptLexer(is);
            lexer.setFilename(fileName);
            lexer.setTokenObjectClass("datascript.antlr.util.FileNameToken");
            TokenStreamHiddenTokenFilter filter = new TokenStreamHiddenTokenFilter(
                    lexer);
            filter.discard(DataScriptParserTokenTypes.WS);
            filter.discard(DataScriptParserTokenTypes.COMMENT);
            filter.hide(DataScriptParserTokenTypes.DOC);
            parser = new DataScriptParser(filter);
        }
        catch (java.io.FileNotFoundException fnfe)
        {
            ToolContext.logError((parser == null) ? null : (TokenAST) parser
                    .getAST(), fnfe.getMessage());
        }

        if (parser == null)
            return null;
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);

        // use custom node class containing line information
        parser.setASTNodeClass("datascript.antlr.util.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        AST retVal = parser.getAST();
        if (context.getErrorCount() != 0 || retVal == null)
            throw new ParserException("DataScriptParser: Parser errors.");

        String pkgName = ToolContext.getFileName();
        pkgName = pkgName.substring(0, pkgName.lastIndexOf(".ds"));
        TokenAST node = (TokenAST) retVal.getFirstChild();
        if (node.getType() != DataScriptParserTokenTypes.PACKAGE
                || node.getText().equals(pkgName))
            ToolContext.logWarning(node,
                    "filename and package name do not match!");
        return retVal;
    }


    /******** Implementation of Parameters interface ******* */

    public String getVersion()
    {
        return VERSION;
    }


    public boolean getCheckSyntax()
    {
        return checkSyntax;
    }


    public String getPathName()
    {
        return srcPathName;
    }


    public String getOutPathName()
    {
        return outPathName;
    }


    public String getFileName()
    {
        return fileName;
    }


    public DataScriptParser getParser()
    {
        return parser;
    }


    public boolean argumentExists(String key)
    {
        return cli.hasOption(key);
    }


    public String getCommandlineArg(String key) throws Exception
    {
        if (!cli.hasOption(key))
            throw new Exception(key + " is non of the commandline arguments.");
        return cli.getOptionValue(key);
    }


    /******** End of Parameters interface ******* */

    public static void main(String[] args)
    {
        System.out.println(VERSION);
        DataScriptTool dsTool = new DataScriptTool();
        dsTool.parseArguments(args);
        try
        {
            dsTool.prepareExtensions(args);
            if (!dsTool.checkArguments() || dsTool.cli.hasOption('h'))
            {
                org.apache.commons.cli.HelpFormatter hf = 
                    new org.apache.commons.cli.HelpFormatter();
                //hf.printHelp("rds <extension options> [-c] [-ext \"pathname to extensions\"] [-out \"pathname for output\"] [-src \"pathname\"] \"filename\"", dsTool.rdsOptions);
                hf.printHelp("rds <options> \"filename\"", "options are:", dsTool.rdsOptions, "\t\"filename\"    main DataScript source file", false);
            }
            else
            {
                dsTool.parseDatascript();
                dsTool.emitDatascript();
            }
        }
        catch (DataScriptException exc)
        {
            System.err.println(exc);
        }
        catch (TokenStreamRecognitionException exc)
        {
            System.err.println(exc);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        System.out.println("done.");
    }
}
