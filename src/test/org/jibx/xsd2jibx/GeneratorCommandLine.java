/*
 * Created on Aug 11, 2004
 */
package org.jibx.xsd2jibx;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneratorCommandLine {
	
	private static final Log log = LogFactory.getLog(GeneratorCommandLine.class);
	
	private GeneratorConfig config = new GeneratorConfig();
	private Generator generator = new Generator();
	
	public GeneratorCommandLine(String[] args) {
		generator.setConfig(config);

		String[] xsdList = args;
		for( int i=0; i < xsdList.length; i++){
			FileXsdResolver resolver = new FileXsdResolver(xsdList[i]);
			config.addXsdResolver( resolver );
		}
	}

	private void importProperties(){
		
		Properties props = new Properties();
		String outputDir = null;
		String targetPackage = null;
		String bindingFile = null;

            // override with any system properties.
            props.putAll(System.getProperties());
            
            
            // TODO this should go through GeneratorConfig
            NameUtil.removeFieldTypeSuffix = props
                    .getProperty("removeFieldTypeSuffix");
            NameUtil.addFieldTypePrefix = props
                    .getProperty("addFieldTypePrefix");
            NameUtil.addFieldPrefix = props.getProperty("addFieldPrefix");
            NameUtil.addListSuffix = props.getProperty("addListSuffix");
            
            outputDir = props.getProperty("outputDir");
            targetPackage = props.getProperty("targetPackage");
            bindingFile = props.getProperty("bindingFile");
            
            NameUtil.reservedWordPrefix = props
                    .getProperty("reservedWordPrefix");

//            if (props.containsKey("namespaceToPackage")) {
//                try {
//                    String propsFile = props.getProperty("namespaceToPackage");
//                    FileInputStream fis = new FileInputStream(propsFile);
//                    NameUtil.namespaceToPackage.load(fis);
//                } catch (FileNotFoundException e) {
//                    log.info("namespaceToPackage not found", e);
//                } catch (IOException e) {
//                    log.error("error loading namespaceToPackage", e);
//                }
//                log.info("namespaceToPackage:");
//                LogUtil.print(NameUtil.namespaceToPackage, log);
//            }

//            if (props.containsKey("schemaLocationReplacement")) {
//                try {
//                    XsdInclude.schemaLocationReplacement
//                            .load(new FileInputStream(props
//                                    .getProperty("schemaLocationReplacement")));
//                } catch (FileNotFoundException e) {
//                    log.info("schemaLocationReplacements not found", e);
//                } catch (IOException e) {
//                    log.error("error loading schemaLocationReplacement", e);
//                }
//                log.info("schemaLocationReplacement:");
//                LogUtil.print(XsdInclude.schemaLocationReplacement, log);
//            }

	}
	
	public static void main(String[] args) {
		
		GeneratorCommandLine gcl = new GeneratorCommandLine(args);
		gcl.importProperties();
		gcl.execute();

	}

	private void execute() {
		generator.execute();
	}
}
