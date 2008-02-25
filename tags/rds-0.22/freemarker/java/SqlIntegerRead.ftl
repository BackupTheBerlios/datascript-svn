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

    public ${fittingType} pack()
    {
        ${fittingType} retValue = 0;
<#assign shiftBits = 0>
<#assign revFields = fields?reverse>
<#list revFields as field>
        retValue |= ((${fittingType})${field.name} & #{field.bitmask}L)<#if shiftBits != 0> << #{shiftBits}</#if>;
    <#assign shiftBits = shiftBits + field.bitsize>
</#list>
        return retValue;
    }


    public ${name}(${fittingType} __in${formalParameterList}) throws Exception 
    {
        read(__in, new CallChain()${actualParameterList});
    }


    public ${name}(${fittingType} __in, CallChain __cc${formalParameterList}) throws Exception 
    {
        read(__in, __cc${actualParameterList});
    }


    public void read(${fittingType} __in, CallChain __cc${formalParameterList}) throws Exception 
    {
<#assign shiftBits = 0>
<#list revFields as field>
        ${field.name} = (${field.javaTypeName})(__in >> #{shiftBits} & #{field.bitmask}L);
    <#assign shiftBits = shiftBits + field.bitsize>
</#list>
    }
}

// END OF FILE
