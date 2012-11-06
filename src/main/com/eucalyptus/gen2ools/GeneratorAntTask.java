/*
 * Created on Jul 27, 2004
 */
package com.eucalyptus.gen2ools;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class GeneratorAntTask extends Task {

	private FileSet xsdfs;
	private String outputDir;
	private String targetPackage;
	
	private List namespaceToPackageList = new ArrayList();
	
	private List schemaLocationList = new ArrayList();
	
	public void execute() throws BuildException {

		GeneratorConfig config = new GeneratorConfig();
		
		String[] xsdList = getXsdList();
		for( int i=0; i < xsdList.length; i++){
			FileXsdResolver resolver = new FileXsdResolver(xsdList[i]);
			config.addXsdResolver( resolver );
		}
		
		if( this.outputDir != null ){
			config.setOutputDir(this.outputDir);
		}
		if( this.targetPackage != null ){
			config.setTargetPackage(this.targetPackage);
		}
		if( namespaceToPackageList.size() > 0 ){
			for(int i=0, n=namespaceToPackageList.size();i<n;i++){
				XmlNamespaceToJavaPackage nsToPackage = (XmlNamespaceToJavaPackage) namespaceToPackageList.get(i);
				config.addXmlNamespaceToJavaPackage(nsToPackage.getXmlNamespace(), nsToPackage.getJavaPackage());
			}
		}
		if( schemaLocationList.size() > 0 ){
			for(int i=0, n=schemaLocationList.size();i<n;i++){
				SchemaLocation location = (SchemaLocation) schemaLocationList.get(i);
				config.addSchemaLocation(location.getFrom(), location.getTo());
			}
		}

		Generator generator = new Generator();
		generator.setConfig(config);
		generator.execute();
		generator.writeJavaSources("");
		generator.marshalJibxBindingDefinition();

	}

	public void addXsdFileSet(FileSet xsdfs) {
		this.xsdfs = xsdfs;
	}

	private String[] getXsdList() {
		DirectoryScanner ds = xsdfs.getDirectoryScanner(getProject());
		String[] matchedFiles = ds.getIncludedFiles();
//		return matchedFiles;
        String[] list = new String[matchedFiles.length];
        for(int i = 0; i < matchedFiles.length; i++){
        	list[i] = ds.getBasedir() + System.getProperty("file.separator") + matchedFiles[i];
        }
        return list;
		
	}
	
	public void setOutputDir( String outputDir ){
		this.outputDir = outputDir;
	}
	
	public void setTargetPackage( String targetPackage ){
		this.targetPackage = targetPackage;
	}
	
	public void addXmlNamespaceToJavaPackage( XmlNamespaceToJavaPackage namespaceToPackage ){
		namespaceToPackageList.add(namespaceToPackage);
	}
	
	public void addSchemaLocation( SchemaLocation schemaLocation ){
		schemaLocationList.add(schemaLocation);
	}
	
	public static class XmlNamespaceToJavaPackage{
		
		private String xmlNamespace = null;
		private String javaPackage = null;
		
		public XmlNamespaceToJavaPackage(){
		}
		
		public String getJavaPackage() {
			return javaPackage;
		}
		public void setJavaPackage(String javaPackage) {
			this.javaPackage = javaPackage;
		}
		public String getXmlNamespace() {
			return xmlNamespace;
		}
		public void setXmlNamespace(String xmlNamespace) {
			this.xmlNamespace = xmlNamespace;
		}
	}
	
	public static class SchemaLocation{
		private String from = null;
		private String to = null;
		
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
	}
	
}