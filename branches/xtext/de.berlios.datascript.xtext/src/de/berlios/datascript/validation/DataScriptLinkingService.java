package de.berlios.datascript.validation;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;

import de.berlios.datascript.dataScript.Expression;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.dataScript.Value;

/**
 * Linking service which extends the default by setting the type for linked
 * dot expressions.
 * 
 * @author HWellmann
 *
 */
public class DataScriptLinkingService extends DefaultLinkingService
{
    @Override
    public List<EObject> getLinkedObjects(EObject context, EReference ref,
            AbstractNode node) throws IllegalNodeException
    {
        EClass requiredType = ref.getEReferenceType();
        if (requiredType == null)
            return Collections.emptyList();

        IScope scope = getScope(context, ref);
        String s = getCrossRefNodeAsString(node);
        if (s == null)
            return Collections.emptyList();

        for (IScopedElement element : scope.getAllContents())
        {
            if (s.equals(element.name()))
            {
                EObject linked = element.element();
                
                // extended behaviour, the rest is copied from the superclass
                setType(node, linked);
                
                return Collections.singletonList(linked);
            }
        }
        return Collections.emptyList();
    }

    /**
     * If the node is the right hand side of a dot expression, the type of
     * the expression is set to the type of the linked object. 
     * 
     * Example: The type of {@code foo.bar} is set to the type of {@code bar}.
     * 
     * @param node
     * @param linked
     */
    private void setType(AbstractNode node, EObject linked)
    {
        /*
         * TODO: Unclear - why do we have to go to the grandparent and not
         * the parent to find the dot expression? And why is the element
         * of the grandparent null for the left hand node of the dot expression?
         */
        AbstractNode parent = node.getParent();
        if (parent == null)
            return;
        
        parent = parent.getParent();
        if (parent == null)
            return;
        
        EObject parentElement = parent.getElement();
        
        if (parentElement instanceof Expression)
        {
            Expression expr = (Expression) parentElement;
            if (".".equals(expr.getOperator()))
            {
                Type type = TypeResolver.getType((Value)linked);
                expr.setType(type);
            }
        }
    }

}
