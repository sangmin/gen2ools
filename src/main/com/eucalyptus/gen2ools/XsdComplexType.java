// $Id: XsdComplexType.java,v 1.13 2005/08/19 19:16:42 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;

public class XsdComplexType extends XsdStructure {

	private static final Log log = LogFactory.getLog(XsdComplexType.class);

	private JSource source;
	private JibxMapping mapping;
    private boolean isReferenced;
	//  children complexTypes, anonymous elements added during processing
	private ArrayList children = new ArrayList();
	private int choiceNum = 0; // helps name a choice field if there are
							   // multiple choice fields
	private boolean importedBase = false;
	private ArrayList structures = new ArrayList();
	private boolean prefixWithParentName = false;

	// A base for extentions or restrictions.
	// Both complexContent and simpleContent are expected to have one. (should
	// verify)
	private String simpleContentBase;

	private String complexContentBase;

	/** Creates a copy/clone. */
	public XsdComplexType(XsdComplexType original) {
		super(original); //	inherited fields
		choiceNum = original.choiceNum;
		name = original.name;
		simpleContentBase = original.simpleContentBase;
		complexContentBase = original.complexContentBase;
        isReferenced = original.isReferenced;
		importedBase = original.importedBase;
		//parentName = original.parentName;

		// elements, choices, attributes, attributeGroups
	}

	public XsdComplexType() {
	}

	/**
	 * Indicates that getFullName() will be prefixed with the parent complex
	 * type name.
	 */
	public void setPrefixWithParentName(boolean prefix) {
		prefixWithParentName = prefix;
	}
    
    /**
     * Indicates that type is used externally and cannot be treated as list
     * wrapper.
     */
    public void setIsReferenced() {
        isReferenced = true;
    }

	/** includes parent complexType if present separated by a dash */
	public String getFullName() {
		String name = getName();
		if (prefixWithParentName) {
			String parentName = NameUtil.trimSuffix(parentComplexType
					.getFullName());
			name = parentName + "-" + name;
		}

		return name;
	}

	public XsdType getXsdType(){
		XsdType xsdType = createXsdType();
		xsdType.setType( getFullName() );
		return xsdType;
	}

	public JQName getQName() {

		String targetPackage = getGenerator().getConfig().getTargetPackage();

		JibxNamespace targetNamespace = getTargetNamespace();
		if (targetNamespace == null) {
			//log.error( "targetNamespace == null" );
		} else {
			targetPackage = getGenerator().getJavaPackageFromXmlNamespaceUri(targetNamespace.getUri());
		}

		String className = NameUtil.toUpperCamelCase(NameUtil
				.trimSuffix(getFullName()));
		if (NameUtil.addFieldTypePrefix != null) {
			className = NameUtil.addFieldTypePrefix + className;
		}
		if( !className.endsWith( "Type" ) && WsdlDocument.document.getMessageType( getFullName( ) ) != null  ) {
		  className = className + "Type";
		} else {
		  log.info( "Failed to find message type: " + getFullName( ) );
		}
		return new JQName(JavaQNameImpl.getInstance(targetPackage, className));
	}

	// same as clone but returns the class type
	public Object clone() {
		return new XsdComplexType(this);
	}

	public XsdComplexType copy() {
		return new XsdComplexType(this);
	}

	public int getNextChoiceNum() {
		return ++choiceNum;
	}

	private int sizeStructures() {
		if (structures == null) {
			log.error("structures == null");
			return 0;
		}

		return structures.size();
	}

	private XsdStructure getStructure(int i) {
		XsdStructure s = (XsdStructure) structures.get(i);
		s.setParentComplexType(this);
		return s;
	}

	public boolean hasComplexContentBase() {
		return complexContentBase != null;
	}

	public XsdType getComplexContentBase() {
		log.info("getComplexContentBase: "+complexContentBase );
		return getXsdType(complexContentBase);	    
	}

	public boolean hasSimpleContentBase() {
		return simpleContentBase != null;
	}

	public XsdType getSimpleContentBase() {
		return getXsdType(simpleContentBase);
	}

	public JibxMapping getMapping() {
		return mapping;
	}

	public void setMapping(JibxMapping mapping) {
		this.mapping = mapping;
	}

	public void addChild(XsdComplexType child) {
		children.add(child);
	}

	public int sizeChildren() {
		return children.size();
	}

