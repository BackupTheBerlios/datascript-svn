<#include "comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "param.html.ftl">
<#include "usedby.html.ftl">
<html>
  <head>
    <title>${categoryPlainText} ${packageName}.${type.name}</title>
    <link rel="stylesheet" type="text/css" href="../../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>${packageName}</h2>
    <div class="msgdetail"><i>${categoryPlainText}</i> ${type.name}</div>
    <p/>
    <@comment documentation/>

    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIdent">
        <tr><td colspan=3>${categoryKeyword} ${type.name}<@parameterlist type/></td></tr>
        <tr><td>{</td><td rowspan="${fields?size+1}">&nbsp;</td><td></td></tr>
<#list fields as field>
  <#assign fname = field.name>
  <#assign array = field.arrayRange!"">
  <#assign opt = field.optionalClause>
  <#assign c = field.constraint>
        <tr class="codeMember">
          <td valign="top" id="tabIdent"><@linkedtype field.type/><@arglist field/></td>
          <td valign="bottom">
            <a href="#${fname}" class="fieldLink">${fname}</a>${array}${opt}${c};</td>
        </tr>
</#list>
<#if functions?has_content>
      </tbody>
      </table>
      <table>
      <tbody id="tabIdent">
  <#list functions as function>
	<tr><td colspan=2 id="tabIdent">&nbsp;</td></tr>
        <tr>
          <td colspan=2 valign="top" id="tabIdent">function ${function.returnTypeName} ${function.funtionType.name}()</td>
        </tr>
        <tr><td colspan=2 id="tabIdent">{</td></tr>
        <tr>
          <td valign="top" id="tabIdent2">return</td>
          <td>${function.result};</td></tr>
        <tr><td colspan=2 id="tabIdent">}</td></tr>
  </#list>
</#if>
        <tr><td colspan=3>};</td></tr>
      </tbody>
      </table>
    </td></tr>
    </table>


    <h2 class="msgdetail">Member Details</h2>
    
    <dl>
<#list fields as field>        
      <dt class="memberItem"><a name="${field.name}">${field.name}:</a></dt>  
      <dd class="memberDetail">
      <@comment field.documentation/>
      </dd>
</#list>
    </dl>

<@usedby containers/>

  </body>
</html>