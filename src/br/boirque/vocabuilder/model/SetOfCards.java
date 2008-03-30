package br.boirque.vocabuilder.model;

import java.util.Vector;

/**
 * @author cleber.goncalves
 * A list of flash cards to be studied
 */
public class SetOfCards {
	private String title;
	private boolean done;
	//total amount of time spent studying this set
	private long totalStudiedTimeInMiliseconds;
	//Flash cards included in this series
	private Vector flashCards;
	//how many times the cards on this set were displayed
	private int totalNumberOfDisplayedCards;
	private long lastTimeViewed;
	private long lastTimeMarkedDone;
	private int markedDoneCounter;
	
	
	
	/**
	 * Default constructor 
	 */
	public SetOfCards() {
	}
	
	
	/**
	 * @param title
	 * @param done
	 * @param totalStudiedTimeInMiliseconds
	 * @param flashCards
	 * 
	 * Convenience constructor
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
