package com.eucalyptus.gen2ools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.IteratorUtils;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;

/** wraps JavaSourceFactory since I can't extend it */
public class JSourceFactory {
  
  private JavaSourceFactory factory = null;
  
  public JSourceFactory( ) {}
  
  public JSource newJSource( JQName qname ) {
    return new JSource( factory.newJavaSource( qname.getJavaQName( ),
                                               JavaSource.PUBLIC ) );
  }
  
  public void write( File dir, String header, boolean doGroovy ) {
    try {
      if ( doGroovy ) {
        ByteArrayOutputStream os = new ByteArrayOutputStream( );
        OutputStreamWriter out = new OutputStreamWriter( os );
        List<JavaSource> list = ( List<JavaSource> ) IteratorUtils.toList( factory.getJavaSources( ) );
        for ( JavaSource js : list ) {
          js.write( out );
        }
        out.flush( );
        out.close( );
        String[] groovyLines = os.toString( ).split( "\n" );
        String packageName = "";
        Set<String> imports = new HashSet<String>( );
        String classes = "";
        for ( String s : groovyLines ) {
          if ( s.startsWith( "package " ) ) {
            packageName = s;
          } else if ( s.startsWith( "import " ) ) {
            imports.add( s );
          } else if ( s.matches( "^\\s*$" ) ) {
            continue;
          } else if ( s.matches( "^\\s+}\\s*$" ) ) {
            classes += s;
          } else {
            classes += "\n" + s;
          }
        }
        
        File f = new File(
                           ( dir.getAbsolutePath( ) + "/" + packageName.substring( 8 ).replaceAll( "\\.|;|.(\\w+$)", "/" ) + Generate.serviceName + "Messages.groovy" ).replaceAll( "//",
                                                                                                                                                                                    "/" ) );
        if ( (f.getParentFile().exists() || f.getParentFile( ).mkdirs( )) && (f.canWrite() || f.createNewFile( )) ) {
          PrintWriter fileOut = new PrintWriter( f );
          fileOut.println( header );
          fileOut.println( packageName );
          for ( String s : imports ) {
            fileOut.println( s );
          }
          fileOut.println( "\n\n" + classes );
          fileOut.flush( );
          fileOut.close( );
        } else {
          throw new GeneratorException( "unable to write source files to directory: " + f.getParentFile( ).getAbsolutePath() );
        }
      } else {
        factory.write( dir );
      }
    } catch ( IOException e ) {
      throw new GeneratorException( "unable to write source files to directory: " + dir, e );
    }
  }
  
  public JavaSourceFactory getJavaSourceFactory( ) {
    return factory;
  }
  
  public void setJavaSourceFactory( JavaSourceFactory factory ) {
    this.factory = factory;
  }
}
