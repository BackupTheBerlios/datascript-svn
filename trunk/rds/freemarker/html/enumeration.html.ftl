<#include "comment.html.ftl">
<#include "linkedtype.html.ftl">
<html>
  <head>
    <title>enum ${packageName}.${type.name}</title>
    <link rel="stylesheet" type="text/css" href="../../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>${packageName}</h2>
    <div class="msgdetail"><i>enum</i> ${type.name}</div>
    <p/>
    <@comment documentation/>

    <table>
    <tr><td class="docuCode">
      <table>

	  <tr><td colspan=3>enum ${baseType} ${type.name}</td></tr>
	  <tr><td>{</td><td rowspan=${type.numItems+1}">&nbsp;</td><td></td></tr>
<#list items as item>	  
          <tr>
       	    <td id="tabIdent"><a href="#${item.name}" class="fieldLink">${item.name}</a></td>
            <td>= ${item.value}<#if item_has_next>,</#if></td>
	  </tr>
</#list>
	  <tr><td colspan=3>};</td></tr>
      </table>
    </td></tr>
    </table>


    <h2>Item Details</h2>
    
    <dl>
<#list items as item>	  
      <dt class="memberItem"><a name="${item.name}">${item.name}:</a></dt>  
      <dd class="memberDetail">
      <@comment getItemDocumentation(item)/>
      </dd>
</#list>
    </dl>

<#assign numOfContainers = 0>    
<#list containers as container>
    <#assign numOfContainers = containers?size>
    <#break>
</#list>
<#if (numOfContainers > 0)>
    <h4>Used By</h4>
    <table>
    <tr><td class="docuCode">
      <table>
      <tbody id="tabIdent">
        <tr>
          <td valign="top">
<#list containers as container>      
          <#if container.linkedType??>
          <@linkedtype container.linkedType/><br/>
          </#if>
</#list>
          </td>
        </tr>
      </tbody>
      </table>
    </td></tr>
    </table>
</#if>

  </body>
</html>