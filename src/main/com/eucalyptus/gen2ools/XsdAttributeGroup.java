// $Id: XsdAttributeGroup.java,v 1.4 2005/02/19 20:02:31 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XsdAttributeGroup extends XsdStructure implements PubliclyCloneable {

	private static final Log log = LogFactory.getLog(XsdAttributeGroup.class);

	private String ref; // references another AttributeGroup by name

	private boolean importedRef = false;

	private String name;

	private ArrayList structures = new ArrayList();

	private ArrayList attributes = null;

	public XsdAttributeGroup() {
	}

	/** Creates a copy/clone. */
	public XsdAttributeGroup(XsdAttributeGroup original) {
		super(original); //	inherited fields
		ref = original.ref;
		name = original.name;
		for (int i = 0, n = original.sizeStructures(); i < n; i++) {
			structures.add(original.getAttribute(i).clone());
		}
	}

	// same as clone but returns the class type
	public Object clone() {
		return new XsdAttributeGroup(this);
	}

	public XsdAttributeGroup copy() {
		return new XsdAttributeGroup(this);
	}

	/**
	 * Loops through the structures and gets all the attributes. The attributes
	 * are added as an instance variable and returned.
	 */
	private ArrayList importAttributes() {
		attributes = new ArrayList();

		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			if (structure instanceof XsdAttributeGroup) {
				attributes.addAll(((XsdAttributeGroup) structure)
						.importAttributes());
			} else if (structure instanceof XsdAttribute) {
				attributes.add(structure);
			}
		}
		return attributes;
	}

	public int sizeAttributes() {
		//importRef(); // 2005-05-28, was commented out (see setSchema), added
		// back in
		// 2004-05-29 infinite loop when importing attributes into
		// XsdAttributeGroup, wms demo
		if (attributes == null) {
			importAttributes();
		}
		return attributes.size();
	}

	public XsdAttribute getAttribute(int i) {
		if (attributes == null)
			importAttributes();
		return (XsdAttribute) attributes.get(i);
	}

	private int sizeStructures() {
		if (structures == null) {
			log.error("structures == null");
			return 0;
		}

		return structures.size();
	}

	private XsdStructure getStructure(int i) {
		return (XsdStructure) structures.get(i);
	}

	public String getName() {
		if (name == null && ref != null) {
			importRef();
		}

		return name;
	}

	// similar to XsdElement.importRef()
	private void importRef() {
		XsdType ref = getXsdType(this.ref);
		if (ref != null && !importedRef) {

			XsdAttributeGroup groupRef = getGenerator().getAttributeGroup(ref);
			if (groupRef == null) {
				log.error("unable to import reference: " + this.ref);
			} else {

				attributes = groupRef.importAttributes(); // 2004-04-14
				for (int i = 0, n = sizeAttributes(); i < n; i++) {
					getAttribute(i).setParentComplexType(parentComplexType);
				}
			}

//			name = ref;

		}

	}

	public void setSchema(XsdSchema schema) {
		super.setSchema(schema);

		// 2004-05-28
		// this occurs before all of the global elements are processed,
		// so it you might not be able to import ref's, so do not call
		if (attributes != null) {
			log.error("attributes != null");
		}
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			structure.setSchema(schema);
			structure.setGenerator(getGenerator()); // 2004-07-27
		}
	}

	public void setGenerator(Generator generator) {
		super.setGenerator(generator);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			getStructure(i).setGenerator(generator);
		}
	}

    public String toString(){
        return "XsdAttribute[ name: "+ name +" ]";
    }
}