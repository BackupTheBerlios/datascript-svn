<#macro comment doc>
<div class="docuTag">
<#if doc.paragraphs?size == 0>
    &lt;<i>no documentation found</i>&gt;
<#else>
  <#list doc.paragraphs as para>
    ${para}
  </#list>
</#if>
</div>
<#list doc.tags as tag>
    <div class="docuTag" id="${tag.name?upper_case}">
  <#switch tag.name>
    <#case "todo">
      <span>todo:</span> ${tag.head} ${tag.tail}
      <#break>
    <#case "seelink">
      <span>see:</span> <a href="${tag.head}.html">${tag.head}</a>
      <#break>
    <#case "see">
      <span>see:</span> ${tag.head}
      <#break>
    <#case "param">
      <span>param:</span> <code>${tag.head}</code> &minus;&gt; ${tag.tail}
      <#break>
    <#default>
      <#stop "unknown tag ${tag.name}">    
  </#switch>
    </div>
</#list>
</#macro>
