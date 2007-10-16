package br.boirque.vocabuilder.view;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;

public class Vocabuilder extends MIDlet implements CommandListener {
    private Command exitCommand;
    private TextBox tbox;
    
    SetOfCardsDAO socdao;
	SetOfCards setOfCards;

    public Vocabuilder() {
        exitCommand = new Command("Exit", Command.EXIT, 1);
        tbox = new TextBox("Hello world MIDlet", "Hello World!", 150, 0);
        tbox.addCommand(exitCommand);
        tbox.setCommandListener(this);
    }

    protected void startApp() {
      
        setUp();
        FlashCard fc = testSaveState();
        String string = fc.getSideOne() + " " + fc.getSideTwo()  + " " + fc.getTip()  + " " + fc.isDone();
        String text= string+"cleber";
		tbox.setString(text);
		Display.getDisplay(this).setCurrent(tbox);
    }
    
    protected void setUp() {
		try {
			socdao = new SetOfCardsDAO();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setOfCards = new SetOfCards();
		setOfCards.setDone(false);
		setOfCards.setSideOneTitle("finnish");
		setOfCards.setSideTwoTitle("english");
		setOfCards.setTitle("Semantic primitives");
		setOfCards.setTotalStudiedTimeInMiliseconds(10000L);

		//Create a vector with flash cards
		Vector v = new Vector();
		FlashCard c1 = new FlashCard();
		c1.setDone(true);
		c1.setSideOne("Auto");
		c1.setSideTwo("car");
		c1.setTip("goes on the street");
		v.addElement(c1);

		FlashCard c2 = new FlashCard();
		c2.setDone(false);
		c2.setSideOne("muna");
		c2.setSideTwo("egg");
		c2.setTip("Did it came before the chicken?");
		v.addElement(c2);

		setOfCards.setFlashCards(v);
	}

	public void testLoadSet() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCards soc = socdao.loadState();
		

	}

	public FlashCard testSaveState() {
		SetOfCards soc = null;
		try {
			socdao.saveState(setOfCards);
			soc = socdao.loadState();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//check if the recovered data from the set is correct
//		assertTrue(soc.isDone());
//		assertEquals("finnish", soc.getSideOneTitle());
//		assertEquals("Semantic primitives", soc.getTitle());
//		assertEquals(10000L, soc.getTotalStudiedTimeInMiliseconds());
		//recover the flash cards from the set and check if the data is 
		//correct
		Vector flashCards = soc.getFlashCards();
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
//		assertEquals("Auto", flashCard1.getSideOne());
//		assertTrue(flashCard1.isDone());
//		FlashCard flashCard2 = ((FlashCard)(flashCards.elementAt(1)));
//		assertEquals("egg", flashCard2.getSideTwo());
		//assert that this card is NOT done
//		assertTrue(!flashCard2.isDone());
		
		return flashCard1;
	}


    protected void pauseApp() {}
    protected void destroyApp(boolean bool) {}

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
    }
}

