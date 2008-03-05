<#--
/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Henrik Wedekind, Harman/Becker Automotive Systems
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


@Generated(
        value = "datascript.tools.DataScriptTool",
        date = "${timeStamp}",
        comments = "generated by ${rdsVersion}"
    )
public class ${name} extends SqlDatabase
{
<#list fields as field>
    private final ${field.javaTypeName} ${field.name} = new ${field.javaTypeName}(this);
</#list>


    public ${name}(String fileName, Mode mode) throws SQLException, ClassNotFoundException
    {
        super(fileName, mode);
        if (mode == Mode.CREATE)
        {
            createSchema();
        }
    }


    @Override
    public void createSchema() throws SQLException
    {
        Connection dbc = getConnection();
        Statement st = dbc.createStatement();
<#if pragmaFields?size != 0>

        // create pragmata
    <#list pragmaFields as pragmaField>
        <#if pragmaField.value??>
        st.executeUpdate("PRAGMA ${pragmaField.name} = ${pragmaField.value}");
        </#if>
    </#list>
</#if>

        // create metadata
        st.executeUpdate("CREATE TABLE __metadata(__name, __value)");
<#if metadataFields?size != 0>
        PreparedStatement pst = 
            dbc.prepareStatement("INSERT INTO __metadata VALUES (?,?)");
    <#list metadataFields as metadataField>
        <#if metadataField.value??>
        pst.setString(1, "${metadataField.name}");
        pst.setObject(2, ${metadataField.value});
        pst.executeUpdate();
        </#if>
    </#list>
</#if>
<#if fields?size != 0>

        // create user defined tables
    <#list fields as field>
        ${field.name}.createTable("${field.name}");
    </#list>
</#if>
    }

}

// END OF FILE
    