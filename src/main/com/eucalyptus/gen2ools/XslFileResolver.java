// $Id: XslFileResolver.java,v 1.2 2004/08/30 08:09:46 ctaggart Exp $

package com.eucalyptus.gen2ools;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XslFileResolver implements URIResolver {

	private static final Log log = LogFactory.getLog(XslFileResolver.class);

	// The relative or absolute directory where the included or imported xsl
	// files reside.
	private String dir = null;

	public XslFileResolver() {
	}

	public XslFileResolver(String dir) {
		this.dir = dir;
	}

	public Source resolve(String href, String base) {
		log.info("resolve href: " + href + ", base: " + base);

		// resolve the included xsl stylesheet, e.g. copy.xml
		// from the relative classpath
		Source source = new StreamSource(XslFileResolver.class.getResourceAsStream(href));

		return source;
	}
}