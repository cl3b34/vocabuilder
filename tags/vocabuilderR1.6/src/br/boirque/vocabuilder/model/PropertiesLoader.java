/**
 * 
 */
package br.boirque.vocabuilder.model;

import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.util.VocaUtil;

/**
 * Loads the properties for the application from a text resource
 * 
 * @author cleber.goncalves
 * 
 */
public class PropertiesLoader {

	private static final String PROPERTIESFILENAME = "properties.txt";

	public Vector loadPropertie() throws IOException {
		VocaUtil util = new VocaUtil();
		String propertiesString = new String(util.readFile(PROPERTIESFILENAME));
		return extractProperties(propertiesString);
	}

	private Vector extractProperties(String propertiesString) {
		Vector props = new Vector();

		char buf[] = propertiesString.toCharArray();
		propertiesString = null;
		int ends[] = { 0, 0 };

		// remove white space, \r, empty lines, comments etc
		VocaUtil.preProcess_TextFile(buf);

		while ((propertiesString = VocaUtil.getNextLine(buf, ends)) != null) {
			char equalSign = '=';
			int equalSignPos = propertiesString.lastIndexOf(equalSign);
			Property p = new Property();
			String propName = propertiesString.substring(0, equalSignPos);
			p.setName(propName);
			String propValue = propertiesString.substring(equalSignPos + 1, propertiesString.length()); 
			p.setValue(propValue);
			props.addElement(p);
		}
		return props;
	}


}
