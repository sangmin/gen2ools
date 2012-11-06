package com.eucalyptus.gen2ools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaSourceFactory;

public class GeneratorConfig {

	private static final Log log = LogFactory.getLog(GeneratorConfig.class);

	private List xsdResolverList = new ArrayList();
	private List complexTypeRemapList = new ArrayList();
	
	/**
	 * Some people and XML schema editors (like XML Spy) append the word "Type"
	 * or some other suffix to the names of their complex types. So if you have
	 * a complex type named "AddressType", the resulting Java source file would
	 * be AddressType.java unless you specify to remove the suffix "Type" here.
	 */
	private String removeFieldTypeSuffix = null; // Type

	private String addFieldTypePrefix = null;

	/** Prefix a field name */
	private String addFieldPrefix = null;

	private String addListSuffix = "";

	/** The destination directory for source files. */
  private String commonOutputDir = "src/gen";
  private String serviceOutputDir = "src/gen";
  private String commonOutputBaseDir = "src/gen";
  private String serviceOutputBaseDir = "src/gen";

  
	/**
	 * If your schema has a namespace, the package will match the namespace. If
	 * your schema doesn't have a namespace, override this property in order to
	 * put the files in the package of your choice.
	 */
	private String targetPackage = "test";

	/**
	 * The bindingfile will be placed in the package. It will also match user
	 * the containing folder name + "jibx.xml" as the binding file name unless
	 * you override the name.
	 */
	private String bindingFile = null; // "test.jibx.xml";

	/**
	 * If a field name ends up being a Java Reserved Keyword or Literal, give it
	 * a prefix.
	 */
	private String reservedWordPrefix = "_";

	private Map xmlNamespaceToJavaPackageMap = new HashMap();

	private Map schemaLocationMap = new HashMap();
	
	private XsdResolver xsdResolver = null; // new FileXsdResolver();

	private JavaSourceFactory javaSourceFactory = new JavaSourceFactory();
	
	public void addXsdResolver(XsdResolver resolver){
		xsdResolverList.add(resolver);
	}
	public int sizeXsdResolverList(){
		return xsdResolverList.size();
	}
	public XsdResolver getXsdResolver(int i){
		return (XsdResolver) xsdResolverList.get(i);
	}

	public String getAddFieldPrefix() {
		return addFieldPrefix;
	}

	public void setAddFieldPrefix(String addFieldPrefix) {
		this.addFieldPrefix = addFieldPrefix;
	}

	public String getAddFieldTypePrefix() {
		return addFieldTypePrefix;
	}

	public void setAddFieldTypePrefix(String addFieldTypePrefix) {
		this.addFieldTypePrefix = addFieldTypePrefix;
	}

	public String getAddListSuffix() {
		return addListSuffix;
	}

	public void setAddListSuffix(String addListSuffix) {
		this.addListSuffix = addListSuffix;
	}

	public String getBindingFile() {
		return bindingFile;
	}

	public void setBindingFile(String bindingFile) {
		this.bindingFile = bindingFile;
	}

  public String getCommonOutputDir() {
    return commonOutputDir;
  }
  public String getServiceOutputDir() {
    return serviceOutputDir;
  }

	public void setOutputDir(String outputDir) {
    File publicDir = new File( outputDir + "/" + Generate.serviceName.toLowerCase( ) + "-common/src/main/java/" );
    File privateDir = new File( outputDir + "/" + Generate.serviceName.toLowerCase( ) + "/src/main/java/" );
    publicDir.mkdirs( );
    privateDir.mkdirs( );
		this.commonOutputDir = publicDir.getAbsolutePath( );
		this.commonOutputBaseDir = publicDir.getParentFile( ).getParentFile( ).getParentFile( ).getAbsolutePath( );
		this.serviceOutputDir = privateDir.getAbsolutePath( );
		this.serviceOutputBaseDir = privateDir.getParentFile( ).getParentFile( ).getParentFile( ).getAbsolutePath( );
	}

	public String getRemoveFieldTypeSuffix() {
		return removeFieldTypeSuffix;
	}

	public void setRemoveFieldTypeSuffix(String removeFieldTypeSuffix) {
		this.removeFieldTypeSuffix = removeFieldTypeSuffix;
	}

	public String getReservedWordPrefix() {
		return reservedWordPrefix;
	}

	public void setReservedWordPrefix(String reservedWordPrefix) {
		this.reservedWordPrefix = reservedWordPrefix;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}
	
	public void addXmlNamespaceToJavaPackage( String nsUri, String javaPackage ){
		xmlNamespaceToJavaPackageMap.put( nsUri, javaPackage );
	}

    public void setXmlNamespaceToJavaPackageMap(Map map) {
        xmlNamespaceToJavaPackageMap = map;
    }

	public Map getXmlNamespaceToJavaPackageMap() {
		return xmlNamespaceToJavaPackageMap;
	}

	public Map getSchemaLocationMap() {
		return schemaLocationMap;
	}
	
	public void addSchemaLocation( String from, String to ){
		schemaLocationMap.put( from, to );
	}
	public XsdResolver getXsdResolver() {
		return xsdResolver;
	}
	public void setXsdResolver(XsdResolver xsdResolver) {
		this.xsdResolver = xsdResolver;
	}

	public JavaSourceFactory getJavaSourceFactory() {
		return javaSourceFactory;
	}
	public void setJavaSourceFactory(JavaSourceFactory javaSourceFactory) {
		this.javaSourceFactory = javaSourceFactory;
	}
	
	public void addComplexTypeRemap(ComplexTypeRemap resolver){
		complexTypeRemapList.add(resolver);
	}
	public int sizeComplexTypeRemapList(){
		return complexTypeRemapList.size();
	}
	public ComplexTypeRemap getComplexTypeRemap(int i){
		return (ComplexTypeRemap) complexTypeRemapList.get(i);
	}
	
	public static class ComplexTypeRemap {
		private XsdType fromType;
		private String toNamespace;
        private String toPrefix;
		private String toName;
		public void setFromType(XsdType fromType) {
			this.fromType = fromType;
		}
		public void setToName(String toName) {
			this.toName = toName;
		}
        public void setToPrefix(String toPrefix) {
            this.toPrefix = toPrefix;
        }
		public XsdType getFromType() {
			return fromType;
		}
		public String getToName() {
			return toName;
		}
		public String getToNamespace() {
			return toNamespace;
		}
		public String getToPrefix() {
			return toPrefix;
		}
		public void setToNamespace(String toNamespace) {
			this.toNamespace = toNamespace;
		}
	}

  /**
   * @return the commonOutputBaseDir
   */
  public String getCommonOutputBaseDir( ) {
    return this.commonOutputBaseDir;
  }
  /**
   * @return the serviceOutputBaseDir
   */
  public String getServiceOutputBaseDir( ) {
    return this.serviceOutputBaseDir;
  }
}