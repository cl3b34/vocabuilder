package br.boirque.vocabuilder;

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
	private Long totalStudiedTimeInMiliseconds;
	//Titles of the sides of the cards on this series
	private String sideOneTitle;
	private String sideTwoTitle;
	//Flash cards included in this series
	private Vector flashCards;
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
	public Long getTotalStudiedTimeInMiliseconds() {
		return totalStudiedTimeInMiliseconds;
	}
	/**
	 * @param totalStudiedTimeInMiliseconds the totalStudiedTimeInMiliseconds to set
	 */
	public void setTotalStudiedTimeInMiliseconds(Long totalStudiedTimeInMiliseconds) {
		this.totalStudiedTimeInMiliseconds = totalStudiedTimeInMiliseconds;
	}
	/**
	 * @return the sideOneTitle
	 */
	public String getSideOneTitle() {
		return sideOneTitle;
	}
	/**
	 * @param sideOneTitle the sideOneTitle to set
	 */
	public void setSideOneTitle(String sideOneTitle) {
		this.sideOneTitle = sideOneTitle;
	}
	/**
	 * @return the sideTwoTitle
	 */
	public String getSideTwoTitle() {
		return sideTwoTitle;
	}
	/**
	 * @param sideTwoTitle the sideTwoTitle to set
	 */
	public void setSideTwoTitle(String sideTwoTitle) {
		this.sideTwoTitle = sideTwoTitle;
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
}
