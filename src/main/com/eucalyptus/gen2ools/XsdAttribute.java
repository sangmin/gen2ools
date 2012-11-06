// $Id: XsdAttribute.java,v 1.4 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdAttribute extends XsdFeature implements PubliclyCloneable {

	private static final Log log = LogFactory.getLog(XsdAttribute.class);

	private String use; // prohibited, optional, required

	//private Annotation annotation;
	private XsdSimpleType simpleType = null;

	public XsdAttribute() {
	}

	/** Creates a copy/clone. */
	public XsdAttribute(XsdAttribute original) {
		super(original); //	inherited fields

		use = original.use;
		simpleType = original.simpleType;
	}

	// same as clone but returns the class type
	public Object clone() {
		return new XsdAttribute(this);
	}

	public XsdAttribute copy() {
		return new XsdAttribute(this);
	}

	protected void importRef() {
		XsdType ref = getXsdType(this.ref);
		if (ref != null) {

			if (getGenerator().isAttribute(ref)) {
				XsdAttribute a = getGenerator().getAttribute(ref);

				if (name == null) {
					name = a.getName();
				} else {
					log.error("imported ref: " + ref + ", but name != null, name: " + name);
				}

				if (type == null) {
					this.xsdType = a.getType(); // 2004-03-03 back to this again
				} else {
					log.error("importRef, but type != null, type = " + type);
				}

			} else {
				log.error("unable to importRef: " + ref);
			}

		}
	}

	public XsdType getType() {
		if (xsdType == null && type == null) {
			importRef();
		}
		if (xsdType == null && type == null && simpleType != null) {
			xsdType = simpleType.getBase();
		}
		return super.getType();
		
	}

	public String getUse() {
		return use;
	}

	public boolean isRequired() {
		return use.equals("required");
	}

	public boolean isProhibited() {
		return use.equals("prohibited");
	}

	/*
	 * public boolean isTypeString(){ String type = removeNamespace( this.type );
	 * return type.equals( "string" ); }
	 */
	public XsdSimpleType getSimpleType() {
		return simpleType;
	}

	public boolean isSimpleType() {
		return simpleType != null;
	}
    
    public String toString(){
        return "XsdAttribute[ name: "+ getName() +" ]";
    }
}