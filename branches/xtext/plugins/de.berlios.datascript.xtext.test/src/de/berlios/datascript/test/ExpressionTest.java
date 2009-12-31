package de.berlios.datascript.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.Test;

import com.google.inject.Injector;

import de.berlios.datascript.DataScriptStandaloneSetup;
import de.berlios.datascript.dataScript.Constant;
import de.berlios.datascript.dataScript.DataScriptPackage;
import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.IntegerLiteral;
import de.berlios.datascript.dataScript.Model;
import de.berlios.datascript.dataScript.StringLiteral;
import de.berlios.datascript.validation.BuiltInTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExpressionTest
{
    private Resource resource;
    private Model model;
    private org.eclipse.emf.common.util.Diagnostic diagnostic;
    
    public static class ValidationDiagnostic extends AbstractDiagnostic
    {

        @Override
        protected AbstractNode getNode()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getMessage()
        {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    

    private void buildModel(String string) throws IOException
    {
        Injector injector = new DataScriptStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        //BuiltInIntegerType.setResourceSet(resourceSet);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        resource = resourceSet.createResource(URI.createURI("dummy:/dummy.ds"));
        InputStream in = new ByteArrayInputStream(string.getBytes());
        resource.load(in, resourceSet.getLoadOptions());
        model = (Model) resource.getContents().get(0);
        
        diagnostic = Diagnostician.INSTANCE.validate(model);      
        //registerIssues(resource, diagnostic);
//        Object object = EValidator.Registry.INSTANCE.get(DataScriptPackage.eINSTANCE);
        System.out.println(diagnostic);
    }
    
//private void registerIssues(Resource res,
//            org.eclipse.emf.common.util.Diagnostic d)
//    {
//        
//        if (d.getSeverity() == Diagnostic.ERROR) {
//                res.getErrors().add(d);
//        } else if (d.getSeverity() == Diagnostic.WARNING){
//                issues.addWarning(this, d.getMessage(), getContextObject(d));
//        }
//        for (Diagnostic diag : d.getChildren()) {
//                registerIssues(diag, issues);
//        }
//    }

//    private void readModel(String string)
//    {
//        MweReader reader = new MweReader();
//        Issues issues = new IssuesImpl();
//        WorkflowContextDefaultImpl context = new WorkflowContextDefaultImpl();
//        context.
//        reader.invoke(context, null, issues);
//        Injector injector = new DataScriptStandaloneSetup().createInjectorAndDoEMFRegistration();
//        XtextResourceSet set = injector.getInstance(XtextResourceSet.class);
//        set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
//        resource = set.createResource(URI.createURI("dummy:/dummy.ds"));
//
//        InputStream in = new ByteArrayInputStream(string.getBytes());
//        resource.load(in, set.getLoadOptions());
//        model = (Model) resource.getContents().get(0);
//
//  
//        Object value = getContent(resource);
//        ctx.set(outputSlot, value);
//        registerIssues(set,issues);
//
//        doValidate(issues, resource);
//    }

    @Test
    public void testIntegerLiteral() throws IOException
    {
        String ds = "const int32 FOO = 42;";
        buildModel(ds);
        List<Diagnostic> errors = resource.getErrors();
        assertTrue(errors.size() == 0);

        Constant constant = (Constant) model.getElements().get(0);
        Expression expr = constant.getValue();
        assertEquals(BuiltInTypes.INTEGER, expr.getType());
        assertTrue(expr instanceof IntegerLiteral);
        IntegerLiteral literal = (IntegerLiteral) expr;
        assertEquals(42, literal.getValue());
        
    }

    @Test
    public void testSum() throws IOException
    {
        String ds = "const int32 FOO = 2 + 3;";
        buildModel(ds);
        List<Diagnostic> errors = resource.getErrors();
        assertTrue(errors.size() == 0);

        Constant constant = (Constant) model.getElements().get(0);
        Expression expr = constant.getValue();
        assertEquals(BuiltInTypes.INTEGER, expr.getType());
        assertEquals("+" , expr.getOperator());

        Expression left = expr.getLeft();
        assertTrue(left instanceof IntegerLiteral);
        IntegerLiteral leftLit = (IntegerLiteral) left;
        assertEquals(2, leftLit.getValue());
        
        Expression right = expr.getRight();
        assertTrue(right instanceof IntegerLiteral);
        IntegerLiteral rightLit = (IntegerLiteral) right;
        assertEquals(3, rightLit.getValue());
        
    }

    @Test
    public void typeMismatch() throws IOException
    {
        String ds = "const int32 FOO = 2 + \"junk\";";
        buildModel(ds);
        List<Diagnostic> errors = resource.getErrors();
        assertTrue(errors.size() == 0);
        
        assertEquals(1, diagnostic.getChildren().size());
        String msg = diagnostic.getChildren().get(0).getMessage();
        assertEquals("integer expression required", msg);

        Constant constant = (Constant) model.getElements().get(0);
        Expression expr = constant.getValue();
        assertEquals(BuiltInTypes.INTEGER, expr.getType());
        assertEquals("+" , expr.getOperator());

        Expression left = expr.getLeft();
        assertTrue(left instanceof IntegerLiteral);
        IntegerLiteral leftLit = (IntegerLiteral) left;
        assertEquals(2, leftLit.getValue());
        
        Expression right = expr.getRight();
        assertTrue(right instanceof StringLiteral);
        StringLiteral rightLit = (StringLiteral) right;
        assertEquals("junk", rightLit.getValue());
    }
}
