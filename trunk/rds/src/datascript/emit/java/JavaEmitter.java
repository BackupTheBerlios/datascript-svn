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


package datascript.emit.java;


import antlr.collections.AST;
import datascript.ast.Subtype;
import datascript.ast.EnumType;
import datascript.ast.SequenceType;
import datascript.ast.SqlDatabaseType;
import datascript.ast.SqlTableType;
import datascript.ast.SqlIntegerType;
import datascript.ast.UnionType;



public class JavaEmitter extends JavaDefaultEmitter
{
    private SequenceEmitter sequenceEmitter;
    private UnionEmitter unionEmitter;


    public JavaEmitter(String outPathName, String defaultPackageName)
    {
        super(outPathName, defaultPackageName);
    }


    @Override
    public void beginTranslationUnit(AST rootNode, AST unitNode)
    {
        findAllPackageNames(rootNode, allPackageNames);
        setPackageName(unitNode);
    }


    @Override
    public void beginSequence(AST s)
    {
        SequenceType sequence = (SequenceType) s;
        String typeName = getTypeName(sequence);
        openOutputFile(dir, typeName + JAVA_EXT);
        sequenceEmitter = new SequenceEmitter(this, sequence);
        sequenceEmitter.setWriter(writer);
        sequenceEmitter.begin(cfg);
    }


    @Override
    public void endSequence(AST s)
    {
        sequenceEmitter.end(cfg);
        writer.close();
    }


    @Override
    public void beginUnion(AST u)
    {
        UnionType union = (UnionType) u;
        String typeName = getTypeName(union);
        openOutputFile(dir, typeName + JAVA_EXT);
        unionEmitter = new UnionEmitter(this, union);
        unionEmitter.setWriter(writer);

        unionEmitter.begin(cfg);
    }


    @Override
    public void endUnion(AST u)
    {
        unionEmitter.end(cfg);
        writer.close();
    }


    @Override
    public void beginEnumeration(AST e)
    {
        EnumType enumType = (EnumType) e;
        String typeName = getTypeName(enumType);
        openOutputFile(dir, typeName + JAVA_EXT);
        EnumerationEmitter enumEmitter = new EnumerationEmitter(this, enumType);
        enumEmitter.setWriter(writer);

        enumEmitter.emit(cfg, enumType);
    }


    @Override
    public void endEnumeration(AST e)
    {
        writer.close();
    }


    @Override
    public void beginSubtype(AST s)
    {
        Subtype subtype = (Subtype) s;
        String typeName = subtype.getName();
        openOutputFile(dir, typeName + JAVA_EXT);
        SubtypeEmitter subtypeEmitter = new SubtypeEmitter(this, subtype);
        subtypeEmitter.setWriter(writer);

        subtypeEmitter.emit(cfg, subtype);
    }


    @Override
    public void endSubtype(AST s)
    {
        writer.close();
    }


    @Override
    public void beginSqlDatabase(AST s)
    {
        SqlDatabaseType db = (SqlDatabaseType) s;
        String typeName = getTypeName(db);
        openOutputFile(dir, typeName + JAVA_EXT);
        SqlDatabaseEmitter dbEmitter = new SqlDatabaseEmitter(this, db);
        dbEmitter.setWriter(writer);

        dbEmitter.emit(cfg, db);
    }


    @Override
    public void endSqlDatabase(AST s)
    {
        writer.close();
    }


    @Override
    public void beginSqlTable(AST s)
    {
        SqlTableType table = (SqlTableType) s;
        String typeName = getTypeName(table);
        openOutputFile(dir, typeName + JAVA_EXT);
        SqlTableEmitter tableEmitter = new SqlTableEmitter(this, table);
        tableEmitter.setWriter(writer);
        tableEmitter.emit(cfg, table);
    }


    @Override
    public void endSqlTable(AST s)
    {
        writer.close();
    }


    @Override
    public void beginSqlInteger(AST s)
    {
        SqlIntegerType integerType = (SqlIntegerType) s;
        String typeName = getTypeName(integerType);
        openOutputFile(dir, typeName + JAVA_EXT);
        SqlIntegerEmitter integerEmitter = new SqlIntegerEmitter(this, integerType);
        integerEmitter.setWriter(writer);
        integerEmitter.emit(cfg, integerType);
    }


    @Override
    public void endSqlInteger(AST s)
    {
        writer.close();
    }
}
