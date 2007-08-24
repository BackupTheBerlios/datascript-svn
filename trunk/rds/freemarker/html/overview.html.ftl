<html>
  <head>
    <title>DataScript Class List</title>
    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>

  <body>
    <ul class="classlist">
<#list types as type>
      <li id="${type.packageNameAsID}"><a class="${type.style}" href="content/${type.packageName}/${type.name}.html" title="Type: ${type.category}" target="detailedDocu" >${type.name}</a></li>
</#list>      
    </ul>    
  </body>
<#list packages as pkg>  
  <style id="style_${pkg}" type="text/css">
  	li#${pkg} { display: list-item; }
  </style>   
</#list>      
</html>    