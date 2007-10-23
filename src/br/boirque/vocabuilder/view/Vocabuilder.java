package br.boirque.vocabuilder.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.controller.Initializer;
import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;

public class Vocabuilder extends MIDlet implements CommandListener {
	private Command exitCommand = new Command("Exit", Command.EXIT, 3);
	private Command turnCommand = new Command("Turn", Command.SCREEN, 1);
	private Command doneCommand = new Command("Done", Command.SCREEN, 2);
	private Command wrongCommand = new Command("Wrong", Command.SCREEN, 1);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command againCommand = new Command("Again", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 2);
	private Command backCommand = new Command("Back", Command.BACK, 1);

	private TextBox tbox;

	private SetOfCards soc;
	private Vector cards;
	FlashCard c;
	private int currentCardIndex = -1;
	boolean sideOne;

	public Vocabuilder() {
		// tbox = new TextBox("Flash card title", "Flash card text side one!",
		// 150, TextField.ANY);
		tbox = new TextBox("", "", 150, TextField.ANY);
		tbox.addCommand(turnCommand);
		// tbox.addCommand(doneCommand);
		// tbox.addCommand(wrongCommand);
		// tbox.addCommand(restartCommand);
		// tbox.addCommand(againCommand);
		// tbox.addCommand(reviewCommand);
		//tbox.addCommand(backCommand);
		tbox.addCommand(exitCommand);
		tbox.setCommandListener(this);
	}

	/**
	 * Takes care of initializing the app
	 */
	protected void startApp() {
		// initialize the application. This will load the list from somewhere
		Initializer initializer = new Initializer();

		try {
			soc = initializer.initializeApp();
			// String setTitle = soc.getTitle(); //dont know what to do with
			// that yet
			cards = soc.getFlashCards();
			// for (int i = 0; i < cards.size(); i++) {
			c = (FlashCard) cards.elementAt(0);
			// if the card is not done yet, we show it
			if (!c.isDone()) {
				tbox.setTitle(c.getSideOneTitle());
				tbox.setString(c.getSideOne());
				// set the index of the current showing card
				currentCardIndex = 0;
				// mark what is the currently displayed side
				sideOne = true;
			}
			// }
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

		Display.getDisplay(this).setCurrent(tbox);
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
				tbox.removeCommand(doneCommand);
				tbox.removeCommand(wrongCommand);
				//show the commands for side one
				tbox.addCommand(turnCommand);
				tbox.addCommand(exitCommand);
//				tbox.setTitle(c.getSideOneTitle());
//				tbox.setString(c.getSideOne());
				
				sideOne = true;
			} else {
				//remove the commands for side one
				tbox.removeCommand(turnCommand);
				tbox.removeCommand(exitCommand);
				//show the commands for side two
				tbox.addCommand(doneCommand);
				tbox.addCommand(wrongCommand);
				
//				tbox.setTitle(c.getSideTwoTitle());
//				tbox.setString(c.getSideTwo());
				
				sideOne = false;
			}
		}

		if (cmd == doneCommand) {
			//mark card as done and show next card, side one
			c.setDone(true);
			currentCardIndex = currentCardIndex + 1;
			if (currentCardIndex < cards.size()) {
				c = (FlashCard) cards.elementAt(currentCardIndex);

//				tbox.setTitle(c.getSideOneTitle());
//				tbox.setString(c.getSideOne());
				//remove the commands for side two
				tbox.removeCommand(doneCommand);
				tbox.removeCommand(wrongCommand);
				//show the commands for side one
				tbox.addCommand(turnCommand);
				tbox.addCommand(exitCommand);
				sideOne = true;
			}
		}

		if (cmd == wrongCommand) {
			//Just show next card, side one
			currentCardIndex = currentCardIndex + 1;
			if (currentCardIndex < cards.size()) {
				c = (FlashCard) cards.elementAt(currentCardIndex);

//				tbox.setTitle(c.getSideOneTitle());
//				tbox.setString(c.getSideOne());
				//remove the commands for side two
				tbox.removeCommand(doneCommand);
				tbox.removeCommand(wrongCommand);
				//show the commands for side one
				tbox.addCommand(turnCommand);
				tbox.addCommand(exitCommand);
				sideOne = true;
			}
		}

	}
}
