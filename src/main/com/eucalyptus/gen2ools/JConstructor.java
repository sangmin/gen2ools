package com.eucalyptus.gen2ools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaConstructor;

public class JConstructor {

	private static final Log log = LogFactory.getLog(JConstructor.class);

	//private int paramSize = 0; //

	private JavaConstructor constructor;

	public JConstructor(JavaConstructor constructor) {
		this.constructor = constructor;
	}

	public void addParam(JQName qname, String name) {
		if (qname == null) {
			log.warn("qname == null, defaulting to String: " + name);
			qname = JQName.STRING;
		}
		constructor.addParam(qname.getJavaQName(), name);
		constructor.addLine("this." + name + " = " + name + ";");
		//paramSize++;
	}

	public void addFieldList(XsdElement element) {
		addParam(JQName.ARRAYLIST, element.getFieldList());
	}

	public void addField(XsdFeature simpleStructure) {
		addParam(simpleStructure.getFieldQName(), simpleStructure
				.getFieldName());
	}

	//  public int getParamSize(){
	//    return paramSize;
	//  }
}