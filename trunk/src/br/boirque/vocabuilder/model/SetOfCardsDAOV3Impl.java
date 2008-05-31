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
public class SetOfCardsDAOV3Impl extends SetOfCardsDAOAbst  {
	private RecordStore recordStore;

	/**
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 *             Initialize the RecordStore TODO: it is probably a good idea
	 *             to generate the store in a factory that takes care of the
	 *             maximum size
	 */
	public SetOfCardsDAOV3Impl(String storeName) throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException {
		super();
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance(storeName);
	}

	/* (non-Javadoc)
	 * @see br.boirque.vocabuilder.model.SetOfCardsDAOIF#addCard(br.boirque.vocabuilder.model.FlashCard)
	 */
	private FlashCard addCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {

		byte[] b = cardToByteArray(card);
		// write a record to the record store
		int recordLength = b.length;
		int cardId = recordStore.addRecord(b, 0, recordLength);
		card.setCardId(cardId);
		return card;
	}

	/* (non-Javadoc)
	 * @see br.boirque.vocabuilder.model.SetOfCardsDAOIF#loadCard(int)
	 */
	public FlashCard loadCard(int recordId) throws IOException,
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
	 * Read back the data from the record store. 
	 * It reads from v3 file format
	 * 
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 *             TODO: Try to recover from some of the exceptions
	 */
	public SetOfCards loadSetOfCards() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = null;
		int numRecords = recordStore.getNumRecords();
		// create a set and loads it's meta data
		if (numRecords > METADATARECORD) {
			setToReturn = loadSetMetadata(METADATARECORD);
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
	 * Saves a whole SetOfCards to persistence <br>
	 * This is a slow operation, use with care. <br>
	 * Implements the v3 file format: <br>
	 * 1st record = file format version <br>
	 * number 2nd record = set meta data <br>
	 * 3rd record on = card data, one card per record <br>
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
	public SetOfCards saveSetOfCards(SetOfCards setOfCards)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			IOException, RecordStoreException {
		this.addFileFormatVersionNumber(THRIRDYFILEFORMAT);
		setOfCards = this.addSetMetadata(setOfCards);

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

}
