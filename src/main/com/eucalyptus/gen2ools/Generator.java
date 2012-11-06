// $Id: Generator.java,v 1.13 2005/02/19 20:02:30 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSourceFactory;

public class Generator {
  
  private static final Log log                  = LogFactory.getLog( Generator.class );
  
  private static HashMap   schemaTypes;                                                // primatives, String, and more
                                                                                        
  static final String      SCHEMA_NAMESPACE     = "http://www.w3.org/2001/XMLSchema";
  
  private HashMap          simpleTypes          = new HashMap( );
  
  //	 need to preserve order for binding file mappings
  private HashMap          complexTypes         = new ListMap( );
  
  private JibxBinding      binding              = new JibxBinding( );
  
  private HashMap          attributeGroups      = new HashMap( );                      // are global
                                                                                        
  // 2.2.2 Global Elements & Attributes
  //   http://www.w3.org/TR/xmlschema-0/#Globals
  //   may be called by reference
  private HashMap          globalElements       = new HashMap( );
  
  private HashMap          globalAttributes     = new HashMap( );
  
  // uri, JibxNamespace
  private TreeMap<String,JibxNamespace>          namespaces           = new TreeMap<String,JibxNamespace>( );
  
  private JSourceFactory   sourceFactory        = new JSourceFactory( );
  private JSourceFactory   serviceSourceFactory = new JSourceFactory( );
  
  //	 so a complexType doesn't get created twice
  private HashSet          createdSources       = new HashSet( );
  
  private GeneratorConfig  config;
  
  private HashMap          xsdTypeToJibxMapping = new HashMap( );
  
  static {
    schemaTypes = new HashMap( 33 );
    // Conversions
    //   http://jibx.sourceforge.net/conversions.html
    //   http://xml.apache.org/xmlbeans/docs/guide/conXMLBeansSupportBuiltInSchemaTypes.html
    //   http://www.castor.org/xmlschema.html
    
    // http://ws.apache.org/jaxme/release-0.3/apidocs/index.html
    
    // primitives
    schemaTypes.put( "byte", JQName.BYTE );
    schemaTypes.put( "char", JQName.CHAR );
    schemaTypes.put( "double", JQName.DOUBLE );
    schemaTypes.put( "float", JQName.FLOAT );
    schemaTypes.put( "int", JQName.INT );
    schemaTypes.put( "long", JQName.LONG );
    schemaTypes.put( "short", JQName.SHORT ); // perhaps it should be mapped
    // to int?
    schemaTypes.put( "boolean", JQName.BOOLEAN );
    
    schemaTypes.put( "unsignedLong", JQName.LONG );
    schemaTypes.put( "unsignedInt", JQName.LONG );
    schemaTypes.put( "unsignedShort", JQName.INT );
    schemaTypes.put( "unsignedByte", JQName.SHORT );
    
    // ID and IDREF
    schemaTypes.put( "ID", JQName.STRING );
    schemaTypes.put( "IDREF", JQName.OBJECT );
    
    // String Types
    schemaTypes.put( "string", JQName.STRING );
    schemaTypes.put( "token", JQName.STRING );
    schemaTypes.put( "NMTOKEN", JQName.STRING );
    schemaTypes.put( "anyURI", JQName.STRING );
    schemaTypes.put( "QName", JQName.STRING );
    schemaTypes.put( "normalizedString", JQName.STRING );
    schemaTypes.put( "NCName", JQName.STRING );
    schemaTypes.put( "language", JQName.STRING );
    schemaTypes.put( "name", JQName.STRING );
    schemaTypes.put( "ENTITY", JQName.STRING );
    schemaTypes.put( "normalizedString", JQName.STRING );
    
    // JiBX uses Date, not Calendar
    schemaTypes.put( "date", JQName.DATE );
    schemaTypes.put( "dateTime", JQName.DATETIME );
    schemaTypes.put( "time", JQName.TIME );
    
    // my own shortcut type for choice object, they return an Object
    schemaTypes.put( "object", JQName.OBJECT );
    
    // general numeric types
    schemaTypes.put( "integer", JQName.INTEGER );
    schemaTypes.put( "decimal", JQName.DECIMAL );
    schemaTypes.put( "positiveInteger", JQName.INTEGER );
    schemaTypes.put( "negativeInteger", JQName.INTEGER );
    schemaTypes.put( "nonPositiveInteger", JQName.INTEGER );
    schemaTypes.put( "nonNegativeInteger", JQName.INTEGER );
    
    schemaTypes.put( "base64Binary", JQName.BYTE_ARRAY );
    
    // TODO JiBX byte[] support for hexBinary?
    schemaTypes.put( "hexBinary", JQName.STRING );
  }
  
