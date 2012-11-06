package com.eucalyptus.gen2ools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;

/**
 * Command line interface for the Xsd2Jibx code generation.
 */
public class Generate {
  /**
   * Faked map for converting schema namespaces to packages. This is a very partial and incomplete
   * implementation that basically just fakes the portions of the
   * Map interface necessary for the conversion. Ugly, but required by the design of the Xsd2Jibx
   * API.
   */
  private class PackageMap implements Map {
    public void clear( ) {}
    
    public boolean containsKey( final Object key ) {
      return true;
    }
    
    public boolean containsValue( final Object value ) {
      return false;
    }
    
    public Set entrySet( ) {
      return null;
    }
    
    /**
     * Get value for key. This is the only method that's actually semi-implemented, since it's the
     * only one actually used by the code.
     */
    public Object get( final Object key ) {
      return Generate.this.pathForNamespace( ( String ) key );
    }
    
    public boolean isEmpty( ) {
      return false;
    }
    
    public Set keySet( ) {
      return null;
    }
    
    public Object put( final Object key, final Object value ) {
      return null;
    }
    
    public void putAll( final Map t ) {}
    
    public Object remove( final Object key ) {
      return null;
    }
    
    public int size( ) {
      return 0;
    }
    
    public Collection values( ) {
      return null;
    }
  }
  
  /**
   * Command line interface for Xsd2Jibx generation.
   * 
   * @param args
   *          command line arguments
   */
  public static void main( final String[] args ) {
    
    // make sure we have schema to process
    boolean help = false;
    if ( args.length > 0 ) {
      String baseTypeName = null;
      // process leading special arguments
      final Generate inst = new Generate( );
      int offset = 0;
      final ArrayList remaps = new ArrayList( );
      for ( ; offset < args.length - 1; offset++ ) {
        final String arg = args[offset];
        if ( "-d".equalsIgnoreCase( arg ) ) {
          inst.m_generateDirectory = args[++offset];
          new File( inst.m_generateDirectory ).mkdirs( );
        } else if ( "-t".equalsIgnoreCase( arg ) ) {
          final String spec = args[++offset];
          final int split = spec.indexOf( '=' );
          if ( split > 0 ) {
            inst.m_pathLeadMatch.add( spec.substring( 0, split ) );
            inst.m_pathLeadReplace.add( spec.substring( split + 1 ) );
          } else {
            System.err.println( "Invalid URI substitution: " + spec );
            System.exit( 1 );
          }
        } else if ( "-p".equalsIgnoreCase( arg ) ) {
          inst.defaultPackage = args[++offset];
        } else if ( "-n".equalsIgnoreCase( arg ) ) {
          inst.serviceName = args[++offset];
          if ( inst.serviceName.endsWith( "Service" ) ) {
            inst.serviceName = inst.serviceName.replaceAll( "Service", "" );
          }
        } else if ( "-m".equalsIgnoreCase( arg ) ) {
          baseTypeName = args[++offset];
        } else if ( "-h".equalsIgnoreCase( arg ) ) {
          header = args[++offset];
        } else {
          break;
        }
      }
      if ( baseTypeName ==null && inst.serviceName != null ) {
        baseTypeName = inst.serviceName + "Message";
      }
      if ( baseTypeName == null || inst.serviceName == null || inst.defaultPackage == null ) {
        System.out.print( "ERROR: All flags are required" );
        printHelp( );
      }
      Generate.baseType = new JQName( JavaQNameImpl.getInstance( Generate.defaultPackage, baseTypeName ) );
      if ( ( offset < args.length ) && args[offset].startsWith( "-" ) ) {
        System.err.println( "Unknown argument " + args[offset] );
        help = true;
      } else if ( offset < args.length ) {
        
        // process all remaining arguments as schemas
        final ArrayList schemas = new ArrayList( );
        while ( offset < args.length ) {
          schemas.add( args[offset++] );
        }
        System.out.println( "SCHEMAS: " + schemas );
        try {
          inst.generate( schemas );
        } catch ( final Throwable t ) {
          t.printStackTrace( );
          System.exit( 1 );
        }
        
      } else {
        System.err.println( "Need one or more schemas to process" );
        help = true;
      }
    }
    
    // print usage information if problem with parameters
    if ( help ) {
      printHelp( );
      System.exit( 1 );
    }
  }
  
