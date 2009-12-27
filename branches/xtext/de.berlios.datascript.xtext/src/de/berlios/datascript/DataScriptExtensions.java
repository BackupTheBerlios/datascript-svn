package de.berlios.datascript;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.scoping.impl.ImportUriUtil;

import com.ibm.icu.impl.Assert;

import de.berlios.datascript.dataScript.Import;
import de.berlios.datascript.dataScript.Model;

public class DataScriptExtensions
{
    /**
     * Finds all models directly or indirectly used by a given one.
     * @param model   DataScript model
     * @return transitive closure of imported models
     */
    public static Collection<Model> getAllUsedModels(Model model)
    {
        Set<Model> models = new HashSet<Model>();
        addImportedModels(models, model);
        
        return models;
    }

    /**
     * Recursively adds all imported models of the given model to the result
     * set.
     * @param models  set of imported models
     * @param model   current model
     */
    private static void addImportedModels(Set<Model> models, Model model)
    {
        models.add(model);
        for (Import imported : model.getImports())
        {       
            String importUri = imported.getImportURI();
            Resource resource = ImportUriUtil.getResource(model.eResource(), importUri);
            EObject object = resource.getContents().get(0);
            if (object instanceof Model)
            {
                Model importedModel = (Model) object;
                boolean seen = models.contains(importedModel);
                if (!seen)
                {
                    addImportedModels(models, importedModel);
                }
            }
        }        
    }    
}
