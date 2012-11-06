<?xml version="1.0" encoding="UTF-8"?>
<!--
  removes the XML namespaces
  http://cocoon.apache.org/2.1/faq/faq-xslt.html#faq-N10075 
  5. How can I remove namespaces from my xml files?
-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt">
  
  <xsl:include href="xsd-copy.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" 
    xalan:indent-amount="2"/>
  
  <xsl:template match="*">
    <!-- remove element prefix (if any) -->
    <xsl:element name="{local-name()}">
      <!-- process attributes -->
      <xsl:for-each select="@*">
        <!-- remove attribute prefix (if any) -->
        <xsl:attribute name="{local-name()}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>

