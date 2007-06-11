<#macro linkedtype type>
<#if (type.packageName)??>
  <a class="${type.style}" href="${type.name}.html" title="Type: ${type.category}" target="detailedDocu" >${type.name}</a>
<#else>
  ${type.name}
</#if>
</#macro>
