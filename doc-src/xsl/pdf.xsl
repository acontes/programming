<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:date="http://exslt.org/dates-and-times"
	xmlns:destination="http://xml.apache.org/fop/extensions"
	exclude-result-prefixes="date" version="1.0">
	
	<xsl:import
		href="http://docbook.sourceforge.net/release/xsl-ns/1.74.0/fo/docbook.xsl" />
	<xsl:import href="common.xsl" />
	<xsl:import href="pdf_titlepage.xsl" /> 
	
	<!-- ignore the scaling values the someone might put in the XML files 
		<xsl:param name="ignore.image.scaling" select="0">1</xsl:param>-->
	
	<!-- numbering depth: will remove numbers from sections but still display them in TOC  
		<xsl:param name="section.autolabel.max.depth">2</xsl:param>
	-->
	
	<!-- Make graphics in pdf be smaller than page width, if needed-->
	<!--
		to scale images in the pdf to the page width if the image is 
		bigger than the page width and to keep it the same size if smaller use 
		scalefit="1" width="100%" contentdepth="100%" 
		on every image :(
	-->
	
	
	<!-- wraps very long lines -->
	<xsl:attribute-set name="monospace.verbatim.properties">
		<xsl:attribute name="wrap-option">wrap</xsl:attribute>
	</xsl:attribute-set>
	
	<!--  center all images in the tag figure horizontally  -->
	<xsl:attribute-set name="figure.properties">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	
	
	<!--  align all images in the tag informalfigure horizontally  -->
	<xsl:attribute-set name="informalfigure.properties">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="table.properties">
		<xsl:attribute name="keep-together.within-page">3</xsl:attribute>
	</xsl:attribute-set>
	
	<xsl:attribute-set name="informaltable.properties">
		<xsl:attribute name="font-family">Lucida Sans Typewriter</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
	</xsl:attribute-set>
	
	<!--  Changing font sizes -->
	<xsl:param name="monospace.font.family">Helvetica</xsl:param>
	
	<xsl:param name="use.extensions" select="'1'" />	
	<xsl:param name="textinsert.extension" select="'1'" />
	<xsl:param name="highlight.source" select="'1'"/>
	<xsl:param name="linenumbering.everyNth" select="'1'"/>
	<xsl:param name="linenumbering.separator" select="':'"/>
	<xsl:param name="tablecolumns.extension" select="'1'" />
	<xsl:param name="linenumbering.extension" select="'1'" />
	<!---->
	
	
	
	<!-- This avoids having "Draft" mode set on. Avoids the other two lines -->
	<xsl:param name="fop1.extensions" select="'1'" />
	<!-- <xsl:param name="draft.mode">no</xsl:param>  -->
	<!-- <xsl:param name="draft.watermark.image"></xsl:param>  -->
	<xsl:param name="draft.watermark.image" />
	
	<!-- Having long lines be broken up  -->
	<xsl:param name="hyphenate.verbatim">yes</xsl:param>
	
	<!-- Have screens written on darker background -->
	<xsl:attribute-set name="verbatim.properties">
		<xsl:attribute name="background-color">#f1e6d7</xsl:attribute>
	</xsl:attribute-set>
	
	
	<!-- set this parameter to a zero width value -->
	<xsl:param name="body.start.indent">4pt</xsl:param>
	<!--  set the title.margin.left parameter to the negative value of the desired indent.  -->
	<xsl:param name="title.margin.left">-4pt</xsl:param>
	
	<!-- All xrefs have the numbering AND the title -->
	<xsl:param name="xref.with.number.and.title" select="'1'" />
	
	
	<!--  Paper feed -->
	<xsl:param name="paper.type">A4</xsl:param>
	<xsl:param name="page.margin.inner">10mm</xsl:param>
	<xsl:param name="page.margin.outer">13mm</xsl:param>
	<!-- http://www.mail-archive.com/docbook-apps@lists.oasis-open.org/msg09900.html -->
	<!--  usefull for : Double-sided documents are printed with a slightly wider margin on the binding edge of the page. -->
	<xsl:param name="double.sided">0</xsl:param>
	
	
	<!-- Make "compact" listitems be *very* close to each other -->
	<xsl:attribute-set name="compact.list.item.spacing">
		<xsl:attribute name="space-before.optimum">0em</xsl:attribute>
		<xsl:attribute name="space-before.minimum">0em</xsl:attribute>
		<xsl:attribute name="space-before.maximum">0.2em</xsl:attribute>
	</xsl:attribute-set>
	
	<!-- Make listitems close to each other -->
	<xsl:attribute-set name="list.item.spacing">
		<xsl:attribute name="space-before.optimum">0.25em</xsl:attribute>
		<xsl:attribute name="space-before.minimum">0.1em</xsl:attribute>
		<xsl:attribute name="space-before.maximum">0.4em</xsl:attribute>
	</xsl:attribute-set>
	
	
	<!-- Indent to the left all listitems, also modified space before & after -->
	<xsl:attribute-set name="list.block.spacing">
		<xsl:attribute name="margin-left">1em</xsl:attribute>
		<!--Added margin!-->
		<xsl:attribute name="space-before.optimum">0.5em</xsl:attribute>
		<xsl:attribute name="space-before.minimum">0.4em</xsl:attribute>
		<xsl:attribute name="space-before.maximum">0.7em</xsl:attribute>
		<xsl:attribute name="space-after.optimum">0.5em</xsl:attribute>
		<xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
		<xsl:attribute name="space-after.maximum">0.7em</xsl:attribute>
	</xsl:attribute-set>
	
	
	
	<!--from  http://xmlguru.cz/2006/07/docbook-syntax-highlighting   highlighting by content type--> 
	<xsl:template name="language.to.xslthl" match="//programlisting/textobject/textdata">
		<xsl:param name="context"/>
	
		<!-- Expand content (to handle e.g. <textdata fileref="..."/>) -->
		<xsl:variable name="content">
			<xsl:apply-templates select="$context/node()"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$context/@language != ''">
				<xsl:value-of select="$context/@language"/>
			</xsl:when>
			<!-- Files containing <?php are considered as PHP scripts -->
			<xsl:when test="contains($content, '&lt;?php')">php</xsl:when>
			<!-- Files containing </ are considered to be XML files -->
			<xsl:when test="contains($content, '&lt;/')">xml</xsl:when>
			<!-- Files containing class are considered to be java files -->
			<xsl:when test="contains($content, 'class')">java</xsl:when>
			
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

<!-- 
	<xsl:message>
	<xsl:text> OK, question.toc </xsl:text> <xsl:copy-of select="$id" /> 
	</xsl:message>
	
	<xsl:for-each select="./@*">
	<xsl:message>
	<xsl:text> Attribute  <xsl:value-of select="name(.)"/> = <xsl:value-of select="."/>  </xsl:text> 
	</xsl:message>
	</xsl:for-each >
-->