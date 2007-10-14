package br.boirque.vocabuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.dao.RecordStoreFactory;

/**
 * 
 * @author cleber.goncalves
 * Encapsulates the persistence of a 
 * set of cards
 */
public class SetOfCardsDAO {
	RecordStore recordStore;
	
	/**
	 * @param recordStore
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 * @throws RecordStoreFullException 
	 * Initialize the RecordStore
	 * TODO: it is probably a good idea to generate the store in a factory that takes care of the maximum size
	 */
	public SetOfCardsDAO() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		super();
		this.recordStore = RecordStoreFactory.getFactory().getStoreInstance();
	}

	/**
	 * Removes the current record store and creates a new
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 */
//	public void resetState() throws RecordStoreNotFoundException, RecordStoreException{
//		this.recordStore.closeRecordStore();
//		RecordStore.deleteRecordStore(recordStore.getName());
//		this.recordStore = RecordStoreFactory.getFactory().getStoreInstance();
//	}
		
	
	/**
	 * Read back the data from the record store in the format:
	 * title;setIsDone;totalTime;side1title;side1text;side2title;side2text;cardIsDone;tip
	 * This is the format written by the SaveState method. 
	 * Both methods have to be changed at the same time
	 * if the record format changes
	 * @return a SetOfCards read from the store
	 * @throws IOException 
	 * @throws RecordStoreException 
	 * @throws InvalidRecordIDException 
	 * TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards LoadSet() throws IOException, InvalidRecordIDException, RecordStoreException {
			int numRecords = recordStore.getNumRecords();
			SetOfCards setToReturn = new SetOfCards();
			Vector cards = new Vector();
		    for(int i = 1; i<=numRecords;i++){
		    	//read the cards one record at a time
		    	ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(i));
				DataInputStream inputStream = new DataInputStream(bais);
				setToReturn.setTitle(inputStream.readUTF());
				setToReturn.setDone(inputStream.readBoolean());
				setToReturn.setTotalStudiedTimeInMiliseconds(inputStream.readLong());
				setToReturn.setSideOneTitle(inputStream.readUTF());
				//recover the card
				FlashCard card = new FlashCard();
				//side one text
				card.setSideOne(inputStream.readUTF());
				//the title for side 2
				setToReturn.setSideTwoTitle(inputStream.readUTF());
				//the text for side two
				card.setSideTwo(inputStream.readUTF());
				//card state
				card.setDone(inputStream.readBoolean());
				//card tip
				card.setTip(inputStream.readUTF());
				cards.addElement(card);
		    }
		    //add the vector of cards to the set
		    setToReturn.setFlashCards(cards);
		
			return setToReturn;
	}
	/**
	 * 
	 * @param setOfCards
	 * @throws IOException
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreFullException
	 * @throws RecordStoreException
	 * Save the current state to persistence (file on the device)
	 * TODO: Try to recover from some of the exceptions
	 * (like record is full)
	 */
	public void SaveState(SetOfCards setOfCards) throws IOException, RecordStoreNotOpenException, RecordStoreFullException, RecordStoreException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);
		int recId; //holds the key for the record. Not used.
		
		//get the data from the SetOfCards into an byte array
		//Generic data shared between all the cards
		String side1title = setOfCards.getSideOneTitle();
		String side2title = setOfCards.getSideTwoTitle();
		String title = setOfCards.getTitle();
		long totalTime = setOfCards.getTotalStudiedTimeInMiliseconds();
		boolean setIsDone = setOfCards.isDone();
		Vector cards = setOfCards.getFlashCards();
		//get each card information and save to the record
		//in the order:
		//title;setIsDone;totalTime;side1title;side1text;side2title;side2text;cardIsDone;tip
		int size = cards.size();
		for(int i =0; i<size; i++) {
			FlashCard card = (FlashCard)cards.elementAt(i);
			String side1text = card.getSideOne();
			String side2text = card.getSideTwo();
			String tip = card.getTip();
			boolean cardIsDone = card.isDone();
			outputStream.writeUTF(title);
			outputStream.writeBoolean(setIsDone);
			outputStream.writeLong(totalTime);
			outputStream.writeUTF(side1title);
			outputStream.writeUTF(side1text);
			outputStream.writeUTF(side2title);
			outputStream.writeUTF(side2text);
			outputStream.writeBoolean(cardIsDone);
			outputStream.writeUTF(tip);
			
			// Extract the byte array
			byte[] b = baos.toByteArray();
			//write a record to the record store
		  //  int storeSize = recordStore.getSize();
			int recordLength = b.length;
			recId = recordStore.addRecord(b, 0, recordLength);
		}   
	}
}
