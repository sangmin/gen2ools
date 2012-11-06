/*
 * Created on Aug 15, 2004
 */
package com.eucalyptus.gen2ools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class FileXsdResolver implements XsdResolver {
	
	private static final Log log = LogFactory.getLog(FileXsdResolver.class);
	
	protected String targetNamespace;
	protected String schemaLocation;
//	protected FileXsdResolver parent;
	
	private FileXsdResolver(){
	}
	
	public String getId() {
		StringBuffer sb = new StringBuffer("FileXsdResolver");
		if( targetNamespace != null ){
			sb.append(" "+targetNamespace);
		}
		if( schemaLocation != null ){
			sb.append(" "+schemaLocation);
		}
		return sb.toString();
	}
	
	public FileXsdResolver( String schemaLocation ){
		this.schemaLocation = schemaLocation;
	}
	
	private void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
//
//	public String getSchemaLocation() {
//		return schemaLocation;
//	}
	
	
	
	public InputStream getInputStream() {
		InputStream in;
		try {
			in = new FileInputStream( schemaLocation );
		} catch (FileNotFoundException e) {
			throw new GeneratorException( "schema not found: " + schemaLocation );
		}
		return in;
	}

//	public void setParent(XsdResolver parent) {
//		this.parent = (FileXsdResolver) parent;
//	}

	public XsdResolver getIncludeResolver(String schemaLocation) {
		File file = null;
		FileXsdResolver resolver = new FileXsdResolver();
		if( this.schemaLocation != null ){
//			log.info("parent schemaLocation: "+this.schemaLocation);
//			log.info("child schemaLocation: "+schemaLocation);
			File parentDir = new File( this.schemaLocation ).getParentFile();
			file = new File( parentDir, schemaLocation );
//			log.info( "file: "+file.getAbsolutePath());
			
		} else {
			file = new File( schemaLocation);
		}
		resolver.setSchemaLocation( file.getAbsolutePath() );
		return resolver;
	}
	
	
	
	// from XsdSchema
	// http://xmlpull.org/v1/doc/api/org/xmlpull/v1/XmlPullParser.html
	private void readTargetNamespace(){
		
		String targetNamespace = "";
		String errMsg = "unable to get targetNamespace";
		
		try {
			InputStream in = getInputStream();
			
			XmlPullParserFactory f = XmlPullParserFactory.newInstance();
			XmlPullParser parser = f.newPullParser();
			parser.setInput(in, null);

			int event;
			while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
				if (event == XmlPullParser.START_TAG) {
					if (NameUtil.trimPrefix(parser.getName()).equals("schema")) {
						targetNamespace = parser.getAttributeValue(null,"targetNamespace");
						break; // schema tag was the only one we wanted to visit
					}
				}
			}
			
			in.close();
		} catch (XmlPullParserException e) {
			log.fatal(errMsg, e);
			throw new GeneratorException(errMsg, e);
		} catch (IOException e) {
			log.fatal(errMsg, e);
			throw new GeneratorException(errMsg, e);
		}

		log.info("targetNamespace: " + targetNamespace);
		this.targetNamespace = targetNamespace;
	}

	public String getTargetNamespace() {
		if(this.targetNamespace == null){
			readTargetNamespace();
		}
		return this.targetNamespace;
	}


	
}
