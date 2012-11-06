
package org.jibx.xsd2jibx;

import junit.framework.TestCase;


public class NameUtilTest extends TestCase {


	public NameUtilTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(NameUtilTest.class);
	}

	public void testTrimSuffix() {
	}

	public void testToLowerCamelCase() {
		assertEquals( "url", NameUtil.toLowerCamelCase( "URL" )  );
		
		assertEquals( "mrUniverse", NameUtil.toLowerCamelCase( "mr_Universe" ) );
		
		assertEquals( "msAmericaUSA", NameUtil.toLowerCamelCase( "Ms-America-USA" ) );
//		Ms-America-USA ... should it be msAmericaUsa instead?

		assertEquals( "book", NameUtil.toLowerCamelCase( "Book" ) );

	}

	public void testToUpperCamelCase() {
    
		assertEquals( "A", NameUtil.toUpperCamelCase( "a" )  );
    
	}

	public void testTrimNamespace() {
		assertEquals( "car", NameUtil.trimPrefix( "nascar:car"  ) );
		assertEquals( "anyURI", NameUtil.trimPrefix( "http://www.w3.org/2001/XMLSchema:anyURI"  ) );
	}
	
//	public void testGetPackage() throws URISyntaxException{
//		assertEquals( "com.sosnoski.class.library", NameUtil.getPackageFromNamespaceUri( "http://www.sosnoski.com/class/library") );
//	}
  
  public void testGetPrefix(){
    assertEquals( "xs", NameUtil.getPrefix( "xs:NMTOKEN" ) );
    
    // 2004-08-03 bug found by Dennis Sosnoski
    assertEquals( "http://www.w3.org/2001/XMLSchema", NameUtil.getPrefix( "http://www.w3.org/2001/XMLSchema:string" ));
  }

}
