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
 * @author cleber.goncalves Encapsulates the persistence of a set of cards
 */
public class SetOfCardsDAO {
	private RecordStore recordStore;
	// The first file format was not publicly released
	// The second does not have the format number in the record
	// third adds file format and separes card data from set meta data
	// the fourth file format has all the cards in a single record
	public static final int THRIRDYFILEFORMAT = 3;
	public static final int FOURTHFILEFORMAT = 4;

	public static final int FILEFORMATVERSIONRECORD = 1;
	public static final int METADATARECORD = 2;
	public static final int FIRSTCARDRECORD = 3;

	/**
	 * @param recordStore
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 *             Initialize the RecordStore TODO: it is probably a good idea
	 *             to generate the store in a factory that takes care of the
	 *             maximum size
	 */
	public SetOfCardsDAO() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException {
		super();
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance();
	}

	/**
	 * Removes the current record store and creates a new
	 * 
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 */
	public void resetState() throws RecordStoreNotFoundException,
			RecordStoreException {
		String recordStoreName = recordStore.getName();
		this.recordStore.closeRecordStore();
		// TODO - This reference to the opencount variable must be removed
		// opencount should be private
		RecordStoreFactory.openCount--;
		RecordStore.deleteRecordStore(recordStoreName);
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance();
	}

	/**
	 * Returns the amount of records currently in the store
	 * 
	 * @return amount of records in the store
	 * @throws RecordStoreNotOpenException
	 */
	public int getRecordCount() throws RecordStoreNotOpenException {
		return recordStore.getNumRecords();
	}

	/**
	 * reads a card identified by ID from RMS
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 * @param inputStream -
	 *            The stream from which to read the card
	 */
	public FlashCard loadCard(int recordId, DataInputStream inputStream)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {

		// file format version
		int fileFormatVersion = loadFileFormatVersionNumber(FILEFORMATVERSIONRECORD);

		if (fileFormatVersion == THRIRDYFILEFORMAT) {
			// old file format, file format version was not found
			return loadCardV3(recordId);
		} else if (fileFormatVersion == FOURTHFILEFORMAT) {
			return loadCardV4(inputStream);
		} else {
			return loadCardV2(recordId);
		}
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
	 * are mixed with 'uninteresting' ones.
	 * 
	 */
	public FlashCard loadCardV2(int recordId) throws IOException,
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

	/**
	 * reads a card identified by ID from RMS
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 */
	public FlashCard loadCardV3(int recordId) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		// Create a input stream for the cards, one record at a time
		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(recordId));
		DataInputStream inputStream = new DataInputStream(bais);

		// recover the card
		String cardSideOneTitle = inputStream.readUTF();
		String cardSideOneText = inputStream.readUTF();
		String cardSideTwoTitle = inputStream.readUTF();
		String cardSideTwoText = inputStream.readUTF();
		boolean cardIsDone = inputStream.readBoolean();
		String cardTip = inputStream.readUTF();
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

	/**
	 * reads a card from RMS. All cards are on the same record
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 */
	public FlashCard loadCardV4(DataInputStream inputStream)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {

		// recover the card
		String cardSideOneTitle = inputStream.readUTF();
		String cardSideOneText = inputStream.readUTF();
		String cardSideTwoTitle = inputStream.readUTF();
		String cardSideTwoText = inputStream.readUTF();
		boolean cardIsDone = inputStream.readBoolean();
		String cardTip = inputStream.readUTF();
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

		System.gc();
		return card;
	}

