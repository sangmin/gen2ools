// $Id: XsdElement.java,v 1.6 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdElement extends XsdFeature {

	private static final Log log = LogFactory.getLog(XsdElement.class);

	private String use;

	private String minOccurs = "1"; // default

	private String maxOccurs = "1"; // default, is String because might be
									// "unbounded"

	private XsdComplexType complexType = null; // an element might be a
											   // ComplexType, inline

	private XsdSimpleType simpleType = null;

	/** Creates a copy/clone. */
	public XsdElement(XsdElement original) {
		super(original); //	inherited fields

		use = original.use;
		minOccurs = original.minOccurs;
		maxOccurs = original.maxOccurs;

		// TODO XsdComplexType and XsdSimpleType should be cloned as well
		complexType = original.complexType;
		simpleType = original.simpleType;
	}

	public XsdElement() {
	}

	public String toString() {
		return "XsdElement [ name: " + name + ", type: " + type + " ]";
	}

	// same as clone but returns the class type
	public XsdElement copy() {
		return new XsdElement(this);
	}

	public Object clone() {
		return new XsdElement(this);
	}

	public String getFieldList() {
		return getFieldName() + NameUtil.addListSuffix;
	}

	public String getAddMethod() {
		StringBuffer sb = new StringBuffer("add");
		sb.append(NameUtil.toUpperCamelCase(getJavaName()));
		return sb.toString();
	}

	public String getSizeMethod() {
		StringBuffer sb = new StringBuffer("size");
		sb.append(NameUtil.toUpperCamelCase(getJavaName()));
		sb.append(NameUtil.addListSuffix);
		return sb.toString();
	}

	protected void importRef() {
		
		if (this.ref != null) {
			XsdType ref = getXsdType( this.ref );

			if (getGenerator().isElement(ref)) {
				XsdElement elementRef = getGenerator().getElement(ref);

				if (name == null) {
					name = elementRef.getName();
				} else {
					log.error("imported ref: " + ref + ", but name != null, name: " + name);
				}

				if (xsdType == null) {
					xsdType = elementRef.getType();
				} else {
					log.error("importRef, but type != null, type = " + type);
				}

				complexType = elementRef.complexType; // 2004-02-16
				schema = elementRef.schema; // 2004-05-26, used for namespace
			} else {
				log.error("unable to importRef: " + ref);
			}
		}
	}

	public String getName() {
		if (name == null) {
			importRef();
		}

		if (name == null) { // if name is still null
			log.error("element name is null after importRef()");
			return null;
		}

		//return NameUtil.trimSuffix( name );
		return name;
	}

	/** returns the type prefixed with the namespace uri if present */
	public XsdType getType() {
		if (xsdType == null && type == null) {
			importRef();
		}
		if (xsdType == null && type == null && simpleType != null) {
			this.xsdType = simpleType.getBase();
		}
		if (xsdType == null && type == null && complexType != null) {
			this.xsdType = createXsdType();
			this.xsdType.setType(name);

			log.info("getType(), anonymous complexType " + xsdType);
		}

		return super.getType();

	}

	public String getUse() {
		return use;
	}

	public int getMin() {
		return Integer.parseInt(minOccurs);
	}

	public int getMax() {
		if (maxOccurs.equals("unbounded")) {
			return -1;
		}

		return Integer.parseInt(maxOccurs);
	}

	/** The element is required if the minOccurs is greater than 0. */
	public boolean isRequired() {
		return getMin() > 0;
	}

	// isCollection if maxOccurs > 1
    public boolean isCollection() {
    	int max = getMax();
    	if (max == -1) {
    		return true; // "unbounded"
    	}
    
    	return max > 1;
    }

	public boolean onlyOne() {
		return (isRequired() == true) && (isCollection() == false);
	}

	public XsdComplexType getComplexType() {
		if (complexType != null) {
			complexType.setSchema(getSchema()); // 2004-05-04
			complexType.setGenerator(getGenerator()); // 2004-07-27
			if (complexType.getName() == null) {
				complexType.setName(getName());
			}
		}

		return complexType;
	}

	public boolean isComplexType() {
		//importRef(); // 2004-02-16
		return complexType != null;
	}

	public XsdSimpleType getSimpleType() {
		return simpleType;
	}

	public boolean isSimpleType() {
		return simpleType != null;
	}

	public void setType(XsdType type) {
		this.xsdType = type;
	}

	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}
	
	/* 2004-12-05 Created to help source code generation for list wrappers. */
	public void setComplexType(XsdComplexType complexType) {
		this.complexType = complexType;
	}
}