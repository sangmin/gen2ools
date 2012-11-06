// $Id: XsdSimpleContent.java,v 1.1 2004/08/03 07:37:11 ctaggart Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;

/**
 * 2004-04-16 class not used yet instead a simpleContentBase attribute is added
 * to the complexType
 */
public class XsdSimpleContent {
	//~ Instance fields
	// ----------------------------------------------------------

	//private String base;
	private ArrayList attributes = new ArrayList();

	private XsdExtension extension;

	//~ Methods
	// ------------------------------------------------------------------

	public int sizeAttributes() {
		return attributes.size();
	}

	public XsdAttribute getAttribute(int i) {
		return (XsdAttribute) attributes.get(i);
	}

	public boolean isExtension() {
		return extension == null;
	}

	public XsdExtension getExtension() {
		return extension;
	}

	public String getBase() {
		return extension.getBase();
	}
}