	/**
	 * adds a card to RMS
	 * 
	 * @param card -
	 *            the card to be added to RMS
	 * 
	 */
	public FlashCard addCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		byte[] b = cardToByteArray(card);
		// write a record to the record store
		int recordLength = b.length;
		int cardId = recordStore.addRecord(b, 0, recordLength);
		card.setCardId(cardId);
		return card;
	}

	/**
	 * adds a card to the outputStream
	 * 
	 * @param card -
	 *            the card to be added to the outputStream
	 */
	public DataOutputStream addCardV4(FlashCard card,
			DataOutputStream outputStream) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

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
		outputStream.writeInt(cardViewedCounter);
		outputStream.writeInt(cardMarkedDoneCounter);
		outputStream.writeLong(cardLastTimeViewed);
		outputStream.writeLong(cardLastTimeMarkedDone);
		return outputStream;
	}

	/**
	 * updates a card identified by ID to RMS
	 * 
	 * @param card -
	 *            The card to be updated
	 */
	public void updateCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		int recordId = card.getCardId();
		byte[] b = cardToByteArray(card);
		// write a record to the record store
		int recordLength = b.length;
		recordStore.setRecord(recordId, b, 0, recordLength);
	}

	/**
	 * reads a the file format version from RMS
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 * 
	 */
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

	/**
	 * saves the file format version number to RMS
	 * 
	 * @param fileVersionNumber -
	 *            The new file version number
	 * @return the recordId where the version number was saved
	 */
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

	/**
	 * Add the set meta data to RMS
	 * 
	 * @param recordID -
	 *            The set to be added
	 */
	public SetOfCards addSetMetadataV4(SetOfCards setOfCards)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		String title = setOfCards.getTitle();
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
	 * updates the set meta data to RMS
	 * 
	 * @param recordID -
	 *            The id of the record to be updated
	 */
	public void updateSetMetadataV4(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		String title = setOfCards.getTitle();
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
	public SetOfCards loadSetMetadataV4(int recordId) throws IOException,
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
		setToReturn.setTitle(setOfCardsTitle);
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

	/**
	 * reads the meta data for the SetOfCards from the record identified by
	 * recordID from RMS
	 * 
	 * This is the old (second) file format
	 * 
	 * I'm reading data from the Card that is not important to the set in here.
	 * This is because the record has a fixed format and the interesting fields
	 * are mixed with 'uninteresting' ones.
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 * 
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public SetOfCards loadSetMetadataV2(int recordId) throws IOException,
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
		setToReturn.setTitle(setOfCardsTitle);
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
	 * @deprecated use one of the loadSetOfCards methods
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 *             TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadSetOfCardsV2() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = loadSetMetadataV4(1);
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
	 * Read back the data from the record store. It reads from v3 file format
	 * 
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 *             TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadSetOfCardsV3() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = null;
		int numRecords = recordStore.getNumRecords();
		// create a set and loads it's meta data
		if (numRecords > METADATARECORD) {
			setToReturn = loadSetMetadataV4(METADATARECORD);
			Vector cards = new Vector();
			// The cards start at the 3rd record
			for (int i = 3; i <= numRecords; i++) {
				// FlashCard card = loadCard(i, null);

				// Duplicated code. Should use the above method.
				// This is here only due to performance issues using loadCard
				// methods (see performance in the wiki)
				//				
				// Create a input stream for the cards, one record at a time
				ByteArrayInputStream bais = new ByteArrayInputStream(
						recordStore.getRecord(i));
				DataInputStream inputStream = new DataInputStream(bais);

				// recover the card
				String cardSideOneTitle = inputStream.readUTF();
				String cardSideOneText = inputStream.readUTF();
				String cardSideTwoTitle = inputStream.readUTF();
				String cardSideTwoText = inputStream.readUTF();
				boolean cardIsDone = inputStream.readBoolean();
				String cardTip = inputStream.readUTF();
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
		}
		return setToReturn;
	}

	/**
	 * Read back the data from the record store. It reads from v4 file format
	 * 
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 *             TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadSetOfCardsV4() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = null;
		int numRecords = recordStore.getNumRecords();
		// create a set and loads it's meta data
		if (numRecords > METADATARECORD) {
			setToReturn = loadSetMetadataV4(METADATARECORD);
			// The amount of cards come from meta data
			int cardCount = setToReturn.getTotalNumberOfCards();
			Vector cards = new Vector();
			ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
					.getRecord(FIRSTCARDRECORD));
			DataInputStream inputStream = new DataInputStream(bais);
			// The cards start at the 3rd record
			for (int i = FIRSTCARDRECORD; i <= cardCount + 2; i++) {

				// FlashCard card = loadCardV4(inputStream);
				// Duplicated code. Should use the above method.
				// This is here only due to performance issues using loadCard
				// methods (see performance in the wiki)
				// recover the card
				String cardSideOneTitle = inputStream.readUTF();
				String cardSideOneText = inputStream.readUTF();
				String cardSideTwoTitle = inputStream.readUTF();
				String cardSideTwoText = inputStream.readUTF();
				boolean cardIsDone = inputStream.readBoolean();
				String cardTip = inputStream.readUTF();
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
				// end of duplicated code

				cards.addElement(card);
			}
			setToReturn.setFlashCards(cards);
		}
		return setToReturn;
	}

	/**
	 * Save the current state to persistence (file on the device)
	 * 
	 * @deprecated Old v2 File format. Use saveSaveSetOfCards(SetOfCards
	 *             setOfCards)instead
	 * @param setOfCards
	 * @throws IOException
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreFullException
	 * @throws RecordStoreException
	 * 
	 */
	public void saveSetOfCardsV2(SetOfCards setOfCards) throws IOException,
			RecordStoreNotOpenException, RecordStoreFullException,
			RecordStoreException {

		// get the data from the SetOfCards into an byte array
		// Generic data shared between all the cards
		// (belongs to the setOfCards)
		String title = setOfCards.getTitle();
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
	}

	/**
	 * Saves a whole SetOfCards to persistence <br>
	 * This is a slow operation, use with care. <br>
	 * Implements the v3 file format: <br>
	 * 1st record = file format version <br>
	 * number 2nd record = set meta data <br>
	 * 3rd record on = card data <br>
	 * <br>
	 * 
	 * @param setOfCards
	 *            the set to save
	 * @return the set just saved, with the setId and cardIds
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public SetOfCards saveSetOfCardsV3(SetOfCards setOfCards)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			IOException, RecordStoreException {
		this.addFileFormatVersionNumber(THRIRDYFILEFORMAT);
		setOfCards = this.addSetMetadataV4(setOfCards);

		Vector cards = setOfCards.getFlashCards();
		// get each card and save to the record store
		int size = cards.size();
		for (int i = 0; i < size; i++) {
			FlashCard card = (FlashCard) cards.elementAt(i);
			card = this.addCard(card);
			cards.setElementAt(card, i);
		}
		setOfCards.setFlashCards(cards);
		return setOfCards;
	}

	/**
	 * Saves a whole SetOfCards to persistence <br>
	 * This is a slow operation, use with care. <br>
	 * Implements the v4 file format: <br>
	 * 1st record = file format version <br>
	 * number 2nd record = set meta data <br>
	 * 3rd record = all cards data <br>
	 * <br>
	 * 
	 * @param setOfCards
	 *            the set to save
	 * @return the set just saved, with the setId and cardIds
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public SetOfCards saveSetOfCardsV4(SetOfCards setOfCards)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			IOException, RecordStoreException {
		this.addFileFormatVersionNumber(FOURTHFILEFORMAT);
		setOfCards = this.addSetMetadataV4(setOfCards);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		Vector cards = setOfCards.getFlashCards();
		// get each card and save to the record store
		int size = cards.size();
		for (int i = 0; i < size; i++) {
			FlashCard card = (FlashCard) cards.elementAt(i);
			outputStream = this.addCardV4(card, outputStream);
		}
		byte[] b = baos.toByteArray();
		int recordLength = b.length;
		recordStore.addRecord(b, 0, recordLength);
		setOfCards.setFlashCards(cards);
		return setOfCards;
	}

	private byte[] cardToByteArray(FlashCard card) throws IOException {
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

}