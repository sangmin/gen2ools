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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaQName;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

public class WsdlDocument {
  private static Log LOG = LogFactory.getLog( WsdlDocument.class );
  public XsdSchema   schema;
  public static WsdlDocument document;
  public static XsdResolver xsdResolver;
  public ArrayList<WsdlMessage> messages = new ArrayList<WsdlMessage>();
  public ArrayList<WsdlOperation> operations = new ArrayList<WsdlOperation>();
  public ArrayList dummy = new ArrayList();
  
  public WsdlDocument( ) {
    super( );
    document = this;
  }
  private Map<String,WsdlMessage> msgMap = null;
  public WsdlMessage getMessageType( String name ) {
    if( msgMap == null ) {
      msgMap = new HashMap<String,WsdlMessage>();
      for( WsdlOperation op : operations ) {
        for( WsdlMessage msg : messages ) {
          if( msg.getName( ).equals( op.getInput( ).replaceFirst( "\\w*:", "" ) ) ) {
            msgMap.put( op.getInput( ), msg );
            msgMap.put( msg.getElement( ).replaceFirst( "\\w*:", "" ), msg );
          }
          if( msg.getName( ).equals( op.getOutput( ).replaceFirst( "\\w*:", "" ) ) ) {
            msgMap.put( op.getOutput( ), msg );
            msgMap.put( msg.getElement( ).replaceFirst( "\\w*:", "" ), msg );
          }
        }
      }
      LOG.info( "MSGMAP: " + msgMap );
    }
    return msgMap.get( name );
  }
  
  /**
   * @return the schema
   */
  public XsdSchema getSchema( ) {
    return this.schema;
  }
  
  public void setSchema( XsdSchema schema ) {
    schema.setXsdResolver( xsdResolver );
    this.schema = schema;
  }
  
  static WsdlDocument unmarshall( XsdResolver xsdResolver, HashSet unmarshalledXsds, Generator generator ) {
    xsdResolver = xsdResolver;
    WsdlDocument wsdl = null;
    XsdSchema schema = null;
    try {
      LOG.info( "unmarshall schema: " + xsdResolver.getTargetNamespace( ) );
      InputStream in = xsdResolver.getInputStream( );
      
      StreamSource xmlSS = new StreamSource( in );
      byte[] xsdBA = transformXml( "xsd-remove-namespace.xsl", xmlSS );
      
      String xsd = new String( xsdBA );
      
      LOG.debug( "after remove-namespace.xsl, " + in + ":\n" + xsd );
      xsdBA = transformXml( "xsd-complextype-extends.xsl", xsdBA );
      xsd = new String( xsdBA );
      LOG.debug( "after complextype-extends.xsl, " + in + ":\n" + xsd );
      
      xsdBA = transformXml( "xsd-remove-elements.xsl", xsdBA );
      xsd = new String( xsdBA );
      LOG.debug( "after remove-elements.xsl, " + in + ":\n" + xsd );
      
      // Bind to the transformed XSD.
      wsdl = WsdlDocument.unmarshall( new ByteArrayInputStream( xsdBA ),
                                        unmarshalledXsds );
      schema = wsdl.getSchema( );
      ByteArrayOutputStream xsdOS = new ByteArrayOutputStream( );
      wsdl.marshall( xsdOS );
      xsdBA = xsdOS.toByteArray( );
      xsd = new String( xsdBA );
      LOG.debug( "Marshalled XSD " + in + ":\n" + xsd );
      
      
    } catch ( TransformerException e ) {
      throw new GeneratorException( "problem with xsl transformer", e );
    }
    
    schema.readNamespaces( xsdResolver.getInputStream( ) );
    schema.sortStructures( generator );
    schema.processIncludedSchemas( unmarshalledXsds, generator );
    LOG.info( wsdl );
    return wsdl;
  }
  
  /**
   * TODO: DOCUMENT WsdlDocument.java
   * 
   * @param byteArrayInputStream
   * @param unmarshalledXsds
   * @return
   */
  private static WsdlDocument unmarshall( ByteArrayInputStream in, HashSet unmarshalledXsds ) {
    WsdlDocument wsdl = null;
    
    IBindingFactory bf;
    try {
      bf = BindingDirectory.getFactory( XsdSchema.class );
      IUnmarshallingContext uctx = bf.createUnmarshallingContext( );
      wsdl = ( WsdlDocument ) uctx.unmarshalDocument( in, null );
    } catch ( JiBXException e ) {
      throw new GeneratorException( "unable to unmarshall", e );
    }
    
    return wsdl;
  }
  
  private static byte[] transformXml( String xslFile, byte[] xmlBA ) {
    byte[] ba;
    StreamSource xmlSS = new StreamSource( new ByteArrayInputStream( xmlBA ) );
    try {
      ba = transformXml( xslFile, xmlSS );
    } catch ( TransformerException e ) {
      String errMsg = "unable to transformXml, xslFile: " + xslFile;
      LOG.fatal( errMsg, e );
      throw new GeneratorException( errMsg, e );
    }
    return ba;
  }
  
  private void marshall(OutputStream out) {
    IBindingFactory bf;
    try {
      bf = BindingDirectory.getFactory(XsdSchema.class);
      IMarshallingContext mctx = bf.createMarshallingContext();
      mctx.setIndent(2); // 0 by default
      mctx.marshalDocument(this, "UTF-8", null, out);
    } catch (JiBXException e) {
      throw new GeneratorException("unable to marshall", e);
    }
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

  /**
   * @return the messages
   */
  public ArrayList getMessages( ) {
    return this.messages;
  }

  /**
   * @param messages the messages to set
   */
  public void setMessages( ArrayList messages ) {
    this.messages = messages;
  }

  /**
   * @return the operations
   */
  public ArrayList<WsdlOperation> getOperations( ) {
    return this.operations;
  }

  /**
   * @param operations the operations to set
   */
  public void setOperations( ArrayList operations ) {
    this.operations = operations;
  }

  /**
   * TODO: DOCUMENT
   * @see java.lang.Object#toString()
   * @return
   */
  @Override
  public String toString( ) {
    return String.format( "WsdlDocument:messages=%s:operations=%s", this.messages, this.operations );
  }

  /**
   * TODO: DOCUMENT WsdlDocument.java
   * @param name
   * @return
   */
  public boolean containsMessage( String name ) {
    for( WsdlMessage msg : this.messages ) {
      if( msg.getElement( ).endsWith( name ) ) {
        return true;
      }
    }
    return false;
  }

}
