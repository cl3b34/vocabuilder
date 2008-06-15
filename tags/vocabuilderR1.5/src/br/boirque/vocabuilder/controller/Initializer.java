package br.boirque.vocabuilder.controller;

import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsLoader;

/**
 * Takes care of all the initialization tasks
 * 
 * @author cleber.goncalves
 * 
 */
public class Initializer {

	public SetOfCards initializeApp() {
		// first try to load a partially studied set from a recordStore

		SetOfCards soc = this.loadState();
		// TODO: the returned set might be marked as 'done'
		// we should ask the user what to do...
		// either try to load another set from other media and
		// replace this or start studying the set again
		// for now I just load another set. This code should reside in
		// the UI, not here (soc.isDone check)
		if (soc == null || soc.isDone()) {
			// Load the set from any available media (defined in the loader)
			SetOfCardsLoader socl = new SetOfCardsLoader();
			try {
				soc = socl.loadSet();
			} catch (IOException e) {
				// TODO should send a message to the UI
				e.printStackTrace();
			}
			// save the set to a local recordstore
			// the return value of this method is ignored
			// since it is only used for performance at this point
			// this.saveState(soc); //removed so the startup is faster
		}
		return soc;
	}

	/**
	 * Save the current state of the set being studied to persistent storage
	 * 
	 * Use the newest file format
	 * 
	 * @param soc
	 * @return true if successfully saved the current state
	 */
	public boolean saveState(SetOfCards soc) {
		boolean savedSuccesfully = false;
		try {
			SetOfCardsDAO socDao = new SetOfCardsDAO();
			// removes the previous recordstore
			socDao.resetState();
			// create a new one with the current state
			socDao.saveSetOfCardsV4(soc);
			savedSuccesfully = true;
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return savedSuccesfully;
	}

	/**
	 * Load the state of the set being currently studied from persistent
	 * storage. It tries to find out what is the file format version by reading
	 * the version from file, if available.
	 * 
	 * @return the currently studied set or null if a error occurred
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 * @throws IOException
	 */
	public SetOfCards loadState() {
		SetOfCards soc = null;
		try {
			if (this.getRecordCount() > 0) {
				SetOfCardsDAO socDao = new SetOfCardsDAO();
				// file format version
				int fileFormatVersion = socDao
						.loadFileFormatVersionNumber(SetOfCardsDAO.FILEFORMATVERSIONRECORD);
				if (fileFormatVersion == SetOfCardsDAO.FOURTHFILEFORMAT) {
					return socDao.loadSetOfCardsV4();
				} else if (fileFormatVersion == SetOfCardsDAO.THRIRDYFILEFORMAT) {
					return socDao.loadSetOfCardsV3();
				} else {
					// old file format, file format version was not found
					return socDao.loadSetOfCardsV2();
				}
			}
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return soc;
	}

	/**
	 * reset the state of the set being studied so we start fresh.
	 */
	public void resetState() {
		try {
			SetOfCardsDAO socDao = new SetOfCardsDAO();
			socDao.resetState();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * return the record count or -1 if an error occurs
	 */
	public int getRecordCount() {
		try {
			SetOfCardsDAO socDao = new SetOfCardsDAO();
			return socDao.getRecordCount();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}
