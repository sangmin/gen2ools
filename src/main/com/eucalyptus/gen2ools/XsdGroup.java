// $Id: XsdGroup.java,v 1.2 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdGroup extends XsdStructure {

	private static final Log log = LogFactory.getLog(XsdGroup.class);

	private String ref;

	private ArrayList structures = new ArrayList();

	private ArrayList elements = null;

	public XsdGroup() {
	}

	public int sizeStructures() {
		if (structures == null) {
			log.error("structures == null");
			return 0;
		}
		return structures.size();
	}

	public XsdStructure getStructure(int i) {
		return (XsdStructure) structures.get(i);
	}

	public ArrayList getElements() {
		if (elements != null)
			return elements; // lazy loaded

		elements = new ArrayList();

		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			if (structure instanceof XsdGroup) {
				elements.addAll(((XsdGroup) structure).getElements());
			} else if (structure instanceof XsdChoice) {
				//TODO support an XsdChoice within an XsdGroup
				log
						.error("XsdChoice not supported within an XsdGroup currently");
			} else if (structure instanceof XsdSequence) {
				XsdSequence sequence = (XsdSequence) structure;
				elements.addAll(sequence.getElements());
			}
		}

		return elements;
	}

    public String toString(){
        return "XsdGroup[ name: "+ getName() +" ]";
    }
}