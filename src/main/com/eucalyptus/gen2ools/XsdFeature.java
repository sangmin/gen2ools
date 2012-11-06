// $Id: XsdFeature.java,v 1.6 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XsdFeature extends XsdStructure {

	private static final Log log = LogFactory.getLog(XsdFeature.class);

	protected String type; // used by JiBX
	protected XsdType xsdType;

	protected XsdFeature() {
	}

	/** Creates a copy/clone. */
	public XsdFeature(XsdFeature original) {
		super(original); //	inherited fields
		name = original.name;
		type = original.type;
		ref = original.ref;
	}

	public String toString() {
		return super.toString() + " && XsdSimpleStructure[ type: " + type
				+ ", ref: " + ref + " ]";
	}
    
    public String getJavaName() {
        if (getParentComplexType().isListWrapper()) {
            return getParentComplexType().getName();
        } else {
            return getName();
        }
    }

	/** returns the name of the Java source file field to be created. */
	public String getFieldName() {
		StringBuffer sb = new StringBuffer();
		if (NameUtil.addFieldPrefix != null) {
			sb.append(NameUtil.addFieldPrefix);
		}
		String name = getJavaName();
		if (name == null) {
			log.info("name == null, trying to import ref");
			importRef();
			name = getName();
			if (name == null) {
				name = "unknownSimpleStructureName";
				log.error("name == null, still! setting to: " + name);
			}
		}
		sb.append(NameUtil.toLowerCamelCase(name));

		// check to see if it is a reserved word.
		if (NameUtil.isReservedWord(sb.toString())) {
			sb.insert(0, NameUtil.reservedWordPrefix);
		}

		return sb.toString();
	}

	protected abstract void importRef();

//	/** returns The Java source file field type. */
//	public JQName getFieldQName() {
//
//		XsdType type = getType();
//		if (getGenerator().isComplexType(type)) {
//			XsdComplexType ct = getGenerator().getComplexType(type);
//			return ct.getQName();
//		}
//
//		JQName qname = getGenerator().getQName(type);
//		if (qname != null) {
//			return qname;
//		}
//		
//		String errMsg = "unable to getFieldQName for type: "+type;
//		log.fatal(errMsg);
//		throw new RuntimeException(errMsg);
//	}

	public JQName getFieldQName() {
		return getGenerator().getQName(getType());
	}
	
	/**
	 * Returns a qualified class name given an XSD xml type, including namespace
	 * prefix. It checks to see if it a default type, an XsdSimpleType, a
	 * XsdComplexType, then an XsdElement. If it is none of those, an error is
	 * logged and it defaults to a java.lang.String.
	 */


	public String getSetMethod() {
		StringBuffer sb = new StringBuffer("set");
		sb.append(NameUtil.toUpperCamelCase(getJavaName()));
		return sb.toString();
	}

	public String getGetMethod() {
		StringBuffer sb = new StringBuffer("get");
		sb.append(NameUtil.toUpperCamelCase(getJavaName()));
		return sb.toString();
	}

	public XsdType getType() {
		if( xsdType == null && type != null ){
			xsdType = getXsdType(type);
		}
		// TODO config.allowBlankType() = false
		if( xsdType == null && parentComplexType != null ){
			xsdType = new XsdType();
//			xsdType.setNamespaceUri(parentComplexType.getXsdType().getNamespaceUri());
//			xsdType.setType(parentComplexType.getXsdType().getType()+"-"+name);
//			log.warn("setting anonymous type to: "+xsdType);
			xsdType.setNamespaceUri(Generator.SCHEMA_NAMESPACE);
			xsdType.setType("string");
			log.warn("type=\"\", complexType: "+parentComplexType.getName()+", name: "+name+", default to:"+xsdType);
		}
		if( xsdType == null ){
			String errMsg = "xsdType should not be null anymore, name: "+name;
			log.fatal(errMsg);
			throw new GeneratorException(errMsg);
		}
		return xsdType;
	}

	protected String ref;

	public abstract boolean isRequired();

}