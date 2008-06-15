package br.boirque.vocabuilder.model;

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
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance();
	}

	/**
	 * Removes the current record store and creates a new
	 * @throws RecordStoreException 
	 * @throws RecordStoreNotFoundException 
	 */
	public void resetState() throws RecordStoreNotFoundException, RecordStoreException{
		String recordStoreName = recordStore.getName();
		this.recordStore.closeRecordStore();
		//TODO - This reference to the opencount variable must be removed
		//opencount should be private
		RecordStoreFactory.openCount--;
		RecordStore.deleteRecordStore(recordStoreName);  
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance();
	}
	
	/**
	 * Returns the amount of records currently in the store
	 * @return amount of records in the store
	 * @throws RecordStoreNotOpenException 
	 */
	public int getRecordCount() throws RecordStoreNotOpenException {
		return recordStore.getNumRecords();
	}
	
	/**
	 * Read back the data from the record store
	 * In the format written by the SaveState method. 
	 * Both methods have to be changed at the same time
	 * if the record format changes
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException 
	 * @throws RecordStoreException 
	 * @throws InvalidRecordIDException 
	 * TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadState() throws IOException, InvalidRecordIDException, RecordStoreException {
			int numRecords = recordStore.getNumRecords();
			SetOfCards setToReturn = null;
			Vector cards = new Vector();
		    for(int i = 1; i<=numRecords;i++){
		    	setToReturn = new SetOfCards();
		    	//Create a input stream for the cards, one record at a time
		    	ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(i));
				DataInputStream inputStream = new DataInputStream(bais);
				
				//Recover data from the set
				String setOfCardsTitle = inputStream.readUTF();
				boolean setOfCardsIsDone = inputStream.readBoolean();
				long setOfCardsStudyTime = inputStream.readLong();
				//recover the card
				String cardSideOneTitle = inputStream.readUTF();
				String cardSideOneText = inputStream.readUTF();
				String cardSideTwoTitle = inputStream.readUTF();
				String cardSideTwoText = inputStream.readUTF();
				boolean cardIsDone = inputStream.readBoolean();
				String cardTip = inputStream.readUTF();
				// new set data 20071208
				int totalCardsDisplayed = inputStream.readInt();
				long lastTimeSetViewed = inputStream.readLong();
				long lastTimeSetMarkedDone = inputStream.readLong();
				int markedDoneSetCounter = inputStream.readInt();
				// new card data 20071208
				int cardViewedCounter = inputStream.readInt();
				int cardMarkedDoneCounter = inputStream.readInt();
				long cardLastTimeViewed = inputStream.readLong();
				long cardLastTimeMarkedDone = inputStream.readLong();
				
				//populate the set
				setToReturn.setTitle(setOfCardsTitle);				
				setToReturn.setDone(setOfCardsIsDone);				
				setToReturn.setTotalStudiedTimeInMiliseconds(setOfCardsStudyTime);
				// new set data 20071208
				setToReturn.setTotalNumberOfDisplayedCards(totalCardsDisplayed);
				setToReturn.setLastTimeViewed(lastTimeSetViewed);
				setToReturn.setLastTimeMarkedDone(lastTimeSetMarkedDone);
				setToReturn.setMarkedDoneCounter(markedDoneSetCounter);
				
				//create a card to the set				
				FlashCard card = new FlashCard();
				card.setSideOneTitle(cardSideOneTitle);
				card.setSideOne(cardSideOneText);
				card.setSideTwoTitle(cardSideTwoTitle);
				card.setSideTwo(cardSideTwoText);
				card.setDone(cardIsDone);
				card.setTip(cardTip);
				// new card data 20071208
				card.setViewedCounter(cardViewedCounter);
				card.setMarkedDoneCounter(cardMarkedDoneCounter);
				card.setLastTimeViewed(cardLastTimeViewed);
				card.setLastTimeMarkedDone(cardLastTimeMarkedDone);				
				
				//Add the card to the Vector
				cards.addElement(card);
		    }
		    //add the vector of cards to the set
		    if(setToReturn != null) {
		    	setToReturn.setFlashCards(cards);	
		    }		    
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
	public void saveState(SetOfCards setOfCards) throws IOException, RecordStoreNotOpenException, RecordStoreFullException, RecordStoreException {

//		int recId; //holds the key for the record. Not used.
		
		//get the data from the SetOfCards into an byte array
		//Generic data shared between all the cards 
		// (belongs to the setOfCards)		
		String title = setOfCards.getTitle();
		boolean setIsDone = setOfCards.isDone();
		long totalTime = setOfCards.getTotalStudiedTimeInMiliseconds();
		int totalOfDisplayedCards = setOfCards.getTotalNumberOfDisplayedCards();
		long setLastTimeViewed = setOfCards.getLastTimeViewed();
		long setLastTimeMarkedDone = setOfCards.getLastTimeMarkedDone();
		int setMarkedDoneCounter = setOfCards.getMarkedDoneCounter();
		Vector cards = setOfCards.getFlashCards();
		//get each card information and save to the record
		int size = cards.size();
		for(int i =0; i<size; i++) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			FlashCard card = (FlashCard)cards.elementAt(i);
			String side1text = card.getSideOne();
			String side1title = card.getSideOneTitle();
			String side2text = card.getSideTwo();
			String side2title = card.getSideTwoTitle();
			String tip = card.getTip();
			boolean cardIsDone = card.isDone();
			int cardViewedCounter = card.getViewedCounter();
			int cardMarkedDoneCounter = card.getMarkedDoneCounter();
			long cardLastTimeViewed = card.getLastTimeViewed();
			long cardLastTimeMarkedDone = card.getLastTimeMarkedDone();
			outputStream.writeUTF(title);
			outputStream.writeBoolean(setIsDone);
			outputStream.writeLong(totalTime);
			outputStream.writeUTF(side1title);
			outputStream.writeUTF(side1text);
			outputStream.writeUTF(side2title);
			outputStream.writeUTF(side2text);
			outputStream.writeBoolean(cardIsDone);
			outputStream.writeUTF(tip);
			// new set data
			outputStream.writeInt(totalOfDisplayedCards);
			outputStream.writeLong(setLastTimeViewed);
			outputStream.writeLong(setLastTimeMarkedDone);
			outputStream.writeInt(setMarkedDoneCounter);
			// new card data
			outputStream.writeInt(cardViewedCounter);
			outputStream.writeInt(cardMarkedDoneCounter);
			outputStream.writeLong(cardLastTimeViewed);
			outputStream.writeLong(cardLastTimeMarkedDone);
			
			// Extract the byte array
			byte[] b = baos.toByteArray();
			//write a record to the record store
			int recordLength = b.length;
			recordStore.addRecord(b, 0, recordLength);
		}   
	}
}
