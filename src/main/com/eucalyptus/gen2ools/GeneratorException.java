/*
 * Created on Aug 30, 2004
 */
package com.eucalyptus.gen2ools;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author ctaggart
 */
public class GeneratorException extends RuntimeException {
    
    /** Exception that caused this exception. */
    private Throwable m_rootCause;
    
    /**
     * Constructor from message.
     * 
     * @param msg message describing the exception condition
     */
     
    public GeneratorException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor from message and wrapped exception.
     * 
     * @param msg message describing the exception condition
     * @param root exception which caused this exception
     */
    
    public GeneratorException(String msg, Throwable root) {
        super(msg);
        m_rootCause = root;
    }
    
    /**
     * Get root cause exception.
     *
     * @return exception that caused this exception
     */
    
    public Throwable getRootCause() {
        return m_rootCause;
    }
    
    /**
     * Build string representation. If there's no wrapped exception this
     * just returns the usual text, otherwise it appends the wrapped
     * exception information to the text generated from this one.
     *
     * @return string representation
     */
    
    public String toString() {
        if (m_rootCause == null) {
            return super.toString();
        } else {
            return super.toString() + "\nRoot cause: " + 
                m_rootCause.toString();
        }
    }

    /**
     * Print stack trace to standard error. This is an override of the base
     * class method to implement exception chaining.
     */
    
    public void printStackTrace() {
        super.printStackTrace();
        if (m_rootCause != null) {
            System.err.print("Cause: ");
            m_rootCause.printStackTrace();
        }
    }

    /**
     * Print stack trace to stream. This is an override of the base class method
     * to implement exception chaining.
     * 
     * @param s stream for printing stack trace
     */
    
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.print("Caused by: ");
            m_rootCause.printStackTrace(s);
        }
    }

    /**
     * Print stack trace to writer. This is an override of the base class method
     * to implement exception chaining.
     * 
     * @param s writer for printing stack trace
     */
    
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        if (m_rootCause != null) {
            s.print("Caused by: ");
            m_rootCause.printStackTrace(s);
        }
    }
}
