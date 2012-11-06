// $Id: JibxMapping.java,v 1.6 2005/08/19 19:45:44 dsosnoski Exp $

package com.eucalyptus.gen2ools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class JibxMapping extends JibxChoice {
  
  private static final Log log           = LogFactory.getLog( JibxMapping.class );
  
  private Generator        generator;
  
  // structures that reference this mapping
  private ArrayList        refList       = new ArrayList( );
  
  private XsdType          xsdType;
  private String           className;
  
  private String           _label;
  private String           _using;
  private String           _ordered;
  private String           _extends;
  private boolean          _abstract;
  
  private ArrayList        namespaces    = new ArrayList( );
  
  private Set              namespaceUris = new HashSet( );
  // so that they don't get added more than once
  
  private ArrayList        _mappingList  = new ArrayList( );
  
  /** JiBX needs a no-argument constructor or factory method. */
  public JibxMapping( ) {}
  
  public ArrayList getValues( ) {
    ArrayList values = new ArrayList( );
    for ( int i = 0, n = choiceList.size( ); i < n; i++ ) {
      Object choice = choiceList.get( i );
      if ( choice instanceof JibxValue ) {
        values.add( choice );
      }
    }
    return values;
  }
  
  public void addValue( JibxValue _value ) {
    choiceList.add( _value );
  }
  
  public void addStructure( JibxStructure _structure ) {
    choiceList.add( _structure );
  }
  
  public void addCollection( JibxCollection _collection ) {
    choiceList.add( _collection );
  }
  
  public void setName( String _name ) {
    this.name = _name;
  }
  
  public String getLabel( ) {
    return this._label;
  }
  
  public void setLabel( String _label ) {
    this._label = _label;
  }
  
  public String getUsing( ) {
    return this._using;
  }
  
  public void setUsing( String _using ) {
    this._using = _using;
  }
  
  public String getOrdered( ) {
    return this._ordered;
  }
  
  public void setOrdered( String _ordered ) {
    this._ordered = _ordered;
  }
  
  public void addNamespace( JibxNamespace ns ) {
    if ( !namespaceUris.contains( ns.getUri( ) ) ) {
      namespaces.add( ns );
      namespaceUris.add( ns.getUri( ) );
    }
  }
  
  public JibxNamespace getNamespace( int index ) {
    return ( JibxNamespace ) namespaces.get( index );
  }
  
  public int sizeNamespaceList( ) {
    return namespaces.size( );
  }
  
  public void addMapping( JibxMapping _mapping ) {
    _mappingList.add( _mapping );
  }
  
  public JibxMapping getMapping( int index ) {
    return ( JibxMapping ) _mappingList.get( index );
  }
  
  public int sizeMappingList( ) {
    return _mappingList.size( );
  }
  
  public String toString( ) {
    return "JibxMapping[ xsdType: " + xsdType + " ]";
  }
  
  /**
   * @param xsdType
   */
  public void setXsdType( XsdType xsdType ) {
    this.xsdType = xsdType;
  }
  
  public XsdType getXsdType( ) {
    return xsdType;
  }
  
  public void addRef( JibxStructure ref ) {
    refList.add( ref );
  }
  
  private int sizeRefList( ) {
    return refList.size( );
  }
  
  private JibxStructure getRef( int i ) {
    return ( JibxStructure ) refList.get( i );
  }
  
  public void convertUsingLabelToMapAs( ) {
    int size = refList.size( );
    log.info( "" + size + " references for " + this );
    String type = generator.getQName( xsdType ).toString( );
    className = type;
//    if ( size == 1 ) {
//      JibxStructure js = getRef( 0 );
//      js.setMapAs( type );
//      setName( js.getName( ) );
//      js.setName( null );
//    } else 
//    if ( size > 1 ) {
    if ( size > 0 ) {
      _abstract = true;
      setName( null );
      for ( int i = 0; i < size; i++ ) {
        JibxStructure js = getRef( i );
        js.setType( type );
      }
    } else if ( size == 0 ) {
      // see if it is a globalElement
      if ( generator.isElement( xsdType ) ) {
        XsdElement element = generator.getElement( xsdType );
        setName( element.getName( ) );
      } else {
        String errMsg = "compleType not used, no references and not a global element, " + this;
        log.warn( errMsg );
//				throw new RuntimeException(errMsg);
//				setName(type);
      }
    }
  }
  
  public void setGenerator( Generator generator ) {
    this.generator = generator;
  }
  
  public Generator getGenerator( ) {
    return generator;
  }
  
  /**
   * sets all the namespaces
   */
  public void setNamespace( Generator generator ) {
//		ns = xsdType.getNamespaceUri();
    
    // if it has a namespace
    if ( xsdType.getNamespaceUri( ) != null ) {
      JibxNamespace defaultNs = new JibxNamespace( );
      defaultNs.setUri( xsdType.getNamespaceUri( ).replaceAll( "https://", "http://" ) );
      defaultNs.setDefault( JibxNamespace.DEFAULT_ELEMENTS );
      addNamespace( defaultNs );
    }
    
    HashSet set = new HashSet( );
    set.add( ns );
    
    for ( int i = 0, n = sizeChoiceList( ); i < n; i++ ) {
      JibxObject choice = getChoice( i );
      String otherNs = choice.getNs( );
      if ( otherNs != null && !set.contains( otherNs ) ) {
        JibxNamespace other = generator.getNamespace( otherNs );
        addNamespace( other );
      }
    }
  }
  
  /**
   * @return the _extends
   */
  public String getExtends( ) {
    return this._extends;
  }
  
  /**
   * @param _extends the _extends to set
   */
  public void setExtends( final String _extends ) {
    this.addStructure( new JibxStructure( ) {
      {
        setMapAs( _extends );
      }
    } );
    this._extends = _extends;
  }

  /**
   * @return the _abstract
   */
  public boolean isAbstract( ) {
    return this._abstract;
  }

  /**
   * @param _abstract the _abstract to set
   */
  public void setAbstract( boolean _abstract ) {
    this._abstract = _abstract;
  }

  /**
   * @return the className
   */
  public String getClassName( ) {
    return this.className;
  }

  /**
   * @param className the className to set
   */
  public void setClassName( String className ) {
    this.className = className;
  }
}