  public Generator( ) {}
  
  public void setConfig( GeneratorConfig config ) {
    this.config = config;
    XsdInclude.schemaLocationMap = config.getSchemaLocationMap( );
    sourceFactory.setJavaSourceFactory( config.getJavaSourceFactory( ) );
    serviceSourceFactory.setJavaSourceFactory( new JavaSourceFactory( ) );
  }
  
  public GeneratorConfig getConfig( ) {
    return config;
  }
  
  XsdElement getElement( XsdType name ) {
    return ( XsdElement ) globalElements.get( name );
  }
  
  /** checks to see if it is a root/named element */
  boolean isElement( XsdType name ) {
    return globalElements.containsKey( name );
  }
  
  /** checks to see if it is a root/named attribute */
  boolean isAttribute( XsdType name ) {
    return globalAttributes.containsKey( name );
  }
  
  XsdAttribute getAttribute( XsdType name ) {
    return ( XsdAttribute ) globalAttributes.get( name );
  }
  
  XsdAttributeGroup getAttributeGroup( XsdType name ) {
    return ( XsdAttributeGroup ) attributeGroups.get( name );
  }
  
  /** Loops through the XsdResolver list and processes each XML Schema. */
  public void execute( ) {
    HashSet unmarshalledXsds = new HashSet( ); // visited includes (may get
    // unmarshalled)
    XsdSchema firstSchema = null;
    
    XsdResolver xsdResolver = config.getXsdResolver( );
    for ( int i = 0, n = config.sizeXsdResolverList( ); i < n; i++ ) {
      XsdResolver resolver = config.getXsdResolver( i );
      
      // TODO may be unmarhsalledXsds should be processedXsdResolvers
      // does it need to be passed if generator is being passed as well?
      // HashSet processedXsdResolverSet = new HashSet();
      unmarshalledXsds.add( resolver.getId( ) );
      
      WsdlDocument wsdl = WsdlDocument.unmarshall( resolver, unmarshalledXsds, this );
      XsdSchema schema = wsdl.getSchema( );
      
      if ( i == 0 ) {
        firstSchema = schema;
      }
      
      simpleTypes.putAll( schema.getSimpleTypes( ) );
      complexTypes.putAll( schema.getComplexTypes( ) );
      globalElements.putAll( schema.getElements( ) );
      attributeGroups.putAll( schema.getAttributeGroups( ) );
      globalAttributes.putAll( schema.getAttributes( ) );
      
      namespaces.putAll( schema.getNamespaces( ) );
      for( JibxNamespace ns : namespaces.values( ) ) {
        if( "tns".equals( ns.getPrefix( ) ) ) {
          ns.setDefault("elements");
          binding.addNamespace( ns );
        }
      }
    }
    
    //		// add namespaces for the entire binding file if (
    //		firstSchema.getTargetNamespace( ) != null ) { //String tnsUri =
    //		firstSchema.getTargetNamespace( ).getUri(); //binding.addNamespace(
    //		firstSchema.getTargetNamespace( ) ); //JibxNamespace[] namespaces =
    //		firstSchema.getNamespaces(); ArrayList namespaces =
    //		firstSchema.getNamespaces(); //for ( int i = 0; i <
    //		namespaces.length; i++ ) { for( int i = 0, n = namespaces.size(); i <
    //		n; i++ ){
    //		
    //		JibxNamespace namespace = (JibxNamespace) namespaces.get(i);
    //		namespace.setDefault( null ); binding.addNamespace( namespace ); } }
    //		else { log.info( "targetNamespace == null" ); }
    
    log.info( "number of unmarshalled schemas: " + unmarshalledXsds.size( ) );
    
    log.info( "" + namespaces.size( ) + " namespaces:" );
    LogUtil.print( namespaces );
    
    log.info( "" + simpleTypes.size( ) + " simpleTypes:" );
    LogUtil.printKeys( simpleTypes );
    
    log.info( "" + complexTypes.size( ) + " complexTypes:" );
    LogUtil.printKeys( complexTypes );
    log.info( "" + globalElements.size( ) + " global elements:" );
    LogUtil.printKeys( globalElements );
    log.info( "" + globalAttributes.size( ) + " global attributes:" );
    LogUtil.printKeys( globalAttributes );
    log.info( "" + attributeGroups.size( ) + " attributeGroups:" );
    LogUtil.printKeys( attributeGroups );
    
    JSource sourceFile;
    
    HashSet remaptypes = new HashSet( );
    for ( int i = 0, n = config.sizeComplexTypeRemapList( ); i < n; i++ ) {
      GeneratorConfig.ComplexTypeRemap remap = config.getComplexTypeRemap( i );
      remaptypes.add( remap.getFromType( ) );
    }
    
    HashMap originalComplexTypes = new ListMap( complexTypes );
    Set keySet = originalComplexTypes.keySet( );
    Iterator keyIt = keySet.iterator( );
    while ( keyIt.hasNext( ) ) {
      XsdType key = ( XsdType ) keyIt.next( );
      XsdComplexType ct = ( XsdComplexType ) originalComplexTypes.get( key );
      if ( remaptypes.contains( key ) ) {
        ct.setIsReferenced( );
      }
      ct.process( );
    }
    
    log.info( "" + complexTypes.size( ) + " complexTypes:" );
    LogUtil.printKeys( complexTypes );
    
//		log.info("" + originalComplexTypes.size() + " originalComplexTypes:");
//		LogUtil.printKeys(originalComplexTypes);
    
    convertUsingLabelToMapAs( );
    
//    for ( int i = 0, n = binding.sizeMappingList( ); i < n; i++ ) {
//      JibxMapping mapping = binding.getMapping( i );
//      mapping.setNamespace( this );
//    }
    // TODO Dennis's request for .NET interopability
    // change mapping: lookup mapping given XsdType setName, setXsdType
    for ( int i = 0, n = config.sizeComplexTypeRemapList( ); i < n; i++ ) {
      GeneratorConfig.ComplexTypeRemap remap = config.getComplexTypeRemap( i );
      JibxMapping mapping = getMapping( remap.getFromType( ) );
      if ( mapping == null ) {
        String errMsg = "unable to remap: " + remap.getFromType( );
        log.fatal( errMsg );
        throw new GeneratorException( errMsg );
      }
      log.info( "remap: " + remap );
      mapping.setName( remap.getToName( ) );
      mapping.setNs( remap.getToNamespace( ) );
//			mapping.getXsdType().setNamespaceUri(remap.getToNamespace());
      JibxNamespace ns = getNamespace( remap.getToNamespace( ) );
      if ( ns == null ) {
        ns = new JibxNamespace( );
        ns.setDefault( JibxNamespace.DEFAULT_NONE );
        ns.setPrefix( remap.getToPrefix( ) );
        ns.setUri( remap.getToNamespace( ) );
        namespaces.put( remap.getToNamespace( ), ns );
      }
      mapping.addNamespace( getNamespace( remap.getToNamespace( ) ) );
    }
    
    if ( firstSchema.getTargetNamespace( ) == null ) {
      log.info( "targetNamespace == null" );
    } else {
      config.setTargetPackage( getJavaPackageFromXmlNamespaceUri( firstSchema.getTargetNamespace( ).getUri( ) ) );
    }
    generateServiceCommon( );
    generateService( );
  }
  
