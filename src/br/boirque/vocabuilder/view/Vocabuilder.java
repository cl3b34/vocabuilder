package br.boirque.vocabuilder.view;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import br.boirque.vocabuilder.controller.Initializer;
import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;

public class Vocabuilder extends MIDlet implements CommandListener {
	private Command exitCommand = new Command("Exit", Command.EXIT, 3);
	private Command turnCommand = new Command("Turn", Command.SCREEN, 1);
	private Command doneCommand = new Command("Done", Command.SCREEN, 1);
	private Command wrongCommand = new Command("Wrong", Command.SCREEN, 2);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 1);
	private Command nextSet = new Command("Next set", Command.SCREEN, 1);

	private Form mainForm;
	private StringItem cardText;

	private SetOfCards soc;
	private Vector cards;
	FlashCard c;

	private int currentCardIndex = -1;
	boolean sideOne;
	
	public Vocabuilder() {

		mainForm = new Form("Vocabuilder");
		cardText = new StringItem("", "Card Text");
		cardText.setLayout(Item.LAYOUT_CENTER);
		cardText.setLayout(Item.LAYOUT_EXPAND);
		// cardText.setPreferredSize(-1, cardText.getPreferredHeight()+3);
//		cardText.setText("height " + cardText.getPreferredHeight() + " "
//		+ cardText.getMinimumHeight());

		mainForm.append(cardText);
		mainForm.setCommandListener(this);
	}

	/**
	 * Takes care of initializing the app
	 */
	protected void startApp() {
		// initialize the application. Load the list
		Initializer initializer = new Initializer();

		try {
			soc = initializer.initializeApp();
			//TODO - transfer this initialization 
			//code to a controller class
			//Look for a set of cards that is not done yet
			while(soc.isDone()) {
				soc = initializer.initializeApp();
			}
			String titleOfThisSet = soc.getTitle();
			mainForm.setTitle(titleOfThisSet);
			cards = soc.getFlashCards();
			displayFirstNotDoneCard();
			Display.getDisplay(this).setCurrent(mainForm);
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void displayFirstNotDoneCard() {
		// Look for a card that is not done yet and display it
		int i = 0;
		c = (FlashCard) cards.elementAt(i);
		boolean notFound = true;
		while(notFound) {
			if(c.isDone()) {
				//get the next card
				i++;
				c = (FlashCard) cards.elementAt(i);
			}else {
				//display the card and leave the loop
				displaySide(1);
				// set the index of the current showing card
				currentCardIndex = i;
				notFound = false;					
			}								
		}
	}

	private void displayNextNotDoneCard() {
		c = (FlashCard) cards.elementAt(currentCardIndex);
		boolean notFound = true;
		while(notFound) {
			if(c.isDone()) {
				//get the next card
				currentCardIndex++;
				c = (FlashCard) cards.elementAt(currentCardIndex);
			}else {
				//display the card and leave the loop
				displaySide(1);
				notFound = false;					
			}								
		}
	}
	
	
	protected void pauseApp() {
	}

	protected void destroyApp(boolean bool) {
	}
	
	private void setCommands(Command[] commands) {
		//remove all commands
		mainForm.removeCommand(doneCommand);
		mainForm.removeCommand(wrongCommand);
		mainForm.removeCommand(exitCommand);
		mainForm.removeCommand(turnCommand);
		mainForm.removeCommand(restartCommand);
		mainForm.removeCommand(reviewCommand);
		mainForm.removeCommand(nextSet);
		
		//add the desired commands
		for(int i=0; i< commands.length; i++) {
			Command c = commands[i];
			mainForm.addCommand(c);
		}
	}

	private void displaySide(int side) {
		switch (side) {
		case 1:
			Command[] commands = {turnCommand, exitCommand};
			setCommands(commands);
			cardText.setLabel(c.getSideOneTitle() + ": \n");
			cardText.setText(c.getSideOne());
			sideOne = true;
			break;
		case 2:
			Command[] cmds = {doneCommand, wrongCommand};
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
		for(int i=0; i<cards.size(); i++) {
			c = (FlashCard) cards.elementAt(i);
			if(c.isDone()) {
				doneAmount++;
			}
		}
		return doneAmount;
	}
	
	private void displayStatistics() {
		int totalOfCards = cards.size();
		int done = getDoneAmount();
		int incorrectAmount = totalOfCards - done;
		// TODO - fix this floating point calculation.
		// MIDP doesnt have float or double
//		int donePercent = (done/totalOfCards)*100;
//		int wrongPercent = 100 - donePercent;
		
		// show the commands for statistics
		if(incorrectAmount > 0) {
			Command[] commands = {reviewCommand, exitCommand};
			setCommands(commands);		
		}else {
			Command[] commands = {restartCommand, exitCommand};
			setCommands(commands);		
		}
		cardText.setLabel("STATISTICS" + "\n");	
	
//		cardText.setText("Total of cards: " + totalOfCards + "\n" +
//				"Correct: " + done + " (" + done/totalOfCards + "%)" + "\n" +
//				"Incorrect: " + incorrectAmount + " (" + wrongPercent + "%)");	

		cardText.setText("Total of cards: " + totalOfCards + "\n" +
				"Correct: " + done + "\n" +
				"Incorrect: " + incorrectAmount );	
	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == exitCommand) {
			// should save the state and exit
			destroyApp(false);
			notifyDestroyed();
		}
		
		if (cmd == reviewCommand) {
			// show again the cards in the set not marked as 'done'
			displayFirstNotDoneCard();
		}
		
		if (cmd == nextSet) {
			// Load a new set of cards
		}
		
		if (cmd == restartCommand) {
			// remove the 'done' mark from the set and all the cards
			// and start over the set
			// TODO: SHOULD ask for confirmation
			for(int i=0; i< cards.size(); i++) {
				c = (FlashCard) cards.elementAt(i);
				c.setDone(false);
			}
			displayFirstNotDoneCard();			
		}
		
		if (cmd == turnCommand) {
			// turn the card
			if (!sideOne) {
				displaySide(1);
			} else {
				displaySide(2);
			}
		}

		if (cmd == doneCommand) {
			// mark card as done and show next card, side one
			c.setDone(true);
			currentCardIndex = currentCardIndex + 1;
			// TODO - keep the done amount in a class variable to
			// save processing
			if (getDoneAmount() < cards.size()) {
				displayNextNotDoneCard();
			} else {
				// got to the end of the set.
				displayStatistics();
			}
		}

		if (cmd == wrongCommand) {
			// Just show next card, side one
			currentCardIndex = currentCardIndex + 1;
			// TODO - keep the done amount in a class variable to
			// save processing
			if (getDoneAmount() < cards.size()) {
				displayNextNotDoneCard();
			}else {
				// got to the end of the set.
				displayStatistics();
			}
		}
	}
}
