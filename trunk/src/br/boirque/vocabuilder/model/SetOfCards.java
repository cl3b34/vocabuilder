package br.boirque.vocabuilder.model;

import java.util.Vector;

/**
 * @author cleber.goncalves
 * A list of flash cards to be studied
 */
public class SetOfCards {
	//This set's title
	private String title;
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
	//TODO: amount of cards in the set
	//TODO: amount of cards marked 'done' in the set
	
	
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
	 * Convenience constructor
	 * @param title - the set title
	 * @param done - the set is done if all cards are marked as done
	 * @param totalStudiedTimeInMiliseconds - how long has this set been studied
	 * @param flashCards - the cards contained in this set
	 */
	public SetOfCards(String title, boolean done,
			long totalStudiedTimeInMiliseconds, Vector flashCards) {
		super();
		this.title = title;
		this.done = done;
		this.totalStudiedTimeInMiliseconds = totalStudiedTimeInMiliseconds;
		this.flashCards = flashCards;
	}
	
	/**
	 * @param title - the set title
	 * @param done - the set is done if all cards are marked as done
	 * @param totalStudiedTimeInMiliseconds - how long has this set been studied
	 * @param flashCards - the cards contained in this set
	 * @param totalNumberOfDisplayedCards - how many cards where displayed since the first time this set was used
	 * @param lastTimeViewed - last time this set was viewed
	 * @param lastTimeMarkedDone - last time this set was marked as 'done'
	 * @param markedDoneCounter - how many times this set was marked 'done'
	 */
	public SetOfCards(String title, boolean done,
			long totalStudiedTimeInMiliseconds, Vector flashCards,
			int totalNumberOfDisplayedCards, long lastTimeViewed,
			long lastTimeMarkedDone, int markedDoneCounter) {
		super();
		this.title = title;
		this.done = done;
		this.totalStudiedTimeInMiliseconds = totalStudiedTimeInMiliseconds;
		this.flashCards = flashCards;
		this.totalNumberOfDisplayedCards = totalNumberOfDisplayedCards;
		this.lastTimeViewed = lastTimeViewed;
		this.lastTimeMarkedDone = lastTimeMarkedDone;
		this.markedDoneCounter = markedDoneCounter;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
}
