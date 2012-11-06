/*
 * Created on Jul 26, 2004
 */
package org.jibx.xsd2jibx;

import java.io.FileInputStream;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SchemaTransformTest {
	
	private static final Log log = LogFactory.getLog(SchemaTransformTest.class);
	
	public static void main(String[] args) throws Exception {
		
//        Resource r = new ClassPathResource("spring-beans.xml", GeneratorConfig.class);
//        ListableBeanFactory bf = new XmlBeanFactory(r);
//        Generator generator = (Generator) bf.getBean("generator");
        
        Generator generator = new Generator();
		
		String file = "customer.xsd";
        StreamSource xmlSS = new StreamSource(new FileInputStream(file));
        byte[] xsdBA = XsdSchema.transformXml("xsd-remove-namespace.xsl", xmlSS);
		
        String xsd = new String(xsdBA);
        log.info( "after remove-namespace.xsl, " + file + ":\n" + xsd );
	}
}
