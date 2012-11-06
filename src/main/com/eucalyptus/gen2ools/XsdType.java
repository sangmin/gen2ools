/*
 * Created on Aug 22, 2004
 */
package com.eucalyptus.gen2ools;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class XsdType {
	
	// I'm using the xml schema object case rather than all caps for these constants.
	public static final XsdType object = new XsdType(Generator.SCHEMA_NAMESPACE, "object");
	public static final XsdType string = new XsdType(Generator.SCHEMA_NAMESPACE, "string");
	
	private String namespaceUri;
	private String type;
	
	public XsdType(){}
	
	public XsdType(String namespaceUri, String type){
		this.namespaceUri = namespaceUri;
		this.type = type;
	}
	
	public String getNamespaceUri() {
		return namespaceUri;
	}
	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString(){
		return "XsdType[ type: "+type+", namespaceUri: "+namespaceUri+" ]";
	}
	
   public int hashCode() {
     // you pick a hard-coded, randomly chosen, non-zero, odd number
     // ideally different for each class
     return new HashCodeBuilder(17, 37).
       append(namespaceUri).
       append(type).
       toHashCode();
   }
   
   public boolean equals(Object o) {
	   if ( !(o instanceof XsdType) ) {
	     return false;
	    }
	   XsdType b = (XsdType) o;
	   return new EqualsBuilder()
//          .appendSuper(super.equals(o))
          .append(namespaceUri, b.namespaceUri)
          .append(type, b.type)
          .isEquals();
   }
	
	
}
