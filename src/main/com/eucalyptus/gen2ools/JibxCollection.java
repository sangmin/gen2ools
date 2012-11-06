// $Id: JibxCollection.java,v 1.3 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

class JibxCollection extends JibxChoice {

	private String _type;
	private String _usage;
	private String _label;
	private String _using;
	private String _ordered;

	public JibxCollection() {
	}

	public void setUsage(boolean required) {
		if (required) {
			_usage = "required";
		} else {
			_usage = "optional";
		}
	}

	public void setOrdered(boolean b) {
		_ordered = "" + b;
	}
    
    public String toString(){
        return "JibxCollection[ name: "+ getName() +" ]";
    }
}