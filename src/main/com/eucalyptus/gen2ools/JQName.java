package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** wraps JavaQName */
public class JQName {

	private static final Log log = LogFactory.getLog(JQName.class);

	static final JQName STRING = new JQName(JavaQNameImpl
			.getInstance(String.class));

	static final JQName OBJECT = new JQName(JavaQNameImpl
			.getInstance(Object.class));

    static final JQName DATE =
        new JQName(JavaQNameImpl.getInstance(java.sql.Date.class));

    static final JQName TIME =
        new JQName(JavaQNameImpl.getInstance(java.sql.Time.class));

	static final JQName DATETIME =
        new JQName(JavaQNameImpl.getInstance(java.util.Date.class));

	static final JQName ARRAYLIST = new JQName(JavaQNameImpl
			.getInstance(ArrayList.class));

	static final JQName BYTE = new JQName(JavaQNameImpl.BYTE);

	static final JQName CHAR = new JQName(JavaQNameImpl.CHAR);

	static final JQName DOUBLE = new JQName(JavaQNameImpl.DOUBLE);

	static final JQName FLOAT = new JQName(JavaQNameImpl.FLOAT);

	static final JQName INT = new JQName(JavaQNameImpl.INT);

	static final JQName BOOLEAN = new JQName(JavaQNameImpl.BOOLEAN);

	static final JQName SHORT = new JQName(JavaQNameImpl.SHORT);

	static final JQName LONG = new JQName(JavaQNameImpl.LONG);

	static final JQName VOID = new JQName(JavaQNameImpl.VOID);
    
    static final JQName INTEGER =
        new JQName(JavaQNameImpl.getInstance(java.math.BigInteger.class));
    
    static final JQName DECIMAL =
        new JQName(JavaQNameImpl.getInstance(java.math.BigDecimal.class));
	
	static final JQName BYTE_ARRAY = new JQName(JavaQNameImpl.getArray(JavaQNameImpl.BYTE));

	private JavaQName qname = null;

	JQName(JavaQName qname) {
		this.qname = qname;
	}

	JavaQName getJavaQName() {
		return qname;
	}

	public String getClassName() {
		return qname.getClassName();
	}

	public String toString() {
		return qname.toString();
	}

}