	public XsdComplexType getChild(int i) {
		return (XsdComplexType) children.get(i);
	}

	public String toString() {
		return "complexType [ fullName: " + getFullName() + ", qName: "
				+ getQName() + " ]";
	}

	public void setSchema(XsdSchema schema) {
		super.setSchema(schema);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			structure.setSchema(schema);
		}
	}

	public void setGenerator(Generator generator) {
		super.setGenerator(generator);
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			getStructure(i).setGenerator(generator);
		}
	}

	public int sizeChoices() {
		int size = 0;
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			if (getStructure(i) instanceof XsdChoice)
				size++;
		}
		return size;
	}

	public JSource getSource() {
		return source;
	}

	public void setSource(JSource source) {
		this.source = source;
	}

	/** Creates a Java source file and a JibxMapping. */
	public void process() {
		
		log.info("process complexType: " + getXsdType());
//		if( getXsdType().getType().equals("")){
//			log.info("found complex type");
//		}
		
		if( isListWrapper()){
            log.info("isListWrapper == true, do not create a mapping or a class file");
            processStructure(getWrappedElement());
            return;
		}
		
		// TODO use XsdType instead
		JQName qname = getQName();
		log.info("  qname: " + qname.toString());
		if (wasProcessed()) {
			log.info("  already created: " + qname.toString());
			return;
		}
		getGenerator().addCreatedSource(getQName().toString());

		JSource source = getGenerator().newJSource(qname);
		setSource(source);

		mapping = new JibxMapping();
		mapping.setXsdType( getXsdType() );
		mapping.setName(name);
		getGenerator().addMapping(mapping);

		checkComplexContentBase();

		checkSimpleContentBase();

		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			processStructure(structure);
		}

		source.createConstructors();

	}

	// see if the source files have already been created
	private boolean wasProcessed() {
		JQName qname = getQName();
		return getGenerator().containsCreatedSources(qname.toString());
	}

	// gets the the
	private JQName getSimpleContentBaseSchemaType(XsdComplexType ct) {
		JQName qname = null;
		XsdType base = ct.getSimpleContentBase();
		if (ct.hasSimpleContentBase()) {
			Generator generator = getGenerator();
			if (generator.isSchemaType(base)) {
				qname = generator.getQNameSchemaType(base);
			} else if (generator.isSimpleType(base)) {
				qname = generator.getQNameSimpleType(base);
			} else if (generator.isComplexType(base)) {
				//log.error("simpleContentBase recursion not supported yet: " +
				// base);
				qname = getSimpleContentBaseSchemaType(generator
						.getComplexType(base)); // recursive
			}
		} else {
			log.error("simpleContentBase not found in baseCT: " + ct);
		}
		return qname;
	}

	private void checkSimpleContentBase() {
		// 2004-04-16, modified 2004-05-25 & 2004-05-31
		if (hasSimpleContentBase()) {
			//String base = "string";
			XsdType base = getSimpleContentBase();

			// import the attributes if it is a complexType
			if (getGenerator().isComplexType(base)) {
				log.info("simpleContentBase is a complexType, base: " + base);
				XsdComplexType baseCT = getGenerator().getComplexType(base);

				// should just be attributes
				ArrayList attributes = baseCT.getAttributes();
				structures.addAll(attributes);
				//                for (int i = 0, n = attributes.size(); i < n; i++) {
				//                    XsdAttribute.
				//                }
				//importBase(baseCT); // marks attributes as imported, no good

			}

			JQName baseQName = getSimpleContentBaseSchemaType(this);

			if (baseQName == null) {
				log.error("simpleContentBase baseQName == null, base: " + base);
			} else if (baseQName.toString().equals("java.lang.String")
					|| baseQName.toString().equals("java.util.Date")
					|| baseQName.toString().equals("int")
					|| baseQName.toString().equals("byte[]")
					|| baseQName.toString().equals("boolean")) {
				source.newBeanProperty(baseQName, "base", true);
				JibxValue text = new JibxValue();
				text.setStyle("text");
				text.setField("base");
				text.setUsage("required");
				mapping.addValue(text);
			} else {
				log.error("simpleContent base not supported: "
						+ getSimpleContentBase() + ", QName: " + baseQName);
			}

		}
	}

	private void checkComplexContentBase() {
		if (hasComplexContentBase()) {
			XsdType base = getComplexContentBase();

			if (getGenerator().isComplexType(base)) {
				XsdComplexType baseCT = getGenerator().getComplexType(base);
				log.info("  imports " + baseCT.getName());
				importBase(baseCT);

				source.addExtends(baseCT.getQName());
        mapping.setExtends(baseCT.getQName().toString());
			} else {
				log.error("    base class NOT found, base: " + base);
			}
		} else if( WsdlDocument.document.containsMessage( this.getName( ) ) ) {
		  log.info( "PARENT: " + this.getName( ) );
		  source.addExtends( Generate.baseType );
      mapping.setExtends( Generate.baseType.toString());
		} else {
      log.info( "PARENT: " + this.getName( ) );
      source.addExtends( new JQName( JavaQNameImpl.getInstance( "edu.ucsb.eucalyptus.msgs", "EucalyptusData" ) ) );
		}
	}

	private void processStructure(XsdStructure structure) {

		if (structure instanceof XsdElement) {
			XsdElement e = (XsdElement) structure;

			// -------------------------------------------------------------------------------
			// anonymous/inline complexType
			// -------------------------------------------------------------------------------
			// element isn't imported from parent class
			// if the element isn't from the parent class which it extends, then
			// add it to the source file
			if (!e.isImportedFromBase()) {
				if (e.isComplexType()) {
					addAnonymousComplexType(e);
				}
				
//				 TODO handle source code generation for list wrappers
				if( getGenerator().isComplexType(e.getType()) && getGenerator().getComplexType(e.getType()).isListWrapper()){
//					log.info( "element is a complexType: " + e );
					log.info("element is a list wrapper: " + e);
//					XsdElement wrappedElement = getGenerator().getComplexType(e.getType()).getWrappedElement();
//					source.addListWrapper(e, wrappedElement);
					
				} else {
					source.addElement(e);
				}
				
			}
			
			processElement(e); // add to JibxMapping
			
		} else if (structure instanceof XsdAttribute) {
			XsdAttribute attr = (XsdAttribute) structure;

			// TODO prohibited attributes shouldn't even show up here
			if (attr.isProhibited()) {
				log.error("prohibited attribute: " + attr.getName());
			} else {
				if (!attr.isImportedFromBase()) {
					source.newBeanProperty(attr);
				}
				processAttribute(attr);
			}
		} else if (structure instanceof XsdAttributeGroup) {
			// 2004-04-14
			XsdAttributeGroup group = (XsdAttributeGroup) structure;
			for (int i = 0, n = group.sizeAttributes(); i < n; i++) {
				XsdAttribute attr = group.getAttribute(i);
				// same as above for XsdAttribute
				if (attr.isProhibited()) {
					log.error("prohibited attribute: " + attr.getName());
				} else {
					if (!attr.isImportedFromBase()) {
						source.newBeanProperty(attr);
					}
					processAttribute(attr);
				}
			}

		} else if (structure instanceof XsdChoice) {
			XsdChoice choice = (XsdChoice) structure;
			processChoice(choice);
		} else if (structure instanceof XsdSequence) {
			XsdSequence sequence = (XsdSequence) structure;
			for (int i = 0, n = sequence.sizeStructures(); i < n; i++) {
				processStructure(sequence.getStructure(i)); // recursive
			}
		}
	}

	private void processChoice(XsdChoice choice) {
		XsdComplexType ct = choice.getParentComplexType();
		JSource source = ct.getSource();

		for (int i = 0, n = choice.sizeStructures(); i < n; i++) {
			XsdStructure structure = choice.getStructure(i);
			if (structure instanceof XsdElement) {
				XsdElement element = (XsdElement) structure;
				if (element.isComplexType()) {
					addAnonymousComplexType(element);
				}
			}

		}

		// reuse the addElementToSource logic... for a choice
		if (ct.sizeChoices() > 1) {
			choice.setName("choice" + ct.getNextChoiceNum());
		}

		if (choice.isCollection()) {

			if (!choice.isImportedFromBase()) {

				XsdElement choiceElement = new XsdElement();
				choiceElement.setName(choice.getName());
				choiceElement.setType(XsdType.object);
				choiceElement.setMinOccurs(choice.getMinOccurs());
				choiceElement.setMaxOccurs(choice.getMaxOccurs());				
				choiceElement.setGenerator( getGenerator() ); // Bug, 2004-08-14
				choiceElement.setSchema( getSchema() ); // Bug, 2004-08-30
                choiceElement.setParentComplexType(this);
				source.addElement(choiceElement);
			}
            processChoiceAsCollection(choice);
		} else {
			// handle choices that are not collections
			// 2004-02-19, for now loop through elements in choice and add as
			// optional structures
			for (int i = 0, n = choice.sizeStructures(); i < n; i++) {
				XsdStructure structure = choice.getStructure(i);
				if (structure instanceof XsdElement) {
					XsdElement element = (XsdElement) structure;
					// make sure it is optional
					element.setMinOccurs("0");
					element.setMaxOccurs("1");
					source.addElement(element);
					processElement(element);
				}

			}
		}
	}

	// TODO Some choices may be mapped as values.
	private void processChoiceAsCollection(XsdChoice choice) {

		JibxCollection collection = new JibxCollection();
		collection.setField(choice.getFieldName());
		collection.setOrdered(false);

		for (int i = 0, n = choice.sizeStructures(); i < n; i++) {
			XsdStructure structure = choice.getStructure(i);
			if (structure instanceof XsdElement) {
				XsdElement element = (XsdElement) structure;

				// 2004-05-27
				//values (primatives) not supported within collections (ArrayList)
				if (getGenerator().isSchemaType(element.getType()) || getGenerator().isSimpleType(element.getType())) {
					
					// a string is not a primative
					if(element.getType().equals(XsdType.string)){
				        JibxValue value = createJibxValue(element);
				        value.setUsage( false );
                        value.setType(element.getFieldQName().getJavaQName().toString());
				        collection.addChoice( value );
					} else {
						String errMsg = "Unable to create a <value> as part of a <collection> (no primatives in an ArrayList)"
							+ "\n  " + element + "\n  " + choice;
						log.error(errMsg);
						throw new GeneratorException(errMsg);
						// you can't load primatives into an ArrayList
						// autoboxing may come in handy for JiBX
					}

				} else {
					JibxStructure s = createJibxStructure(element);
					s.setUsage(false); // optional
					collection.addChoice(s);
				}

			}

		}

		choice.getParentComplexType().getMapping().addCollection(collection);
	}

	/**
	 * Create a mapping in the JiBX binding file for an XsdAttribute. returns he
	 * number of required fields added to the constructorRequired.
	 *  
	 */
	private void processAttribute(XsdAttribute attr) {

		JibxMapping mapping = attr.getParentComplexType().getMapping();

		JibxValue value = new JibxValue();
		value.setNamespace(attr.getTargetNamespace());
		value.setStyle("attribute");
		value.setUsage(attr.isRequired());

		//    if ( attr.isRequired( ) ) {
		//      constructorRequired.addField( attr );
		//    }

		//newBeanProperty( sourceFile, attr );
		value.setName(attr.getName());
		value.setField(attr.getFieldName());
		checkIdIdref(attr, value);
		mapping.addValue(value);

		//return requiredSize;
	}

	/**
	 * Adds an XsdElement to a JibxMapping.
	 */
	private void processElement(XsdElement element) {
		
		log.info("processElement: "+element);
		
		JibxMapping mapping = element.getParentComplexType().getMapping();

//		if (element.getName().equals("MgmtData")) {
//			log.info("MgmtData element");
//		}

		if (element.isCollection()) {
            if (!isListWrapper()) {
                JibxCollection collection = new JibxCollection();
                collection.setField(element.getFieldList());

                if (getGenerator().isSchemaType(element.getType()) || getGenerator().isSimpleType(element.getType())) {
                    JibxValue value = createJibxValue(element);
                    value.setType(element.getFieldQName().getJavaQName().toString());
                    collection.addChoice(value);
                } else {
                    JibxStructure structure = createJibxStructure(element);
                    collection.addChoice(structure);
                }

                collection.setUsage(element.isRequired());

                mapping.addCollection(collection);
                
            }
		} else {
			// it is not a collection

			if (getGenerator().isSchemaType(element.getType())
					|| getGenerator().isSimpleType(element.getType())) {
				JibxValue value = new JibxValue();
				value.setStyle("element");
				value.setUsage(element.isRequired());
				value.setName(element.getName());
				value.setField(element.getFieldName());
				value.setNamespace(element.getTargetNamespace());
				checkIdIdref(element, value);
				mapping.addValue(value);

			} else {
				
				XsdComplexType ct = getGenerator().getComplexType(element.getType());
				if( ct.isListWrapper() ){
					
					XsdElement wrapped = ct.getWrappedElement();
//				    <structure name="Cats">
//				      <collection field="cats">
//				        <structure map-as="org.petstore.Cats"></structure>
//				      </collection>
//				    </structure>
					
					JibxStructure s = new JibxStructure();
					s.setName(element.getName());
                    s.setUsage(element.isRequired());
					
					JibxCollection collection = new JibxCollection();
					collection.setField(wrapped.getFieldList());
					
					// TODO same as a few lines above, refractor
					if (getGenerator().isSchemaType(wrapped.getType()) || getGenerator().isSimpleType(wrapped.getType())) {
						JibxValue value = createJibxValue(element);
                        value.setType(wrapped.getFieldQName().getJavaQName().toString());
						collection.addChoice(value);
					} else {
						JibxStructure structure = new JibxStructure();
						structure.setName(wrapped.getName());
						structure.setType(wrapped.getType());
						collection.addChoice(structure);
					}
					s.addChoice(collection);
					
					mapping.addStructure(s);
					
					
				} else {
					//log.info( " element is a structure" );
					JibxStructure structure = new JibxStructure();

					structure.setName(element.getName());
					structure.setType(element.getType());

					structure.setField(element.getFieldName());
					structure.setUsage(element.isRequired());
					mapping.addStructure(structure);
				}

			}
		}
	}

	private JibxStructure createJibxStructure(XsdElement element) {
		JibxStructure structure = new JibxStructure();
		structure.setName(element.getName());
		structure.setType(element.getType());
		return structure;
	}

	private JibxValue createJibxValue(XsdElement element) {
		JibxValue value = new JibxValue();
		value.setStyle(JibxValue.STYLE_ELEMENT);
		value.setName(element.getName());
		value.setNamespace(element.getTargetNamespace());
		return value;
	}

	/**
	 * Checks to see if the attribute or element is of xmlType ID or IDREF. Adds
	 * the necessary value for the mapping/binding generation.
	 */
	private void checkIdIdref(XsdFeature simple, JibxValue value) {
		if (simple.getType() == null) {
			log.info("checkIdIdref() simple.getType() == null, probably attribute with no type set");
			return;
		}

//		String xmlType = NameUtil.trimPrefix(simple.getType());
		String xmlType = simple.getType().getType();
		if (xmlType.equals("ID")) {
			log.info("ID: " + simple);

			if (value.getUsage().equals("optional")) {
				log.warn("can not set ident=\"def\" because it is optional \n  "
								+ simple);
				// I'm not sure if that is intended JiBX behavior.
			} else {
				value.setIdent("def"); // def or id?
			}

		} else if (xmlType.equals("IDREF")) {
			log.info("IDREF:  " + simple);
			//if( simple.hasIdref() ){

			//} else {
			log.warn("IDREF set as Object, must in .java file, field: \n  "
					+ simple);
			//}

			value.setIdent("ref");
		}
	}

	public void addAnonymousComplexType(XsdElement element) {

		XsdComplexType complexType = element.getComplexType();
		XsdComplexType parent = element.getParentComplexType();
		complexType.setSchema(parent.getSchema());
		complexType.setGenerator(getGenerator()); // 2004-07-27

		// prefix element type with complexType parent name
		//complexType.setParentName();
		complexType.setParentComplexType(this);
		complexType.setPrefixWithParentName(true);

		complexType.setName(element.getName());
		
		XsdType xsdType = complexType.getXsdType();
		if(xsdType==null){
			throw new NullPointerException("xsdType being set for element: "+element.getName());
		}
		element.setType(xsdType);
		parent.addChild(complexType); // 2004-02-09 Add as child complexType

		// TODO Everything above is same in ComplexType importBase...
		log.info("adding anonymous complexType: "
				+ complexType.getXsdType());
		getGenerator().addComplexType(complexType);
        
        // pass on current source for use by wrappers
		complexType.source = source;
        complexType.mapping = mapping;
		complexType.process(); // recursive call
	}
	
	//  should probably create a copy (clone), use constructor, instead of
	// modifying original
	// mainly for importing complex content base
	public void importBase(XsdComplexType base) {
		log.info("" + getFullName() + " importing " + base.getFullName());
		if (importedBase) {
			log.info("already imported base for complexType: " + name
					+ ", base" + base.getName());
			return;
		}
        
        // It looks like this code was originally intended to duplicate the
        //  structure of a complexType base definition in a derived definition.
        //  This is no longer necessary, since the base complexType is invoked
        //  directly using a <structure map-as="base-class"/>. Most of the code
        //  has now been commented out because of this, since otherwise it led
        //  to duplicate attribute definitions in the binding.

//		Set attributeNames = new HashSet();

		// set the namespace to be same as the parent...
		setSchema(base.getSchema());

		//Set prohibited = getProhibitedAttributeNames( );
/*		ArrayList list = new ArrayList();

		for (int i = 0, n = base.sizeStructures(); i < n; i++) {
			XsdStructure structure = base.getStructure(i);

			//log.info( ""+name+" import structure" );
			if (structure instanceof XsdElement) {
				XsdElement element = new XsdElement((XsdElement) structure); // clone
				element.setImportedFromBase(true);

				// anonymous complexType
				if (element.isComplexType()) {
					// VERY similar to Generator.addAnonymousComplexType !!!
					XsdComplexType complexType = element.getComplexType();
					complexType.setSchema(getSchema());
					complexType.setGenerator(getGenerator()); // 2004-07-27

					// prefix element type with complexType parent name
					//complexType.setParentName(getFullName());
					complexType.setParentComplexType(this);
					complexType.setPrefixWithParentName(true);

					complexType.setName(element.getName());
					XsdType type = complexType.getXsdType();
					log.info(" anonymous type: " + type);
					element.setType(type);

					getGenerator().addComplexType(complexType);

					complexType.process(); // recursive call
				}

				list.add(element);

				log.info("  imported element, name: " + element.getName()
						+ ", type: " + element.getType());

			} else if (structure instanceof XsdAttribute) {
				XsdAttribute attribute = new XsdAttribute(
						(XsdAttribute) structure); // clone
				attributeNames.add(attribute.getName());
				//if ( prohibited.contains( attribute.getName( ) ) ) {
				//  log.info( " not importing prohibited attribute: " +
				// attribute.getName( ) );
				//} else {
				attribute.setImportedFromBase(true);
				list.add(attribute);
				log.info("  imported attribute: " + attribute.getName());
				//}
			} else if (structure instanceof XsdChoice) {
				XsdChoice choice = new XsdChoice((XsdChoice) structure); // clone
				choice.setImportedFromBase(true);
				list.add(choice);
				log.info("  imported choice");
			}
		}

		//  the base structures should go first
		//list.addAll( structures );
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);

			// bug fix 2004-04-12
			// Do not define an attribute with the same name again in the
			// subclass.
			if (structure instanceof XsdAttribute) {
				XsdAttribute attribute = (XsdAttribute) structure;
				if (attributeNames.contains(attribute.getName())) {
					log.info("attribute already defined in parent class, "
							+ name + "." + attribute.getName());
				} else {
					list.add(structure);
				}
			} else {
				list.add(structure);
			}

		}

		structures = list;    */

		importedBase = true;
	}

	// maby call this getSimpleContentAttributes
	// may need to recursively go through simpleContent bases
	private ArrayList getAttributes() {
		ArrayList attributes = new ArrayList();

		// TODO what about XsdAttributeGroup
		for (int i = 0, n = sizeStructures(); i < n; i++) {
			XsdStructure structure = getStructure(i);
			if (structure instanceof XsdAttribute) {
				attributes.add(structure);
			}
		}
		return attributes;
	}
	
	/* check to see if the complexType is just a wrapper around a single element collection */
	boolean isListWrapper(){
        if (isReferenced) return false;
		if( sizeStructures() != 1 ) return false; // 1 sequence
		XsdStructure structure = getStructure(0);
		if( ! (structure instanceof XsdSequence) ) return false;
		XsdSequence sequence = (XsdSequence) structure;
		if( sequence.sizeStructures() != 1 ) return false; // 1 element
		XsdStructure structure2 = sequence.getStructure(0);
		if( ! (structure2 instanceof XsdElement) ) return false;
		XsdElement element = (XsdElement) structure2;
		if( ! element.isCollection()) return false;
        if (getParentComplexType() == null) return false;
		return true;
	}
	
	// see isListWrapper()
	public XsdElement getWrappedElement() {
		XsdSequence sequence = (XsdSequence) getStructure(0);
		return (XsdElement) sequence.getStructure(0);
	}

}