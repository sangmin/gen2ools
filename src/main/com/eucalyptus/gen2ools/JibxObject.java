/*
 * Created on Aug 29, 2004
 */
package com.eucalyptus.gen2ools;

/**
 * @author ctaggart
 */
public abstract class JibxObject {

	protected String ns;

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	protected String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	protected String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
