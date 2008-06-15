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
 * @deprecated substituted by V3 or V4 implementations
 * @author cleber.goncalves Encapsulates the persistence of a set of cards
 */
public class SetOfCardsDAOV2Impl extends SetOfCardsDAO {
	private RecordStore recordStore;

	/**
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 *             Initialize the RecordStore TODO: it is probably a good idea
	 *             to generate the store in a factory that takes care of the
	 *             maximum size
	 */
	public SetOfCardsDAOV2Impl(String storeName)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException {
		super();
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance(storeName);
	}

	/**
	 * reads a card identified by ID from RMS *
	 * 
	 * This is the old file format used in v. 1.04 of the application (second
	 * file format)
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 * 
	 * I'm reading data from the set of cards that is not used anywhere in here.
	 * This is because the record has a fixed format and the interesting fields
	 * are mixed with 'uninteresting' ones. This was solved in later versions.
	 * 
	 */
	public FlashCard loadCard(int recordId) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		// Create a input stream for the cards, one record at a time
		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(recordId));
		DataInputStream inputStream = new DataInputStream(bais);

		// Just skip data from the set of cards
		inputStream.readUTF();
		inputStream.readBoolean();
		inputStream.readLong();
		// recover the card
		String cardSideOneTitle = inputStream.readUTF();
		String cardSideOneText = inputStream.readUTF();
		String cardSideTwoTitle = inputStream.readUTF();
		String cardSideTwoText = inputStream.readUTF();
		boolean cardIsDone = inputStream.readBoolean();
		String cardTip = inputStream.readUTF();
		// more set data, skip
		inputStream.readInt();
		inputStream.readLong();
		inputStream.readLong();
		inputStream.readInt();
		// new card data 20071208
		int cardViewedCounter = inputStream.readInt();
		int cardMarkedDoneCounter = inputStream.readInt();
		long cardLastTimeViewed = inputStream.readLong();
		long cardLastTimeMarkedDone = inputStream.readLong();

		// create a card from the data read
		FlashCard card = new FlashCard();
		card.setSideOneTitle(cardSideOneTitle);
		card.setSideOne(cardSideOneText);
		card.setSideTwoTitle(cardSideTwoTitle);
		card.setSideTwo(cardSideTwoText);
		card.setDone(cardIsDone);
		card.setTip(cardTip);
		card.setViewedCounter(cardViewedCounter);
		card.setMarkedDoneCounter(cardMarkedDoneCounter);
		card.setLastTimeViewed(cardLastTimeViewed);
		card.setLastTimeMarkedDone(cardLastTimeMarkedDone);
		card.setCardId(recordId);

		System.gc();
		return card;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.boirque.vocabuilder.model.ISetOfCardsDAO#loadSetMetadataV2(int)
	 */
	public SetOfCards loadSetMetadata(int recordId) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		SetOfCards setToReturn = new SetOfCards();

		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(recordId));
		DataInputStream inputStream = new DataInputStream(bais);

		// Recover meta data from the set
		String setOfCardsTitle = inputStream.readUTF();
		boolean setOfCardsIsDone = inputStream.readBoolean();
		long setOfCardsStudyTime = inputStream.readLong();
		// skip card data
		inputStream.readUTF();
		inputStream.readUTF();
		inputStream.readUTF();
		inputStream.readUTF();
		inputStream.readBoolean();
		inputStream.readUTF();
		// new set data 20071208
		int totalCardsDisplayed = inputStream.readInt();
		long lastTimeSetViewed = inputStream.readLong();
		long lastTimeSetMarkedDone = inputStream.readLong();
		int markedDoneSetCounter = inputStream.readInt();
		// More card data, skip
		inputStream.readInt();
		inputStream.readInt();
		inputStream.readLong();
		inputStream.readLong();

		// populate the set
		setToReturn.setSetName(setOfCardsTitle);
		setToReturn.setDone(setOfCardsIsDone);
		setToReturn.setTotalStudiedTimeInMiliseconds(setOfCardsStudyTime);
		setToReturn.setTotalNumberOfDisplayedCards(totalCardsDisplayed);
		setToReturn.setLastTimeViewed(lastTimeSetViewed);
		setToReturn.setLastTimeMarkedDone(lastTimeSetMarkedDone);
		setToReturn.setMarkedDoneCounter(markedDoneSetCounter);

		System.gc();
		return setToReturn;
	}

	/**
	 * Read back the data from the record store. It reads from the v2 format
	 * 
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 *             TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadSetOfCards() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = loadSetMetadata(1);
		int numRecords = recordStore.getNumRecords();
		Vector cards = new Vector();
		// Old format, the cards start at the 1st record
		for (int i = 1; i <= numRecords; i++) {

			// FlashCard card = loadCardV2(i);

			// Duplicated code. Should use the above method.
			// This is here only due to performance issues using loadCard
			// methods (see performance in the wiki)
			ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
					.getRecord(i));
			DataInputStream inputStream = new DataInputStream(bais);

			// Just skip data from the set of cards
			inputStream.readUTF();
			inputStream.readBoolean();
			inputStream.readLong();
			// recover the card
			String cardSideOneTitle = inputStream.readUTF();
			String cardSideOneText = inputStream.readUTF();
			String cardSideTwoTitle = inputStream.readUTF();
			String cardSideTwoText = inputStream.readUTF();
			boolean cardIsDone = inputStream.readBoolean();
			String cardTip = inputStream.readUTF();
			// more set data, skip
			inputStream.readInt();
			inputStream.readLong();
			inputStream.readLong();
			inputStream.readInt();
			// new card data 20071208
			int cardViewedCounter = inputStream.readInt();
			int cardMarkedDoneCounter = inputStream.readInt();
			long cardLastTimeViewed = inputStream.readLong();
			long cardLastTimeMarkedDone = inputStream.readLong();

			// create a card from the data read
			FlashCard card = new FlashCard();
			card.setSideOneTitle(cardSideOneTitle);
			card.setSideOne(cardSideOneText);
			card.setSideTwoTitle(cardSideTwoTitle);
			card.setSideTwo(cardSideTwoText);
			card.setDone(cardIsDone);
			card.setTip(cardTip);
			card.setViewedCounter(cardViewedCounter);
			card.setMarkedDoneCounter(cardMarkedDoneCounter);
			card.setLastTimeViewed(cardLastTimeViewed);
			card.setLastTimeMarkedDone(cardLastTimeMarkedDone);
			card.setCardId(i);

			cards.addElement(card);
		}
		setToReturn.setFlashCards(cards);
		return setToReturn;
	}

	/**
	 * Save the current state to persistence (file on the device) Saves in V2
	 * format
	 * 
	 * @param setOfCards
	 * @throws IOException
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreFullException
	 * @throws RecordStoreException
	 * 
	 */
	public SetOfCards saveSetOfCards(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, RecordStoreFullException,
			RecordStoreException {

		// get the data from the SetOfCards into an byte array
		// Generic data shared between all the cards
		// (belongs to the setOfCards)
		String title = setOfCards.getSetName();
		boolean setIsDone = setOfCards.isDone();
		long totalTime = setOfCards.getTotalStudiedTimeInMiliseconds();
		int totalOfDisplayedCards = setOfCards.getTotalNumberOfDisplayedCards();
		long setLastTimeViewed = setOfCards.getLastTimeViewed();
		long setLastTimeMarkedDone = setOfCards.getLastTimeMarkedDone();
		int setMarkedDoneCounter = setOfCards.getMarkedDoneCounter();
		Vector cards = setOfCards.getFlashCards();
		// get each card information and save to the record
		int size = cards.size();
		for (int i = 0; i < size; i++) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputStream = new DataOutputStream(baos);
			FlashCard card = (FlashCard) cards.elementAt(i);
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
			// write a record to the record store
			int recordLength = b.length;
			recordStore.addRecord(b, 0, recordLength);
		}
		// this setOfCards was not updated. Not needed in this version
		return setOfCards;
	}

	/**
	 * The amount of cards in V2 format is the same as the amount of records
	 */
	public int getCardCount() throws InvalidRecordIDException, IOException,
			RecordStoreException {
		return super.getRecordCount();
	}

	// ******************************************************
	// Methods overidden for safety. There is no implementation
	// on this class.

	public SetOfCards addSetMetadata(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		// There is no implementation in this version.
		// The meta data is saved with card data
		return null;
	}

	public void updateSetMetadata(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		// There is no implementation in this version.
		// The meta data is saved with card data
	}

	public int addFileFormatVersionNumber(int fileVersionNumber)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		// we had no version number at this point
		return 2;
	}

	public int loadFileFormatVersionNumber(int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
		return 2;
	}
}