  private void generateServiceCommon( ) {
    JSource baseMsgsource = sourceFactory.newJSource( Generate.baseType );
    baseMsgsource.addExtends( Generate.systemMessageType );
  }
  
  private void generateService( ) {
    //RequestQueue
    JSource source = serviceSourceFactory.newJSource( new JQName( JavaQNameImpl.getInstance( this.config.getTargetPackage( ), Generate.serviceName + "Service" ) ) );
    log.info( "===============================================================" );
    log.info( complexTypes );
    log.info( "===============================================================" );
    log.info( namespaces );
    log.info( "===============================================================" );
    log.info( globalElements );
    log.info( "===============================================================" );
    log.info( WsdlDocument.document.getOperations( ) );
    log.info( "===============================================================" );
    log.info( WsdlDocument.document.getMessages( ) );
    log.info( "===============================================================" );
    for ( final WsdlOperation operation : WsdlDocument.document.getOperations( ) ) {
      try {
        final JQName input = resolve( WsdlDocument.document.getMessageType( operation.getInput( ) ).getElement( ) + "Type" );
        JQName output = resolve( WsdlDocument.document.getMessageType( operation.getOutput( ) ).getElement( ) + "Type" );
        JavaMethod method = source.addMethod( operation.name.substring( 0, 1 ).toLowerCase( ) + operation.name.substring( 1 ), new HashMap<String,JQName>( ) {
          {
            put( "request", input );
          }
        }, output, new ArrayList<JQName>( ) );
        method.addLine( output.getClassName( ), " reply = request.getReply( );" );
        method.addLine( "return reply;" );
      } catch ( NullPointerException ex ) {
        log.error( "Failed: " + ex.getMessage( ) + " for " + operation, ex );
        log.error( WsdlDocument.document.getMessageType( operation.getInput( ) ) );
        log.error( resolve( WsdlDocument.document.getMessageType( operation.getInput( ) ).getElement( ) ) );
        log.error( WsdlDocument.document.getMessageType( operation.getOutput( ) ) );
        log.error( resolve( WsdlDocument.document.getMessageType( operation.getOutput( ) ).getElement( ) ) );
      }
    }
  }
  
