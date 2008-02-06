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


package datascript.emit.html;


import datascript.ast.ArrayType;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.SqlDatabaseType;
import datascript.ast.SqlMetadataType;
import datascript.ast.SqlPragmaType;
import datascript.ast.SqlTableType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.UnionType;



/**
 * @author HWellmann
 * 
 */
public class LinkedType
{
    private TypeInterface type;
    private boolean isDoubleDefinedType;
    private String style;
    private String category = "";


    public LinkedType(TypeInterface type)
    {
        this.type = type;
        this.isDoubleDefinedType = false;
        init();
    }


    public LinkedType(TypeInterface type, boolean isDoubleDefinedType)
    {
        this.type = type;
        this.isDoubleDefinedType = isDoubleDefinedType;
        init();
    }


    private void init()
    {
        while (type instanceof ArrayType)
        {
            type = ((ArrayType) type).getElementType();
            style = "arrayLink";
            category += "array of ";
        }

        if (TypeNameEmitter.isBuiltinType(type))
        {
            style = "noStyle";
        }
        else
        {
            // generate styles depending on the field type

            if (type instanceof SequenceType)
            {
                style = "sequenceLink";
                category += createTitle("Sequence");
            }
            else if (type instanceof UnionType)
            {
                style = "unionLink";
                category += createTitle("Union");
            }
            else if (type instanceof EnumType)
            {
                style = "enumLink";
                category += createTitle("Enum");
            }
            else if (type instanceof datascript.ast.Subtype)
            {
                style = "subtypeLink";
                category += createTitle("Subtype");
            }
            else if (type instanceof datascript.ast.ConstType)
            {
                style = "consttypeLink";
                category += createTitle("Consttype");
            }
            else if (type instanceof SqlMetadataType)
            {
                style = "sqlMetaLink";
                category += createTitle("SQL Metadata");
            }
            else if (type instanceof SqlPragmaType)
            {
                style = "sqlPragmaLink";
                category += createTitle("SQL Pragma");
            }
            else if (type instanceof SqlTableType)
            {
                style = "sqlTableLink";
                category += createTitle("SQL Table");
            }
            else if (type instanceof SqlDatabaseType)
            {
                style = "sqlDBLink";
                category += createTitle("SQL Database");
            }
            else if (type instanceof TypeInstantiation)
            {
                style = "instantLink";
                category += createTitle("TypeInstantiation");
            }
            else if (type instanceof TypeReference)
            {
                style = "referenceLink";
                category += createTitle("TypeReference");
            }
            else
            {
                style = "noStyle";
            }
        }
    }


    private String createTitle(String category)
    {
        String packageName = "";
        if (isDoubleDefinedType)
        {
            packageName = ", defined in: " + type.getPackage().getPackageName();
        }
        return category + packageName;
    }


    public String getName()
    {
        String typeName = TypeNameEmitter.getTypeName(type);
        return typeName;
    }


    public String getStyle()
    {
        return style;
    }


    public String getCategory()
    {
        return category;
    }


    public String getPackageName()
    {
        return type.getPackage().getPackageName();
    }


    public String getPackageNameAsID()
    {
        return type.getPackage().getPackageName().replace('.', '_');
    }
}
