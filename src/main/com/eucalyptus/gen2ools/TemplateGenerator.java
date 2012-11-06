/*******************************************************************************
 * Copyright (c) 2009  Eucalyptus Systems, Inc.
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, only version 3 of the License.
 * 
 * 
 *  This file is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 * 
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Please contact Eucalyptus Systems, Inc., 130 Castilian
 *  Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/>
 *  if you need additional information or have any questions.
 * 
 *  This file may incorporate work covered under the following copyright and
 *  permission notice:
 * 
 *    Software License Agreement (BSD License)
 * 
 *    Copyright (c) 2008, Regents of the University of California
 *    All rights reserved.
 * 
 *    Redistribution and use of this software in source and binary forms, with
 *    or without modification, are permitted provided that the following
 *    conditions are met:
 * 
 *      Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *      Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 * 
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *    IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *    TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. USERS OF
 *    THIS SOFTWARE ACKNOWLEDGE THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE
 *    LICENSED MATERIAL, COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS
 *    SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *    IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
 *    BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
 *    THE REGENTS’ DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
 *    OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
 *    WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
 *    ANY SUCH LICENSES OR RIGHTS.
 *******************************************************************************
 * @author chris grzegorczyk <grze@eucalyptus.com>
 */

package com.eucalyptus.gen2ools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class TemplateGenerator {
  private static Log LOG = LogFactory.getLog( TemplateGenerator.class );
  
  public static void 
  generate( final GeneratorConfig config, Generator gen ) {
    
    String authorName = "Chris Grzegorczyk";
    String authorEmail = "grze@eucalyptus.com";

    
    final String serviceName = Generate.serviceName;
    final String serviceLname = Generate.serviceName.toLowerCase( );
    final String serviceUname = Generate.serviceName.toUpperCase( );
    final String serviceMessageClassSimple = Generate.baseType.getJavaQName( ).getClassName( );
    final String serviceMessageClass = config.getTargetPackage( ) + "." + Generate.baseType.getJavaQName( ).getClassName( );
    final String serviceClass = config.getTargetPackage( ) + "." + Generate.serviceName + "Service";
    final String servicePackage = config.getTargetPackage( );
    
    Velocity.init( );
    
    VelocityContext context = new VelocityContext( );
    context.put( "author-name", authorName );
    context.put( "author-email", authorEmail );
    context.put( "package-name", servicePackage );
    context.put( "service-name", serviceName );
    context.put( "service-lname", serviceLname );
    context.put( "service-uname", serviceUname );
    context.put( "service-class", serviceClass );
    context.put( "service-package", servicePackage );
    context.put( "service-message-class", serviceMessageClass );
    context.put( "service-message-simple-class", serviceMessageClassSimple );
    context.put( "binding-name", gen.getBinding( ).getName( ) );
    context.put( "binding-namespace", gen.getBinding( ).getTargetNamespace( ) );
    
    for( Object key : context.getKeys( ) ) {
      LOG.info( key + " => " + context.get( ( String ) key ) );
    }

    Map<String,File> templateOutputMap = new HashMap<String,File>() {{
      put( "templates/base-model.xml.vm", new File( config.getServiceOutputBaseDir( ) + "/src/main/resources/" + serviceLname + "-model.xml"  ) );
      put( "templates/build.xml.vm", new File( config.getServiceOutputBaseDir( ) + "/build.xml"  ) );
      put( "templates/build-common.xml.vm", new File( config.getCommonOutputBaseDir( ) + "/build.xml"  ) );
      put( "templates/ErrorHandler.java.vm", new File( config.getServiceOutputBaseDir( ) + "/src/main/java/" + config.getTargetPackage( ).replaceAll( "\\.", File.separator ) + "/ws", serviceName + "ErrorHandler.java"  ) );
      put( "templates/QueryBinding.java.vm", new File( config.getServiceOutputBaseDir( ) + "/src/main/java/" + config.getTargetPackage( ).replaceAll( "\\.", File.separator ) + "/ws", serviceName + "QueryBinding.java"  ) );
      put( "templates/QueryPipeline.java.vm", new File( config.getServiceOutputBaseDir( ) + "/src/main/java/" + config.getTargetPackage( ).replaceAll( "\\.", File.separator ) + "/ws", serviceName + "QueryPipeline.java"  ) );
      put( "templates/Exception.java.vm", new File( config.getServiceOutputBaseDir( ) + "/src/main/java/" + config.getTargetPackage( ).replaceAll( "\\.", File.separator ), serviceName + "Exception.java"  ) );
    }};
    for( String k : templateOutputMap.keySet( ) ) {
      LOG.info( "Writing out from template: " + k + " => " + templateOutputMap.get( k ).getAbsolutePath( ) ); 
      writeTemplate( context, k, templateOutputMap.get( k ) ); 
    }
  }

  private static void writeTemplate( VelocityContext context, String templateName, File outFile ) {
    if( !outFile.getParentFile( ).exists( ) ) {
      outFile.getParentFile( ).mkdirs( );
    }
    Template template = null;
    try {
      template = Velocity.getTemplate( templateName );
      FileWriter out = new FileWriter( outFile );
      template.merge( context, out );
      out.flush( );
      out.close( );
    } catch ( ResourceNotFoundException ex ) {
      // couldn't find the template
      LOG.error( ex , ex );
    } catch ( ParseErrorException ex ) {
      // syntax error: problem parsing the template
      LOG.error( ex , ex );
    } catch ( MethodInvocationException ex ) {
      // something invoked in the template
      // threw an exception
      LOG.error( ex , ex );
    } catch ( IOException ex ) {
      LOG.error( ex , ex );
    }
  }
}
