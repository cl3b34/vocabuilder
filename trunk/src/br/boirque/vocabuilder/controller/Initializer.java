package br.boirque.vocabuilder.controller;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.PropertiesLoader;
import br.boirque.vocabuilder.model.Property;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.ISetOfCardsDAO;
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
	
	// Beans
	private SetOfCards soc;
	private Vector cards;
	
	// Statistics
	private int totalDoneSession = 0;
	private long sessionStudyTime = 0;
	private long lastActivityTime = 0; // Last time the user interacted with
	
	// the app
	private final long maxIdleTime = 30000L; // thirty Seconds. After that activity time is not computed

	
	// Random list management
	private static Vector cardsIndexes;
	private static Vector viewedIndexes;
	
	// Shall we use a sequential or random list?
	boolean useRandom = true;
	private int amountToReview = -1;
	private int totalOfCards = -1;
	private int totalReviewed = -1;
	
	//Sequential list
	private int lastViewedCardIndex = -1;


	public SetOfCards initializeApp(String setToLoad) {

		SetOfCards soc = null;
		if (setToLoad.indexOf(".txt") == -1) {
			// we are loading a set from RMS
			soc = this.loadSet(setToLoad);
		} else {
			// Load the set from a TXT resource
			SetOfCardsLoader socl = new SetOfCardsLoader();
			try {
				soc = socl.loadSet(setToLoad);
			} catch (IOException e) {
				// TODO should send a message to the UI
				e.printStackTrace();
			}
		}
		return soc;
	}
	
	public int getDoneAmount(Vector cards) {
		int doneAmount = 0;
		FlashCard c ;
		for (int i = 0; i < cards.size(); i++) {
			c = (FlashCard) cards.elementAt(i);
			if (c.isDone()) {
				doneAmount++;
			}
		}
		return doneAmount;
	}

	/**
	 * Returns the index of the next card to be displayed
	 * @param cards
	 * @return
	 */
	public int getNextCardIndex(Vector cards) {
		if(useRandom) {
			if(cardsIndexes == null|| viewedIndexes == null) {
				initIndexes(cards);
			}
			lastViewedCardIndex = getNextRandomCardIndex(cardsIndexes);
		}else {
			// sequential list
			lastViewedCardIndex = lastViewedCardIndex +1;
		}
		return lastViewedCardIndex;
	}
	
	/**
	 * return the record count or -1 if an error occurs
	 */
	public int getCardCount(String setName) {
		try {
			ISetOfCardsDAO socDao = new SetOfCardsDAOV4Impl(setName);
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

	public void initIndexes(Vector cards) {

		lastViewedCardIndex = -1;
		viewedIndexes = new Vector();
		cardsIndexes = initializeRandomCardIndex(cards);
	}
	
	/*
	 * Utility class to initialize the vector of indexes with the index of each
	 * element in 'cards' vector
	 */
	public Vector initializeRandomCardIndex(Vector toBeIndexed) {
		viewedIndexes = new Vector();
		Vector indexes = new Vector();
		for (int i = 0; i < toBeIndexed.size(); i++) {
			Integer index = new Integer(i);
			indexes.addElement(index);
		}
		return indexes;
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
			ISetOfCardsDAO socDao = new SetOfCardsDAOV4Impl(soc.getSetName());
			// removes the previous recordstore
			socDao.resetSetState();
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
	 * reset the state of the set being studied so we start fresh.
	 */
	public void resetState(String setName) {
		try {
			ISetOfCardsDAO socDao = new SetOfCardsDAOV4Impl(setName);
			socDao.resetSetState();
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
	 * Delete permanently the set from RMS
	 */
	public void deleteSetOfCardsFromRMS(String setName) {
		try {
			ISetOfCardsDAO socDao = new SetOfCardsDAOV4Impl(setName);
			socDao.deleteSetOfCards(setName);
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

	/**
	 * Load the set being currently studied from persistent
	 * storage (RMS)
	 * 
	 * @return the currently studied set or null if a error occurred
	 * @throws RecordStoreException
	 * @throws RecordStoreNotFoundException
	 * @throws RecordStoreFullException
	 * @throws IOException
	 */
	protected SetOfCards loadSet(String setName) {
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
	 * @return an array containing the names of all the default (loaded from txt
	 *         resources) sets available.
	 */
	public String[] loadDefaultSetNames() {
		PropertiesLoader pldr = new PropertiesLoader();
		Vector props;
		try {
			props = pldr.loadPropertie();
			Vector setNames = new Vector();
			for (int i = 0; i < props.size(); i++) {
				Property p = (Property) props.elementAt(i);
				if (p.getName().equalsIgnoreCase(DEFAULTSET)) {
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

	/**
	 * @return an array containing the names of sets whose study is in progress
	 *         (are loaded from RMS storage)
	 */
	public String[] loadOnProgressSetNames() {
		try {
			return SetOfCardsDAO.getAvailableSets();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return an array containing unique set names, loaded from RMS and TXT
	 *         resources (in /res folder). If a certain set is both in TXT
	 *         resource and in RMS (meaning that the study is in progress) only
	 *         the RMS resource name is returned. Both TXT and RMS names must be
	 *         the same in order to this 'filter' to work.
	 */
	public String[] loadUniqueSetNames() {
		String[] defaultSetsTemp = loadDefaultSetNames();
		String[] onProgressSets = loadOnProgressSetNames();

		if (onProgressSets == null) {
			return defaultSetsTemp;
		}

		Vector defaultSetsClean = stripEnding(defaultSetsTemp);
		defaultSetsClean = removeDuplicateSets(defaultSetsClean, onProgressSets);
		defaultSetsClean = addEndingBack(defaultSetsClean);

		String[] allUniqueSetNames = new String[onProgressSets.length
				+ defaultSetsClean.size()];
		// copy the results on the array to be returned
		defaultSetsClean.copyInto(allUniqueSetNames);
		System.arraycopy(onProgressSets, 0, allUniqueSetNames, defaultSetsClean
				.size(), onProgressSets.length);

		return allUniqueSetNames;
	}

	/*
	 * Returns a random integer corresponding to the index of the next card to
	 * be displayed
	 */
	private int getNextRandomCardIndex(Vector listOfIndexes) {
		Random r = new Random();
		int indexOfTheIndex = r.nextInt(listOfIndexes.size());
		Integer index = (Integer) listOfIndexes.elementAt(indexOfTheIndex);
		// remove this index from the list of indexes to be viewed
		listOfIndexes.removeElementAt(indexOfTheIndex);
		// add it to the list of already viewed indexes
		viewedIndexes.addElement(index);
		return index.intValue();
	}

	/**
	 * check if there are repeated set names. Strip '.txt.' from the defaultSet
	 * names and compare with onProgressSets. Sets in progress always have the
	 * precedence and stay in the returned Vector
	 * 
	 * @param defaultSets -
	 *            Array with the default sets loaded from TXT resources
	 * @param onProgressSets -
	 *            Array with the sets in progress
	 * @return Vector of default sets with no duplicates among the sets in
	 *         progress
	 */
	private Vector removeDuplicateSets(Vector defaultSets,
			String[] onProgressSets) {
		for (int i = 0; i < onProgressSets.length; i++) {
			String setName = onProgressSets[i];
			if (defaultSets.contains(setName)) {
				// there is already one set with that name being studied.
				defaultSets.removeElement(setName);
			}
		}
		return defaultSets;
	}

	private Vector stripEnding(String[] arrayToStrip) {
		Vector stripped = new Vector();
		for (int i = 0; i < arrayToStrip.length; i++) {
			String dSet = arrayToStrip[i];
			int dotTxtIndex = dSet.indexOf(".txt");
			if (dotTxtIndex != -1) {
				stripped.addElement(dSet.substring(0, dotTxtIndex));
			} else {
				// nothing to be stripped
				stripped.addElement(dSet);
			}
		}
		return stripped;
	}

	private Vector addEndingBack(Vector defaultSetsClean) {
		Vector toReturn = new Vector();
		for (int i = 0; i < defaultSetsClean.size(); i++) {
			String setName = (String) defaultSetsClean.elementAt(i);
			setName = setName + ".txt";
			toReturn.addElement(setName);
		}
		return toReturn;
	}
}
