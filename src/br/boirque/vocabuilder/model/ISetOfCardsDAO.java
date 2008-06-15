package br.boirque.vocabuilder.model;

import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * Encapsulates the persistence of a set of cards
 * This interface defines the common functionality
 * that all implementing classes must have
 * @author cleber.goncalves
 *
 */
public interface ISetOfCardsDAO{

	/** The first file format was not publicly released
	 *  The second does not have the format number in the record
	 *  third adds file format and separates card data from set meta data
	 *  the fourth file format has all the cards in a single record
	 */	 
	public static final int THRIRDYFILEFORMAT = 3;
	public static final int FOURTHFILEFORMAT = 4;
	
	// Record that contains the file format version number
	public static final int FILEFORMATVERSIONRECORD = 1;
	// Record that contains the meta data
	public static final int METADATARECORD = 2;
	//record that contains the first card record
	public static final int FIRSTCARDRECORD = 3;

	/**
	 * updates a card identified by ID to RMS
	 * @param card - Card to be updated
	 */
	public abstract void updateCard(FlashCard card) throws IOException,
			RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException;

	/**
	 * reads a card identified by recordID from RMS
	 * @param recordId -  The id of the record to be recovered
	 * @param inputStream - The stream from which to load the card. Null if none.
	 * @return
	 * @throws IOException
	 * @throws RecordStoreNotOpenException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreException
	 */
	public abstract FlashCard loadCard(int recordId)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * saves the file format version number to RMS
	 * @param fileVersionNumber - The file format version number
	 * @return the recordId where the version number was saved
	 */
	public abstract int addFileFormatVersionNumber(int fileVersionNumber)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * reads a the file format version from RMS
	 * @param recordID - Id of the record to be recovered
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public abstract int loadFileFormatVersionNumber(int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			RecordStoreException;

	/**
	 * Add the set meta data to RMS
	 * @param recordID - The set whose metadata is to be added
	 */
	public abstract SetOfCards addSetMetadata(SetOfCards setOfCards)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * updates the set meta data to RMS
	 * @param recordID - Id of the record to be updated
	 */
	public abstract void updateSetMetadata(SetOfCards setOfCards)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * reads the meta data for the SetOfCards from the record identified by
	 * recordID from RMS
	 * @param recordID - Id of the record to be recovered
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public abstract SetOfCards loadSetMetadata(int recordId)
			throws IOException, RecordStoreNotOpenException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * Saves a whole SetOfCards to persistence <br>
	 * This is a potentially slow operation, use with care. <br>
	 * @param setOfCards - set to save
	 * @return the set just saved, with the updated setId and cardIds
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public abstract SetOfCards saveSetOfCards(SetOfCards setOfCards)
			throws RecordStoreNotOpenException, InvalidRecordIDException,
			IOException, RecordStoreException;

	/**
	 * Read back the data from the record store.
	 * @return a SetOfCards read from the store or null if the store was empty
	 * @throws IOException
	 * @throws RecordStoreException
	 * @throws InvalidRecordIDException
	 */
	public abstract SetOfCards loadSetOfCards() throws IOException,
			InvalidRecordIDException, RecordStoreException;

	/**
	 * Removes the current record store and creates a new
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 */
	public abstract void resetSetState() throws RecordStoreNotFoundException,
			RecordStoreException;

	/**
	 * Retrieves the amount of cards currently in the store
	 * @return amount of cards in the store
	 * @throws InvalidRecordIDException
	 * @throws IOException
	 * @throws RecordStoreException
	 */
	public abstract int getCardCount() throws InvalidRecordIDException, IOException, RecordStoreException;
	
	/**
	 * Returns the amount of records currently in the store
	 * @return amount of records in the store
	 * @throws RecordStoreNotOpenException
	 */
	public abstract int getRecordCount() throws RecordStoreNotOpenException;
	
	/**
	 * Permanently delete a SetOfCards from RMS
	 * @param setName
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 */
	public void deleteSetOfCards(String setName) throws RecordStoreNotFoundException, RecordStoreException;
	
}