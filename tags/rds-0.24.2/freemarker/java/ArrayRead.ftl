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
<#if isVariable>
                <#lt>{
                    ArrayList<${elType}> v = new ArrayList<${elType}>();
                    long __afpos = 0;
                    try
                    {
                        while (true)
                        {
                            __afpos = __in.getBitPosition();
                            v.add(${currentElement});
                        }
                    }
                    catch (DataScriptError __e)
                    {
                        __in.setBitPosition(__afpos);
                    }
                    ${setterName}(new ObjectArray<${elType}>(v));
                }
<#else>
                <#lt>{
                    long __maxIndex = ${lengthExpr};
                    if (__maxIndex > Integer.MAX_VALUE)
                        throw new DataScriptError("truncate indexvalue will fail");
                    ArrayList<${elType}> v = new ArrayList<${elType}>((int)__maxIndex);
                    for (int __index = 0; __index < __maxIndex; __index++) 
                    {
<#if equalsCanThrowExceptions>
                        ${elType} __newElType;
                        try
                        {
                            __newElType = ${currentElement};
                            v.add(__newElType);
                        }
                        catch(DataScriptError dse)
                        {
                            throw new DataScriptError("invalid ${elType} item in array at index " + __index, dse);
                        }
<#else>
                        v.add(${currentElement});
</#if>
                    }
                    ${setterName}(v);
                }
</#if>
