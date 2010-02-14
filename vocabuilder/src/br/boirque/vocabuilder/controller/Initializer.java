package br.boirque.vocabuilder.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.ISetDownloader;
import br.boirque.vocabuilder.model.PropertiesLoader;
import br.boirque.vocabuilder.model.Property;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.ISetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsDAOV4Impl;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import br.boirque.vocabuilder.model.StudyStackDownloader;

/**
 * Takes care of all the initialization tasks
 * 
 * @author cleber.goncalves
 * 
 */
public class Initializer implements Runnable {

	private static final String DEFAULTSET = "defaultset";

	// list management
	private static Vector originalOrderCardsIndexes;
	private static Vector randomOrderCardsIndexes;
	private static int amountToReview = 0;
	private static int totalOfCards = 0;
	private static int totalReviewed = 0;

	
	// TODO: Those should become user preferences
	// Shall we use a sequential or random list?
	boolean useRandom = true;


	// Sequential list
	private int lastViewedCardIndex = -1;

	// set to be downloaded
	private String setToDownload;

	/**
	 * Constructor used by the download set thread
	 * @param setToDownload name of the set to be downloaded
	 */
	public Initializer(String setToDownload) {
		this.setToDownload = setToDownload;
	}

	/**
	 * Default constructor
	 */
	public Initializer() {
	}

