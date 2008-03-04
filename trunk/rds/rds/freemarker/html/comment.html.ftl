<#macro comment doc>
<#list doc.paragraphs as para>
    <div class="docuTag">${para}</div>
</#list>
<#if doc.paragraphs?size == 0>&lt;<i>no documentation found</i>&gt;</#if>
<#list doc.tags as tag>
    <div class="docuTag" id="${tag.name?upper_case}">
      <span>${tag.name}:</span>
<#switch tag.name>
  <#case "todo">
      ${tag.head} ${tag.tail}
      <#break>
  <#case "see">
      <a href="${tag.head}.html">${tag.head}</a>
      <#break>
  <#case "param">
      <code>${tag.head}</code> &minus;&gt; ${tag.tail}
      <#break>
  <#default>
    <#stop "unknown tag ${tag.name}">    
</#switch>
    </div>
</#list>
</#macro>
