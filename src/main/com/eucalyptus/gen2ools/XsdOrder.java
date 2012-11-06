package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XsdOrder extends XsdStructure {

	private static final Log log = LogFactory.getLog(XsdOrder.class);

	public XsdOrder() {
		super();
	}

	public XsdOrder(XsdOrder original) {
		super(original);
	}

	protected ArrayList structures = new ArrayList();

	public int sizeStructures() {
		if (structures == null) {
			log.error("structures == null");
			return 0;
		}

		return structures.size();
	}

	public XsdStructure getStructure(int i) {
		return (XsdStructure) structures.get(i);
	}

	public void setSchema(XsdSchema schema) {
		super.setSchema(schema);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			getStructure(i).setSchema(schema);
		}
	}

	public void setGenerator(Generator generator) {
		super.setGenerator(generator);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			getStructure(i).setGenerator(generator);
		}
	}

	public void setParentComplexType(XsdComplexType parentComplexType) {
		super.setParentComplexType(parentComplexType);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			getStructure(i).setParentComplexType(parentComplexType);
		}
	}
	
//	public void addMappingRefs(JibxMapping jm) {
//		super.addMappingRefs((XsdStructure)this);		
//	}
}