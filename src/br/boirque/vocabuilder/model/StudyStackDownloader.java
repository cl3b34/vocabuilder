/**
 * 
 */
package br.boirque.vocabuilder.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import br.boirque.vocabuilder.util.VocaUtil;

/**
 * @author cleber.goncalves
 * 
 */
public class StudyStackDownloader implements ISetDownloader {

	public SetOfCards downloadSet(String setName) {

		String uri = "http://www.studystack.com/servlet/simpledelim/ss.txt?studyStackName=a&charset=UTF-8&delimiter=|&studyStackId="
				+ setName;
		// displays squares (missing font?)
		String downloadedSet =readUtfString(uri);
		// this one displays rabish
//		String downloadedSet =readUtfStringV2(uri);
		// displays squares (missing font?)
//		String downloadedSet = readUtfStringV3(uri);
//		System.out.println("DownloadeSet: " + downloadedSet);
		Vector lines = splitInLines(downloadedSet);
		SetOfCards setCreated = createSetFromVector(lines, setName);
//		System.out.print("set Created " + setCreated);
		return setCreated;
	}

	private SetOfCards createSetFromVector(Vector lines, String setName) {
		SetOfCards set = new SetOfCards();
		set.setSetName(setName);
		set.setDone(false);
		set.setFlashCards(extractFlashCards(lines));
		set.setTotalNumberOfCards(set.getFlashCards().size());
		return set;
	}

	private Vector extractFlashCards(Vector lines) {
		Vector cards = new Vector();
		for(int i=0; i<lines.size();i++) {
			String line = (String)lines.elementAt(i);
			FlashCard card = extractCard(line);
			cards.addElement(card);
		}
		return cards;
	}
	

	private FlashCard extractCard(String line) {
		FlashCard card = new FlashCard();
		int firstPipe = line.indexOf('|');
		card.setSideOne(line.substring(0, firstPipe));
		int secondPipe = line.lastIndexOf('|');
		if(secondPipe != firstPipe) {
			card.setSideTwo(line.substring(firstPipe+1,secondPipe ));
			card.setTip(line.substring(secondPipe+1));
		}else {
			card.setSideTwo(line.substring(firstPipe+1));
		}
		
		// Add dummy titles
		card.setSideOneTitle("side one");
		card.setSideTwoTitle("side two");		
		
		return card;
	}

	protected Vector splitInLines(String downloadedSet) {
		char [] buf = downloadedSet.toCharArray();
		VocaUtil.preProcess_TextFile(buf, false);
		int [] ends = {0,0}; 
		String line = null;
		Vector lines = new Vector();
		while((line = VocaUtil.getNextLine(buf, ends))!=null) {
			lines.addElement(line);
		}		
		return lines;
	}

	/**
	 * @param in
	 * @param text
	 * @param bytes
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readUtfString(String uri) {
		StreamConnection conn = null;
		InputStream in = null;

		StringBuffer text = new StringBuffer();
		try {
			conn = (StreamConnection) Connector.open(uri);
			in = conn.openInputStream();
			byte[] bytes = new byte[1024];
			while (in.read(bytes) != -1) {
				String textUtf = new String(bytes, "UTF8");
				text.append(textUtf);
				bytes = new byte[1024];
			}
		} catch (IOException e) {
			System.err.print("IOException caught" +e);
		}
//		System.out.println("String buffer " + text.toString());
		return text.toString();
	}

	/**
	 * @param in
	 * @param text
	 * @param bytes
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readUtfStringV2(String uri){
		StreamConnection conn = null;
		InputStream in = null;
		StringBuffer text = new StringBuffer();
		try {
			conn = (StreamConnection) Connector.open(uri);
			in = conn.openInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				text.append((char) ch);
			}
		} catch (IOException e) {
			System.err.print("IOException caught" +e);
		}
//		System.out.println("V2" + text.toString());
		return text.toString();
	}

	/**
	 * @param in
	 * @param text
	 * @param bytes
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readUtfStringV3(String uri){
		StreamConnection conn = null;
		InputStream in = null;
		StringBuffer text = new StringBuffer();
		try {
			conn = (StreamConnection) Connector.open(uri);
			in = conn.openInputStream();
			InputStreamReader isr = new InputStreamReader(in, "UTF8");
			int ch;
			while ((ch = isr.read()) != -1) {
				text.append((char) ch);
			}
		} catch (IOException e) {
			System.err.print("IOException caught" +e);
		}
//		System.out.println("V3 " + text.toString());
		return text.toString();
	}

	public Vector listCategories() {
		// TODO Replace with proper implementation
		Vector categories = new Vector();
		categories.addElement("Languages");
		categories.addElement("Science");
		return categories;
	}

	public Vector listDownloadableSets(String category) {
		// TODO replace with proper implementation
		Vector sets = new Vector();
		sets.addElement("138055");
		sets.addElement("71681");
		sets.addElement("1");
		return sets;
	}

}
