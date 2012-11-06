<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt">
  
  <xsl:include href="xsd-copy.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" 
    xalan:indent-amount="2"/>
  
  <!-- remove elements -->
  <xsl:template match="annotation"/>
  <xsl:template match="documentation"/>
  <xsl:template match="any"/>
  <xsl:template match="all"/><!-- 2004-05-04, in udc.xsd -->
  <xsl:template match="anyAttribute"/><!-- 2004-05-04, in soap envelope ns-->
  <xsl:template match="list"/><!-- 2004-05-04, in soap envelope ns, within simpleType -->
  
  <!-- remove elements, facets -->
  <xsl:template match="minExclusive"/>
  <xsl:template match="minInclusive"/>
  <xsl:template match="maxExclusive"/>
  <xsl:template match="maxInclusive"/>
  <xsl:template match="totalDigits"/>
  <xsl:template match="fractionDigits"/>
  <xsl:template match="length"/>
  <xsl:template match="minLength"/>
  <xsl:template match="maxLength"/>
  <xsl:template match="enumeration"/>
  <xsl:template match="whiteSpace"/>
  <xsl:template match="pattern"/>
  
  <!-- remove keys -->
  <xsl:template match="key"/>
  <xsl:template match="keyref"/>
  <xsl:template match="unique"/>
  
  <!-- remove imports (not includes) 
  <xsl:template match="import"/>
  -->
  
  <!-- remove tag only 
  <xsl:template match="sequence">
    <xsl:apply-templates/>
  </xsl:template>
  -->
  

  <!-- remove tags, leave contents: -->
  
  <xsl:template match="restriction">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="extension">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="complexContent">
    <xsl:apply-templates/>
  </xsl:template>
  
  <!-- -->
  <xsl:template match="simpleContent">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <!-- remove tags and content -->
  <!--<xsl:template match="simpleType"/>-->
  <xsl:template match="attribute/simpleType"/>
  
  
  
</xsl:stylesheet>
