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


    // Contructor for ${className} 
    public ${className}(
<#list fields as field>
        ${field.javaTypeName} ${field.name}<#if field_has_next>, </#if>
</#list>
        )
    {
<#list fields as field>
        this.${field.name} = ${field.name};
</#list>
    }


    public void write(String __filename) throws Exception 
    {
        FileBitStreamWriter __out = new FileBitStreamWriter(__filename);
        write(__out, new CallChain());
        __out.close();
    }


    public void write(BitStreamWriter __out) throws Exception 
    {
        write(__out, new CallChain());
    }


    public void write(BitStreamWriter __out, CallChain __cc) throws Exception 
    {
        try 
        {
            __cc.push("${className}", this);
            __fpos = __out.getBitPosition();
            try 
            {
<#list fields as field>
    <#if field.labelExpression??>
                if (__out.getBitPosition() != ${field.labelExpression})
                {
                    throw new IOException("wrong offset for field '${field.name}'");
                }

    </#if>
    <#if field.optionalClause??>
                if (${field.optionalClause})
                {
        <#if field.constraint??>
                    if (!(${field.constraint}))
                    {
                        throw new IOException("constraint violated");
                    }
        </#if>
                    ${field.writeField}
                }
    <#else>
        <#if field.constraint??>
                if (!(${field.constraint}))
                {
                       throw new IOException("constraint violated");
                }
        </#if>
                ${field.writeField}
    </#if>
</#list>
            } 
            catch (Exception __e1) 
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
