package de.berlios.datascript.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.antlr.IAntlrParser;
import org.eclipse.xtext.parsetree.SyntaxError;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Test;

import com.google.inject.Injector;

import de.berlios.datascript.DataScriptStandaloneSetup;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.IntegerLiteral;
import de.berlios.datascript.dataScript.Model;

public class FileTest {
	

	@Test
	public void testAdditiveExpr()
	{
		DataScriptStandaloneSetup.doSetup();
		XtextResourceSet rs = new XtextResourceSet();
		URI uri = URI.createURI("src/model/add.ds");
		Resource r = rs.getResource(uri, true);
		assertNotNull(r);
		EObject content = r.getContents().get(0);		
		handleContent(content);		
	}

	@Test
	public void parseFile() throws FileNotFoundException
	{
		Injector guiceInjector = new DataScriptStandaloneSetup().createInjectorAndDoEMFRegistration();
		IAntlrParser parser = guiceInjector.getInstance(IAntlrParser.class);		
		
		FileInputStream is = new FileInputStream("src/model/add.ds");
		IParseResult result = parser.parse(is);
		List<SyntaxError> errors = result.getParseErrors();
		assertTrue(errors.size() == 0);
		EObject content = result.getRootASTElement();
		handleContent(content);		
	}

	private void handleContent(EObject content) {
		assertTrue(content instanceof Model);
		Model root = (Model) content;
		
		List<Expression> expressions = root.getExpressions();
		assertEquals(1, expressions.size());
		
		Expression expr = expressions.get(0);
		List<Expression> operands = expr.getOperands();
		String operator = expr.getOperator();
		assertEquals("+", operator);
		Expression left = operands.get(0);
		Expression right = operands.get(1);
		assertTrue(left instanceof IntegerLiteral);
		assertTrue(right instanceof IntegerLiteral);
		
		assertEquals(2, ((IntegerLiteral)left).getValue());
		assertEquals(3, ((IntegerLiteral)right).getValue());
	}

}
