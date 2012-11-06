// $Id: JibxBinding.java,v 1.3 2004/08/31 07:19:25 ctaggart Exp $

package com.eucalyptus.gen2ools;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

class JibxBinding {

	private static final Log log = LogFactory.getLog(JibxBinding.class);

	private String _name;
//	private String _direction;
//	private String _globalIds;
//	private String _forwards;
//	private String _autoPrefix;
	private String _package;
//	private String _valueStyle;
//	private String _autoLink;
//	private String _accessLevel;
//	private String _stripPrefix;
//	private String _stripSuffix;
//	private String _nameStyle;

	private ArrayList _namespaceList = new ArrayList();
	private ArrayList _mappingList = new ArrayList();
	private String _namespace;
	private Boolean _force_classes=true;
	private Boolean _add_constructors=true;
	
	
	/* JiBX needs a no-argument constructor or factory method. */
	public JibxBinding() {
	}

	/** Unmarshal from XML using JiBX. */
	public static JibxBinding unmarshal(InputStream in) {
		JibxBinding binding = null;
		try {
			IBindingFactory bf = BindingDirectory.getFactory(JibxBinding.class);
			IUnmarshallingContext uctx = bf.createUnmarshallingContext();
			binding = (JibxBinding) uctx.unmarshalDocument(in, null);
		} catch (Exception e) {
			log.fatal("Unable to unmarshall. " + e);
		}

		return binding;
	}

	/** Marshal to XML using JiBX. */
	public void marshal(OutputStream out)
	//throws JiBXException
	{
    JibxMapping baseTypeMapping = new JibxMapping( );
    baseTypeMapping.setAbstract( true );
    baseTypeMapping.setClassName( Generate.defaultPackage + "." + Generate.baseType.getClassName( ) );
    _mappingList.add( baseTypeMapping );

		IBindingFactory bf;
		try {
			bf = BindingDirectory.getFactory(JibxBinding.class);
			IMarshallingContext mctx = bf.createMarshallingContext();
			mctx.setIndent(2); // 0 by default
			mctx.marshalDocument(this, "UTF-8", null, out);
		} catch (JiBXException e) {
			throw new GeneratorException("unable to marshal", e);
		}

	}

	public String getName() {
		return this._name;
	}

  public String getTargetNamespace() {
    return this._namespace;
  }


//	public String getDirection() {
//		return this._direction;
//	}
//
//	public void setDirection(String _direction) {
//		this._direction = _direction;
//	}
//
//	public String getGlobalIds() {
//		return this._globalIds;
//	}
//
//	public void setGlobalIds(String _globalIds) {
//		this._globalIds = _globalIds;
//	}
//
//	public String getForwards() {
//		return this._forwards;
//	}
//
//	public void setForwards(String _forwards) {
//		this._forwards = _forwards;
//	}
//
//	public String getAutoPrefix() {
//		return this._autoPrefix;
//	}
//
//	public void setAutoPrefix(String _autoPrefix) {
//		this._autoPrefix = _autoPrefix;
//	}

	public String getPackage() {
		return this._package;
	}

	public void setPackage(String _package) {
		this._package = _package;
	}

//	public String getValueStyle() {
//		return this._valueStyle;
//	}
//
//	public void setValueStyle(String _valueStyle) {
//		this._valueStyle = _valueStyle;
//	}
//
//	public String getAutoLink() {
//		return this._autoLink;
//	}
//
//	public void setAutoLink(String _autoLink) {
//		this._autoLink = _autoLink;
//	}
//
//	public String getAccessLevel() {
//		return this._accessLevel;
//	}
//
//	public void setAccessLevel(String _accessLevel) {
//		this._accessLevel = _accessLevel;
//	}
//
//	public String getStripPrefix() {
//		return this._stripPrefix;
//	}
//
//	public void setStripPrefix(String _stripPrefix) {
//		this._stripPrefix = _stripPrefix;
//	}
//
//	public String getStripSuffix() {
//		return this._stripSuffix;
//	}
//
//	public void setStripSuffix(String _stripSuffix) {
//		this._stripSuffix = _stripSuffix;
//	}
//
//	public String getNameStyle() {
//		return this._nameStyle;
//	}
//
//	public void setNameStyle(String _nameStyle) {
//		this._nameStyle = _nameStyle;
//	}

	public void addNamespace(JibxNamespace _namespace) {
	  this._namespace = _namespace.getUri( ).replaceAll( "https://", "http://" );
	  this._name = this._namespace.replaceAll( "(http://)|(/$)", "" ).replaceAll( "[./-]", "_" );
		_namespaceList.add(_namespace);
	}

	public JibxNamespace getNamespace(int index) {
		return (JibxNamespace) _namespaceList.get(index);
	}

	public int sizeNamespaceList() {
		return _namespaceList.size();
	}

//	public void addFormat(JibxFormat _format) {
//		_formatList.add(_format);
//	}
//
//	public JibxFormat getFormat(int index) {
//		return (JibxFormat) _formatList.get(index);
//	}
//
//	public int sizeFormatList() {
//		return _formatList.size();
//	}

	public void addMapping(JibxMapping _mapping) {
		_mappingList.add(_mapping);
	}

	public JibxMapping getMapping(int index) {
		return (JibxMapping) _mappingList.get(index);
	}

	public int sizeMappingList() {
		return _mappingList.size();
	}
}