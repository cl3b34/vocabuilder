package br.boirque.vocabuilder.dao;

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
	private String storeName = "currentSet";
	private static RecordStoreFactory factory = null;
	
	private RecordStoreFactory() {
		
	}
	
	public static RecordStoreFactory getFactory() {
		if(factory == null) {
			factory = new RecordStoreFactory();			
		}
		return factory;
	}
	
	public RecordStore getStoreInstance() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		if(recordStore == null) {
			recordStore = RecordStore.openRecordStore(storeName , true);
		}
		return recordStore;
	}
}
