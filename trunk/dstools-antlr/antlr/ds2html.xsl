<?xml version='1.0'?>
<!--
 * BSD License
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
-->

<!-- ds2html.xsl
     Stylesheet for producing Javadoc-style HTML documentation
     from a DataScript specification
     doxygen-like HTML
     (C) 2006 Harald Wellmann, Harman/Becker
     
     version 0.1  26 Jul 2006
-->

<!-- Unlike HTML, non-breaking space  is not defined by default. -->
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" indent="yes"/>

<!-- ====================================================================== -->

  <xsl:template match="/MEMBERS">
  <html>
    <head>
      <title>DataScript Module <xsl:value-of select="@name"/></title>
      <style type="text/css">
	  TD.msgdetail { background-color: #f2f2ff; font-weight: bold; 
	                 color:blue; vertical-align:top; }
      </style>
    </head>
    <body>
      <h1>DataScript Module <xsl:value-of select="@name"/></h1>

      <h2>Synopsis</h2>	

      <h3>Types</h3>
      <ul>		
        <xsl:apply-templates select="FIELD/*">
          <xsl:sort select="ID"/>
        </xsl:apply-templates>
      </ul> 


      <h2>Detailed Documentation</h2>

      <xsl:apply-templates select="FIELD" mode="topleveltype"/>

    </body>
  </html>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="FIELD" mode="topleveltype">
    <xsl:apply-templates select="*" mode="summary"/>
    <h3>Member Details</h3>
    <xsl:apply-templates select="*" mode="detail"/>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="STRUCT">
    <li><xsl:call-template name="linkbyname"/></li>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template name="linkbyname">
    <a>
      <xsl:attribute name="href">
	  <xsl:text>#</xsl:text>
	  <xsl:value-of select="ID"/>
       </xsl:attribute>
      <xsl:value-of select="ID"/>
    </a>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="STRUCT" mode="summary">
    <xsl:call-template name="detailsheader">
      <xsl:with-param name="prefix" select="'sequence'"/>
    </xsl:call-template>
    <h3>Type Description</h3>
    <xsl:apply-templates select="../DOC" mode="doc"/>

    <h3>Member Overview</h3>
    <table cellpadding="5" cellspacing="0" border="0" >
      <xsl:apply-templates select="MEMBERS/FIELD" mode="field"/>	
    </table>    
    <br/><br/>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="STRUCT" mode="detail">
    <dl>
      <xsl:apply-templates select="MEMBERS/FIELD" mode="fielddoc"/>	
    </dl>    
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="UNION" mode="detail">
    <xsl:call-template name="detailsheader">
      <xsl:with-param name="prefix" select="'union'"/>
    </xsl:call-template>

    <table cellpadding="5" cellspacing="0" border="0" >
      <xsl:apply-templates select="MEMBERS/FIELD" mode="field"/>	
    </table>    
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="ENUM" mode="detail">
    <xsl:call-template name="detailsheader">
      <xsl:with-param name="prefix" select="'enumeration'"/>
    </xsl:call-template>

    <h3>Type Description</h3>
    <xsl:apply-templates select="../DOC" mode="doc"/>

    <h3>Member Overview</h3>
    <table cellpadding="5" cellspacing="0" border="0" >
      <xsl:apply-templates select="MEMBERS/ITEM" mode="enum"/>	
    </table>    

    <h3>Member Details</h3>
    <table cellpadding="5" cellspacing="0" border="0" >
      <dl>
        <xsl:apply-templates select="MEMBERS/ITEM" mode="fielddoc"/>	
      </dl>  
    </table>    
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="ITEM" mode="enum">
    <tr>
      <td><xsl:value-of select="ID"/></td>
      <xsl:if test="INTEGER_LITERAL">
        <td>= <xsl:value-of select="INTEGER_LITERAL"/></td>
      </xsl:if>
    </tr>  
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="FIELD" mode="field">
    <tr>
      <td><xsl:apply-templates select="*[1]" mode="type"/></td>
      <td><xsl:value-of select="ID"/></td>
    </tr>  
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="FIELD|ITEM" mode="fielddoc">
      <dt><b><xsl:value-of select="ID"/></b></dt>
      <dd><xsl:value-of select="DOC"/></dd>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="TYPEREF" mode="type">
      <a>
        <xsl:attribute name="href">
	  <xsl:text>#</xsl:text>
	  <xsl:value-of select="ID"/>
        </xsl:attribute>
        <xsl:value-of select="ID"/>
      </a>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="ARRAY" mode="type">
    <xsl:apply-templates select="*[1]" mode="type"/>
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select="*[2]" mode="expr"/>
    <xsl:text>]</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="UINT8" mode="type">
    <xsl:text>uint8</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="UINT16" mode="type">
    <xsl:text>uint16</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="UINT32" mode="type">
    <xsl:text>uint32</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="UINT64" mode="type">
    <xsl:text>uint32</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="INT8" mode="type">
    <xsl:text>int8</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="INT16" mode="type">
    <xsl:text>int16</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="INT32" mode="type">
    <xsl:text>int32</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="INT64" mode="type">
    <xsl:text>int32</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="BIT" mode="type">
    <xsl:text>bit</xsl:text><xsl:apply-templates mode="bitfield"/>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="INTEGER_LITERAL" mode="bitfield">
    <xsl:text>:</xsl:text><xsl:value-of select="."/>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="*" mode="bitfield">
    <xsl:text>&lt;</xsl:text><xsl:value-of select="."/><xsl:text>&gt;</xsl:text>
  </xsl:template>

<!-- ====================================================================== -->

  <xsl:template match="*" mode="type">
  </xsl:template>

  <xsl:template match="*" mode="summary">
  </xsl:template>

  <xsl:template match="DOC" mode="detail">
  </xsl:template>

  <xsl:template match="DOC" mode="doc">
    <xsl:apply-templates select="P"/>
  </xsl:template>

  <xsl:template match="P">
    <p><xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="*">
  </xsl:template>



  <xsl:template name="detailsheader">
    <xsl:param name="prefix"/>
    <a>
      <xsl:attribute name="name">
	 <xsl:value-of select="ID"/>
      </xsl:attribute>
    </a>

    <span style="font-weight:bold; color:blue; background-color:#f2f2ff">
    <table width="100%" style="background-color:#f2f2ff"><tr><td>
    <table cellpadding="3" cellspacing="0" border="0" >
      <tr>
	<td class="msgdetail">
	  <i><xsl:value-of select="$prefix"/></i>&nbsp;
	  <xsl:value-of select="ID"/>
	</td>
      </tr>

    </table>
    </td></tr></table>
    </span>

  </xsl:template>


<!-- ====================================================================== -->

</xsl:stylesheet>