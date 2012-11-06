// $Id: XsdRestriction.java,v 1.1 2004/08/03 07:37:11 ctaggart Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;

/**
 * 2004-04-16 class not used yet
 */
public class XsdRestriction
//extends XsdStructure
{
	//~ Instance fields
	// ----------------------------------------------------------

	private String base;

	private ArrayList attributes = new ArrayList();

	//~ Methods
	// ------------------------------------------------------------------

	public String getBase() {
		return base;
	}

	public int sizeAttributes() {
		return attributes.size();
	}

	public XsdAttribute getAttribute(int i) {
		return (XsdAttribute) attributes.get(i);
	}
}