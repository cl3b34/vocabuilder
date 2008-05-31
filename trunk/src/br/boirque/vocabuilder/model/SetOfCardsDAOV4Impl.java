package br.boirque.vocabuilder.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * 
 * @author cleber.goncalves 
 */
public class SetOfCardsDAOV4Impl extends SetOfCardsDAOAbst{
	
	
	/**
	 * Initialize the RecordStore 
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 */
	public SetOfCardsDAOV4Impl(String storeName) throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException {
		super();
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		this.recordStore = factory.getStoreInstance(storeName);
	}

	/**
	 * adds a card to the outputStream 
	 * @param card - the card to be added to the outputStream
	 * @param outputStream - the stream to which to add the card
	 */
	protected DataOutputStream addCardV4(FlashCard card,
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
	 * Overridden since currently there is no need for an
	 * implementation of this method here.
	 * The implementation would need to load all the cards
	 * select the desired card and return it. 
	 */
	public FlashCard loadCard(int recordId)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException {
		return null;
	}

	/**
	 * reads a card from RMS. All cards are on the same record
	 * 
	 * @param recordID -
	 *            The id of the record to be recovered
	 */
	protected FlashCard loadCardV4(DataInputStream inputStream)
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
	 * Overridden since there is no need for an implementation
	 * at the moment. The update card method in this class
	 * would have to load
	 * the whole set, change the needed card and save the
	 * whole set back 
	 */
	public void updateCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException {
	}

	/* (non-Javadoc)
	 * @see br.boirque.vocabuilder.model.SetOfCardsDAOIF#saveSetOfCardsV4(br.boirque.vocabuilder.model.SetOfCards)
	 */
	public SetOfCards saveSetOfCards(SetOfCards setOfCards)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			IOException, RecordStoreException {
		this.addFileFormatVersionNumber(FOURTHFILEFORMAT);
		setOfCards = this.addSetMetadata(setOfCards);
	
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

	/* (non-Javadoc)
	 * @see br.boirque.vocabuilder.model.SetOfCardsDAOIF#loadSetOfCards()
	 */
	public SetOfCards loadSetOfCards() throws IOException,
			InvalidRecordIDException, RecordStoreException {
		SetOfCards setToReturn = null;
		int numRecords = recordStore.getNumRecords();
		// create a set and loads it's meta data
		if (numRecords > METADATARECORD) {
			setToReturn = loadSetMetadata(METADATARECORD);
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
				// methods (see document performance in the project's wiki)
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

}
