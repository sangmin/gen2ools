// $Id: XsdSimpleType.java,v 1.2 2004/08/23 05:31:57 ctaggart Exp $

package com.eucalyptus.gen2ools;

public class XsdSimpleType extends XsdStructure {

	private String base;
	private XsdType baseType = null;

	private XsdRestriction restriction;

	public XsdType getBase() {
		if( baseType != null ) return baseType;
		baseType = getXsdType(base);
		return baseType;
	}

}