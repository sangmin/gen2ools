// $Id: XsdInclude.java,v 1.5 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdInclude {

	private static final Log log = LogFactory.getLog(XsdInclude.class);

	// TODO figure out a better way to set schemaLocations
	static protected Map schemaLocationMap = null; // replacements

	protected String schemaLocation;

	protected XsdSchema schema = null;

	protected XsdResolver xsdResolver;

	public XsdInclude() {
	}

	public String getSchemaLocation() {
		// check to see if the user has a substitute for the schemaLocation
		if (schemaLocationMap != null
				&& schemaLocationMap.containsKey(schemaLocation)) {
			return (String) schemaLocationMap.get(schemaLocation);
		}
		return schemaLocation;
	}

	public InputStream getXsd() {
		return xsdResolver.getInputStream();
	}

	/** Unmarshalls the Schema based on the schema location if needed. */
	public XsdSchema getSchema(XsdResolver parent, HashSet unmarshalled,
			Generator generator) {
		if (schema == null) {

			log.info("include/import schema: " + getSchemaLocation());

			// might have to pass in the directory, for relative files
			schema = XsdSchema.unmarshall(parent, unmarshalled, generator);

		}

		return schema;
	}

	public XsdResolver getXsdResolver() {
		return xsdResolver;
	}

	public void setXsdResolver(XsdResolver xsdResolver) {
		this.xsdResolver = xsdResolver;
	}

    public String toString(){
        return "XsdInclude[ location: "+ schemaLocation +" ]";
    }
}