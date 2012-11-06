// $Id: XsdImport.java,v 1.3 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdImport extends XsdInclude {
	private static final Log log = LogFactory.getLog(XsdImport.class);
	
	public XsdImport(){
	}
	
	private String namespace;

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String string) {
		namespace = string;
	}

    public String toString(){
        return "XsdImport[ namespace: "+ namespace +" ]";
    }
}