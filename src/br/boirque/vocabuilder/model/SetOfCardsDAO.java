package br.boirque.vocabuilder.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * Abstract class that implements the common functionality shared between the
 * implementations of SetOfCardsDAO
 * 
 * @author cleber.goncalves
 * 
 */
public abstract class SetOfCardsDAO implements SetOfCardsDAOIF {

	protected RecordStore recordStore;

	public SetOfCardsDAO() {
		super();
	}

	public int addFileFormatVersionNumber(int fileVersionNumber)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		outputStream.writeInt(fileVersionNumber);

		// Extract the byte array
		byte[] b = baos.toByteArray();
		// write a record to the record store
		int recordLength = b.length;
		return recordStore.addRecord(b, 0, recordLength);
	}

	public int loadFileFormatVersionNumber(int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		// Create a input stream for the cards, one record at a time
		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(recordId));
		DataInputStream inputStream = new DataInputStream(bais);
		int fileFormatVersion = -1;
		try {
			fileFormatVersion = inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.gc();
		return fileFormatVersion;
	}

	public void resetSetState() throws RecordStoreNotFoundException,
			RecordStoreException {
		String recordStoreName = recordStore.getName();
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		factory.closeStoreInstance(recordStoreName);
		RecordStore.deleteRecordStore(recordStoreName);
		this.recordStore = factory.getStoreInstance(recordStoreName);
	}

	public static String[] getAvailableSets()
			throws RecordStoreNotOpenException {
		return RecordStore.listRecordStores();
	}

	public int getCardCount() throws InvalidRecordIDException, IOException,
			RecordStoreException {
		if (this.getRecordCount() > 0) {
			SetOfCards soc = this.loadSetMetadata(METADATARECORD);
			return soc.getTotalNumberOfCards();
		}
		return 0;
	}

	public int getRecordCount() throws RecordStoreNotOpenException {
		return recordStore.getNumRecords();
	}

	/**
	 * Converts a card into a byte array so it can be stored in RMS
	 * 
	 * @param card
	 *            Card to be converted
	 * @return the FlashCard converted into a byte array
	 * @throws IOException
	 */
	protected byte[] cardToByteArray(FlashCard card) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);
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
		outputStream.writeUTF(side1title);
		outputStream.writeUTF(side1text);
		outputStream.writeUTF(side2title);
		outputStream.writeUTF(side2text);
		outputStream.writeBoolean(cardIsDone);
		outputStream.writeUTF(tip);
		// new card data
		outputStream.writeInt(cardViewedCounter);
		outputStream.writeInt(cardMarkedDoneCounter);
		outputStream.writeLong(cardLastTimeViewed);
		outputStream.writeLong(cardLastTimeMarkedDone);

		// Extract the byte array
		return baos.toByteArray();
	}

	public SetOfCards addSetMetadata(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		String title = setOfCards.getSetName();
		boolean setIsDone = setOfCards.isDone();
		long totalTime = setOfCards.getTotalStudiedTimeInMiliseconds();
		int totalOfDisplayedCards = setOfCards.getTotalNumberOfDisplayedCards();
		long setLastTimeViewed = setOfCards.getLastTimeViewed();
		long setLastTimeMarkedDone = setOfCards.getLastTimeMarkedDone();
		int setMarkedDoneCounter = setOfCards.getMarkedDoneCounter();
		int setTotalOfCards = setOfCards.getTotalNumberOfCards();
		int setTotalOfCardsMarkedDone = setOfCards
				.getTotalNumberOfCardsMarkedDone();

		outputStream.writeUTF(title);
		outputStream.writeBoolean(setIsDone);
		outputStream.writeLong(totalTime);
		outputStream.writeInt(totalOfDisplayedCards);
		outputStream.writeLong(setLastTimeViewed);
		outputStream.writeLong(setLastTimeMarkedDone);
		outputStream.writeInt(setMarkedDoneCounter);
		outputStream.writeInt(setTotalOfCards);
		outputStream.writeInt(setTotalOfCardsMarkedDone);

		// Extract the byte array
		byte[] b = baos.toByteArray();
		// write a record to the record store
		int recordLength = b.length;
		int setId = recordStore.addRecord(b, 0, recordLength);
		setOfCards.setSetId(setId);
		return setOfCards;
	}

	/**
	 * reads the meta data for the SetOfCards from the record identified by
	 * recordID from RMS
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
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
		int totalCardsDisplayed = inputStream.readInt();
		long lastTimeSetViewed = inputStream.readLong();
		long lastTimeSetMarkedDone = inputStream.readLong();
		int markedDoneSetCounter = inputStream.readInt();
		int totalNumberOfCards = inputStream.readInt();
		int totalNumberOfCardsMarkedDone = inputStream.readInt();

		// populate the set
		setToReturn.setSetName(setOfCardsTitle);
		setToReturn.setDone(setOfCardsIsDone);
		setToReturn.setTotalStudiedTimeInMiliseconds(setOfCardsStudyTime);
		setToReturn.setTotalNumberOfDisplayedCards(totalCardsDisplayed);
		setToReturn.setLastTimeViewed(lastTimeSetViewed);
		setToReturn.setLastTimeMarkedDone(lastTimeSetMarkedDone);
		setToReturn.setMarkedDoneCounter(markedDoneSetCounter);
		setToReturn.setSetId(recordId);
		setToReturn.setTotalNumberOfCards(totalNumberOfCards);
		setToReturn
				.setTotalNumberOfCardsMarkedDone(totalNumberOfCardsMarkedDone);

		System.gc();
		return setToReturn;

	}

	public void updateSetMetadata(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		String title = setOfCards.getSetName();
		boolean setIsDone = setOfCards.isDone();
		long totalTime = setOfCards.getTotalStudiedTimeInMiliseconds();
		int totalOfDisplayedCards = setOfCards.getTotalNumberOfDisplayedCards();
		long setLastTimeViewed = setOfCards.getLastTimeViewed();
		long setLastTimeMarkedDone = setOfCards.getLastTimeMarkedDone();
		int setMarkedDoneCounter = setOfCards.getMarkedDoneCounter();
		int setTotalOfCards = setOfCards.getTotalNumberOfCards();
		int setTotalOfCardsMarkedDone = setOfCards
				.getTotalNumberOfCardsMarkedDone();

		outputStream.writeUTF(title);
		outputStream.writeBoolean(setIsDone);
		outputStream.writeLong(totalTime);
		outputStream.writeInt(totalOfDisplayedCards);
		outputStream.writeLong(setLastTimeViewed);
		outputStream.writeLong(setLastTimeMarkedDone);
		outputStream.writeInt(setMarkedDoneCounter);
		outputStream.writeInt(setTotalOfCards);
		outputStream.writeInt(setTotalOfCardsMarkedDone);

		// Extract the byte array
		byte[] b = baos.toByteArray();
		// write a record to the record store
		int setId = setOfCards.getSetId();
		int recordLength = b.length;
		recordStore.setRecord(setId, b, 0, recordLength);
	}

	public void updateCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		int recordId = card.getCardId();
		byte[] b = cardToByteArray(card);
		// write a record to the record store
		int recordLength = b.length;
		recordStore.setRecord(recordId, b, 0, recordLength);
	}

	public void deleteSetOfCards(String setName) throws RecordStoreNotFoundException, RecordStoreException {
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		factory.closeStoreInstance(setName);
		RecordStore.deleteRecordStore(setName);
	}
}