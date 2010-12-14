package br.boirque.vocabuilder.view;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

import br.boirque.vocabuilder.controller.Initializer;
import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.util.VocaUtil;
//import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Ticker;

public class Vocabuilder extends MIDlet implements CommandListener {
	// initial list menu options
	private static final String DELETE_SET = "delete list";
	private static final String STUDY_SET = "study";
	private static final String DOWNLOAD_SET = "download";

    // File Chooser API availability flag
    private static boolean isJsr75Available = false;

	// Commands
	private Command exitCommand = new Command("Exit", Command.EXIT, 3);
	private Command statsCommand = new Command("Stats", Command.EXIT, 1);
	private Command turnCommand = new Command("Turn", Command.OK, 1);
	private Command doneCommand = new Command("Done", Command.OK, 1);
	private Command wrongCommand = new Command("Wrong", Command.CANCEL, 1);
	private Command restartCommand = new Command("Restart", Command.SCREEN, 2);
	private Command reviewCommand = new Command("Review", Command.SCREEN, 1);
	private Command nextSetCommand = new Command("Load another", Command.SCREEN,2);
	private Command loadSetCommand = new Command("Load", Command.OK, 1);
	private Command deleteCommand = new Command("Delete", Command.SCREEN, 1);
	private Command selectCommand = new Command("Select", Command.SCREEN, 1);
	private Command back = new Command("Back", Command.BACK,1);
	private Command downloadSetCommand = new Command("Download",Command.SCREEN, 1);
    private Command openFileChooserCommand = new Command("Browse",Command.SCREEN, 2);
    private Command reverseModeOffCommand = new Command("Turn reverse OFF",Command.SCREEN, 3);
    private Command reverseModeOnCommand = new Command("Turn reverse ON",Command.SCREEN, 3);

	// UI elements
	private Form mainForm;
	private StringItem cardText;
    private StringItem cardTitle;
	private StringItem cardStatistics;
    private FileSelector fileSelector = null;

        // Beans
	private SetOfCards soc;
	private Vector cards;
	FlashCard c;

	// Statistics
	private int statsTotalDoneSession = 0;
	private int statsTotalReviewedSession=0;
	private long sessionStudyTime = 0;

	// Time last user interaction happened
	private long lastActivityTime = 0; 
	
	// thirty Seconds, after that we ignore as the user is inactive
	private final long maxIdleTime = 30000L; 

	// Which side is being displayed
	boolean sideOne;

        // 0= Normal mode (Side One comes first)
        // 1= Reverse mode
        int reverseMode = 0;

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

    //isJsr75Available = VocaUtil.checkJsr75(); //have to be moved out from startApp()
    isJsr75Available = true;
            // if there is only one list, doesn't display the menu
//		String[] setNames = (init.loadUniqueSetNames());
//		if (setNames.length == 1) {
//			displaySetOfCards(setNames[0]);
//		} else {
			//displayLoadSetMenu();
			displayInitialOptionsMenu();
//		}
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

	protected void displayLoadSetMenu() {
		List listSelection = new List("Select a set", Choice.IMPLICIT, init
				.loadUniqueSetNames(), null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(back);
                listSelection.addCommand(exitCommand);
                listSelection.addCommand(loadSetCommand);
                if (reverseMode == 0) {
                    listSelection.addCommand(reverseModeOnCommand);
                    listSelection.setTicker(null);
                }
                else {
                    listSelection.addCommand(reverseModeOffCommand);
                    listSelection.setTicker(new Ticker("Reverse mode"));
                }
                listSelection.setSelectCommand(loadSetCommand);

                if (isJsr75Available) {
                	
                	listSelection.addCommand(openFileChooserCommand);

                }

		
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}

	private void displayMainForm() {
		mainForm = new Form("Vocabuilder");
		
        cardTitle = new StringItem(null, "\n");
        cardText = new StringItem(null, "");
		cardStatistics = new StringItem("", "");
		
                // alertStats = new Alert("","",null,AlertType.INFO);
        cardTitle.setLayout(Item.LAYOUT_LEFT);
        cardTitle.setLayout(Item.LAYOUT_EXPAND);
        

        mainForm.append(cardTitle);

//                Image cardMarker = null;
//
//                try {
//                    cardMarker = Image.createImage("/Favourites.png");
//                    
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }

                
                //mainForm.append(cardMarker);
        mainForm.append(cardText);
		mainForm.append(cardStatistics);
		mainForm.setCommandListener(this);
	}

