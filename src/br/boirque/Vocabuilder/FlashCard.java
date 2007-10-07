package br.boirque.vocabuilder;

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
	 * @return the sideOne
	 */
	public String getSideOne() {
		return sideOne;
	}
	/**
	 * @param sideOne the sideOne to set
	 */
	public void setSideOne(String sideOne) {
		this.sideOne = sideOne;
	}
	/**
	 * @return the sideTwo
	 */
	public String getSideTwo() {
		return sideTwo;
	}
	/**
	 * @param sideTwo the sideTwo to set
	 */
	public void setSideTwo(String sideTwo) {
		this.sideTwo = sideTwo;
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
