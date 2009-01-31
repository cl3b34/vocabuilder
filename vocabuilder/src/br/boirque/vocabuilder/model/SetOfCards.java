package br.boirque.vocabuilder.model;

import java.util.Vector;

/**
 * @author cleber.goncalves
 * A list of flash cards to be studied
 */
public class SetOfCards {
	//This set's setName
	private String setName;
	//If all the cards in this set are marked as done
	private boolean done;
	//total amount of time spent studying this set
	private long totalStudiedTimeInMiliseconds;
	//Flash cards included in this series
	private Vector flashCards;
	//how many times the cards on this set were displayed
	private int totalNumberOfDisplayedCards;
	//last time the set was viewed
	private long lastTimeViewed;
	//last time the set was marked done
	private long lastTimeMarkedDone;
	//how many times it was marked done
	private int markedDoneCounter;
	//the recordId of the set
	private int setId;
	//the amount of cards in this set
	private int totalNumberOfCards;
	//amount of cards marked 'done' in the set
	private int totalNumberOfCardsMarkedDone;
	
	
	
	
	/**
	 * @param setName
	 * @param done
	 * @param flashCards
	 */
	public SetOfCards(String title, boolean done, Vector flashCards) {
		super();
		this.setName = title;
		this.done = done;
		this.flashCards = flashCards;
	}


	/**
	 * @param setName
	 * @param done
	 * @param totalStudiedTimeInMiliseconds
	 * @param flashCards
	 * @param totalNumberOfDisplayedCards
	 * @param lastTimeViewed
	 * @param lastTimeMarkedDone
	 * @param markedDoneCounter
	 * @param setId
	 * @param totalNumberOfCards
	 * @param totalNumberOfCardsMarkedDone
	 */
	public SetOfCards(String title, boolean done,
			long totalStudiedTimeInMiliseconds, Vector flashCards,
			int totalNumberOfDisplayedCards, long lastTimeViewed,
			long lastTimeMarkedDone, int markedDoneCounter, int setId,
			int totalNumberOfCards, int totalNumberOfCardsMarkedDone) {
		super();
		this.setName = title;
		this.done = done;
		this.totalStudiedTimeInMiliseconds = totalStudiedTimeInMiliseconds;
		this.flashCards = flashCards;
		this.totalNumberOfDisplayedCards = totalNumberOfDisplayedCards;
		this.lastTimeViewed = lastTimeViewed;
		this.lastTimeMarkedDone = lastTimeMarkedDone;
		this.markedDoneCounter = markedDoneCounter;
		this.setId = setId;
		this.totalNumberOfCards = totalNumberOfCards;
		this.totalNumberOfCardsMarkedDone = totalNumberOfCardsMarkedDone;
	}


	public int getTotalNumberOfCards() {
		return totalNumberOfCards;
	}


	public void setTotalNumberOfCards(int totalNumberOfCards) {
		this.totalNumberOfCards = totalNumberOfCards;
	}


	public int getTotalNumberOfCardsMarkedDone() {
		return totalNumberOfCardsMarkedDone;
	}


	public void setTotalNumberOfCardsMarkedDone(int totalNumberOfCardsMarkedDone) {
		this.totalNumberOfCardsMarkedDone = totalNumberOfCardsMarkedDone;
	}


	public int getSetId() {
		return setId;
	}


	public void setSetId(int setId) {
		this.setId = setId;
	}


	/**
	 * Default constructor 
	 */
	public SetOfCards() {
	}
	
	/**
	 * @return the setName
	 */
	public String getSetName() {
		return setName;
	}
	/**
	 * @param setName the setName to set
	 */
	public void setSetName(String setName) {
		this.setName = setName;
	}
	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}
	/**
	 * @param done the done to set
	 */
	public void setDone(boolean done) {
		this.done = done;
	}
	/**
	 * @return the totalStudiedTimeInMiliseconds
	 */
	public long getTotalStudiedTimeInMiliseconds() {
		return totalStudiedTimeInMiliseconds;
	}
	/**
	 * @param totalStudiedTimeInMiliseconds the totalStudiedTimeInMiliseconds to set
	 */
	public void setTotalStudiedTimeInMiliseconds(long totalStudiedTimeInMiliseconds) {
		this.totalStudiedTimeInMiliseconds = totalStudiedTimeInMiliseconds;
	}
	
	/**
	 * @return the flashCards
	 */
	public Vector getFlashCards() {
		return flashCards;
	}
	/**
	 * @param flashCards the flashCards to set
	 */
	public void setFlashCards(Vector flashCards) {
		this.flashCards = flashCards;
	}


	public int getTotalNumberOfDisplayedCards() {
		return totalNumberOfDisplayedCards;
	}


	public void setTotalNumberOfDisplayedCards(int totalNumberOfDisplayedCards) {
		this.totalNumberOfDisplayedCards = totalNumberOfDisplayedCards;
	}


	public long getLastTimeViewed() {
		return lastTimeViewed;
	}


	public void setLastTimeViewed(long lastTimeViewed) {
		this.lastTimeViewed = lastTimeViewed;
	}


	public long getLastTimeMarkedDone() {
		return lastTimeMarkedDone;
	}


	public void setLastTimeMarkedDone(long lastTimeMarkedDone) {
		this.lastTimeMarkedDone = lastTimeMarkedDone;
	}


	public int getMarkedDoneCounter() {
		return markedDoneCounter;
	}


	public void setMarkedDoneCounter(int markedDoneCounter) {
		this.markedDoneCounter = markedDoneCounter;
	}


	public String toString() {
		StringBuffer setText = new StringBuffer();
		setText.append("SetName: " + this.setName + "\n" +
		"Done: " + this.done+ "\n\n");		
		
		for (int i = 0; i < this.flashCards.size(); i++) {
			FlashCard card = (FlashCard) this.flashCards.elementAt(i);
			setText.append(card.toString());
		}
		return setText.toString();
	}
}
