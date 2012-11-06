<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt">
  
  <xsl:include href="xsd-copy.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" 
    xalan:indent-amount="2"/>
  <!--<xsl:strip-space elements="*"/>-->

  <!-- sort "attribute" then "element" -->
  <xsl:template match="extension">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
      <xsl:apply-templates select="attribute"/>
      <xsl:apply-templates select="element"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
