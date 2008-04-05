package br.boirque.vocabuilder.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * Loads a SetOfCards from external medium (Http, Bluetooth, IR, flashmemory)
 * 
 * @author cleber.goncalves
 * 
 */
public class SetOfCardsLoader {

	//load the default set
	public SetOfCards loadSet() throws IOException {
		return loadSet("/Swahilli/longlist_eng_swa.txt");
	}
	
	// this method tries to load a set
	// from whatever medium it can
	public SetOfCards loadSet(String setName) throws IOException {
		SetOfCards setToReturn = null;
		setToReturn = textFileLoader(setName);
		return setToReturn;
	}
	

	/*
	 * Only generates a fake set of cards and returns it
	 * Early development purposes only...
	 */
	private SetOfCards mockLoader() {
		// just return a fake SetOfCards
		FlashCard mockCard1 = new FlashCard("tuli", "fin", "fire", "eng",
				false, "burns");
		FlashCard mockCard2 = new FlashCard("veta", "fin", "water", "eng",
				false, "wets");
		FlashCard mockCard3 = new FlashCard("leipa", "fin", "bread", "eng",
				false, "is eatable");
		FlashCard mockCard4 = new FlashCard("tietokone", "fin", "computer",
				"eng", false, "knowledge machine");

		Vector cards = new Vector();
		cards.addElement(mockCard1);
		cards.addElement(mockCard2);
		cards.addElement(mockCard3);
		cards.addElement(mockCard4);

		SetOfCards mockSet = new SetOfCards("mockSet", false, 2000L, cards);
		return mockSet;
	}

	/**
	 * Loads and parses a file from the /res folder the file must be in the
	 * format: word = translation
	 * 
	 * @param fileToLoad -
	 *            The file to load
	 * @return a SetOfCards with the cards parsed from the file
	 * @throws IOException
	 */
	public SetOfCards textFileLoader(String fileToLoad) throws IOException {
		// read and buffers the file for better performance
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];

		InputStream istream = getClass().getResourceAsStream(fileToLoad);
		boolean done = false;

		while (!done) {
			int count = istream.read(buffer);
			if (count == -1) {
				done = true;
			} else {
				baos.write(buffer, 0, count);
			}
		}

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		SetOfCards soc = extractSetOfCards(bais);
		return soc;
	}

	/*
	 * Extracts a SetOfCards from the given ByteArray It is highly dependent on
	 * the file format
	 * TODO: Needs a major refactoring. Lots of repeated and misplaced code.
	 * Create constants and methods to do repeated tasks.
	 */
	private SetOfCards extractSetOfCards(ByteArrayInputStream bais) {
		boolean done = false;
		boolean isMetadata = false;
		//the metadata
		String setName = "default set";
		String sideOneTitle = "ENG";
		String sideTwoTitle = "FIN";
		String metadata = "";

		// process the file into a set of cards
		SetOfCards soc = new SetOfCards(setName, false, 0L, null);
		FlashCard readCard = new FlashCard();
		Vector cards = new Vector();
		StringBuffer sb = new StringBuffer();
//		char space = ' ';
		char lineFeed = '\n';
		char chariageReturn = '\r';
		char equalSign = '=';
		char numberSign = '#';
		while (!done) {
			int readChar = bais.read();
			if (readChar == -1) {
				// Save the last Flashcard to the vector before exiting
				String sideTwoText = sb.toString();
				sideTwoText = sideTwoText.trim();
				readCard.setSideTwo(sideTwoText);
				readCard.setSideTwoTitle(sideTwoTitle);
				readCard.setDone(false);
				readCard.setTip("");
				cards.addElement(readCard);
				done = true;
			} else {
				char c = (char) readChar;
				if (c == numberSign) {
					isMetadata = true;
					continue;
				}
				if (c == lineFeed || c == chariageReturn && isMetadata) {
					if (sb.length() > 0) {
						// got the value of the metadata
						if(metadata.equals("setName")) {
							setName = sb.toString().trim();
							soc.setTitle(setName);
						}else if (metadata.equals("sideOneTitle")) {
							sideOneTitle = sb.toString().trim();
						}else if(metadata.equals("sideTwoTitle")) {
							sideTwoTitle = sb.toString().trim();
						}
						
						//reset metadata and the string buffer
						metadata = "";
						sb = new StringBuffer();
						isMetadata = false;
						}
					continue;
				}					
				if (c == lineFeed || c == chariageReturn) {
					// end of the second word
					// Save this Flashcard to the vector and
					// continue to the next Flashcard
					if (sb.length() > 0) {
						String sideTwoText = sb.toString();
						sideTwoText = sideTwoText.trim();
						readCard.setSideTwo(sideTwoText);
						readCard.setSideTwoTitle(sideTwoTitle);
						readCard.setDone(false);
						readCard.setTip("");
						cards.addElement(readCard);
						readCard = new FlashCard();
						sb = new StringBuffer();
					}
					continue;
				}
				if (c == equalSign && isMetadata) {
					if (sb.length() > 0) {
						// got the title of the metadata
						metadata = sb.toString().trim();
						sb = new StringBuffer();
					}
					continue;
				}					
				if (c == equalSign) {
					// end of the first word, start of the translation word
					String sideOneText = sb.toString();
					sideOneText = sideOneText.trim();
					readCard.setSideOne(sideOneText);
					readCard.setSideOneTitle(sideOneTitle);
					sb = new StringBuffer();
					continue;
				}
				// if none of the conditions above apply, it is a valid char
				// just add it to our string
				sb.append(c);
			}
		}
		soc.setFlashCards(cards);
		return soc;
	}
	
}
