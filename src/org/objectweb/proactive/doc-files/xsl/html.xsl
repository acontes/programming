<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:date="http://exslt.org/dates-and-times"  
                exclude-result-prefixes="date"  
                version="1.0">

<xsl:import href="common.xsl"/>
<xsl:import href="html.titlepage.xsl"/>

<!-- Use chapter ids for html filenames -->
<xsl:param name="use.id.as.filename">1</xsl:param>

<!-- Configure the html stylesheet to use -->
<xsl:param name="html.stylesheet" select="'ProActive.css'"/>

<!-- Only make new files for new chapters and parts, not for sections -->
<xsl:param name="chunk.section.depth">0</xsl:param>
<!-- Please put list of tables/examples/figures on separate page from full table of contents -->
<xsl:param name="chunk.separate.lots">1</xsl:param> 
<!--  Indent html output -->
<xsl:param name="chunker.output.indent">yes</xsl:param> 

<!-- No idea what these two do -->
<!-- <xsl:param name="chunk.tocs">0</xsl:param> -->
<!-- <xsl:param name="chunk.lots">0</xsl:param> -->

<!-- Add copyright information to all the page footers. -->
<xsl:template name="user.footer.content">
  <P class="copyright" align="right">
      © 2001-2006 
     <A href="http://www-sop.inria.fr/">INRIA Sophia Antipolis</A> All Rights Reserved
   </P>
</xsl:template>



<!-- Just use the image size for the html output. Width=... has no effect. -->
<xsl:param name="ignore.image.scaling">yes</xsl:param> 


<!-- Replace the standard home button (bottom line, middle) by 'Table of Content' -->
<xsl:param name="local.l10n.xml" select="document('')" />
<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
 <l:l10n language="en">
  <l:gentext key="nav-home" text="Table of Contents"/>
 </l:l10n>
</l:i18n>

<!-- Customizes the navigation bar (at the top) contain part title, then chapter title -->
<xsl:template name="header.navigation">
  <xsl:param name="prev" select="Prev"/>  <!--Values here are those if param not specified-->
  <xsl:param name="next" select="Next"/>  <!--Values here are those if param not specified-->
  <xsl:param name="nav.context"/>         <!-- just states you can pass a "nav.context" parameter -->

  
<xsl:variable name="home" select="/*[1]"/>
  <xsl:variable name="up" select="parent::*"/>

  <xsl:variable name="row1" select="$navig.showtitles != 0"/> 
  <xsl:variable name="row2" select="count($prev) &gt; 0
                                    or (count($up) &gt; 0 
                                        and generate-id($up) != generate-id($home)
                                        and $navig.showtitles != 0)
                                    or count($next) &gt; 0"/>

  <xsl:if test="$suppress.navigation = '0' and $suppress.header.navigation = '0'">
    <div class="navheader">
        <table width="100%" summary="Navigation header">
            <tr>
<!-- prev button -->
              <td width="15%" align="left">
                <xsl:if test="count($prev)>0">
                  <a accesskey="p">
                    <xsl:attribute name="href">
                      <xsl:call-template name="href.target">
                        <xsl:with-param name="object" select="$prev"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="navig.content">
                      <xsl:with-param name="direction" select="'prev'"/>
                    </xsl:call-template>
                  </a>
                </xsl:if>
                <xsl:text>&#160;</xsl:text>
              </td>
<!--  part and chapter lines -->
              <td width="50%" align="center">
                <xsl:choose>
                  <!--  Not sure of the exact meaning, but interpret it as "is part and chapter strings exists" -->
                  <xsl:when test="count($up) > 0
                                  and generate-id($up) != generate-id($home)
                                  and $navig.showtitles != 0">
                    <!-- Insert the 'up' string, ie the Part title -->
                    <xsl:apply-templates select="$up" mode="object.title.markup"/>
                    <br/>
                    <!-- Insert the 'local' string, ie the Chapter title -->                    
                    <xsl:apply-templates select="." mode="object.title.markup"/>
                  </xsl:when>

                  <xsl:otherwise>
                     <!--  Just insert the local string, if there is no up string -->                    
                     <xsl:apply-templates select="." mode="object.title.markup"/>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
<!--  ProActive Logo -->
              <td width="20%" align="center">
                 <a href="http://ProActive.ObjectWeb.org">
                  <img> 
                    <xsl:attribute name="src"> <xsl:copy-of select="$header.image.filename"/> </xsl:attribute> 
                    <xsl:attribute name="alt">Back to the ProActive Home Page</xsl:attribute>
                    <xsl:attribute name="title">Back to the ProActive Home Page</xsl:attribute>
                   </img>
                  </a>
                </td>
<!-- next button -->
              <td width="15%" align="right">
                <xsl:text>&#160;</xsl:text>
                <xsl:if test="count($next)>0">
                  <a accesskey="n">
                    <xsl:attribute name="href">
                      <xsl:call-template name="href.target">
                        <xsl:with-param name="object" select="$next"/>
                      </xsl:call-template>
                    </xsl:attribute>
                    <xsl:call-template name="navig.content">
                      <xsl:with-param name="direction" select="'next'"/>
                    </xsl:call-template>
                  </a>
                </xsl:if>
              </td>
            </tr>
        </table>
      <xsl:if test="$header.rule != 0">
        <hr/>
      </xsl:if>
    </div>
  </xsl:if>
</xsl:template>

<!--  Adding the generation date in the headers of the files -->
<xsl:template name="user.head.content">  
  <meta name="date">  
    <xsl:attribute name="content">  
      <xsl:call-template name="datetime.format">  
        <xsl:with-param name="date" select="date:date-time()"/>  
        <xsl:with-param name="format" select="'Y-m-d'"/>  
      </xsl:call-template>
    </xsl:attribute>
  </meta>
</xsl:template>

<!-- Make the legal notice appear, but only as a link - no need for everyone to see it -->
<xsl:param name="generate.legalnotice.link">1</xsl:param >

<!-- Where did this go? -->
<xsl:template name="division.title">  
  <hr/>Hey, lost division title<hr/>
</xsl:template>

<!-- I don't know where the titlepage image got resized. So I'm rewritting this one -->
<xsl:template match="mediaobject" mode="book.titlepage.recto.auto.mode">
<!-- <div xsl:use-attribute-sets="book.titlepage.recto.style"> -->
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
<!-- </div> -->
</xsl:template>

<!-- Redefining the corporate authors, by adding a picture just after the string. 
This should not be done this way. The media object should have been in the corpauthor block. -->
<xsl:template match="corpauthor" mode="book.titlepage.recto.mode">
    <br/> <!--Just adding more space before -->
    <xsl:apply-templates mode="titlepage.mode"/>
    <br/>
    <div style="margin-left: 40px;">
    <img> 
        <xsl:attribute name="src">  <xsl:copy-of select="$threeinstitutes.image.filename"/> </xsl:attribute>
        <xsl:attribute name="width">250</xsl:attribute>
        <xsl:attribute name="alt">A CNRS-INRIA-UNSA Research team</xsl:attribute>
        <xsl:attribute name="title">A CNRS-INRIA-UNSA Research team</xsl:attribute>
    </img>
    </div>
    <br/> <!--Just adding more space after -->
</xsl:template>

</xsl:stylesheet>