  private JQName resolve( String name ) {
    for ( Entry<String, JibxNamespace> namespace : ( Set<Map.Entry<String, JibxNamespace>> ) namespaces.entrySet( ) ) {
      if ( name.startsWith( namespace.getValue( ).getPrefix( ) + ":" ) ) {
        String ns = namespace.getKey( );
        XsdType xsdType = new XsdType( ns, name.substring( namespace.getValue( ).getPrefix( ).length( ) + 1 ) );
        if ( !complexTypes.containsKey( xsdType ) ) {
//          throw new NoSuchElementException( "Failed to find xsdComplexType: " + xsdType + " in " + complexTypes );
          return new JQName( JavaQNameImpl.getInstance( xsdType.getType( ) ) );
        } else {
          return new JQName( JavaQNameImpl.getInstance( ( ( XsdComplexType ) complexTypes.get( xsdType ) ).getQName( ).getClassName( ) ) );
        }
      }
    }
    throw new NoSuchElementException( "Failed to find namespace for reference: " + name + " in " + namespaces );
  }
  
  /**
   * Checks for non default namespaces and sets the namespace if necessary to
   * the mapping and value.
   */
  private void checkForAdditionalNamespaces( ) {
    for ( int i = 0, n = binding.sizeMappingList( ); i < n; i++ ) {
      JibxMapping mapping = binding.getMapping( i );
      String uri = mapping.getNamespace( 0 ).getUri( );
      ArrayList values = mapping.getValues( );
      for ( int j = 0, m = values.size( ); j < m; j++ ) {
        JibxValue value = ( JibxValue ) values.get( j );
        if ( value.getNamespace( ) != null ) {
          String valueUri = value.getNamespace( ).getUri( );
          if ( !uri.equals( valueUri ) ) {
            value.setNs( valueUri );
            JibxNamespace namespace = ( JibxNamespace ) namespaces
                                                                  .get( valueUri );
            if ( namespace != null ) {
              mapping.addNamespace( namespace );
            } else {
              log.error( "unable to get namespace for: "
                         + valueUri );
            }
            
          }
        }
      }
    }
  }
  
  /**
   * Every structure starts out with the using/label approach. As many
   * structures as possible are converted to the map-as approach, since the
   * using/label approach is more of a work-a-round.
   */
  private void convertUsingLabelToMapAs( ) {
    for ( int i = 0, n = binding.sizeMappingList( ); i < n; i++ ) {
      JibxMapping mapping = binding.getMapping( i );
      mapping.setGenerator( this );
      if ( mapping == null ) { // shouldn't happen
        String errMsg = "mapping == null, i: " + i;
        log.error( errMsg );
        throw new NullPointerException( errMsg );
      }
      xsdTypeToJibxMapping.put( mapping.getXsdType( ), mapping );
    }
    
    log.info( "" + xsdTypeToJibxMapping.size( ) + " mappings:" );
    LogUtil.printKeys( xsdTypeToJibxMapping );
    
    for ( int i = 0, n = binding.sizeMappingList( ); i < n; i++ ) {
      JibxMapping mapping = binding.getMapping( i );
      mapping.addMappingRefs( this );
    }
    
    for ( int i = 0, n = binding.sizeMappingList( ); i < n; i++ ) {
      JibxMapping mapping = binding.getMapping( i );
      mapping.convertUsingLabelToMapAs( );
    }
    
  }
  
