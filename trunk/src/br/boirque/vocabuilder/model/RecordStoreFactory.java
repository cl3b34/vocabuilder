package br.boirque.vocabuilder.model;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * @author cleber.goncalves Implements the singletown pattern to generate
 *         RecordStores in order to obtain better control on which stores are
 *         open
 */
public class RecordStoreFactory {
	private static RecordStore recordStore;
	private static int openCount = 0;
	// private String storeName = "currentSet";
	private static RecordStoreFactory factory = null;

	private RecordStoreFactory() {

	}

	/**
	 * Generates a RecordStore factory by calling the private constructor
	 * 
	 * @return a factory for the record store
	 */
	public static RecordStoreFactory getFactory() {
		if (factory == null) {
			factory = new RecordStoreFactory();
		}
		return factory;
	}

	/**
	 * Gets the current open instance of the recordStore or opens (creates) one
	 * 
	 * @param storeName
	 * @return The RecordStore currently being used
	 * @throws RecordStoreFullException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 * 
	 * TODO: it is probably a good idea to take care of the maximum size
	 */
	public RecordStore getStoreInstance(String storeName)
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException {

		if (RecordStoreFactory.recordStore == null || openCount == 0) {
			recordStore = RecordStore.openRecordStore(storeName, true);
			openCount++;
			return recordStore;
		}

			if (storeName.equals(RecordStoreFactory.recordStore.getName())) {
				return recordStore;
			}

		// we want to open another store
		this.closeStoreInstance(RecordStoreFactory.recordStore.getName());
		recordStore = RecordStore.openRecordStore(storeName, true);
		openCount++;
		return recordStore;
	}

	public void closeStoreInstance(String storeName) {
		while (openCount > 0) {
			try {
				RecordStoreFactory.recordStore.closeRecordStore();
				openCount--;
			} catch (RecordStoreNotOpenException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}

	}
}
