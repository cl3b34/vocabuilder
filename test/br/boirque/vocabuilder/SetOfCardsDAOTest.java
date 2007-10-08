package br.boirque.vocabuilder;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import junit.framework.TestCase;

public class SetOfCardsDAOTest extends TestCase {

	SetOfCardsDAO socdao;
	SetOfCards setOfCards;
	protected void setUp() throws Exception {
		socdao = new SetOfCardsDAO();
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

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoadSet() {
		SetOfCards soc = socdao.LoadSet();
		assertNotNull(soc);
	}

	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		socdao.SaveState(setOfCards);
		SetOfCards soc = socdao.LoadSet();
		assertNotNull(soc);
		//check if the recovered data from the set is correct
		assertFalse(soc.isDone());
		assertEquals("finnish", soc.getSideOneTitle());
		assertEquals("Semantic primitives", soc.getTitle());
		assertEquals(10000L, soc.getTotalStudiedTimeInMiliseconds());
		//recover the flash cards from the set and check if the data is 
		//correct
		Vector flashCards = soc.getFlashCards();
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("Auto", flashCard1.getSideOne());
		assertTrue(flashCard1.isDone());
		FlashCard flashCard2 = ((FlashCard)(flashCards.elementAt(1)));
		assertEquals("egg", flashCard2.getSideTwo());
		assertFalse(flashCard2.isDone());
	}

}
