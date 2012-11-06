// $Id: XsdStructure.java,v 1.5 2004/08/31 07:19:25 ctaggart Exp $

package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XsdStructure
//implements PubliclyCloneable
{
	private static final Log log = LogFactory.getLog(XsdStructure.class);

	private boolean importedFromBase = false;

	protected XsdComplexType parentComplexType;

	protected String name;

	// the schema that the structure came from
	//   it is needed to look up the namespace info
	protected XsdSchema schema;

	private Generator generator;

	public XsdStructure() {
	}

	// IoC, in order to lookup global elements
	public void setGenerator(Generator generator) {
		if (generator == null) {
			throw new IllegalStateException("generator == null");
		}
		this.generator = generator;
	}

	protected Generator getGenerator() {
		if (generator == null) {
			throw new IllegalStateException("generator == null");
		}
		return generator;
	}

	/** Creates a copy/clone. */
	public XsdStructure(XsdStructure original) {
		name = original.name;
		//targetNamespace = original.getTargetNamespace().copy();
		schema = original.schema;
		generator = original.generator;
	}

	protected void setImportedFromBase(boolean importedFromBase) {
		this.importedFromBase = importedFromBase;
	}

	protected boolean isImportedFromBase() {
		return importedFromBase;
	}

	public JibxNamespace getTargetNamespace() {
		JibxNamespace targetNamespace = null;
		// TODO check the prefix in simple structures
		if (schema != null) {
			targetNamespace = schema.getTargetNamespace();
		} else {
			log.warn("schema == null for structure:\n  " + this);
		}
		return targetNamespace;
	}

	//  public void setTargetNamespace(JibxNamespace namespace) {
	//    targetNamespace = namespace;
	//  }

	public void setSchema(XsdSchema schema) {
		if (this.schema == null) {
			this.schema = schema;
		} else {
			//log.warn( "schema already defined for structure:\n " + this );
		}
	}

	public XsdSchema getSchema() {
		return schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "XsdStructure[ name: " + name + ", schema: " + schema + " ]";
	}

	protected XsdType getXsdType(String type){
		XsdType xsdType = new XsdType();
		xsdType.setType( NameUtil.trimPrefix(type));
		if (NameUtil.hasPrefix(type)) {
			String prefix = NameUtil.getPrefix(type);
			if(schema == null){
				String errMsg = "schema == null, "+this;
				log.fatal(errMsg);
				throw new NullPointerException(errMsg);
			}
			xsdType.setNamespaceUri(schema.getUri(prefix ));
			
			if( xsdType.getNamespaceUri() == null ){
				String errMsg = "unable to get XsdType, uri == null";
				log.fatal(errMsg);
				throw new GeneratorException(errMsg);
			}
		} else {
			log.info( "getXsdType using defaultNamespace for type: "+type+", namespace: "+schema.getDefaultNamespace());
			xsdType.setNamespaceUri( schema.getDefaultNamespace() );
		}
		return xsdType;
	}
	
	protected XsdType createXsdType(){
		XsdType xsdType = new XsdType();
		JibxNamespace targetNamespace = getTargetNamespace();
		if (targetNamespace != null) {
			xsdType.setNamespaceUri(targetNamespace.getUri());
		}
		return xsdType;
	}

	public XsdComplexType getParentComplexType() {
		return parentComplexType;
	}
	
	public void setParentComplexType(XsdComplexType parentComplexType) {
		this.parentComplexType = parentComplexType;
	}

}