<#include "comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "param.html.ftl">
<html>
  <head>
    <title>${categoryPlainText} ${packageName}.${type.name}</title>
    <link rel="stylesheet" type="text/css" href="../webStyles.css">
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
        <tr>
          <td valign="top" id="tabIdent"><@linkedtype field.type/><@arglist field/></td>
          <td valign="bottom"><a href="#${field.name}" class="fieldLink">${field.name}</a>${field.arrayRange!""}${field.optionalClause}<#if (field.constraint)??>
 : ${field.constraint}</#if>;</td>
        </tr>
</#list>
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

  </body>
</html>