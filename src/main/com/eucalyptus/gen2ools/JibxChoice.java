/*
 * Created on Aug 29, 2004
 */
package com.eucalyptus.gen2ools;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * for JibxObjects that contain a list of other JibxObjects
 * extended by JibxCollection, JibxStructure, JibxMapping
 * @author ctaggart
 */
public abstract class JibxChoice extends JibxObject {
	
	private static final Log log = LogFactory.getLog(JibxChoice.class);

	protected ArrayList choiceList = new ArrayList();
	
	public int sizeChoiceList(){
		return choiceList.size();
	}
	
	public JibxObject getChoice(int i){
		return (JibxObject) choiceList.get(i);
	}
	
	public void addChoice(JibxObject choice){
		choiceList.add(choice);
	}
	
	public void addMappingRefs(Generator generator){
		for(int j=0,m=sizeChoiceList();j<m;j++){
			JibxObject jo = getChoice(j);
			
			if( jo instanceof JibxChoice ){
				JibxChoice choice = (JibxChoice) jo;
				
				if( jo instanceof JibxStructure ){
					JibxStructure js = (JibxStructure) choice;
					log.info("addMappingRef: "+js);
					XsdType xsdType = js.getType();
					if( xsdType == null){
						String errMsg = "unable to get xsdType for "+js;
						log.warn(errMsg);
//						throw new NullPointerException(errMsg);
					} else {
						JibxMapping jm = generator.getMapping(xsdType);
						if( jm == null){
							String errMsg = "unable to retrieve mapping for "+xsdType;
							log.fatal(errMsg);
//							throw new NullPointerException(errMsg);
						} else {
							jm.addRef(js);
						}
					}
				}
				
				// recursive
				choice.addMappingRefs(generator);

			}

		}
	}
}
