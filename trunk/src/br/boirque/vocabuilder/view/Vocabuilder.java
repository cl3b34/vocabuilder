package br.boirque.vocabuilder.view;

import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import br.boirque.vocabuilder.controller.Initializer;
import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;

public class Vocabuilder extends MIDlet implements CommandListener {
	// Commands
	private Command exitCommand = new Command("Exit", Command.EXIT, 3);
	private Command turnCommand = new Command("Turn", Command.SCREEN, 1);
	private Command doneCommand = new Command("Done", Command.SCREEN, 2);
	private Command wrongCommand = new Command("Wrong", Command.SCREEN, 1);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 1);
	private Command nextSet = new Command("Next set", Command.SCREEN, 1);

	// UI elements
	private Form mainForm;
	private StringItem cardText;

	// Beans
	private SetOfCards soc;
	private Vector cards;
	FlashCard c;

	// Sequential list index management
	private int amountToReview = -1;
	private int totalOfCards = -1;
	private int totalReviewed = -1;
	private int lastViewedCardIndex = -1;

	// Random list management
	private Vector cardsIndexes;
	private Vector viewedIndexes;

	// Which side is being displayed
	boolean sideOne;

	// Is the app running?
	boolean isRunning = false;

	// Shall we use a sequential or random list?
	boolean useRandom = true;

	public Vocabuilder() {

		mainForm = new Form("Vocabuilder");
		cardText = new StringItem("", "Card Text");
		cardText.setLayout(Item.LAYOUT_CENTER);
		cardText.setLayout(Item.LAYOUT_EXPAND);
		// cardText.setPreferredSize(-1, cardText.getPreferredHeight()+3);
		// cardText.setText("height " + cardText.getPreferredHeight() + " "
		// + cardText.getMinimumHeight());

		mainForm.append(cardText);
		mainForm.setCommandListener(this);
	}

	/**
	 * Takes care of initializing the app
	 */
	protected void startApp() {
		// check if the application is resuming from paused state
		if (!isRunning) {
			// initialize the application. Load the list
			Initializer initializer = new Initializer();
			// TODO: the initialization might return a null set of cards
			// if so, display an error message
			soc = initializer.initializeApp();

			// TODO - transfer this initialization of the set of cards
			// code to a controller class
			// Look for a set of cards that is not done yet
			while (soc.isDone()) {
				soc = initializer.initializeApp();
			}
			String titleOfThisSet = soc.getTitle();
			mainForm.setTitle(titleOfThisSet);
			cards = soc.getFlashCards();
			amountToReview = cards.size() - getDoneAmount();
			totalOfCards = cards.size();
			totalReviewed = 0;
			if (useRandom) {
				cardsIndexes = initializeRandomCardIndex(cards);
				viewedIndexes = new Vector();
			}
			displayNextNotDoneCard();
			Display.getDisplay(this).setCurrent(mainForm);

			isRunning = true;
		}
	}

	/*
	 * Utility class to initialize the vector of indexes with the index of each
	 * element in 'cards' vector
	 */
	private Vector initializeRandomCardIndex(Vector toBeIndexed) {
		Vector indexes = new Vector();
		for (int i = 0; i < toBeIndexed.size(); i++) {
			Integer index = new Integer(i);
			indexes.addElement(index);
		}
		return indexes;
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

	// TODO - Move all this 'use random' or
	// and sequential list stuff to a method
	// that simply returns the next index to be displayed
	private void displayNextNotDoneCard() {
		if (totalReviewed < amountToReview) {
			// Look for a card that is not done yet and display it
			int index;
			if (useRandom) {
				index = getNextRandomCardIndex(cardsIndexes);
			} else {
				// sequential list
				index = lastViewedCardIndex + 1;
			}
			c = (FlashCard) cards.elementAt(index);
			boolean notFound = true;
			while (notFound) {
				if (c.isDone()) {
					// get the next card
					if (useRandom) {
						index = getNextRandomCardIndex(cardsIndexes);
					} else {
						index++;
					}
					c = (FlashCard) cards.elementAt(index);
				} else {
					// display the card and leave the loop
					displayCardSide(1);
					notFound = false;
					// update the index of the last viewed card
					lastViewedCardIndex = index;
				}
			}
		} else {
			// Reached the end of the set.
			// Mark the set as done if all cards are marked done
			if (getDoneAmount() == totalOfCards) {
				soc.setDone(true);
			}
			// display the statistics
			displayStatistics(true);
		}
	}

	protected void pauseApp() {
		cardText.setLabel("APPLICATION PAUSED" + "\n");
		// displayStatistics(false);
		// Initializer initializer = new Initializer();
		// initializer.saveState(soc);
	}

	protected void destroyApp(boolean bool) {
		displayStatistics(false);
		Initializer initializer = new Initializer();
		initializer.saveState(soc);
	}

	/*
	 * Set the currently displayed commands. If the argument is null, no command
	 * is show.
	 */
	private void setCommands(Command[] commands) {
		// remove all commands
		mainForm.removeCommand(doneCommand);
		mainForm.removeCommand(wrongCommand);
		mainForm.removeCommand(exitCommand);
		mainForm.removeCommand(turnCommand);
		mainForm.removeCommand(restartCommand);
		mainForm.removeCommand(reviewCommand);
		mainForm.removeCommand(nextSet);

		if (commands != null) {
			// add the desired commands
			for (int i = 0; i < commands.length; i++) {
				Command c = commands[i];
				mainForm.addCommand(c);
			}
		}
	}

	private void displayCardSide(int side) {
		switch (side) {
		case 1:
			Command[] commands = { turnCommand, exitCommand };
			setCommands(commands);
			cardText.setLabel(c.getSideOneTitle() + ": \n");
			cardText.setText(c.getSideOne());
			sideOne = true;
			break;
		case 2:
			Command[] cmds = { doneCommand, wrongCommand };
			setCommands(cmds);
			cardText.setLabel(c.getSideTwoTitle() + ": \n");
			cardText.setText(c.getSideTwo());
			sideOne = false;
			break;
		default:
			break;
		}
	}

	private int getDoneAmount() {
		int doneAmount = 0;
		for (int i = 0; i < cards.size(); i++) {
			c = (FlashCard) cards.elementAt(i);
			if (c.isDone()) {
				doneAmount++;
			}
		}
		return doneAmount;
	}

	/*
	 * Display the statistics. If showCommands is set to true, also show
	 * commands specific for this screen
	 */
	private void displayStatistics(boolean showCommands) {
		int done = getDoneAmount();
		int incorrectAmount = totalOfCards - done;
		if (showCommands) {
			// show the commands for statistics
			if (incorrectAmount > 0) {
				Command[] commands = { reviewCommand, exitCommand };
				setCommands(commands);
			} else {
				Command[] commands = { restartCommand, exitCommand };
				setCommands(commands);
			}
		} else {
			setCommands(null);
		}
		cardText.setLabel("STATISTICS" + "\n");
		cardText.setText("Total of cards: " + totalOfCards + 
				"\n" + "Correct: "	+ done + 
				"\n" + "Incorrect: " + incorrectAmount +
				"\n" + "Viewed in this session: " + totalReviewed);
	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == exitCommand) {
			// should save the state and exit
			destroyApp(false);
			notifyDestroyed();
		}

		if (cmd == reviewCommand) {
			// show again the cards in the set not marked as 'done'
			amountToReview = totalOfCards - getDoneAmount();
			totalReviewed = 0;
			lastViewedCardIndex = -1;
			// TODO: this is not very efficient
			// all the cards are going to be tested 
			// for 'done' mark. should return only 
			// the marked as wrong...
			if (useRandom) {
				cardsIndexes = initializeRandomCardIndex(cards);
				viewedIndexes = new Vector();
			}
			displayNextNotDoneCard();
		}

		if (cmd == nextSet) {
			// Load a new set of cards
		}

		if (cmd == restartCommand) {
			// remove the 'done' mark from the set and all the cards
			// and start over the set
			// TODO: SHOULD ask for confirmation
			for (int i = 0; i < cards.size(); i++) {
				c = (FlashCard) cards.elementAt(i);
				c.setDone(false);
			}
			soc.setDone(false);
			amountToReview = totalOfCards;
			totalReviewed = 0;
			lastViewedCardIndex = -1;
			if (useRandom) {
				cardsIndexes = initializeRandomCardIndex(cards);
				viewedIndexes = new Vector();
			}
			displayNextNotDoneCard();
		}

		if (cmd == turnCommand) {
			// turn the card
			if (!sideOne) {
				displayCardSide(1);
			} else {
				displayCardSide(2);
			}
		}

		if (cmd == doneCommand) {
			// mark card as done and show next card, side one
			c.setDone(true);
			totalReviewed++;
			displayNextNotDoneCard();
		}

		if (cmd == wrongCommand) {
			// Just show next card, side one
			totalReviewed++;
			displayNextNotDoneCard();
		}
	}
}