  private static void printHelp( ) {
    System.out.println( "\nUsage: java org.jibx.xsd2jibx.Generate [-flags] wsdl" +
                        "Where all are of the following are require:\n"
                        + " -d  target directory for generated classes,\n"
                        + " -p  package used for no-namespace schemas,\n"
                        + " -m  base type from which to inherit message types\n"
                        + " -t  target namespace prefix mapping (e.g., tns=com.eucalyptus...)\n"
                        + " -n  name of the service\n"
                        + " -h  header for Java/Groovy sources\n");
  }
  
  // base directory for code generation
  private String          m_generateDirectory = ".";
  // default package used for no-namespace schemas
  public static String    defaultPackage      = "";
  // file name for header text
  public static String    header = "";

  // substitution match and replacement strings
  private final ArrayList m_pathLeadMatch;
  
  private final ArrayList m_pathLeadReplace;
  public static String    serviceName;
  public static JQName    baseType;
  public static JQName    systemMessageType   = new JQName( JavaQNameImpl.getInstance( "edu.ucsb.eucalyptus.msgs.BaseMessage" ) );
  
  private Generate( ) {
    this.m_pathLeadMatch = new ArrayList( );
    this.m_pathLeadReplace = new ArrayList( );
  }
  
  /**
   * Generate JiBX code and bindings for a list of schema definitions.
   * 
   * @param schemas
   *          list of schema elements to be processed
   */
  private void generate( final List schemas ) {
    
    // set up for generation of all schemas
    final Generator gen = new Generator( );
    final GeneratorConfig cfg = new GeneratorConfig( );
    cfg.setOutputDir( this.m_generateDirectory );
    cfg.setBindingFile( Generate.serviceName.toLowerCase( ) + "-binding.xml" );
    cfg.setTargetPackage( this.defaultPackage );
    cfg.setXmlNamespaceToJavaPackageMap( new PackageMap( ) );
    
    // add all schemas to be processed
    for ( final Iterator iter = schemas.iterator( ); iter.hasNext( ); ) {
      final String path = ( String ) iter.next( );
      final FileXsdResolver resolver = new FileXsdResolver( path );
      cfg.addXsdResolver( resolver );
    }
    
    // handle the actual generation
    gen.setConfig( cfg );
    gen.execute( );
    gen.writeJavaSources( readFile( header ) );
    gen.marshalJibxBindingDefinition( );
    TemplateGenerator.generate( cfg, gen );
  }

  private String readFile( final String path ) {
    try {
      String content = "";
      if ( path != null && !path.isEmpty() ) {
        final File file = new File( path );
        final FileInputStream in = new FileInputStream(file);
        final byte[] data = new byte[(int)file.length()];
        in.read( data );
        in.close();
        content = new String( data );
      }
      return content;
    } catch ( IOException e ) {
      throw new GeneratorException( "unable to read header from: " + path, e );
    }
  }

  /**
   * Convert a namespace URI to a package path.
   * 
   * @param uri
   *          namespace URI to be converted
   */
  private String pathForNamespace( final String uri ) {
    
    // first check for match to a replacement specification
    String path = uri;
    for ( int i = 0; i < this.m_pathLeadMatch.size( ); i++ ) {
      final String match = ( String ) this.m_pathLeadMatch.get( i );
      if ( path.startsWith( match ) ) {
        return ( String ) this.m_pathLeadReplace.get( i ) + path.substring( match.length( ) ).toLowerCase( );
      }
    }
    return this.defaultPackage;
  }
}
