/*
 * Created on Aug 15, 2004
 */
package com.eucalyptus.gen2ools;

import java.io.InputStream;

public interface XsdResolver {
		
	/** Returns the target namespace for the schema.
	 * Returns a an empty String if the schemas doesn't use namespaces.
	 */
	public String getTargetNamespace();
	
	public InputStream getInputStream();

	/** returns a resolver for
	 * <xs:include schemaLocation="someother.xsd"/>
	 */
	public XsdResolver getIncludeResolver(String schemaLocation);
	
	/** Get the identifier. 
	 * This ensures that a schema doesn't get processed more than once.
	 */
	public String getId();
	
}
