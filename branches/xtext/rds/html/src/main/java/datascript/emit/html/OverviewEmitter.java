/* BSD License
 *
 * Copyright (c) 2007, Henrik Wedekind, Harman/Becker Automotive Systems
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


package datascript.emit.html;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import antlr.collections.AST;
import datascript.ast.DataScriptException;
import datascript.ast.Package;
import datascript.ast.TypeInterface;
import freemarker.template.Template;
import freemarker.template.TemplateException;



public class OverviewEmitter extends DefaultHTMLEmitter
{
    private final Map<String, LinkedType> typeMap = new TreeMap<String, LinkedType>();
    private final Map<String, Boolean> doubleTypeNames = new HashMap<String, Boolean>();
    private final HashSet<String> packageNames = new HashSet<String>();
    private String packageName;


    public OverviewEmitter(String outputPath)
    {
        super(outputPath);
    }


    public Set<String> getPackageNames()
    {
        return packageNames;
    }


    @Override
    public void endRoot()
    {
        try
        {
            for (String pkgName : datascript.ast.Package.getPackageNames())
            {
                Package pkg = Package.lookup(pkgName);
                for (String typeName : pkg.getLocalTypeNames())
                {
                    boolean isDoubleDefinedType = doubleTypeNames.get(typeName);
                    TypeInterface t = pkg.getLocalType(typeName);
                    LinkedType linkedType = new LinkedType(t, isDoubleDefinedType);
                    String fullTypeName = typeName + "." + pkg.getReversePackageName();
                    typeMap.put(fullTypeName, linkedType);
                }
            }

            Template tpl = cfg.getTemplate("html/overview.html.ftl");
            openOutputFile(directory, "overview" + HTML_EXT);

            tpl.process(this, writer);
            writer.close();
        }
        catch (IOException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TemplateException exc)
        {
            throw new DataScriptException(exc);
        }
    }


    @Override
    public void endPackage(AST p)
    {
        for (String typeName : currentPackage.getLocalTypeNames())
        {
            if (doubleTypeNames.containsKey(typeName))
                doubleTypeNames.put(typeName, true);
            else
                doubleTypeNames.put(typeName, false);
        }
        String pkgName = currentPackage.getPackageName();
        pkgName = pkgName.replace('.', '_');
        packageNames.add(pkgName);
    }


    @Override
    public String getPackageName()
    {
        return packageName;
    }


    public Collection<LinkedType> getTypes()
    {
        return typeMap.values();
    }
}
