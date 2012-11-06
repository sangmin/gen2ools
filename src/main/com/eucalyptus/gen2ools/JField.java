package com.eucalyptus.gen2ools;

import org.apache.ws.jaxme.js.JavaField;

public class JField {

	private JavaField field = null;

	private boolean required = false;

	public JField(JavaField field, boolean required) {
		this.field = field;
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public JavaField getJavaField() {
		return field;
	}
    
    public String toString(){
        return "JibxStructure[ field: "+
            (field == null ? "null" : field.getName())+" ]";
    }
}