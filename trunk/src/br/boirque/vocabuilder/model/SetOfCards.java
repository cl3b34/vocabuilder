package br.boirque.vocabuilder.model;

import java.util.Vector;

import com.sun.j2me.global.DateTimeFormat;

/**
 * @author cleber.goncalves
 * A list of flash cards to be studied
 */
public class SetOfCards {
	private String title;
	private boolean done;
	//total amount of time spent studying this set
	private long totalStudiedTimeInMiliseconds;
	private int totalNumberOfDisplayedCards;
	//Flash cards included in this series
	private Vector flashCards;
	
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
}
