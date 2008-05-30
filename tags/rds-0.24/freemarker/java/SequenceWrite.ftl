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
<#assign LabeledFieldCnt=0>


    // Constructor for ${className} 
    public ${className}(
<#list fields as field>
    <#if field.labelExpression??>
        <#assign LabeledFieldCnt=LabeledFieldCnt+1>
    </#if>
        ${field.javaTypeName} ${field.name}<#if field_has_next>, </#if>
</#list>
        )
    {
<#list fields as field>
	<#if equalsCanThrowExceptions && field.isSimple>
        if ((#{field.maxVal}L < ${field.name}) || (${field.name} < #{field.minVal}L))
            throw new DataScriptError("Value " + ${field.name} + " of field '${field.name}' exceeds the range of type ${field.typeName}!");
	</#if>
        this.${field.name} = ${field.name};
</#list>
    }


    public void write(String __filename) throws IOException
    {
        FileBitStreamWriter __out = new FileBitStreamWriter(__filename);
        __cc = new CallChain();
        write(__out, __cc);
        __out.close();
    }


    public void write(BitStreamWriter __out) throws IOException
    {
        __cc = new CallChain();
        write(__out, __cc);
    }


    public void write(BitStreamWriter __out, CallChain __cc) throws IOException
    {
        this.__cc = __cc;
<#if LabeledFieldCnt!=0>
        computeLabelValues();

</#if>
        try 
        {
            __cc.push("${className}", this);
            __fpos = __out.getBitPosition();
            try 
            {
<#list fields as field>
    <#if field.optionalClause??>
                if (${field.optionalClause})
                {
        <#if field.constraint??>
                    if (!(${field.constraint}))
                    {
                        throw new DataScriptError("constraint violated");
                    }
        </#if>
        <#if field.hasAlignment>
                    __out.alignTo(#{field.alignmentValue});
        </#if>
        <#if field.labelExpression??>
                if (__out.getBitPosition() != ${field.labelExpression})
                {
                    throw new DataScriptError("wrong offset for field '${field.name}'");
                }

        </#if>
                    ${field.writeField}
                }
    <#else>
        <#if field.constraint??>
                if (!(${field.constraint}))
                {
                   throw new DataScriptError("constraint violated");
                }
        </#if>
        <#if field.hasAlignment>
                __out.alignTo(#{field.alignmentValue});
        </#if>
        <#if field.labelExpression??>
                if (__out.getBitPosition() != ${field.labelExpression})
                {
                    throw new DataScriptError("wrong offset for field '${field.name}'");
                }

        </#if>
                ${field.writeField}
    </#if>
</#list>
            } 
            catch (DataScriptError __e1) 
            {
                __out.setBitPosition(__fpos);
                throw __e1;
            }
        }
        finally 
        { 
            __cc.pop(); 
        }
    }

<#if LabeledFieldCnt!=0>

    private void computeLabelValues()
    {
        int bitoffset = 0;
    <#list fields as field>
        <#assign indent="">
        <#if field.optionalClause??>
        <#assign indent="    ">
        if (${field.optionalClause})
        {
        </#if><#t>
        <#if field.hasAlignment>
        ${indent}if (bitoffset % #{field.alignmentValue} != 0)
            ${indent}bitoffset = ((bitoffset / #{field.alignmentValue}) + 1) * #{field.alignmentValue};
        </#if>
        <#if field.labelExpression??><#t>
        ${indent}${field.labelSetter}((${field.labelTypeName})Util.bitsToBytes(bitoffset));
            <#assign LabeledFieldCnt=LabeledFieldCnt-1>
            <#if LabeledFieldCnt == 0>
              <#if field.optionalClause??>
        }
              </#if>
                <#break>
            </#if>
        </#if>
        <#if !field.isSimple>
            <#assign bitsizeof>${field.getterName}().bitsizeof()</#assign>
        <#elseif (field.canonicalTypeName == "datascript.ast.BitFieldType" && field.bitFieldLength == 0)>
            <#assign bitsizeof>${field.getterName}().bitLength()</#assign>
        <#else>
            <#assign bitsizeof=field.bitsizeof>
        </#if>
        ${indent}bitoffset += ${bitsizeof};	// ${field.name}
        <#if field.optionalClause??>
        }
        </#if>
    </#list>
    }
</#if>
