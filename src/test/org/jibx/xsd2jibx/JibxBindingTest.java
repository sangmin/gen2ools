// $Id: JibxBindingTest.java,v 1.2 2004/08/30 08:09:46 ctaggart Exp $

package org.jibx.xsd2jibx;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.jibx.xsd2jibx.JibxBinding;
import org.jibx.xsd2jibx.JibxMapping;
import org.jibx.xsd2jibx.JibxStructure;
import org.jibx.xsd2jibx.JibxValue;

public class JibxBindingTest {
	//~ Methods
	// ------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		OutputStream out = new FileOutputStream("test.jibx.xml");

		JibxBinding binding = new JibxBinding();
		//JibxMapping mapping = new JibxMapping( "anyobject", "AnyObject" );
		JibxMapping mapping = new JibxMapping();
//		mapping.setName("anyObject");
//		mapping.setClassName("test.AnyObject");

		JibxStructure structure = new JibxStructure();
		structure.setName("anotherobject");
		structure.setField("AnotherObject");

		JibxValue value = new JibxValue();
		value.setName("label");
		value.setField("_label");
		value.setStyle("attribute");
		value.setUsage("optional");

//		structure.addValue(value);
		value = new JibxValue();
		value.setName("using");
		value.setField("_using");
		value.setStyle("attribute");
		value.setUsage("optional");

//		structure.addValue(value);
		mapping.addStructure(structure);

		value = new JibxValue();
		value.setName("auto-link");
		value.setField("_autoLink");
		value.setStyle("attribute");
		value.setUsage("optional");

		mapping.addValue(value);

		binding.addMapping(mapping);

		binding.marshal(out);
	}
}