  public JibxMapping getMapping( XsdType xsdType ) {
    return ( JibxMapping ) xsdTypeToJibxMapping.get( xsdType );
  }
  
  boolean isSchemaType( XsdType type ) {
    //log.info( "isSchemaType, type: " + type );
    String uri = type.getNamespaceUri( );
    if ( uri != null && !uri.equals( SCHEMA_NAMESPACE ) ) {
      return false;
    }
    return schemaTypes.containsKey( type.getType( ) );
  }
  
  JQName getQNameSchemaType( XsdType type ) {
    return ( JQName ) schemaTypes.get( type.getType( ) );
  }
  
  JQName getQNameSimpleType( XsdType type ) {
    JQName qname = null;
    
    XsdSimpleType st = ( XsdSimpleType ) getSimpleType( type );
    XsdType base = st.getBase( );
    
    log.info( "getQNameSimpleType, type: " + type + ", base: " + base );
    
    if ( isSchemaType( base ) ) {
      qname = getQNameSchemaType( base );
    } else if ( isSimpleType( base ) ) {
      qname = getQNameSimpleType( base ); // recursive
    }
    
    return qname;
    
  }
  
  boolean isComplexType( XsdType type ) {
    return complexTypes.containsKey( type );
  }
  
  XsdComplexType getComplexType( XsdType type ) {
    XsdComplexType ct = ( XsdComplexType ) complexTypes.get( type );
    if ( ct == null ) {
      throw new NullPointerException( "unable to get XsdComplexType for " + type );
    }
    return ct;
  }
  
  boolean isSimpleType( XsdType type ) {
    return simpleTypes.containsKey( type );
  }
  
  XsdSimpleType getSimpleType( XsdType type ) {
    return ( XsdSimpleType ) simpleTypes.get( type );
  }
  
  void addComplexType( XsdComplexType complexType ) {
    complexTypes.put( complexType.getXsdType( ), complexType );
  }
  
  /** returns the package name, given the namespace uri. */
  String getJavaPackageFromXmlNamespaceUri( String uriStr ) {
    
    if ( config != null ) {
      Map xmlNamespaceToJavaPackage = config.getXmlNamespaceToJavaPackageMap( );
      // check to see if the user specified a certain package for the namespace uri
      if ( xmlNamespaceToJavaPackage != null && xmlNamespaceToJavaPackage.containsKey( uriStr ) ) {
        return ( String ) xmlNamespaceToJavaPackage.get( uriStr );
      }
    }
    
    try {
      // discard scheme component of URI
      int split = uriStr.indexOf( ':' );
      String uri = uriStr.substring( split + 1 );
      
      // skip leading slashes
      while ( uri.charAt( 0 ) == '/' ) {
        uri = uri.substring( 1 );
      }
      
      // authority is everything up to next slash, path the rest
      split = uri.indexOf( '/' );
      String authority = uri.substring( 0, split );
      String path = uri.substring( split );
      
      //log.info( "uri authority: " + authority );
      //log.info( "uri path: " + path );
      // ignore the "www" part of "www.jibx.org"
      if ( authority.substring( 0, 4 ).equalsIgnoreCase( "www." ) ) {
        authority = authority.substring( 4 );
//				log.info( "uri authority: " + authority );
        System.out.println( "uri authority: " + authority );
      }
      
      // ignore port, if given
      split = authority.indexOf( ':' );
      if ( split >= 0 ) {
        authority = authority.substring( 0, split );
      }
      
      // reverse the domains for package path
      StringBuffer pckg = new StringBuffer( );
      while ( authority.length( ) > 0 ) {
//                split = authority.indexOf('.');
        split = authority.lastIndexOf( '.' );
        String domain;
        if ( split >= 0 ) {
//                    domain = authority.substring(0, split);
//                    authority = authority.substring(split+1);
          domain = authority.substring( split + 1 );
          authority = authority.substring( 0, split );
        } else {
          domain = authority;
          authority = "";
        }
        if ( pckg.length( ) > 0 ) {
          pckg.append( '.' );
        }
        pckg.append( NameUtil.toLowerCamelCase( domain ) );
      }
      
      // append components of path
      while ( path.length( ) > 0 ) {
        split = path.indexOf( '/' );
        String step;
        if ( split >= 0 ) {
          step = path.substring( 0, split );
          path = path.substring( split + 1 );
        } else {
          step = path;
          path = "";
        }
        if ( step.length( ) > 0 ) {
          pckg.append( '.' );
        }
        pckg.append( NameUtil.toLowerCamelCase( step ) );
      }
      
      //log.info( "uri: " + uri + ", package: " + pckg.toString() );
      return pckg.toString( );
      //} catch ( URISyntaxException e ) {
    } catch ( Exception e ) {
      log.error( " unable to create package name from uri: " + uriStr );
      return "error"; // default uri
    }
    
    //return "error";
  }
  
