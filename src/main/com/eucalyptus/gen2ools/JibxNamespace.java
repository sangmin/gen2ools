// $Id: JibxNamespace.java,v 1.3 2004/08/30 08:09:46 ctaggart Exp $

package com.eucalyptus.gen2ools;

class JibxNamespace implements PubliclyCloneable {

	// the default usage of this namespace
	public final static String DEFAULT_NONE = "none"; // default
	public final static String DEFAULT_ELEMENTS = "elements"; // elements only
	public final static String DEFAULT_ATTRIBUTES = "attributes"; // attributes only
	public final static String DEFAULT_ALL = "all"; // both elements and attributes

	private String uri;
	private String prefix;
	private String _default;

	/** JiBX needs a no-argument constructor or factory method. */
	public JibxNamespace() {
	}

	public JibxNamespace(JibxNamespace original) {
		uri = original.uri;
		prefix = original.uri;
		_default = original._default;
	}

	public JibxNamespace copy() {
		return new JibxNamespace(this);
	}

	public Object clone() {
		return new JibxNamespace(this);
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDefault() {
		return this._default;
	}

	public void setDefault(String _default) {
		this._default = _default;
	}

	public String toString() {
		return "JibxNamespace[ prefix: " + prefix + ", uri: " + uri+" ]";
	}

}