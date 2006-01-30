<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">


<!--  Changing font sizes -->
<xsl:param name="body.font.family">Times New Roman</xsl:param> 
<!-- <xsl:param name="body.font.master">11</xsl:param> -->
<!--<xsl:param name="title.font.family">Times New Roman</xsl:param> 
<xsl:param name="footnote.font.size">9</xsl:param>-->
<xsl:param name="monospace.font.family">Helvetica</xsl:param> 
<!-- <xsl:param name="monospace.font.size">5</xsl:param> -->

<!-- Where should the titles of formal objects be placed? -->
<!--<xsl:param name="formal.title.placement">
figure before
example before
equation before
table before
procedure before
</xsl:param>-->



<!-- Specifies that if an empty toc element is found in a source document, an automated TOC is generated. Note: Depending the value of the generate.toc parameter, setting this parameter to 1 could result in generation of duplicate automated TOCs. So the process.empty.source.toc parameter is primarily useful as an "override." By placing an empty toc in your document and setting this parameter to 1, you can force a TOC to be generated even if generate.toc says not to. -->
<xsl:param name="process.empty.source.toc">1</xsl:param>
<!-- Specifies that the contents of a non-empty "hard-coded" toc element in a source document are processed to generate a TOC in output. Note: This parameter has no effect on automated generation of TOCs. An automated TOC may still be generated along with the "hard-coded" TOC. To suppress automated TOC generation, adjust the value of the generate.toc paramameter. The process.source.toc parameter also has no effect if the toc element is empty; handling for an empty toc is controlled by the process.empty.source.toc parameter. -->
<xsl:param name="process.source.toc">0</xsl:param>


<!-- Turn on admonition graphics. -->
<xsl:param name="admon.graphics" select="'1'"/>
<!-- <xsl:param name="admon.graphics.path"></xsl:param> -->
<!--  TODO : make nice graphics for the next/prev buttons-->


<xsl:param name="callout.graphics">1</xsl:param>
<xsl:param name="callout.graphics.path"></xsl:param>
<xsl:param name="callout.list.table">1</xsl:param>
<!--  TODO : are we going to use these callout graphics ? We're not using images here!-->

<!-- force all sections to have a number assigned, like "1. First section"-->
<xsl:param name="section.autolabel">1</xsl:param>
<!-- stop labelling at the fourth nesting level -->
<xsl:param name="section.autolabel.max.depth">4</xsl:param>
<!-- sections bear the names of their inherited sections, like in "4.2.1.3. A subsubsubsection" -->
<xsl:param name="section.label.includes.component.label">1</xsl:param>
<!--  Force chapter 2 of part 3 to be labelled as Chap III.2 -->
<!-- <xsl:param name="component.label.includes.part.label">1</xsl:param> -->


<xsl:param name="generate.index">0</xsl:param>

<xsl:param name="generate.toc">
    appendix  toc,title
    book      toc,title,figure,table,example,equation
    article   nop
    chapter   title
    part      toc,title
    qandadiv  toc
    qandaset  nop
</xsl:param>

<!-- The header image -->
<xsl:param name="header.image.filename">images/ProActiveLogoSmall.png</xsl:param> 
<!-- the 3 institutes images -->
<xsl:param name="threeinstitutes.image.filename">images/logo-cnrs-inria-unsa.png</xsl:param> 

</xsl:stylesheet>
