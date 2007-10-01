xsltproc  --stringparam body.start.indent 0in --stringparam title.margin.left 0in --stringparam fop.extensions 1 --stringparam section.autolabel 1 --stringparam paper.type A4 /usr/share/xml/docbook/stylesheet/nwalsh/1.69.0/fo/docbook.xsl DataScriptLanguageOverview.xml > DataScriptLanguageOverview.fo

fop DataScriptLanguageOverview.fo DataScriptLanguageOverview.pdf

xsltproc  --stringparam body.start.indent 0in --stringparam title.margin.left 0in --stringparam fop.extensions 1 --stringparam section.autolabel 1 --stringparam paper.type A4 /usr/share/xml/docbook/stylesheet/nwalsh/1.69.0/fo/docbook.xsl InstallationGuide.xml > InstallationGuide.fo

fop InstallationGuide.fo InstallationGuide.pdf

