package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdSequence extends XsdOrder {

	public XsdSequence() {
		super();
	}

	public XsdSequence(XsdSequence original) {
		super(original);
	}

	private static final Log log = LogFactory.getLog(XsdSequence.class);

	public ArrayList getElements() {

		ArrayList elements = new ArrayList();

		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			if (structure instanceof XsdGroup) {
				elements.addAll(((XsdGroup) structure).getElements());
			} else if (structure instanceof XsdElement) {
				elements.add(structure);
			}
		}
		return elements;
	}

}