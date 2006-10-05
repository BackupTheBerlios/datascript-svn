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
package datascript;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import datascript.parser.DSConstants;
import datascript.templates.XmlDumperTemplate;
import datascript.templates.XmlStructTemplate;
import datascript.templates.XmlUnionTemplate;

/**
 * @author HWellmann
 *
 */
public class XmlDumperEmitter
{
    private PrintWriter writer;
    private XmlDumperTemplate xmlDumperTemplate;
    private XmlStructTemplate structTemplate;
    private XmlUnionTemplate unionTemplate;
    private CompoundType globals;
    private TypeInterface currentType;
    private String packageName;
    private JavaEmitter javaEmitter;

    public XmlDumperEmitter(JavaEmitter javaEmitter, 
                            PrintWriter writer, 
                            String packageName) throws FileNotFoundException
    {        
        this.javaEmitter = javaEmitter;
        this.writer = writer;
        this.packageName = packageName;
        xmlDumperTemplate = new XmlDumperTemplate();
        structTemplate = new XmlStructTemplate();
        unionTemplate = new XmlUnionTemplate();
    }
    
    public void emit(CompoundType globals)
    {
        this.globals = globals;
        String result = xmlDumperTemplate.generate(this);
        writer.print(result);
        //System.out.print(result);
    }
    
    public String emitTypes() 
    {
        return emitNestedTypes(globals);
    }
    
    public String emitNestedTypes(CompoundType compound) 
    {
        StringBuffer result = new StringBuffer();
        for (TypeInterface type: compound.getNestedTypes_())
        {
            currentType = type;
            if (type instanceof StructType)
            {
                StructType struct = (StructType) type;
                if (struct.fields.size() > 0)
                {
                    result.append(structTemplate.generate(this));
                    String nested = emitNestedTypes(struct);
                    result.append(nested);
                }
            }
            else if (type instanceof UnionType)
            {
                result.append(unionTemplate.generate(this));
                String nested = emitNestedTypes((UnionType)type);               
                result.append(nested);
            }
        }
        return result.toString();
    }
    
    public String emitStructFieldVisitor(Field field)
    {
        StringBuffer result = new StringBuffer("visit");
        TypeInterface type = field.getType();
        
        if (type instanceof SetType)
        {
            type = ((SetType)type).getType();
        }
        if (type instanceof BitFieldType)
        {
            BitFieldType b = (BitFieldType)type;
            int kind = b.getKind();
            String typeName = null;
            String param = "";
            switch (kind)
            {
                case DSConstants.INT8:
                    typeName = "uint8";
                    break;
                case DSConstants.INT16:
                    typeName = "uint16";
                    break;
                case DSConstants.INT32:
                    typeName = "uint32";
                    break;
                case DSConstants.INT64:
                    typeName = "uint64";
                    break;
                default:
                    typeName = "BitField";
                    param = ", 0 /*dummy*/";
            }
            result.append(typeName + "(" + "n." + field.getName() + param + ")");
        }
        else if (type instanceof BuiltinType)
        {
            BuiltinType b = (BuiltinType)type;
            result.append(b + "(" + "n." + field.getName() + ")");
        }
        else if (type instanceof SetType)
        {
            SetType s = (SetType)type;
            BuiltinType b = (BuiltinType) s.getType();
            result.append(b + "(" + "n." + field.getName() + ")");
        }
        else if (type instanceof ArrayType)
        {
            result.append("Array(" + "n." + field.getName() + ")");
        }
        else if (type instanceof CompoundType)
        {
            result.append("(" + "n." + field.getName() + ")");
        }
        else if (type instanceof TypeInstantiation)
        {
            //TypeInterface baseType = ((TypeInstantiation)type).getBaseType();
            result.append("(" + "n." + field.getName() + ")");
        }
        else
        {
            throw new InternalError("unhandled type " + type.getClass().getName());
        }
        return result.toString();
    }
    

    
    public String emitUnionFieldVisitor(Field field)
    {
        HashMap fieldName2armName = javaEmitter.getArmNameMapping(field.getCompound());
        
        StringBuffer result = new StringBuffer("visit");
        TypeInterface type = field.getType();
        if (type instanceof BuiltinType)
        {
            BuiltinType b = (BuiltinType)type;
            String fieldName = (String)fieldName2armName.get(field.getName());            
            result.append(b + "(" + "n." + fieldName + ")");
        }
        else if (type instanceof CompoundType)
        {
            CompoundType c = (CompoundType) type;
            if (c.fields.size() == 0)
            {
                return "// empty type";
            }
            result.append("((" + JavaEmitter.printType(c) + ")n.object_choice)");
        }
        else if (type instanceof TypeInstantiation)
        {
            TypeInterface baseType = ((TypeInstantiation)type).getBaseType();
            CompoundType c = (CompoundType)baseType;
            result.append("((" + JavaEmitter.printType(c) + ")n.object_choice)");
        }
        else
        {
            throw new InternalError("unhandled type " + type.getClass().getName());
        }
        return result.toString();
    }
    
    
    
    
    
    public String getPackage()
    {
        return packageName;
    }
    
    public String getRoot()
    {
        return "root";
    }
    
    public TypeInterface getType()
    {
        return currentType;
    }
}
