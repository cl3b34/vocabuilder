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

	//Filename must start with / (slash)
	private static final String PROPERTIESFILENAME = "/properties.txt";

	public Propertie[] loadPropertie() throws IOException {
		VocaUtil util = new VocaUtil();
		String propertiesString = new String(util.readFile(PROPERTIESFILENAME));
		return extractProperties(propertiesString);
	}

	private Propertie[] extractProperties(String propertiesString) {
		Vector props = new Vector();

		char buf[] = propertiesString.toCharArray();
		propertiesString = null;
		int ends[] = { 0, 0 };

		// remove white space, \r, empty lines, comments etc
		preProcess_TextFile(buf);

		while ((propertiesString = getNextLine(buf, ends)) != null) {
			char equalSign = '=';
			int equalSignPos = propertiesString.lastIndexOf(equalSign);
			Propertie p = new Propertie();
			String propName = propertiesString.substring(0, equalSignPos);
			p.setName(propName);
			String propValue = propertiesString.substring(equalSignPos + 1, propertiesString.length()); 
			p.setValue(propValue);
			props.addElement(p);
		}
		Propertie[] propertiesExtracted = new Propertie[props.size()];
		props.copyInto(propertiesExtracted);
		return propertiesExtracted;
	}

	/**
	 * Processes a byte[] containing text so it can be read as a String, line by
	 * line. From:
	 * http://discussion.forum.nokia.com/forum/showthread.php?t=28898&highlight=rms
	 * 
	 * @param buf
	 */
	public static void preProcess_TextFile(char buf[]) {
		// preProcess_removeCarriageReturns(buf);
		int i = 0, k = 0;
		boolean inquote;
		while (i < buf.length) {
			buf[k] = buf[i];
			if (buf[i] != '\r')
				k++;
			i++;
		}
		while (k < buf.length)
			buf[k++] = '\0';
		// buf[k]='\0';

		// preProcess_ReprocessSpecialChars(buf);
		i = 0; // eg [\][n] becomes [\n]
		k = 0;
		while (i < buf.length - 1) {
			buf[k] = buf[i];
			if (buf[i] == '\\' && buf[i + 1] == 'n') {
				buf[k] = '\n';
				i++;
			}
			i++;
			k++;
		}
		while (k < buf.length)
			buf[k++] = '\0';

		// preProcess_tolower(buf);
		i = 0;
		inquote = false;
		while (i < buf.length) {
			if (buf[i] == '"')
				inquote = !inquote;
			else if (!inquote)
				if (buf[i] >= 'A' && buf[i] <= 'Z')
					buf[i] = (char) ((int) buf[i] + 'a' - 'A');
			i++;
		}

		// preProcess_removeComments(buf);
		i = 0;
		while (i < buf.length) {
			while (i < buf.length && buf[i] != '#')
				i++;
			while (i < buf.length && buf[i] != '\n')
				buf[i++] = ' ';
		}

		// preProcess_removeWhiteSpace(buf);
		i = 0;
		k = 0;
		inquote = false;
		while (i < buf.length) {
			buf[k] = buf[i];

			if (buf[k] == '"')
				inquote = !inquote;

			if (inquote || (buf[i] != ' ' && buf[i] != '\t'))
				k++;
			i++;
		}
		while (k < buf.length)
			buf[k++] = '\0';

		// preProcess_cutEmptyLines(buf);
		i = 0;
		k = 0;
		inquote = false;
		while (i < buf.length) {
			buf[k] = buf[i];

			if (buf[k] == '"')
				inquote = !inquote;

			if (inquote || buf[i] != '\n' || buf[i + 1] != '\n')
				k++; // advance k (dest) if [i] and [i+1] are not both \n

			i++;
		}
		while (k < buf.length)
			buf[k++] = '\0';
	}

	public static String getNextLine(char buf[], int ends[]) {
		int i = ends[0];
		int k = ends[1];

		// terminated by \n
		while (buf[i] == '\n' && buf[i] > 0 && i < buf.length)
			i++;
		k = i;
		while (buf[k] != '\n' && buf[k] > 0 && k < buf.length)
			k++;

		// if (buf[k] == 0 || k >= buf.length)
		if (i == k)
			return null;

		ends[0] = k;
		ends[1] = k;

		return new String(buf, i, k - i);
	}
}
