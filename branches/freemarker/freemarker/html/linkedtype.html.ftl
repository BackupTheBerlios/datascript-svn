<#macro linkedtype type>
<#if type.packageName != "__builtin__">
  <a class="${type.style}" href="${type.name}.html" title="Type: ${type.category}" target="detailedDocu" >${type.name}</a>
<#else>
  ${type.name}<#t>
</#if>
</#macro>