  void addCreatedSource( String string ) {
    createdSources.add( string );
  }
  
  JSource newJSource( JQName qname ) {
    return sourceFactory.newJSource( qname );
  }
  
  boolean containsCreatedSources( String string ) {
    return createdSources.contains( string );
  }
  
  public void writeJavaSources( final String header ) {
    sourceFactory.write( ( new File( config.getCommonOutputDir( ) ) ), header, true );
    serviceSourceFactory.write( ( new File( config.getServiceOutputDir( ) ) ), header, false );
  }
  
  public void marshalJibxBindingDefinition( ) {
    String targetPackage = config.getTargetPackage( );
    String bindingFile = config.getBindingFile( );
    if ( bindingFile == null ) {
      //    use the last/containing package name
      int dotIndex = targetPackage.lastIndexOf( '.' );
      if ( dotIndex > 0 && dotIndex < targetPackage.length( ) ) {
        bindingFile = targetPackage.substring( dotIndex + 1 );
      } else {
        bindingFile = targetPackage;
        if ( bindingFile.length( ) == 0 ) {
          bindingFile = "default";
        }
      }
      bindingFile = bindingFile + ".jibx.xml";
    }
    if ( targetPackage.length( ) > 0 ) {
      targetPackage = targetPackage + '/';
    }
    bindingFile = config.getCommonOutputDir( ).replaceAll( "main/java", "main/resources/" ) + bindingFile;
    log.info( "marshal out jibx binding file: " + bindingFile );
    new File( bindingFile ).getParentFile( ).mkdirs( );
    OutputStream out;
    try {
      out = new FileOutputStream( bindingFile );
    } catch ( FileNotFoundException e ) {
      throw new GeneratorException( "unable to create JiBX binding file: ", e );
    }
    binding.marshal( out );
  }
  
  // used to be apart of XsdFeature (element/attribute)
  JQName getQName( XsdType type ) {
    
    JQName qname = null;
    
    if ( isSchemaType( type ) ) {
      return getQNameSchemaType( type );
    } else if ( isSimpleType( type ) ) {
      return getQNameSimpleType( type );
    } else if ( isComplexType( type ) ) {
      XsdComplexType ct = ( XsdComplexType ) getComplexType( type );
      return ct.getQName( );
    } else if ( isElement( type ) ) {
      // get the reference
      XsdElement element = getElement( type );
      log.info( "element: " + type + ", type: " + element.getType( ) );
      if ( element.getType( ) != null ) {
        return getQName( element.getType( ) ); // recursive
      }
    }
    
    if ( qname == null ) {
      String errMsg = "type not yet supported: " + type;
      log.fatal( errMsg );
      throw new GeneratorException( errMsg );
    }
    
    return qname;
  }
  
  public JibxNamespace getNamespace( String nsUri ) {
    return ( JibxNamespace ) namespaces.get( nsUri );
  }
  
  public void addMapping( JibxMapping mapping ) {
    binding.addMapping( mapping );
  }

  /**
   * @return the binding
   */
  public JibxBinding getBinding( ) {
    return this.binding;
  }
  
}
