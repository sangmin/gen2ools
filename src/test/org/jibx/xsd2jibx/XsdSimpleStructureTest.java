// $Id: XsdSimpleStructureTest.java,v 1.2 2004/08/03 07:31:43 ctaggart Exp $

package org.jibx.xsd2jibx;

import org.apache.commons.logging.Log; import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class XsdSimpleStructureTest
  extends TestCase {
  //~ Static fields/initializers -----------------------------------------------

  private static final Log log = LogFactory.getLog( XsdSimpleStructureTest.class );

  //~ Constructors -------------------------------------------------------------

  public XsdSimpleStructureTest( String arg0 ) {
    super( arg0 );
  }

  //~ Methods ------------------------------------------------------------------

  public void testGetField(  ) {
    XsdElement e = new XsdElement(  );
    e.setName( "fieldA" );
    assertEquals( "fieldA", e.getName(  ) );

    assertEquals( "fieldA", e.getFieldName(  ) );

    //log.info( e.getField());
  }
  
  public void testIsCollection(){
    XsdElement a = new XsdElement(  );
    log.info( "isCollection: " + a.isCollection());
    assertFalse( a.isCollection() );
    a.setMaxOccurs( "1" );
    assertFalse( a.isCollection() );
    a.setMaxOccurs( "2" );
    assertTrue( a.isCollection() );
    a.setMaxOccurs( "unbounded" );
    assertTrue( a.isCollection() );
  }
  
  public void testMaxOccurs(){
    XsdElement a = new XsdElement(  );
    a.setMaxOccurs( "0" );
    assertEquals( 0, a.getMax() );
    
    
  }
  
  /*
  public static void main( String[] args ) {
    junit.textui.TestRunner.run( XsdSimpleStructureTest.class );
  }
  
  
  protected void setUp(  )
    throws Exception {
    super.setUp(  );
  }

  protected void tearDown(  )
    throws Exception {
    super.tearDown(  );
  }

  public void testXsdSimpleStructure(  ) {
  }

  public void testXsdSimpleStructureXsdSimpleStructure(  ) {
  }

  public void testToString(  ) {
  }

  public void testGetFieldType(  ) {
  }

  public void testGetSetMethod(  ) {
  }

  public void testGetGetMethod(  ) {
  }

  public void testGetType(  ) {
  }

  public void testGetName(  ) {
  }

  public void testSetName(  ) {
  }
  */
}