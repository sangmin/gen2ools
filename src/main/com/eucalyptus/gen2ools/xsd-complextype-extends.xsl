<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt">
  
  <xsl:include href="xsd-copy.xsl"/>
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" 
    xalan:indent-amount="2"/>
  
  <!-- complexType complexContent extension/restriction -->
  
  <xsl:template match="complexType">
    <xsl:copy>
      
      <xsl:variable name="baseA" select="complexContent/extension/@base"/>
      <xsl:if test="boolean($baseA)">
        <xsl:attribute name="complexContentBase">
          <xsl:value-of select="$baseA" />
        </xsl:attribute>
      </xsl:if>
      
      <xsl:variable name="baseB" select="complexContent/restriction/@base"/>
      <xsl:if test="boolean($baseB)">
        <xsl:attribute name="complexContentBase">
          <xsl:value-of select="$baseB" />
        </xsl:attribute>
      </xsl:if>
      
      <!-- 2004-04-03 simpleContent also -->
      <xsl:variable name="baseC" select="simpleContent/restriction/@base"/>
      <xsl:if test="boolean($baseC)">
        <xsl:attribute name="simpleContentBase">
          <xsl:value-of select="$baseC" />
        </xsl:attribute>
      </xsl:if>
      
      <!-- 2004-04-16 simpleContent extension too -->
      <xsl:variable name="baseD" select="simpleContent/extension/@base"/>
      <xsl:if test="boolean($baseD)">
        <xsl:attribute name="simpleContentBase">
          <xsl:value-of select="$baseD" />
        </xsl:attribute>
      </xsl:if>
      
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- simpleType restriction -->
  
  <xsl:template match="attribute">
    <xsl:copy>
      <xsl:variable name="type" select="simpleType/restriction/@base"/>
      <xsl:if test="boolean($type)">
        <xsl:attribute name="type">
          <xsl:value-of select="$type" />
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="element">
    <xsl:copy>
      <xsl:variable name="type" select="simpleType/restriction/@base"/>
      <xsl:if test="boolean($type)">
        <xsl:attribute name="type">
          <xsl:value-of select="$type" />
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  
  <!-- this is for root simpleTypes for reference lookups -->
  <xsl:template match="simpleType">
    <xsl:copy>
      <xsl:variable name="base" select="restriction/@base"/>
      <xsl:if test="boolean($base)">
        <xsl:attribute name="base">
          <xsl:value-of select="$base" />
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
