
package org.jibx.xsd2jibx;
import junit.framework.TestCase;

import org.apache.commons.logging.Log; import org.apache.commons.logging.LogFactory;

public class XsdComplexTypeTest extends TestCase {

  
  private static final Log log = LogFactory.getLog( XsdComplexTypeTest.class );
  
  public XsdComplexTypeTest(String arg0) {
    super(arg0);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(XsdComplexTypeTest.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    
    
    
    
    
    
  }


  protected void tearDown() throws Exception {
    super.tearDown();
  }


  public void testXsdComplexTypeXsdComplexType() {
  }


  public void testXsdComplexType() {
  }

  public void testGetFullName() {
  }

  public void testGetPrefixedFullName() {
  }

  public void testGetQName() {
    
    // setup
    //Generator.targetPackage = "com.some.package";
    
    XsdComplexType a = new XsdComplexType();
    
    a.setName( "ServiceResponse" );
    assertEquals( "com.some.package.ServiceResponse", a.getQName().toString() );
    //log.info( "getQName() " + a.getQName() );
    
//    a.setParentName( "Parent");
//    assertEquals( "com.some.package.ParentServiceResponse", a.getQName().toString() );
    
  }


  public void testClone() {
  }

  public void testCopy() {
  }

  public void testGetNextChoiceNum() {
  }

  public void testSizeStructures() {
  }

  public void testGetStructure() {
  }

  public void testSizeAttributes() {
  }

  public void testImportBase() {
  }

  public void testSizeElements() {
  }

  public void testSizeChoices() {
  }

  public void testGetAttribute() {
  }

  public void testGetElement() {
  }

  public void testGetChoice() {
  }

  public void testGetName() {
  }

  public void testSetName() {
  }

  public void testGetNamePrefix() {
  }

  public void testSetNamePrefix() {
  }

  public void testGetSimpleContent() {
  }

  public void testGetComplexContent() {
  }

  public void testGetRestriction() {
  }

  public void testGetBase() {
  }

  public void testHasBase() {
  }

  public void testIsExtension() {
  }

  public void testGetMapping() {
  }

  public void testSetMapping() {
  }

  public void testAddChild() {
  }

  public void testSizeChildren() {
  }

  public void testGetChild() {
  }

  public void testAddMapping() {
  }

  public void testGetTargetNamespace() {
  }

  public void testSetTargetNamespace() {
  }

  public void testGetParentName() {
  }

  public void testSetParentName() {
  }

}
