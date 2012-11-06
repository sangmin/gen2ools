// $Id: XsdSchema.java,v 1.7 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XsdSchema {
  
  //	private File file; // the xsd, so that we can locate the includes/imports
  private XsdResolver         xsdResolver;
  
  private static final Log    log                         = LogFactory.getLog( XsdSchema.class );
  
  private ArrayList           includes                    = new ArrayList( );                      // xsd includes
                                                                                                    
  private ArrayList           structures                  = new ArrayList( );
  
  private HashMap             complexTypes                = new ListMap( );
  
  private HashMap             elements                    = new HashMap( );
  
  private HashMap             simpleTypes                 = new HashMap( );
  
  private HashMap             attributeGroups             = new HashMap( );
  
  private HashMap             attributes                  = new HashMap( );
  
  private TreeMap             namespaceUriToJibxNamespace = new TreeMap( );
  
  private HashMap             prefixToUri                 = new HashMap( );
  
  private JibxNamespace       targetNamespace;
  
  private String              defaultNamespace;
  
  private static final String XML_NAMESPACE               = "http://www.w3.org/XML/1998/namespace";
  
  public XsdSchema( ) {
    this.xsdResolver = WsdlDocument.xsdResolver;
  }
  
  public String toString( ) {
    return "XsdSchema[ " + xsdResolver.getTargetNamespace( ) + " ]";
  }
  
  private void addNamespace( JibxNamespace ns ) {
    namespaceUriToJibxNamespace.put( ns.getUri( ), ns );
    prefixToUri.put( ns.getPrefix( ), ns.getUri( ) );
  }
  
  TreeMap getNamespaces( ) {
    return namespaceUriToJibxNamespace;
  }
  
  HashMap getComplexTypes( ) {
    return complexTypes;
  }
  
  HashMap getElements( ) {
    return elements;
  }
  
  HashMap getSimpleTypes( ) {
    return simpleTypes;
  }
  
  HashMap getAttributeGroups( ) {
    return attributeGroups;
  }
  
  public Map getAttributes( ) {
    return attributes;
  }
  
  private XsdType getXsdType( XsdStructure structure ) {
    XsdType xsdType = new XsdType( );
    xsdType.setType( structure.getName( ) );
    if ( targetNamespace != null ) {
      xsdType.setNamespaceUri( targetNamespace.getUri( ) );
    }
    return xsdType;
  }
  
  /**
   * Divides up the structures into complexTypes, elements, and simpleTypes.
   * 
   * @param unmarshalled
   *          Included XML Schemas that have been unmarshalled already.
   */
  void sortStructures( Generator generator ) {
    for ( int n = sizeStructures( ), i = 0; i < n; i++ ) {
      XsdStructure structure = getStructure( i );
      
      structure.setGenerator( generator );
      
      if ( structure instanceof XsdComplexType ) {
        putComplexType( ( XsdComplexType ) structure );
      } else if ( structure instanceof XsdAttribute ) {
        putAttribute( ( XsdAttribute ) structure );
      } else if ( structure instanceof XsdElement ) {
        XsdElement element = ( XsdElement ) structure;
        
        // Allows root elements to be looked up by reference.
        putElement( element );
        if ( element.isComplexType( ) ) {
          
          // force generation of global element types
          XsdComplexType type = element.getComplexType( );
          type.setIsReferenced( );
          putComplexType( element.getComplexType( ) );
          
        } else if ( element.isSimpleType( ) ) {
          XsdSimpleType st = element.getSimpleType( );
          st.setName( element.getName( ) );
          putSimpleType( st );
        }
      } else if ( structure instanceof XsdSimpleType ) {
        putSimpleType( ( XsdSimpleType ) structure );
      } else if ( structure instanceof XsdAttributeGroup ) {
        XsdAttributeGroup group = ( XsdAttributeGroup ) structure;
        putAttributeGroup( group );
      }
      
    }
  }
  
  void processIncludedSchemas( HashSet unmarshalled,
                               Generator generator ) {
    // process included schemas
    for ( int j = 0, m = sizeIncludes( ); j < m; j++ ) {
      XsdInclude include = getInclude( j );
      //include.setParentXsd(file);
      String schemaLocation = include.getSchemaLocation( );
      include.setXsdResolver( xsdResolver
                                         .getIncludeResolver( schemaLocation ) );
      
      if ( !unmarshalled.contains( schemaLocation ) ) {
        //				if (include.exists()) {
        unmarshalled.add( schemaLocation );
        log.info( "process import, schemaLocation: " + schemaLocation );
        
        XsdSchema schema = include.getSchema( include.getXsdResolver( ),
                                              unmarshalled, generator );
        
        // add in structures from includes
        elements.putAll( schema.getElements( ) );
        complexTypes.putAll( schema.getComplexTypes( ) );
        simpleTypes.putAll( schema.getSimpleTypes( ) );
        attributeGroups.putAll( schema.getAttributeGroups( ) );
        attributes.putAll( schema.getAttributes( ) );
        //				} else {
        //					log.error("unable to unmarshall schema, files does not exist:
        // " + include.getXsd());
        //				}
        
      }
    }
  }
  
  private void putAttributeGroup( XsdAttributeGroup group ) {
    group.setSchema( this );
    attributeGroups.put( getXsdType( group ), group );
  }
  
  private void putComplexType( XsdComplexType ct ) {
    ct.setSchema( this );
    log.info( "putComplexType: " + getXsdType( ct ) );
    complexTypes.put( getXsdType( ct ), ct );
  }
  
  private void putSimpleType( XsdSimpleType st ) {
    st.setSchema( this );
    log.info( "putSimpleType: " + getXsdType( st ) );
    simpleTypes.put( getXsdType( st ), st );
  }
  
  private void putElement( XsdElement element ) {
    element.setSchema( this );
    elements.put( getXsdType( element ), element );
  }
  
  private void putAttribute( XsdAttribute attribute ) {
    attribute.setSchema( this );
    attributes.put( getXsdType( attribute ), attribute );
  }
  
  private static byte[] transformXml( String xslFile, byte[] xmlBA ) {
    byte[] ba;
    StreamSource xmlSS = new StreamSource( new ByteArrayInputStream( xmlBA ) );
    try {
      ba = transformXml( xslFile, xmlSS );
    } catch ( TransformerException e ) {
      String errMsg = "unable to transformXml, xslFile: " + xslFile;
      log.fatal( errMsg, e );
      throw new GeneratorException( errMsg, e );
    }
    return ba;
  }
  
  static byte[] transformXml( String xslFile, StreamSource xmlSS ) throws TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance( );
    tf.setURIResolver( new XslFileResolver( ".." ) );
    ByteArrayOutputStream xmlOS = new ByteArrayOutputStream( );
    Transformer t = tf.newTransformer( new StreamSource(
                                                         XsdSchema.class.getResourceAsStream( xslFile ) ) );
    t.transform( xmlSS, new StreamResult( xmlOS ) );
    return xmlOS.toByteArray( );
  }
  
  static XsdSchema unmarshall( XsdResolver xsdResolver,
                               HashSet unmarshalledXsds, Generator generator ) {
    XsdSchema schema = null;
    try {
      log.info( "unmarshall schema: " + xsdResolver.getTargetNamespace( ) );
      InputStream in = xsdResolver.getInputStream( );
      
      StreamSource xmlSS = new StreamSource( in );
      byte[] xsdBA = transformXml( "xsd-remove-namespace.xsl", xmlSS );
      
      String xsd = new String( xsdBA );
      
      log.info( "after remove-namespace.xsl, " + in + ":\n" + xsd );
      xsdBA = transformXml( "xsd-complextype-extends.xsl", xsdBA );
      xsd = new String( xsdBA );
      log.info( "after complextype-extends.xsl, " + in + ":\n" + xsd );
      
      xsdBA = transformXml( "xsd-remove-elements.xsl", xsdBA );
      xsd = new String( xsdBA );
      log.info( "after remove-elements.xsl, " + in + ":\n" + xsd );
      
      // Bind to the transformed XSD.
      schema = XsdSchema.unmarshall( new ByteArrayInputStream( xsdBA ),
                                     unmarshalledXsds );
      ByteArrayOutputStream xsdOS = new ByteArrayOutputStream( );
      schema.marshall( xsdOS );
      xsdBA = xsdOS.toByteArray( );
      xsd = new String( xsdBA );
      log.info( "Marshalled XSD " + in + ":\n" + xsd );
      
      schema.setXsdResolver( xsdResolver );
      
    } catch ( TransformerException e ) {
      throw new GeneratorException( "problem with xsl transformer", e );
    }
    
//    schema.readNamespaces( xsdResolver.getInputStream( ) );
    schema.sortStructures( generator );
    schema.processIncludedSchemas( unmarshalledXsds, generator );
    
    return schema;
  }
  
  // http://xmlpull.org/v1/doc/api/org/xmlpull/v1/XmlPullParser.html
  public void readNamespaces( InputStream is ) {
    ArrayList namespaces = new ArrayList( );
    try {
      XmlPullParserFactory f = XmlPullParserFactory.newInstance( );
      XmlPullParser parser = f.newPullParser( );
      parser.setInput( is, null );
      
      int event;
      while ( ( event = parser.next( ) ) != XmlPullParser.END_DOCUMENT ) {
        if ( event == XmlPullParser.START_TAG ) {
          if ( NameUtil.trimPrefix( parser.getName( ) ).equals( "schema" ) || NameUtil.trimPrefix( parser.getName( ) ).equals( "definitions" ) ) {
            String tnsUri = parser.getAttributeValue( null, "targetNamespace" );
            defaultNamespace = parser.getAttributeValue( null, "xmlns" );
            // 2004-05-25 default targetNamespace to xmlns
            if ( tnsUri == null && defaultNamespace != null ) {
              tnsUri = defaultNamespace;
            }
            if ( tnsUri != null ) {
              targetNamespace = new JibxNamespace( );
              targetNamespace.setUri( tnsUri );
            }
            
            for ( int i = 0, n = parser.getAttributeCount( ); i < n; i++ ) {
              String name = parser.getAttributeName( i );
              
              //log.info( "attribute " + i + ": " + name );
              if ( name.startsWith( "xmlns:" ) ) {
                String prefix = NameUtil.trimPrefix( name );
                String uriStr = parser.getAttributeValue( i );
                JibxNamespace ns = new JibxNamespace( );
                ns.setPrefix( prefix );
                
                // Figure out what to default the namespace to:
                //   DEFAULT_ELEMENTS or DEFAULT_NONE
                //ns.setDefault( JibxNamespace.DEFAULT_ELEMENTS
                // );
//								try {
//									URI uri = new URI(uriStr);
//									ns.setUri(uri.toString());
//									
//								} catch (URISyntaxException e1) {
//									log.error("problem importing namespace uri: "+ uriStr);
//									e1.printStackTrace();
//								}
                ns.setUri( uriStr );
                
                namespaces.add( ns );
              }
            }
            
            break; // schema tag was the only one we wanted to visit
          }
        }
      }
    } catch ( XmlPullParserException e ) {
      log.error( "unable to get namespace: " + e );
    } catch ( IOException e ) {
      log.error( "unable to get namespace: " + e );
    }
    
    for ( int i = 0, n = namespaces.size( ); i < n; i++ ) {
      JibxNamespace ns = ( JibxNamespace ) namespaces.get( i );
      log.info( ns );
      
      String uri = ns.getUri( ).toString( );
      //if ( uri.equals( targetNamespace ) ) {
      //  log.info( "targetNamespace: " + ns );
      //  this.targetNamespace = ns;
      //addNamespace( ns );
      //} else
      if ( uri.equals( "http://www.w3.org/2001/XMLSchema" ) ) {
        // skip xml schema
        addNamespace( ns ); // 2004-05-04 see what happens
      } else {
        //this.namespaces.add( ns );
        addNamespace( ns );
      }
    }
    
    log.info( "targetNamespace: " + targetNamespace );
  }
  
  private static XsdSchema unmarshall( InputStream in, HashSet unmarshalled ) {
    XsdSchema schema = null;
    
    IBindingFactory bf;
    try {
      bf = BindingDirectory.getFactory( XsdSchema.class );
      IUnmarshallingContext uctx = bf.createUnmarshallingContext( );
      schema = ( XsdSchema ) uctx.unmarshalDocument( in, null );
    } catch ( JiBXException e ) {
      throw new GeneratorException( "unable to unmarshall", e );
    }
    
    return schema;
  }
  
  // marshall to xml using JIBX
  private void marshall( OutputStream out ) {
    IBindingFactory bf;
    try {
      bf = BindingDirectory.getFactory( XsdSchema.class );
      IMarshallingContext mctx = bf.createMarshallingContext( );
      mctx.setIndent( 2 ); // 0 by default
      mctx.marshalDocument( this, "UTF-8", null, out );
    } catch ( JiBXException e ) {
      throw new GeneratorException( "unable to marshall", e );
    }
  }
  
  private int sizeStructures( ) {
    if ( structures == null ) {
      log.error( "structures == null" );
      return 0;
    }
    
    return structures.size( );
  }
  
  private XsdStructure getStructure( int i ) {
    return ( XsdStructure ) structures.get( i );
  }
  
  private int sizeIncludes( ) {
    if ( includes == null ) {
      //log.info( "includes == null" );
      return 0;
    }
    
    return includes.size( );
  }
  
  private XsdInclude getInclude( int i ) {
    return ( XsdInclude ) includes.get( i );
  }
  
  public JibxNamespace getTargetNamespace( ) {
    return targetNamespace;
  }
  
  public String getDefaultNamespace( ) {
    return defaultNamespace;
  }
  
  public void setTargetNamespace( JibxNamespace namespace ) {
    targetNamespace = namespace;
  }
  
  /** dereferences a namesapce uri from the prefix */
  public String getUri( String prefix ) {
    // see if it has already been dereferenced
    if ( namespaceUriToJibxNamespace.containsKey( prefix ) ) {
      // TODO figure out why this happens so much
      log.info( "namespace uri already dereferenced" );
      return prefix;
    }
    if ( prefix.equals( "xml" ) ) {
      return XML_NAMESPACE;
    } else if ( prefix.equals( "xs" ) ) {
      return "http://www.w3.org/2001/XMLSchema";
    }
    if ( prefixToUri.containsKey( prefix ) ) {
      return ( String ) prefixToUri.get( prefix );
    } else {
      String msg = "unable to dereference a namespace uri from prefix: "
                   + prefix;
      log.fatal( msg );
      log.fatal( prefixToUri );
      throw new GeneratorException( msg );
    }
  }
  
  public XsdResolver getXsdResolver( ) {
    return xsdResolver;
  }
  
  public void setXsdResolver( XsdResolver xsdResolver ) {
    this.xsdResolver = xsdResolver;
  }
}