	/**
	 * Load a set either from RMS or TXT resources
	 * 
	 * @param setToLoad
	 *            the name of the set to be loaded
	 * @return the set loaded
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws InvalidRecordIDException
	 */
	public SetOfCards loadSet(String setToLoad) {

		SetOfCards soc = null;
		if (setToLoad.indexOf(".txt") == -1) {
			// we are loading a set from RMS
			// Do not return a empty set... return null instead
			if (this.getCardCount(setToLoad) > 0) {
				SetOfCardsDAO socDao;
				try {
					socDao = new SetOfCardsDAOV4Impl(setToLoad);
					soc = socDao.loadSetOfCards();
				} catch (RecordStoreFullException e) {
					// TODO Notify the user somehow
					e.printStackTrace();
				} catch (RecordStoreNotFoundException e) {
					// TODO Notify the user somehow
					e.printStackTrace();
				} catch (RecordStoreException e) {
					// TODO Notify the user somehow
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Notify the user somehow
					e.printStackTrace();
				}
			}
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
		//all the indexes are initialized when the set is loaded
		initIndexes(soc.getFlashCards());
		return soc;
	}

	public int getDoneAmount(Vector cards) {
		int doneAmount = 0;
		FlashCard c;
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
	 * 
	 * @param cards
	 * @return the index of the next card to be displayed or -1 if all the cards
	 *         were already displayed
	 */
	public int getNextCardIndex(Vector cards) {
		if(amountToReview == 0) {
			//all cards done already
			return -1;
		}
		
		lastViewedCardIndex = lastViewedCardIndex + 1;
		Integer cardIndex;
		if (useRandom) {
			cardIndex = (Integer) randomOrderCardsIndexes.elementAt(lastViewedCardIndex);
		} else {
			cardIndex = (Integer) originalOrderCardsIndexes.elementAt(lastViewedCardIndex);
		}
		int indexToReturn = cardIndex.intValue();

		//update total of cards viewed
		totalReviewed++;
		//update total to review
		amountToReview--;
		return indexToReturn;
	}


	private Vector groupCardsByViewAmount(Vector cards, boolean ignoreCardsDone) {
		Vector cardsGrouped = new Vector();
		FlashCard c;
		for (int i = 0; i < cards.size(); i++) {
			c = (FlashCard) cards.elementAt(i);
			if(c.isDone()) {
				continue;
			}
			int viewedCount = c.getViewedCounter();
			int groupedSize = cardsGrouped.size();
			//the table should be in the position in the table 
			//equivalent to the amount of times it's cards where viewed
			Hashtable sortedCards = new Hashtable();
			if (groupedSize <= viewedCount) {
				for(int k=0; k< (viewedCount +1) - groupedSize; k++) {
					cardsGrouped.addElement(new Hashtable());
				}
			}
			sortedCards = (Hashtable) cardsGrouped.elementAt(viewedCount);
			//the key for the table is the original card position
			sortedCards.put(new Integer(i), c);
		}
		return cardsGrouped;
	}

	/**
	 * return the record count or -1 if an error occurs
	 */
	protected int getCardCount(String setName) {
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
		totalOfCards = cards.size();
		totalReviewed = 0;
		amountToReview = totalOfCards - this.getDoneAmount(cards);
		lastViewedCardIndex = -1;
		originalOrderCardsIndexes = initializeCardIndexVector(cards, true);
		randomOrderCardsIndexes = initializeRandomCardIndexVector(cards, true);
	}

	private Vector initializeRandomCardIndexVector(Vector cards, boolean ignoreDoneCards) {
		Vector cardsGroupedByViewAmount = groupCardsByViewAmount(cards,ignoreDoneCards);
		//cards are grouped, get the keys for them, randomize 
		// within each group (thus maintaining the grouping by viewing times)
		// and return as a vector
		Vector randomizedCardIndexes = ExtractRandomizedCardIndexes(cardsGroupedByViewAmount);
		return randomizedCardIndexes;
	}

	private Vector ExtractRandomizedCardIndexes(Vector cardsGroupedByViewAmount) {
		Vector groupedRandomizedIndexes = new Vector();
		Enumeration tables = cardsGroupedByViewAmount.elements();
		while(tables.hasMoreElements()) {
			Hashtable cardGroup = (Hashtable) tables.nextElement();
			Enumeration cardIndexes = cardGroup.keys();
			Vector tempIndexes = new Vector();
			while(cardIndexes.hasMoreElements()) {
				Integer index = (Integer) cardIndexes.nextElement();
				tempIndexes.addElement(index);
			}
			if(tempIndexes.size() > 0) {
				tempIndexes = randomizeVector(tempIndexes);
				groupedRandomizedIndexes.addElement(tempIndexes);
			}
		}
		groupedRandomizedIndexes = flatten(groupedRandomizedIndexes);
		return groupedRandomizedIndexes;
	}

	private Vector randomizeVector(Vector toBeRandomized) {
		// FOR some reason CLDC 1.1 doesn't work and I dont have random.nextInt(int)
		Random r = new Random();
		Vector randomized = new Vector();
		do {
			int randomIndex = r.nextInt();
			if(randomIndex < 0){
				randomIndex *= -1;
			}
			randomIndex = randomIndex % toBeRandomized.size();
				
			Integer index = (Integer) toBeRandomized.elementAt(randomIndex);
//			// remove this index as it is already randomized
			toBeRandomized.removeElementAt(randomIndex);
			randomized.addElement(index);
		}while(toBeRandomized.size()>0);
		return randomized;
	}


	private Vector flatten(Vector groupedRandomizedIndexes) {
		Vector flattened = new Vector();
		Enumeration vectors = groupedRandomizedIndexes.elements();
		while(vectors.hasMoreElements()) {
			Vector v = (Vector) vectors.nextElement();
			Enumeration e = v.elements();
			while(e.hasMoreElements()) {
				flattened.addElement(e.nextElement());
			}
		}
		return flattened;
	}

	/**
	 * Return a vector of indexes with the index of each element in 'cards'
	 * vector given as argument
	 * 
	 * @param cards
	 *            vector of cards to extract the index of
	 * @param ignoreCardDone
	 *            if true, check if the card is already marked as done and
	 *            ignore it if so
	 * @return a Vector containing the indexes of the cards in the argument
	 *         vector
	 */
	protected Vector initializeCardIndexVector(Vector cards,
			boolean ignoreCardDone) {
		Vector indexes = new Vector();
		FlashCard c;
		for (int i = 0; i < cards.size(); i++) {
			if (ignoreCardDone) {
				c = (FlashCard) cards.elementAt(i);
				if (c.isDone()) {
					continue;
				}
			}
			Integer index = new Integer(i);
			indexes.addElement(index);
		}
		c = null;
		return indexes;
	}
	
	

	/**
	 * Save the current state of the set being studied to persistent storage Use
	 * the newest file format
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
	protected void resetState(String setName) {
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
	 * @return an array containing the names of all the default (loaded from txt
	 *         resources) sets available.
	 */
	public String[] loadDefaultSetNames() {
		PropertiesLoader pldr = new PropertiesLoader();
		Vector props;
		try {
			props = pldr.loadProperties();
			Vector setNames = new Vector();
			for (int i = 0; i < props.size(); i++) {
				Property p = (Property) props.elementAt(i);
				if (p.getName().toLowerCase().equals(DEFAULTSET)) {
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

	/**
	 * 	remove the 'done' mark from the set and all the cards
	 *  so we can start over the set
	 *  TODO: SHOULD ask for confirmation	
	 * @param soc the set to be reset
	 */
	public SetOfCards restartSet(SetOfCards soc) {
		Vector cards = soc.getFlashCards();
		for (int i = 0; i < cards.size(); i++) {
			FlashCard c = (FlashCard) cards.elementAt(i);
			c.setDone(false);
		}
		soc.setDone(false);
		return soc;
	}

//	public static int getAmountToReview() {
//		return amountToReview;
//	}
//
	public static int getTotalOfCards() {
		return totalOfCards;
	}

	public static int getTotalReviewed() {
		return totalReviewed;
	}
//
//	public static void setAmountToReview(int amountToReview) {
//		Initializer.amountToReview = amountToReview;
//	}
//
//	public static void setTotalReviewed(int totalReviewed) {
//		Initializer.totalReviewed = totalReviewed;
//	}

	/**
	 * Load a list of the sets available for download.
	 * 
	 * @param category the category to load the available sets from
	 * @return an array of available sets
	 */
	public String[] loadDownloadableSets(String category) {
		ISetDownloader setDownloader = new StudyStackDownloader();
		Vector downloadableSets = setDownloader.listDownloadableSets(category);
		String[] toReturn = new String[downloadableSets.size()];
		downloadableSets.copyInto(toReturn);
		return toReturn;
	}

	/**
	 * Download the set of cards from internet and save to RMS
	 * 
	 * @param setName the name of the set to be downloaded
	 * 
	 */
	public SetOfCards downloadSetOfCards(String setName) {
		System.out.println("Downloading set");
		ISetDownloader setDownloader = new StudyStackDownloader();
		SetOfCards downloadedSet = setDownloader.downloadSet(setName);
		SetOfCardsDAO socDao = null;
		try {
//			System.out.println("Set downloaded" + downloadedSet);
			socDao = new SetOfCardsDAOV4Impl(downloadedSet.getSetName());
			socDao.saveSetOfCards(downloadedSet);
//			System.out.println("returning set");
			return downloadedSet;
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	/**
	 * allows for the set download to be run in a different thread
	 */
	public void run() {
		downloadSetOfCards(this.setToDownload);
		
	}
	
}
