package br.boirque.vocabuilder.view;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

import br.boirque.vocabuilder.controller.Initializer;
import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.util.VocaUtil;

public class Vocabuilder extends MIDlet implements CommandListener {
	private static final String DELETE_SET = "delete list";
	private static final String STUDY_SET = "STUDY";
	// Commands
	private Command exitCommand = new Command("Exit", Command.EXIT, 3);
	private Command quitCommand = new Command("Stats", Command.EXIT, 1);
	private Command turnCommand = new Command("Turn", Command.SCREEN, 1);
	private Command doneCommand = new Command("Done", Command.SCREEN, 2);
	private Command wrongCommand = new Command("Wrong", Command.SCREEN, 1);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 1);
	private Command nextSetCommand = new Command("Load another", Command.SCREEN,2);
	private Command loadSetCommand = new Command("Load", Command.SCREEN, 1);
	private Command deleteCommand = new Command("Delete", Command.SCREEN, 1);
	private Command selectCommand = new Command("Select", Command.SCREEN, 1);
	private Command back = new Command("Back", Command.BACK,1);

	// UI elements
	private Form mainForm;
	private StringItem cardText;
	private StringItem cardStatistics;

	// Beans
	private SetOfCards soc;
	private Vector cards;
	FlashCard c;

	// Statistics
	private int totalDoneSession = 0;
	private long sessionStudyTime = 0;
	private long lastActivityTime = 0; // Time last user interaction happened
	private final long maxIdleTime = 30000L; // thirty Seconds


	//TODO Remove this. should be only in initializer.java
	private int amountToReview = -1;
	private int totalOfCards = -1;
	private int totalReviewed = -1;

	// Which side is being displayed
	boolean sideOne;

	// Utility class
	VocaUtil vocaUtil = new VocaUtil();
	
	// controller class
	Initializer init = new Initializer();

	public Vocabuilder() {// Constructor
	}

	/**
	 * Takes care of initializing the app
	 */
	protected void startApp() {
		// if there is only one list, doesn't display the menu
		String[] setNames = (init.loadUniqueSetNames());
		if (setNames.length == 1) {
			displaySetOfCards(setNames[0]);
		} else {
			//displayLoadSetMenu();
			displayInitialOptionsMenu();
		}
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean bool) {
		save(soc);
	}

	private void save(SetOfCards soc) {
		if (soc != null) {
			init.saveState(soc);
		}
	}

	private void displayLoadSetMenu() {
		List listSelection = new List("Select a set", Choice.IMPLICIT, init
				.loadUniqueSetNames(), null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(back);
		listSelection.setSelectCommand(loadSetCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}

	private void displayMainForm() {
		mainForm = new Form("Vocabuilder");
		cardText = new StringItem("", "Card Text");
		cardStatistics = new StringItem("", "");
		// alertStats = new Alert("","",null,AlertType.INFO);
		cardText.setLayout(Item.LAYOUT_CENTER);
		cardText.setLayout(Item.LAYOUT_EXPAND);

		mainForm.append(cardText);
		mainForm.append(cardStatistics);
		mainForm.setCommandListener(this);
	}

	private void displayInitialOptionsMenu() {
		String[] actions = {STUDY_SET, DELETE_SET};
		List listSelection = new List("Select action", Choice.IMPLICIT, actions , null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(exitCommand);
		listSelection.setSelectCommand(selectCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}
	
	private void displayDeleteSetMenu() {
		List listSelection = new List("DELETE set", Choice.IMPLICIT, init.loadOnProgressSetNames() , null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(back);
		listSelection.setSelectCommand(deleteCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}
	
	private void displaySetOfCards(String setToLoad) {
		System.out.println(setToLoad);
		// initialize the application. Load the list
		soc = init.loadSet(setToLoad);

		displayMainForm();
		mainForm.setTitle(soc.getSetName());
		cards = soc.getFlashCards();
		soc.setLastTimeViewed(System.currentTimeMillis());
		amountToReview = cards.size() - init.getDoneAmount(cards);
		totalOfCards = cards.size();
		totalReviewed = 0;
		lastActivityTime = System.currentTimeMillis();
		// totalDoneSession = 0;
		init.initIndexes(cards);

		displayNextCard();
		Display.getDisplay(this).setCurrent(mainForm);

	}

	
	private void displayNextCard() {
		if (totalReviewed < amountToReview) {
			// Look for a card that is not done yet and display it
			int index = init.getNextCardIndex(cards);
			c = (FlashCard) cards.elementAt(index);
			boolean notFound = true;
			while (notFound) {
				if (c.isDone()) {
						index = init.getNextCardIndex(cards);
					c = (FlashCard) cards.elementAt(index);
				} else {
					// display the card and leave the loop
					displayCardSide(1);
					notFound = false;
				}
			}
		} else {
			// Reached the end of the set.
			// Mark the set as done if all cards are marked done
			if (init.getDoneAmount(cards) == totalOfCards) {
				soc.setDone(true);
				soc.setLastTimeMarkedDone(System.currentTimeMillis());
				soc.setMarkedDoneCounter(soc.getMarkedDoneCounter() + 1);
			}
			// display the statistics
			displayStatistics(true);
		}
	}

	/**
	 * Set the currently displayed commands. If the argument is null, no command
	 * is show.
	 */
	private void setCommands(Command[] commands, Displayable commandHost) {
		// remove all commands
		mainForm.removeCommand(doneCommand);
		mainForm.removeCommand(wrongCommand);
		mainForm.removeCommand(exitCommand);
		mainForm.removeCommand(turnCommand);
		mainForm.removeCommand(restartCommand);
		mainForm.removeCommand(reviewCommand);
		mainForm.removeCommand(nextSetCommand);
		mainForm.removeCommand(quitCommand);
		mainForm.removeCommand(loadSetCommand);

		if (commands != null) {
			// add the desired commands
			for (int i = 0; i < commands.length; i++) {
				Command c = commands[i];
				commandHost.addCommand(c);
			}
		}
	}
	
	/**
	 * Display the desired side of the Flash Card
	 * @param side to be displayed
	 */
	private void displayCardSide(int side) {
		switch (side) {
		case 1:
			Command[] commands = { turnCommand, quitCommand };
			setCommands(commands, mainForm);
			cardText.setLabel(c.getSideOneTitle() + ": \n");
			cardText.setText(c.getSideOne());
			sideOne = true;
			// show statistics for the card
			String lastTimeViewed = "";
			String tip = "";
			String viewCounter = "";
			// only show if there are meaningful values
			if (c.getLastTimeViewed() > 0) {
				lastTimeViewed = "last: "
						+ vocaUtil.getLastTimeViewedAsString(c
								.getLastTimeViewed());
			}
			if (c.getTip() != null && !(c.getTip().equals(""))) {
				tip = "tip: " + c.getTip() + "\n";
			}
			if (c.getViewedCounter() > 0) {
				viewCounter = "viewed " + c.getViewedCounter() + " times \n";
			}
			String cardStats = "\n\n\n\n" + viewCounter + tip + lastTimeViewed;
			cardStatistics.setLabel(cardStats);
			break;
		case 2:
			Command[] cmds = { doneCommand, wrongCommand };
			setCommands(cmds, mainForm);
			// remove statistics
			cardStatistics.setLabel("");
			// show the side
			cardText.setLabel(c.getSideTwoTitle() + ": \n");
			cardText.setText(c.getSideTwo());
			// Show side one again as a reminder to the user
			cardStatistics.setLabel("\n\n\n" + c.getSideOne());
			sideOne = false;
			break;
		default:
			break;
		}
	}


	/**
	 * Display the statistics. If showCommands is set to true, also show
	 * commands specific for this screen
	 */
	private void displayStatistics(boolean showCommands) {
		int done = init.getDoneAmount(cards);
		int incorrectAmount = totalOfCards - done;
		if (showCommands) {
			// show the commands for statistics
			if (incorrectAmount > 0) {
				Command[] commands = { nextSetCommand, reviewCommand,
						exitCommand };
				setCommands(commands, mainForm);
			} else {
				Command[] commands = { nextSetCommand, restartCommand,
						exitCommand };
				setCommands(commands, mainForm);
			}
		} else {
			setCommands(null, mainForm);
		}
		cardText.setLabel("");
		cardText.setText("");
		cardStatistics.setLabel("STATISTICS" + "\n\n" + "Total of cards: "
				+ totalOfCards
				+ "\n"
				+ "Correct: "
				+ done
				+ "\n"
				+ "Incorrect: "
				+ incorrectAmount
				+ "\n"
				+ "Viewed this session: "
				+ totalReviewed
				+ "\n"
				+ "Correct this session: "
				+ totalDoneSession
				+ "\n"
				+ "Total viewed: "
				+ soc.getTotalNumberOfDisplayedCards()
				+ "\n"
				+ "Session duration: "
				+ vocaUtil.getStudyTimeAsString(sessionStudyTime)
				+ "\n"
				+ "Total Study time: "
				+ vocaUtil.getStudyTimeAsString(soc
						.getTotalStudiedTimeInMiliseconds()));
	}

	public void commandAction(Command cmd, Displayable disp) {
		this.sessionStudyTime = Initializer.updateSessionStudyTime(
				sessionStudyTime, lastActivityTime, maxIdleTime);
		lastActivityTime = System.currentTimeMillis();

		if (cmd == quitCommand) {
			// Display statistics
			// update the total study time for the set
			long previousTotalStudiedTime = soc
					.getTotalStudiedTimeInMiliseconds();
			long newTotalStudiedTime = previousTotalStudiedTime
					+ sessionStudyTime;
			soc.setTotalStudiedTimeInMiliseconds(newTotalStudiedTime);
			displayStatistics(true);
		}

		if (cmd == exitCommand) {
			destroyApp(false);
			notifyDestroyed();
		}

		if (cmd == reviewCommand) {
			// show again the cards in the set not marked as 'done'
			amountToReview = totalOfCards - init.getDoneAmount(cards);
			totalReviewed = 0;
			// TODO: this is not very efficient
			// all the cards are going to be tested
			// for 'done' mark. should return only
			// the marked as wrong...
			init.initIndexes(cards);
			displayNextCard();
		}

		if (cmd == nextSetCommand) {
			//save the previous set
			save(soc);
			// Load a new set of cards
			displayLoadSetMenu();
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
			totalDoneSession = 0;
			
			init.initIndexes(cards);
			displayNextCard();
		}

		if (cmd == turnCommand) {
			// Update the total of cards viewed
			soc.setTotalNumberOfDisplayedCards(soc
					.getTotalNumberOfDisplayedCards() + 1);
			// update the number of times this card has been viewed
			c.setViewedCounter(c.getViewedCounter() + 1);
			// update the last time this card was viewed
			c.setLastTimeViewed(System.currentTimeMillis());
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
			c.setMarkedDoneCounter(c.getMarkedDoneCounter() + 1);
			c.setLastTimeMarkedDone(System.currentTimeMillis());
			totalReviewed++;
			totalDoneSession++;
			displayNextCard();
		}

		if (cmd == wrongCommand) {
			// Just show next card, side one
			totalReviewed++;
			displayNextCard();
		}

		if (cmd == loadSetCommand) {
			// Load the set selected
			List l = (List) disp;
			int index = l.getSelectedIndex();
			String selectedItem = l.getString(index);
			displaySetOfCards(selectedItem);
		}
		
		if (cmd == deleteCommand) {
			//TODO: display a confirmation dialog
			List l = (List) disp;
			int index = l.getSelectedIndex();
			String selectedItem = l.getString(index);
			init.deleteSetOfCardsFromRMS(selectedItem);
			displayDeleteSetMenu();
		}
		
		if (cmd == selectCommand) {
			//which command was selected?
			List l = (List) disp;
			int index = l.getSelectedIndex();
			String selectedItem = l.getString(index);
			if(selectedItem.equalsIgnoreCase(STUDY_SET)) {
			displayLoadSetMenu();
			}else if(selectedItem.equalsIgnoreCase(DELETE_SET)) {
				displayDeleteSetMenu();				
			}
		}
		
		if (cmd == back) {
			displayInitialOptionsMenu();
		}
		
		
	}
}
