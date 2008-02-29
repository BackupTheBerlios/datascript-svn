<#include "comment.html.ftl">
<#include "linkedtype.html.ftl">
<#include "usedby.html.ftl">
<html>
  <head>
    <title>Subtype ${packageName}.${type.name}</title>
    <link rel="stylesheet" type="text/css" href="../../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>

    <h2>${packageName}</h2>
    <div class="msgdetail"><i>Subtype</i> ${type.name}</div>
    <p/>
    <@comment documentation/>

    <table>
    <tr><td class="docuCode">
      <table>
        <tr>
          <td colspan=3>subtype <@linkedtype baseType/><#--@arglist baseType--> ${type.name};</td>
        </tr>
      </table>
    </td></tr>
    </table>

<@usedby containers/>
    
  </body>
</html>