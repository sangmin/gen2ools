// $Id: LogUtil.java,v 1.1 2004/08/03 07:37:11 ctaggart Exp $

package com.eucalyptus.gen2ools;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class LogUtil {

	private static final Log log = LogFactory.getLog(LogUtil.class);

	private LogUtil() {
	}

	/** Logs the values of a HashMap's keys at debug level. */
	static void printKeys(Map map) {
		printKeys(map, log);
	}

	static void printKeys(Map map, Log log) {
		Set keySet = map.keySet();
		Iterator keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			Object key = keyIt.next();
			log.info("  " + key);
		}
	}

	static void print(Map map) {
		print(map, log);
	}

	static void print(Map map, Log logger) {
		Set keySet = map.keySet();
		Iterator keyIt = keySet.iterator();
		while (keyIt.hasNext()) {
			Object key = keyIt.next();
			Object value = map.get(key);
			log.info("  " + key);
			log.info("    " + value);
		}
	}

}