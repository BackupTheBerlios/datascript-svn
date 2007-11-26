<#include "comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "param.html.ftl">
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
      <table style="empty-cells:show">
      <tbody id="tabIdent">
        <tr><td colspan=4>${categoryKeyword} ${type.name}<@parameterlist type/> on ${selector}</td></tr>
        <tr><td colspan=2>{</td>
            <td rowspan="${(choiceType.choiceMembers?size*2)+1}">&nbsp;</td>
            <td></td></tr>
<#list choiceType.choiceMembers as member>
        <tr>
          <td valign="top" id="tabIdent">
    <#if member.cases?has_content>
        <#list member.cases as c>
            case ${c}:<br/>
        </#list>
    <#else>
            default:<br/>
    </#if>
          </td>
          <td colspan=3></td>
        </tr>
        <tr>
          <td></td>
          <td valign="bottom">
    <#assign fname = member.field.name>
    <#assign array = member.field.arrayRange!"">
    <#assign opt = member.field.optionalClause!"">
    <#assign c = member.field.constraint!"">
            <@linkedtype member.fieldType/><@arglist member.field/>
          </td>
          <td valign="bottom">
            <a href="#${fname}" class="fieldLink">${fname}</a>${array}${opt}${c};
          </td>
        </tr>
</#list>
<#if functions?has_content>
      </tbody>
      </table>
      <table>
      <tbody id="tabIdent">
  <#list functions as function>
	<tr><td colspan=4 id="tabIdent">&nbsp;</td></tr>
        <tr>
          <td colspan=4 valign="top" id="tabIdent">function ${function.returnTypeName} ${function.funtionType.name}()</td>
        </tr>
        <tr><td colspan=4 id="tabIdent">{</td></tr>
        <tr>
          <td valign="top" id="tabIdent2">return</td>
          <td colspan=3>${function.result};</td></tr>
        <tr><td colspan=4 id="tabIdent">}</td></tr>
  </#list>
</#if>
        <tr><td colspan=4>};</td></tr>
      </tbody>
      </table>
    </td></tr>
    </table>


    <h2 class="msgdetail">Member Details</h2>
    
    <dl>
<#list choiceType.choiceMembers as member>
      <dt class="memberItem"><a name="${member.field.name}">${member.field.name}:</a></dt>  
      <dd class="memberDetail">
      <@comment member.fieldDocumentation/>
      </dd>
</#list>
    </dl>

  </body>
</html>