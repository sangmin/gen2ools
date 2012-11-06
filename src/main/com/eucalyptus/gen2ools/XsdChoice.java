// $Id: XsdChoice.java,v 1.2 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdChoice extends XsdOrder
//implements PubliclyCloneable
{

	private static final Log log = LogFactory.getLog(XsdChoice.class);

	private String minOccurs = "1"; // default

	private String maxOccurs = "1"; // default, is String because might be
									// "unbounded"

	public XsdChoice() {
		name = "choice";
	}

	/** Creates a copy/clone. */
	public XsdChoice(XsdChoice original) {
		super(original); //	inherited fields
		minOccurs = original.minOccurs;
		maxOccurs = original.maxOccurs;

		/*
		 * loop through original elements and copy/clone each element. for ( int
		 * i = 0, n = original.sizeElements( ); i < n; i++ ) { elements.add(
		 * original.getElement( i ).clone( ) ); }
		 */
	}

	public String getFieldName() {
		StringBuffer sb = new StringBuffer();
		if (NameUtil.addFieldPrefix != null) {
			sb.append(NameUtil.addFieldPrefix);
		}
		sb.append(name);
		sb.append(NameUtil.addListSuffix);
		return sb.toString();
	}

	public String getMinOccurs() {
		return minOccurs;
	}

	public String getMaxOccurs() {
		return maxOccurs;
	}

	public boolean isCollection() {
		if (maxOccurs.equals("unbounded"))
			return true;
		int max = Integer.parseInt(maxOccurs);
		return max > 1;
	}

    public String toString(){
        return "XsdChoice[ name: "+ getName() +" ]";
    }
}