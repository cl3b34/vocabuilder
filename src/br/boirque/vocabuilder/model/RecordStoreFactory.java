package br.boirque.vocabuilder.model;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * @author cleber.goncalves
 * Implements the singletown pattern to generate RecordStores
 * in order to obtain better control on which stores are open
 */
public class RecordStoreFactory {
	private static RecordStore recordStore;
	public static int openCount = 0;
	private String storeName = "currentSet";
	private static RecordStoreFactory factory = null;
	
	private RecordStoreFactory() {
		
	}
	
	/**
	 * Generates a RecordStore factory by calling the private constructor
	 * @return a factory for the record store
	 */
	public static RecordStoreFactory getFactory() {
		if(factory == null) {
			factory = new RecordStoreFactory();			
		}
		return factory;
	}
	
	/**
	 * Gets the current open instance of the recordStore or opens one
	 * @return The RecordStore currently being used
	 * @throws RecordStoreFullException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreException
	 */
	public RecordStore getStoreInstance() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		if(recordStore == null || openCount == 0) {
			recordStore = RecordStore.openRecordStore(storeName , true);
			openCount++;
		}
		return recordStore;
	}
}
