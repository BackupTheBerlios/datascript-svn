<#--
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
-->
<#include "FileHeader.inc.ftl">
// DS-Import
${packageImports}

import java.sql.*;



public class ${name} implements ${rootPackageName}.__Visitor.Acceptor, SizeOf
{

    public void accept(${rootPackageName}.__Visitor visitor, Object arg)
    {
        visitor.visit(this, arg);
    }


    public int sizeof() 
    {
        return ${rootPackageName}.__SizeOf.sizeof(this);
    }


    public int bitsizeof() 
    {
        return ${rootPackageName}.__SizeOf.bitsizeof(this);
    }


    public boolean equals(Object obj)
    {
        if (obj instanceof ${name})
        {
            ${name} that = (${name}) obj;
<#if equalsCanThrowExceptions>
    <#list fields as field>
            if (<#if (field.optionalClause?? && field.optionalClause?has_content)>(!(${field.optionalClause}))? false : </#if>(<#rt>
            <#switch field.canonicalTypeName>
                <#case "EnumType">
                    <#t>this.${field.name}.getValue() != that.${field.name}.getValue()
                    <#break>

                <#case "BitFieldType">
                    <#if field.bitFieldLength == 0>
                        <#t>this.${field.name}.compareTo(that.${field.name}) != 0
                    </#if>
                    <#break>

                <#case "SequenceType">
                <#case "UnionType">
                <#case "ArrayType">
                <#case "TypeInstantiation">
                <#case "StringType">
                    <#break>

                <#case "StdIntegerType">
                    <#if field.isUINT64>
                        <#t>!this.${field.name}.equals(that.${field.name})
                    </#if>
                    <#break>

                <#default>
                    <#t>this.${field.name} != that.${field.name}
                    <#break>
            </#switch>))  /* ${field.canonicalTypeName} */
                throw new RuntimeException("Field '${field.name}' is not equal!");
    </#list>
            return true;
<#else>
            return 
    <#list fields as field>
                (<#if (field.optionalClause?? && field.optionalClause?has_content)>(!(${field.optionalClause}))? true : </#if>(<#rt>
            <#switch field.canonicalTypeName>
                <#case "EnumType">
                    <#t>this.${field.name}.getValue() == that.${field.name}.getValue()
                    <#break>

                <#case "BitFieldType">
                    <#if field.bitFieldLength == 0>
                        <#t>this.${field.name}.compareTo(that.${field.name}) == 0
                    </#if>
                    <#break>

                <#case "SequenceType">
                <#case "UnionType">
                <#case "ArrayType">
                <#case "TypeInstantiation">
                <#case "StringType">
                    <#break>

                <#case "StdIntegerType">
                    <#if field.isUINT64>
                        <#t>this.${field.name}.equals(that.${field.name})
                    </#if>
                    <#break>

                <#default>
                    <#t>this.${field.name} == that.${field.name}
                    <#break>
            </#switch>)) && /* ${field.canonicalTypeName} */
    </#list>
                true;
</#if>
        }
        return super.equals(obj);
    }


    public ${name}()
    {
    }


    // Contructor for ${name} 
    public ${name}(
<#list fields as field>
        ${field.javaTypeName} ${field.name}<#if field_has_next>, </#if>
</#list>
        )
    {
<#list fields as field>
        this.${field.name} = ${field.name};
        this.${field.name} &= #{field.bitmask}L;
</#list>
    }

