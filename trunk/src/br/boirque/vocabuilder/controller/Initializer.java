package br.boirque.vocabuilder.controller;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.PropertiesLoader;
import br.boirque.vocabuilder.model.Property;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsDAOIF;
import br.boirque.vocabuilder.model.SetOfCardsDAOV4Impl;
import br.boirque.vocabuilder.model.SetOfCardsLoader;

/**
 * Takes care of all the initialization tasks
 * 
 * @author cleber.goncalves
 * 
 */
public class Initializer {

	private static final String DEFAULTSET = "defaultset";

	public SetOfCards initializeApp(String setToLoad) {
		
//		String[] availableSets = null;
//		try {
//			availableSets = SetOfCardsDAO.getAvailableSets();
//		} catch (RecordStoreNotOpenException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		SetOfCards soc = null;
//		if (availableSets != null && availableSets.length > 0) {
//			soc = this.loadState(availableSets[0]);
//		}
//		//TODO: This code should reside in
//		// the UI, not here (soc.isDone check)
//		if (soc == null || soc.isDone()) {
			// Load the set from any available media (defined in the loader)
			SetOfCardsLoader socl = new SetOfCardsLoader();
			try {
				soc = socl.loadSet(setToLoad);
			} catch (IOException e) {
				// TODO should send a message to the UI
				e.printStackTrace();
			}
			// save the set to a local recordstore
			// the return value of this method is ignored
			// since it is only used for performance at this point
			// this.saveState(soc); //removed so the startup is faster
//		}
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
			SetOfCardsDAOIF socDao = new SetOfCardsDAOV4Impl(soc.getSetName());
			// removes the previous recordstore
			socDao.resetState();
			// create a new one with the current state
			socDao.saveSetOfCards(soc);
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
	public SetOfCards loadState(String setName) {
		SetOfCards soc = null;
		try {
			if (this.getCardCount(setName) > 0) {
				SetOfCardsDAO socDao = new SetOfCardsDAOV4Impl(setName);
				return socDao.loadSetOfCards();
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
	public void resetState(String setName) {
		try {
			SetOfCardsDAOIF socDao = new SetOfCardsDAOV4Impl(setName);
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
	public int getCardCount(String setName) {
		try {
			SetOfCardsDAOIF socDao = new SetOfCardsDAOV4Impl(setName);
			return socDao.getCardCount();
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
		return -1;
	}
	
	public static long updateSessionStudyTime(long sessionStudyTime,
			long lastActivityTime, long maxIdleTime) {
		// this is not rocket science! if the user is
		// idle more than 30s we ignore the update as the user
		// was talking to someone :)
		long currentTime = System.currentTimeMillis();
		long idleTime = currentTime - lastActivityTime;
		if (idleTime < maxIdleTime) {
			long aditionalStudyTime = currentTime - lastActivityTime;
			sessionStudyTime = sessionStudyTime + aditionalStudyTime;
		}
		return sessionStudyTime;
	}
	
	public String[] loadDefaultSetNames() {
		PropertiesLoader pldr = new PropertiesLoader();
		Vector props;
		try {
			props = pldr.loadPropertie();
			Vector setNames = new Vector();
			for(int i = 0; i<props.size(); i++) {
				Property p = (Property)props.elementAt(i);
				if(p.getName().equalsIgnoreCase(DEFAULTSET)) {
					setNames.addElement(p.getValue());
				}
			}
			String[] temp = new String[setNames.size()];
			setNames.copyInto(temp);
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
}
