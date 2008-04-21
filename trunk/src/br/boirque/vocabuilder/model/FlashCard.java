package br.boirque.vocabuilder.model;

/**
 * @author cleber.goncalves
 * A FlashCard typically has a question in 
 * one side and an answer in the other.
 * It might also contain a tip about the answer.
 */
public class FlashCard {

	private String sideOne;
	private String sideOneTitle;
	private String sideTwo;
	private String sideTwoTitle;
	private boolean done;
	private String tip;
	private int viewedCounter;
	private int markedDoneCounter;
	private long lastTimeViewed;
	private long lastTimeMarkedDone;
	private int cardId;
	
		
	/**
	 * Default constructor 
	 */
	public FlashCard() {
	}
	
	/**
	 * @param sideOne
	 * @param sideOneTitle
	 * @param sideTwo
	 * @param sideTwoTitle
	 * @param done
	 * @param tip
	 * 
	 * Convenience constructor
	 */
	public FlashCard(String sideOne, String sideOneTitle, String sideTwo,
			String sideTwoTitle, boolean done, String tip) {
		super();
		this.sideOne = sideOne;
		this.sideOneTitle = sideOneTitle;
		this.sideTwo = sideTwo;
		this.sideTwoTitle = sideTwoTitle;
		this.done = done;
		this.tip = tip;
	}

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

	public int getViewedCounter() {
		return viewedCounter;
	}

	public void setViewedCounter(int viewedCounter) {
		this.viewedCounter = viewedCounter;
	}

	public int getMarkedDoneCounter() {
		return markedDoneCounter;
	}

	public void setMarkedDoneCounter(int markedDoneCounter) {
		this.markedDoneCounter = markedDoneCounter;
	}

	public long getLastTimeMarkedDone() {
		return lastTimeMarkedDone;
	}

	public void setLastTimeMarkedDone(long lastTimeMarkedDone) {
		this.lastTimeMarkedDone = lastTimeMarkedDone;
	}

	public long getLastTimeViewed() {
		return lastTimeViewed;
	}

	public void setLastTimeViewed(long lastTimeViewed) {
		this.lastTimeViewed = lastTimeViewed;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	
}
