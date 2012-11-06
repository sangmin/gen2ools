// $Id: JibxValue.java,v 1.3 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

class JibxValue extends JibxObject {

	public static final String STYLE_ATTRIBUTE = "attribute";
	public static final String STYLE_ELEMENT = "element";

	private String _format;
	private String _ident;
	private String _style;
	private String _type;
	private String _usage;
	private String _testMethod;
	private String _getMethod;
	private String _setMethod;
	private String _default;
	private String _serializer;
	private String _deserializer;

	// used to compare namespace to that of the JibxMapping
	// if they are different, then the I'll set _ns
	private JibxNamespace namespace;

	public JibxValue() {
	}

	public void setNamespace(JibxNamespace namespace) {
		this.namespace = namespace;
	}

	public JibxNamespace getNamespace() {
		return namespace;
	}

	public String getFormat() {
		return this._format;
	}

	public void setFormat(String _format) {
		this._format = _format;
	}

	public String getIdent() {
		return this._ident;
	}

	public void setIdent(String _ident) {
		this._ident = _ident;
	}

	public String getStyle() {
		return this._style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	public String getType() {
		return this._type;
	}

	public void setType(String _type) {
		this._type = _type;
	}

	public String getUsage() {
		return this._usage;
	}

	public void setUsage(String _usage) {
		this._usage = _usage;
	}

	public String getTestMethod() {
		return this._testMethod;
	}

	public void setTestMethod(String _testMethod) {
		this._testMethod = _testMethod;
	}

	public String getGetMethod() {
		return this._getMethod;
	}

	public void setGetMethod(String _getMethod) {
		this._getMethod = _getMethod;
	}

	public String getSetMethod() {
		return this._setMethod;
	}

	public void setSetMethod(String _setMethod) {
		this._setMethod = _setMethod;
	}

	public String getDefault() {
		return this._default;
	}

	public void setDefault(String _default) {
		this._default = _default;
	}

	public String getSerializer() {
		return this._serializer;
	}

	public void setSerializer(String _serializer) {
		this._serializer = _serializer;
	}

	public String getDeserializer() {
		return this._deserializer;
	}

	public void setDeserializer(String _deserializer) {
		this._deserializer = _deserializer;
	}

	public void setUsage(boolean required) {
		if (required) {
			_usage = "required";
		} else {
			_usage = "optional";
		}
	}

    
    public String toString(){
        return "JibxValue[ name: "+ getName() +" ]";
    }
}