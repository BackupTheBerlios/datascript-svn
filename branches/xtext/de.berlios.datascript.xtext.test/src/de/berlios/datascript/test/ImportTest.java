package de.berlios.datascript.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.antlr.IAntlrParser;
import org.eclipse.xtext.parsetree.SyntaxError;
import org.eclipse.xtext.resource.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Test;

import com.google.inject.Injector;

import de.berlios.datascript.DataScriptStandaloneSetup;
import de.berlios.datascript.dataScript.Model;

public class ImportTest {
	

	@Test
	public void parseFile() throws FileNotFoundException
	{
		Injector guiceInjector = new DataScriptStandaloneSetup().createInjectorAndDoEMFRegistration();
		IAntlrParser parser = guiceInjector.getInstance(IAntlrParser.class);
		ILinker linker = guiceInjector.getInstance(ILinker.class);
		
		FileInputStream is = new FileInputStream("MyModel.ds");
		IParseResult result = parser.parse(is);
		List<SyntaxError> errors = result.getParseErrors();
		assertTrue(errors.size() == 0);
		EObject content = result.getRootASTElement();
		handleContent(content);
		ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
		//linker.linkModel(content, consumer);
	}
	
	@Test
	public void parseResource()
	{
//		new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
//		Injector injector = new MyDslStandaloneSetup().createInjectorAndDoEMFRegistration();
//		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
//		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
//		Resource resource = resourceSet.getResource(
//		    URI.createURI("platform:/resource/org.xtext.example.mydsl/src/example.mydsl"), true);
//		Model model = (Model) resource.getContents().get(0);		
		
		DataScriptStandaloneSetup.doSetup();
		XtextResourceSet rs = new XtextResourceSet();
		rs.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, true);
		URI uri = URI.createURI("MyModel.ds");
		Resource r = rs.getResource(uri, true);
		assertNotNull(r);
		for (Diagnostic diag : r.getErrors())
		{
			System.out.println(diag.getLine() + ":" + diag.getColumn()+ ": " + diag.getMessage());
		}
		EObject content = r.getContents().get(0);		
		handleContent(content);		
	}

	

	private void handleContent(EObject content) {
		assertTrue(content instanceof Model);
		Model root = (Model) content;
		
//		List<Expression> expressions = root.getExpressions();
//		assertEquals(1, expressions.size());
//		
//		Expression expr = expressions.get(0);
//		List<Expression> operands = expr.getOperands();
//		String operator = expr.getOperator();
//		assertEquals("+", operator);
//		Expression left = operands.get(0);
//		Expression right = operands.get(1);
//		assertTrue(left instanceof IntegerLiteral);
//		assertTrue(right instanceof IntegerLiteral);
//		
//		assertEquals(2, ((IntegerLiteral)left).getValue());
//		assertEquals(3, ((IntegerLiteral)right).getValue());
	}

}
