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

	private String propertiesFileName = "properties.txt";

	/**
	 * default constructor 
	 */
	public PropertiesLoader() {
	}

	/**
	 * @param propertiesFileName
	 */
	public PropertiesLoader(String propertiesFileName) {
		super();
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Load the properties from the default file named "properties.txt" 
	 * @return a Vector with the properties found 
	 * @throws IOException
	 */
	public Vector loadProperties() throws IOException {
		return loadProperties(propertiesFileName);
	}
	
	/**
	 * Load the properties from the file given as argument
	 * @param propertiesFileName the file from where to load the properties
	 * @return a Vector with the properties found 
	 * @throws IOException
	 */
	public Vector loadProperties(String propertiesFileName) throws IOException {
		VocaUtil util = new VocaUtil();
		String propertiesString = new String(util.readFile(propertiesFileName));
		return extractProperties(propertiesString);
	}

	private Vector extractProperties(String propertiesString) {
		Vector props = new Vector();

		char buf[] = propertiesString.toCharArray();
		propertiesString = null;
		int ends[] = { 0, 0 };

		// remove white space, \r, empty lines, comments etc
		VocaUtil.preProcess_TextFile(buf, true);

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
