package com.eucalyptus.gen2ools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSource.Protection;

public class JSource {
  
  public static boolean                     CREATE_ALL_CONSTRUCTOR      = true;
  public static boolean                     CREATE_REQUIRED_CONSTRUCTOR = true;
  private static final Log                  log                         = LogFactory.getLog( JSource.class );
  
  public static final JavaSource.Protection PUBLIC                      = JavaSource.PUBLIC;
  public static final JavaSource.Protection PRIVATE                     = JavaSource.PRIVATE;
  public static final JavaSource.Protection DEFAULT                     = JavaSource.DEFAULT_PROTECTION;
  public static final JavaSource.Protection PROTECTED                   = JavaSource.PROTECTED;
  
  private static HashMap                    s_collectionTypeMap         = new HashMap( ) {
                                                                          {
                                                                            put( "int", JavaQNameImpl.getInstance( "java.lang.Integer" ) );
                                                                            put( "boolean", JavaQNameImpl.getInstance( "java.lang.Boolean" ) );
                                                                            put( "byte", JavaQNameImpl.getInstance( "java.lang.Byte" ) );
                                                                            put( "char", JavaQNameImpl.getInstance( "java.lang.Character" ) );
                                                                            put( "short", JavaQNameImpl.getInstance( "java.lang.Short" ) );
                                                                            put( "long", JavaQNameImpl.getInstance( "java.lang.Long" ) );
                                                                            put( "float", JavaQNameImpl.getInstance( "java.lang.Float" ) );
                                                                            put( "double", JavaQNameImpl.getInstance( "java.lang.Double" ) );
                                                                          }
                                                                        };
  private JavaSource                        source;
  
  private ArrayList                         fields                      = new ArrayList( );
  
  JSource( JavaSource source ) {
    this.source = source;
  }
  
  public JavaMethod addMethod( String name, Map<String,JQName> args, JQName returnType, List<JQName> exceptions ) {
    source.addImport( JavaQNameImpl.getInstance( Generate.defaultPackage + "." + returnType.getJavaQName( ) ) );
    JavaMethod method = source.newJavaMethod( name, returnType.getJavaQName( ) );
    method.setProtection( PUBLIC );
    for ( String arg : args.keySet( ) ) {
      source.addImport( JavaQNameImpl.getInstance( Generate.defaultPackage + "." + args.get( arg ).getJavaQName( ) ) );
      method.addParam( args.get( arg ).getJavaQName( ), arg );
    }
    for ( JQName ex : exceptions ) {
      source.addImport( JavaQNameImpl.getInstance( Generate.defaultPackage + "." + ex.getJavaQName( ) ) );
      method.addThrows( ex.getJavaQName( ) );
    }
    return method;
  }
  
  public void addExtends( JQName qname ) {
    source.addExtends( qname.getJavaQName( ) );
  }
  
  public void newBeanProperty( JQName qname, String name, boolean required ) {
    if ( qname == null ) {
      log.error( "qname == null, name: " + name );
    } else {
      source.newBeanProperty( qname.getJavaQName( ), name );
    }
  }
  
  private JConstructor newJConstructor( Protection protection ) {
    return new JConstructor( source.newJavaConstructor( protection ) );
  }
  
  void addElement( XsdElement element ) {
    if ( element.isCollection( ) ) {
      source.addImport( ArrayList.class );
      String type = element.getFieldQName( ).getClassName( );
      JavaQName jqname = ( JavaQName ) s_collectionTypeMap.get( type );
      if ( jqname == null ) {
        jqname = element.getFieldQName( ).getJavaQName( );
      } else {
        type = jqname.getClassName( );
      }
      source.addRawJavaSource( "  ArrayList<" + type + "> " + element.getFieldList( ) + " = new ArrayList<" + type + ">();" );
    } else if( element.isComplexType( ) ) {
      String type = element.getFieldQName( ).getClassName( );
      source.addRawJavaSource( "  " + type + " " + element.getFieldList( ) + " = new " + type + "();" );
    } else {
      newBeanProperty( element );
    }
  }
  
  void newBeanProperty( XsdFeature s ) {
    try {
      log.info( "newBeanProperty, " + s );
      newField( s );
    } catch ( IllegalStateException e ) {
      log.error( "field probably exists already", e );
    }
  }
  
  public void createConstructors( ) {
    newJConstructor( JSource.PUBLIC );
  }
  
  private void newField( XsdFeature s ) {
    JavaQName qname = s.getFieldQName( ).getJavaQName( );
    if ( s_collectionTypeMap.containsKey( qname.getClassName( ) ) ) {
      qname = ( JavaQName ) s_collectionTypeMap.get( qname.getClassName( ) );
    }
    
    JField field = new JField( source.newJavaField( s.getFieldName( ), qname, DEFAULT ), s.isRequired( ) );
    fields.add( field );
  }
  
  public void addListWrapper( XsdElement element, XsdElement wrappedElement ) {
    source.addRawJavaSource( "  protected ArrayList " + element.getFieldList( ) + " = new ArrayList();" );
    source.addImport( ArrayList.class );
  }
  
  public String toString( ) {
    return "JSource[ name: " + source.getClassName( ) + " ]";
  }
}
