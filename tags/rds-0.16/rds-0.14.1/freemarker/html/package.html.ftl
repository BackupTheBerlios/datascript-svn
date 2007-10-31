<html>
  <head>
    <title>DataScript Package-List</title>
    <link rel="stylesheet" type="text/css" href="webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script language="JavaScript">
    	/*
    	 * returns an array of CSS rules
    	 */
    	function getCSS(docToChange, index)
    	{
    	return null;
    		if (!docToChange.styleSheets)
    			return null;

			var theRules = new Array();
			var styleSheet = docToChange.styleSheets[index];
			if (styleSheet.cssRules)
				theRules = styleSheet.cssRules
			else if (styleSheet.rules)
				theRules = styleSheet.rules
			else
				return null;
			return theRules;
    	}


    	function showhidePackage(clickedElement)
    	{
	    	clickedElement.style.listStyleType =  
	    		(clickedElement.style.listStyleType == 'circle')? 'disc' : 'circle';

	   		docToChange = parent.overview.document;
			theRules = getCSS(docToChange, 0);
			if (theRules)
			{
				styleToChange = theRules[theRules.length-1].style;				
				styleToChange.display = 
					(styleToChange.getPropertyValue('display') == 'none')? 'list-item' : 'none';
			}
			else
			{
    			var styleItemId = "style_" + clickedElement.firstChild.firstChild.data.replace(/\./g, '_');
		    	var styleElement = docToChange.getElementById(styleItemId);
				var styleElementSheet = (styleElement.sheet)? styleElement.sheet : styleElement.styleSheet;
		    	var styleElementRules = (styleElementSheet.rules)? styleElementSheet.rules : styleElementSheet.cssRules;
		    	var styleElementStyle = styleElementRules[0].style;
		    	styleElementStyle.display = (styleElementStyle.display == "none")? "list-item" : "none";
			}
    	}
    </script>
  </head>

  <body>
    <h2>DataScript Package: ${rootPackageName}</h2>
<#list packages as pkg>
    <ul class="packagelist" onclick="showhidePackage(this);"><li>${pkg}</li></ul>   
</#list>
  </body>
</html>
  