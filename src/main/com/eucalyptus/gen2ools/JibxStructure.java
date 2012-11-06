// $Id: JibxStructure.java,v 1.3 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;


class JibxStructure extends JibxChoice {

	private String _mapAs;
	private XsdType type;
	private String _type;
	private String _usage;
	private String _label;
	private String _using;
	private String _ordered;

	public JibxStructure() {
	}
	
	public String toString(){
		return "JibxStructure[ name: "+name+" ]";
	}

	public void setMapAs(String _mapAs) {
		this._mapAs = _mapAs;
	}

	public String getUsage() {
		return this._usage;
	}

	public void setUsage(String _usage) {
		this._usage = _usage;
	}

	public String getLabel() {
		return this._label;
	}

	public void setLabel(String _label) {
		this._label = _label;
	}

	public String getUsing() {
		return this._using;
	}

	public void setUsing(String _using) {
		this._using = _using;
	}

	public String getOrdered() {
		return this._ordered;
	}

	public void setOrdered(String _ordered) {
		this._ordered = _ordered;
	}

	public void setUsage(boolean required) {
		if (required) {
			_usage = "required";
		} else {
			_usage = "optional";
		}
	}
    public void setType(String type) {
        _type = type;
    }


	public XsdType getType() {
		return type;
	}
	public void setType(XsdType type) {
		this.type = type;
	}

}