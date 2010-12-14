package br.boirque.vocabuilder.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.util.VocaUtil;
import java.io.InputStreamReader;

/**
 * Loads a SetOfCards from external medium (Http, Bluetooth, IR, flashmemory)
 * 
 * @author cleber.goncalves
 * 
 */
public class SetOfCardsLoader {
	
	public SetOfCards loadSet(String setName) throws IOException{
		SetOfCards setToReturn = null;
		setToReturn = textFileLoader(setName);
		return setToReturn;
	}
		

	/**
	 * Loads and parses a file from the /res folder the file must be in the
	 * format: word = translation
	 * 
	 * TODO: only works with windows line delimiters
	 * TODO: If the last line is empty it includes a null element in the set
	 * 
	 * @param fileToLoad -
	 *            The file to load
	 * @return a SetOfCards with the cards parsed from the file
	 * @throws IOException 
	 * @throws IOException
	 */
	private SetOfCards textFileLoader(String fileToLoad) throws IOException {
		VocaUtil util = new VocaUtil();
		ByteArrayInputStream bais = new ByteArrayInputStream(util.readFile(fileToLoad));
		SetOfCards soc = extractSetOfCards(bais);
		//set the total amount of cards as set meta data
		soc.setTotalNumberOfCards(soc.getFlashCards().size());
		return soc;
	}
	
	/*
	 * Extracts a SetOfCards from the given ByteArray.
	 * It is highly dependent on the file format
	 */
	private SetOfCards extractSetOfCards(ByteArrayInputStream bais) throws IOException {
		boolean done = false;
		boolean isMetadata = false;
		
		final char LINEFEED = '\n';
		final char CHARIAGERETURN = '\r';
		final char EQUALSIGN = '=';
		final char NUMBERSIGN = '#';
		final int BOM_BE = 0xFEFF;
		final int BOM_LE = 0xFFFE;
	//	final char SPACE = ' ';
		
		//the meta data. Default values
		String setName = "default set";
		String sideOneTitle = "ENG";
		String sideTwoTitle = "FIN";
		String metadata = null;

		// process the file into a set of cards
		SetOfCards soc = new SetOfCards(setName, false, null);
		FlashCard readCard = new FlashCard();
		Vector cards = new Vector();
		StringBuffer sb = new StringBuffer();
		
        InputStreamReader isreader = new InputStreamReader(bais, "UTF-8");
        
		while (!done) {
			int readChar = isreader.read();
			if (readChar == -1) {
				// Save the last Flashcard to the vector before exiting
				readCard = fillCardSide(false, sb.toString(), sideTwoTitle, readCard);
				cards.addElement(readCard);
				sb = null;
				done = true;
			} 
			else 
				//Check for BOM sequence and skip it
				if (readChar == BOM_BE || readChar == BOM_LE) 	continue;
			
			else {
				char c = (char) readChar;
				if (c == NUMBERSIGN) {
					isMetadata = true;
					continue;
				}
				if ((c == LINEFEED || c == CHARIAGERETURN )&& isMetadata) {
					if (sb.length() > 0) {
						// got the value of the metadata
						if(metadata.equals("setName")) {
							setName = sb.toString().trim();
							soc.setSetName(setName);
						}else if (metadata.equals("sideOneTitle")) {
							sideOneTitle = sb.toString().trim();
						}else if(metadata.equals("sideTwoTitle")) {
							sideTwoTitle = sb.toString().trim();
						}
						//reset metadata and the string buffer
						metadata = "";
						sb = null;
						sb = new StringBuffer();
						isMetadata = false;
					}
					continue;
				}					
				if (c == LINEFEED || c == CHARIAGERETURN) {
					// end of the second word
					// Save this Flashcard to the vector and
					// continue to the next Flashcard
					if (sb.length() > 0) {
						readCard = fillCardSide(false, sb.toString(), sideTwoTitle, readCard);
						cards.addElement(readCard);
						readCard = new FlashCard();
						sb = null;
						sb = new StringBuffer();
					}
					continue;
				}
				if (c == EQUALSIGN && isMetadata) {
					if (sb.length() > 0) {
						// got the title of the metadata
						metadata = sb.toString().trim();
						sb = null;
						sb = new StringBuffer();
					}
					continue;
				}					
				if (c == EQUALSIGN) {
					// end of the first word, start of the translation word
					readCard = fillCardSide(true, sb.toString(), sideOneTitle, readCard);
					sb = null;
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
	
	private FlashCard fillCardSide(boolean sideOne, String value, String sideTitle, FlashCard card) {
		value = value.trim();
		if(sideOne) {
		card.setSideOne(value);
		card.setSideOneTitle(sideTitle);
		}else {
			card.setSideTwo(value);
			card.setSideTwoTitle(sideTitle);
			card.setDone(false);
			card.setTip("");
		}
		return card;
	}
	
}
