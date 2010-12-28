/**
 * 
 */
package br.boirque.vocabuilder.model;

import java.lang.Exception;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;

import br.boirque.vocabuilder.controller.DisplayManager;
import br.boirque.vocabuilder.util.VocaUtil;

/**
 * @author cleber.goncalves
 * 
 */
public class StudyStackDownloader implements ISetDownloader {

	//private Command cancelCommand = new Command("Cancel",Command.CANCEL, 0);
	//private boolean cancel = false;
	
	public SetOfCards downloadSet(String setName) {

		
		String uri = "http://www.studystack.com/servlet/simpledelim/ss.txt?studyStackName=a&charset=UTF-8&delimiter=|&studyStackId="
				+ setName;
		// displays squares (missing font?)
		//String downloadedSet = readUtfString(uri);
		// this one displays rabish
//		String downloadedSet =readUtfStringV2(uri);
		// displays squares (missing font?)
		String downloadedSet = readUtfStringV3(uri);
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
		
		String textUtf = null;
		
//		Displayable currentScreen;
//		currentScreen = DisplayManager.getCurrent();
//	
		//StringBuffer text = new StringBuffer();
		
		try {
			conn = (StreamConnection) Connector.open(uri);
			in = conn.openInputStream();
			

			Gauge gauge = new Gauge(null,
									false,
									Gauge.INDEFINITE,
									Gauge.INCREMENTAL_UPDATING);
			Alert busyAlert = new Alert("Download",null,null,AlertType.INFO);
			busyAlert.setIndicator(gauge);
			busyAlert.setTimeout(60*1000);	// 1 min
			
			DisplayManager.setCurrent(busyAlert);
			
			
			byte[] bytes = new byte[4096];
			
			byte[] collector = {};
			byte[] tempCollector = {};
			
			int rxBytes;
			int offset = 0;
			
			while ((rxBytes = in.read(bytes)) != -1) {
				System.out.println(" Bytes read: " + rxBytes);
				busyAlert.getIndicator().setValue(Gauge.INCREMENTAL_UPDATING);
				
				offset = collector.length;
				tempCollector = collector;
				
				collector = new byte[ offset + rxBytes ];
				System.arraycopy(tempCollector, 0, collector, 0, offset);
				System.arraycopy(bytes, 0, collector, offset, rxBytes);
				
				bytes = new byte[4096];
			}
			
			textUtf = new String(collector, "UTF-8");
			
			conn.close();
			System.out.println(" Bytes downloaded: " + collector.length);

			busyAlert.setTimeout(Alert.FOREVER);
			busyAlert.setTitle("Completed");
			busyAlert.setString(textUtf.length() + " bytes downloaded");
			busyAlert.setIndicator(null);
			
		} catch (Exception e) {
			System.err.print("Exception caught");
			e.printStackTrace();
		}
//		System.out.println("String buffer " + text.toString());
		
		return textUtf;
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
		} catch (Exception e) {
			System.err.print("Exception caught" +e);
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
		
		
		Displayable currentScreen;
		currentScreen = DisplayManager.getCurrent();
	
		
		try {
			conn = (StreamConnection) Connector.open(uri);
			in = conn.openInputStream();
			InputStreamReader isr = new InputStreamReader(in, "UTF-8");
			
			//progress bar
			Gauge gauge = new Gauge(null,
									false,
									Gauge.INDEFINITE,
									Gauge.CONTINUOUS_RUNNING);
			Alert busyAlert = new Alert("Download",null,null,AlertType.INFO);
			busyAlert.setIndicator(gauge);
			busyAlert.setTimeout(60*1000);	// 1 min
			
			DisplayManager.setCurrent(busyAlert);
		
						
			
			// read cycle
			int ch;
			while ((ch = isr.read()) != -1) {
				text.append((char) ch);
				//System.out.print(".");
				//busyAlert.getIndicator().setValue(Gauge.INCREMENTAL_UPDATING);
			}
			
			//clean up
			isr.close();
			in.close();
			conn.close();
			
			System.out.println("\n Chars downloaded: " + text.length());

			busyAlert.setTimeout(Alert.FOREVER);
			busyAlert.setTitle("Completed");
			busyAlert.setString(text.length() + " chars downloaded");
			busyAlert.setIndicator(null);
			
		} catch (Exception e) {
			System.err.print("Exception caught");
			e.printStackTrace();
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
		sets.addElement("521903");
		return sets;
	}


}