	private void displayInitialOptionsMenu() {
		String[] actions = {STUDY_SET, DOWNLOAD_SET ,DELETE_SET};
		List listSelection = new List("Select action", Choice.IMPLICIT, actions , null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(exitCommand);
                listSelection.addCommand(selectCommand);
		listSelection.setSelectCommand(selectCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}
	
	private void displayDeleteSetMenu() {
		List listSelection = new List("DELETE set", Choice.IMPLICIT, init.loadOnProgressSetNames() , null);
		// Command[] c = {exitCommand, loadSetCommand};
		// setCommands(c, listSelection);
		listSelection.addCommand(back);
                listSelection.addCommand(deleteCommand);
		listSelection.setSelectCommand(deleteCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);
	}
	
	protected void displaySetOfCards(String setToLoad) {
		
		// initialize the application. Load the list
		soc = init.loadSet(setToLoad);

		displayMainForm();
		mainForm.setTitle(soc.getSetName());
		cards = soc.getFlashCards();
		resetStatsCounters();
		soc.setLastTimeViewed(System.currentTimeMillis());
		lastActivityTime = System.currentTimeMillis();
		// statsTotalDoneSession = 0;
		//init.initIndexes(cards);

		displayNextCard();
		Display.getDisplay(this).setCurrent(mainForm);

	}

	
	private void displayNextCard() {
		int index = init.getNextCardIndex(cards);
		if (index >= 0) {
			c = (FlashCard) cards.elementAt(index);
			displayCardSide(1);
		} else {
			// Reached the end of the set.
			// Mark the set as done if all cards are marked done
			soc.setDone(true);
			soc.setLastTimeMarkedDone(System.currentTimeMillis());
			soc.setMarkedDoneCounter(soc.getMarkedDoneCounter() + 1);
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
		mainForm.removeCommand(statsCommand);
		mainForm.removeCommand(loadSetCommand);

		if (commands != null) {
			// add the desired commands
			for (int i = 0; i < commands.length; i++) {
				Command cmd = commands[i];
				commandHost.addCommand(cmd);
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
			Command[] commands = { turnCommand, statsCommand };
			setCommands(commands, mainForm);

			if (reverseMode == 0) {
                            cardTitle.setText(c.getSideOneTitle() + ":\n");
                            cardText.setText(c.getSideOne());
                        }
                        else {
                            cardTitle.setText(c.getSideTwoTitle() + ":\n");
                            cardText.setText(c.getSideTwo());
                        }

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
            
			if (reverseMode==0) {
				cardTitle.setText(c.getSideTwoTitle() + ":\n");
				cardText.setText(c.getSideTwo());
                // Show a reminder to the user
                cardStatistics.setLabel("\n\n\n" + c.getSideOne());
             }
             else {
                cardTitle.setText(c.getSideOneTitle() + ":\n");
                cardText.setText(c.getSideOne());
                // Show a reminder to the user
                cardStatistics.setLabel("\n\n\n" + c.getSideTwo());
             }
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
		int incorrectAmount = Initializer.getTotalOfCards() - done;
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
		cardTitle.setText("");
		cardText.setText("");
		cardStatistics.setLabel("STATISTICS" + "\n\n" + "Total of cards: "
				+ Initializer.getTotalOfCards()
				+ "\n"
				+ "Correct: "
				+ done
				+ "\n"
				+ "Incorrect: "
				+ incorrectAmount
				+ "\n"
				+ "Viewed this session: "
				+ statsTotalReviewedSession
				+ "\n"
				+ "Correct this session: "
				+ statsTotalDoneSession
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

        private void displayFileChooser () {

		Display.getDisplay(this).setCurrent(fileSelector);
        }

	
	private void resetStatsCounters() {
		statsTotalDoneSession = 0;
		statsTotalReviewedSession=0;
		sessionStudyTime = 0;
	}
	
	
	
	public void commandAction(Command cmd, Displayable disp) {
		this.sessionStudyTime = Initializer.updateSessionStudyTime(
				sessionStudyTime, lastActivityTime, maxIdleTime);
		lastActivityTime = System.currentTimeMillis();

		if (cmd == statsCommand) {
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
			soc = init.restartSet(soc);
			resetStatsCounters();
			
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
			//update the counters
			statsTotalReviewedSession++;
//			Initializer.setAmountToReview(Initializer.getAmountToReview()-1);
//			Initializer.setTotalReviewed(Initializer.getTotalReviewed()+1);
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
			statsTotalDoneSession++;
			displayNextCard();
		}

		if (cmd == wrongCommand) {
			// show next card, side one
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

		if (cmd == downloadSetCommand) {
			// Download the set selected
			List l = (List) disp;
			int index = l.getSelectedIndex();
			String selectedItem = l.getString(index);
			Thread t = new Thread(new Initializer(selectedItem));
			//System.out.println("starting download");
			t.start();
//			Thread.sleep(1000);
			displayLoadSetMenu();			
		}

		if (cmd == selectCommand ) {
			//which command was selected?
			List l = (List) disp;
			int index = l.getSelectedIndex();
			String selectedItem = l.getString(index);
			if(selectedItem.toLowerCase().equals(STUDY_SET)) {
				displayLoadSetMenu();
			}else if(selectedItem.toLowerCase().equals(DELETE_SET)) {
				displayDeleteSetMenu();				
			}else if(selectedItem.toLowerCase().equals(DOWNLOAD_SET)) {
				displayDownloadSetMenu();				
			}
		}

        if (cmd == openFileChooserCommand) {

        	if (fileSelector == null) {
        		
    	       	fileSelector = new FileSelector(this);
    	       	
        	}
            displayFileChooser();
      	}

		if (cmd == back) {
            displayInitialOptionsMenu();
		}

        if (cmd == reverseModeOffCommand) {
            reverseMode = 0;
            displayLoadSetMenu();
        }
        
        if (cmd == reverseModeOnCommand) {
            reverseMode = 1;
            displayLoadSetMenu();
        }
        //System.out.println("cmd pri=" + String.valueOf(cmd.getPriority()) +" type="+ String.valueOf(cmd.getCommandType()) + " longlbl=" + cmd.getLongLabel() );



	}

	
	private void displayDownloadSetMenu() {
		// TODO retrieve the categories first, them show the sets of this category
		List listSelection = new List("Select a set", Choice.IMPLICIT, init
				.loadDownloadableSets("dummy"), null);
		listSelection.addCommand(back);
                listSelection.addCommand(downloadSetCommand);
		listSelection.setSelectCommand(downloadSetCommand);
		listSelection.setCommandListener(this);
		Display.getDisplay(this).setCurrent(listSelection);		
	}
}
