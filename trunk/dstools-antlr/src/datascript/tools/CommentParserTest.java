package datascript.tools;
import java.io.FileInputStream;

import antlr.CommonAST;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.debug.misc.ASTFrame;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;

public class CommentParserTest 
{
    private CommonAST rootNode;
    private DocCommentParser parser;
    
    public void parseComment(String fileName) throws Exception
    {
        FileInputStream is = new FileInputStream(fileName); 
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer buffer = new TokenBuffer(lexer);
        parser = new DocCommentParser(buffer);
        parser.comment();
        rootNode = (CommonAST) parser.getAST(); //.getFirstChild();
        rootNode.setText("root");
    }

    public void printTokens(String fileName) throws Exception
    {
        FileInputStream is = new FileInputStream(fileName); 
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer buffer = new TokenBuffer(lexer);
        while (buffer.LA(1) != Token.EOF_TYPE)
        {
            Token token = buffer.LT(1);
            System.out.println(DocCommentParser._tokenNames[token.getType()] + 
                    ": " + token.getText());
            buffer.consume();
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            String fileName = args[0];
            CommentParserTest self = new CommentParserTest();
            self.parseComment(fileName);
            //self.printTokens(fileName);
            //OutputStreamWriter os = new OutputStreamWriter(System.out);
            //root.xmlSerialize(os);
            //os.flush();
            //printXml(dsTool.rootNode);
            //System.out.println(dsTool.rootNode.toStringList());
            ASTFrame frame = new ASTFrame("AST", self.rootNode);
            frame.setVisible(true);
            
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
