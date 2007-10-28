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
	private Command wrongCommand = new Command("Wrong", Command.SCREEN, 1);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command againCommand = new Command("Again", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 2);
	private Command backCommand = new Command("Back", Command.BACK, 1);

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
//		cardText.setPreferredSize(-1, cardText.getPreferredHeight()+3);
		cardText.setText("height " + cardText.getPreferredHeight() + " " + cardText.getMinimumHeight());
				
		mainForm.append(cardText);
		
		
		mainForm.addCommand(exitCommand);
		mainForm.addCommand(turnCommand);
//		 mainForm.addCommand(doneCommand);
//		 mainForm.addCommand(wrongCommand);
//		 mainForm.addCommand(restartCommand);
//		 mainForm.addCommand(againCommand);
//		 mainForm.addCommand(reviewCommand);
//		mainForm.addCommand(backCommand);
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
			String setTitle = soc.getTitle(); 
			cards = soc.getFlashCards();
			// for (int i = 0; i < cards.size(); i++) {
			c = (FlashCard) cards.elementAt(0);
			// if the card is not done yet, we show it
			if (!c.isDone()) {
				mainForm.setTitle(setTitle);
				
				cardText.setLabel(c.getSideOneTitle() + ": \n");
				cardText.setText(c.getSideOne());
				// set the index of the current showing card
				currentCardIndex = 0;
				// mark what is the currently displayed side
				sideOne = true;
			}
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

	protected void pauseApp() {
	}

	protected void destroyApp(boolean bool) {
	}

	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == exitCommand) {
			// should save the state and exit
			destroyApp(false);
			notifyDestroyed();
		}

		if (cmd == turnCommand) {
			// turn the card
			if (!sideOne) {
				//remove the commands for side two
				mainForm.removeCommand(doneCommand);
				mainForm.removeCommand(wrongCommand);
				//show the commands for side one
				mainForm.addCommand(turnCommand);
				mainForm.addCommand(exitCommand);
				cardText.setText(c.getSideOne());
				
				sideOne = true;
			} else {
				//remove the commands for side one
				mainForm.removeCommand(turnCommand);
				mainForm.removeCommand(exitCommand);
				//show the commands for side two
				mainForm.addCommand(doneCommand);
				mainForm.addCommand(wrongCommand);
				
				mainForm.setTitle(c.getSideTwoTitle());
				cardText.setText(c.getSideTwo());
				
				sideOne = false;
			}
		}

		if (cmd == doneCommand) {
			//mark card as done and show next card, side one
			c.setDone(true);
			currentCardIndex = currentCardIndex + 1;
			if (currentCardIndex < cards.size()) {
				c = (FlashCard) cards.elementAt(currentCardIndex);

				mainForm.setTitle(c.getSideOneTitle());
				cardText.setText(c.getSideOne());
				//remove the commands for side two
				mainForm.removeCommand(doneCommand);
				mainForm.removeCommand(wrongCommand);
				//show the commands for side one
				mainForm.addCommand(turnCommand);
				mainForm.addCommand(exitCommand);
				sideOne = true;
			}
		}

		if (cmd == wrongCommand) {
			//Just show next card, side one
			currentCardIndex = currentCardIndex + 1;
			if (currentCardIndex < cards.size()) {
				c = (FlashCard) cards.elementAt(currentCardIndex);

				mainForm.setTitle(c.getSideOneTitle());
				cardText.setText(c.getSideOne());
				//remove the commands for side two
				mainForm.removeCommand(doneCommand);
				mainForm.removeCommand(wrongCommand);
				//show the commands for side one
				mainForm.addCommand(turnCommand);
				mainForm.addCommand(exitCommand);
				sideOne = true;
			}
		}

	}
}
