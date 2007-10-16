package br.boirque.vocabuilder.model;

/**
 * @author cleber.goncalves
 * A FlashCard tipically has a question in 
 * one side and an answer in the other.
 * It might also contain a tip about the answer.
 */
public class FlashCard {

	private String sideOne;
	private String sideTwo;
	private boolean done;
	private String tip;
	
	/**
	 * @return the word on sideOne
	 */
	public String getSideOne() {
		return sideOne;
	}
	/**
	 * @param sideOne the word to set
	 */
	public void setSideOne(String sideOne) {
		this.sideOne = sideOne;
	}
	/**
	 * @return the word on sideTwo
	 */
	public String getSideTwo() {
		return sideTwo;
	}
	/**
	 * @param sideTwo the word to set
	 */
	public void setSideTwo(String sideTwo) {
		this.sideTwo = sideTwo;
	}
	/**
	 * @return true if the card is marked done
	 */
	public boolean isDone() {
		return done;
	}
	/**
	 * @param done - true if the card is done
	 */
	public void setDone(boolean done) {
		this.done = done;
	}
	/**
	 * @return the tip
	 */
	public String getTip() {
		return tip;
	}
	/**
	 * @param tip the tip to set
	 */
	public void setTip(String tip) {
		this.tip = tip;
	}
	
}
