/*
 * Created on Dec 5, 2004
 */
package org.jibx.xsd2jibx;

import junit.framework.TestCase;

/**
 * @author ctaggart
 */
public class XmlNamespaceToPackageTest extends TestCase {

	public void testA(){
		Generator g = new Generator();
//		System.out.println( "package: " + g.getJavaPackageFromXmlNamespaceUri("http://www.jibx.org/schemas/"));
		assertEquals("org.jibx.schemas", g.getJavaPackageFromXmlNamespaceUri("http://www.jibx.org/schemas/"));
		
	}
	
}
