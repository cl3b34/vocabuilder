package br.boirque.vocabuilder;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;

import j2meunit.framework.*;

public class SetOfCardsDAOTest extends TestCase {

	
	SetOfCards setOfCards;

	/**
	 * Default contructor
	 */
	public SetOfCardsDAOTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seens to be required by J2MEUnit
	 */
	public SetOfCardsDAOTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {	
		//Create a vector with flash cards
		Vector v = new Vector();
		FlashCard c1 = new FlashCard("Auto","Suomi","Car","English",true,"runs on the street");
		v.addElement(c1);

		FlashCard c2 = new FlashCard("Muna","Fin","Egg","Eng",false,"who came first?");
		v.addElement(c2);

		//Create a set and assign the vector of cards to it
		setOfCards = new SetOfCards("Semantic primitives",false, 10000L, v );
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		setOfCards = null;
	}

	public void testLoadSet() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		SetOfCards soc = socdao.loadState();
		assertNotNull(soc);
	}

	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
//		socdao.resetState();
		socdao.saveState(setOfCards);
		SetOfCards soc = socdao.loadState();
		assertNotNull("set null", soc);
		//check if the recovered data from the set is correct
		assertTrue("set should not be done", !soc.isDone());
		assertEquals("value is not equal","Semantic primitives", soc.getTitle());
		assertEquals("value is not equal",10000L, soc.getTotalStudiedTimeInMiliseconds());
		//recover the flash cards from the set and check
		// if the data is matches the original
		Vector flashCards = soc.getFlashCards();
		//Card 1
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("value is not equal","Auto", flashCard1.getSideOne());
		assertEquals("value is not equal","Suomi", flashCard1.getSideOneTitle());
		assertEquals("value is not equal","Car", flashCard1.getSideTwo());
		assertEquals("value is not equal","English", flashCard1.getSideTwoTitle());
		assertEquals("value is not equal","runs on the street", flashCard1.getTip());
		assertTrue("should be done", flashCard1.isDone());
		
		// Card 2
		FlashCard flashCard2 = ((FlashCard)(flashCards.elementAt(1)));
		assertEquals("value is not equal","Muna", flashCard2.getSideOne());
		assertEquals("value is not equal","Fin", flashCard2.getSideOneTitle());
		assertEquals("value is not equal","Egg", flashCard2.getSideTwo());
		assertEquals("value is not equal","Eng", flashCard2.getSideTwoTitle());
		assertEquals("value is not equal","who came first?", flashCard2.getTip());
		assertTrue("should not be done", !flashCard2.isDone());
	}

	public void testDummy() {
		assertTrue(true);
	}
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsDAOTest("testLoadSet", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSet(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testSaveState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testSaveState(); 
			} 
		}));

		testsuite.addTest(new SetOfCardsDAOTest("testDummy", new TestMethod(){ 
			public void run(TestCase tc){
				((SetOfCardsDAOTest) tc).testDummy(); 
			} 
		}));

		
		return testsuite;
	}
}
