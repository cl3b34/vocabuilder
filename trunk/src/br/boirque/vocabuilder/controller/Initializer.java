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

	// In the future, this guy should check if there is any partially saved
	// state available
	// (record stores in the hand set)
	public SetOfCards initializeApp() {
		// first try to load a partially studied set from a recordStore

		// TODO: remove this. Hack to remove the RS so we can
		// start afresh. Should do better RS management
		if(this.getRecordCount() > 150) {
			this.resetState();
		}

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
			socDao.saveState(soc);
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
	 * storage.
	 * 
	 * @return the currently studied set or null if a error occurred
	 */
	public SetOfCards loadState() {
		SetOfCards soc = null;
		try {
			SetOfCardsDAO socDao = new SetOfCardsDAO();
			soc = socDao.loadState();
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
