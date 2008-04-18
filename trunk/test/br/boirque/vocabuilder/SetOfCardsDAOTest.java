package br.boirque.vocabuilder;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;

import j2meunit.framework.*;

public class SetOfCardsDAOTest extends TestCase {

	
	SetOfCards setOfCards;

	/**
	 * Default constructor
	 */
	public SetOfCardsDAOTest() {
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
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
		setOfCards = new SetOfCards("Semantic primitives",false, 10000L, v, 0,20L, 10000L,100);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		setOfCards = null;
	}

	public void testLoadState() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		SetOfCards soc = socdao.loadState();
		assertNull(soc); //there is nothing in the set
	}

	
	
	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
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
	
	public void testLoadCard() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		FlashCard flashCard1 = socdao.loadCard(1);
		assertEquals("value is not equal","Auto", flashCard1.getSideOne());
		assertEquals("value is not equal","Suomi", flashCard1.getSideOneTitle());
		assertEquals("value is not equal","Car", flashCard1.getSideTwo());
		assertEquals("value is not equal","English", flashCard1.getSideTwoTitle());
		assertEquals("value is not equal","runs on the street", flashCard1.getTip());
		assertTrue("should be done", flashCard1.isDone());
	}
	
	/*
	 * setOfCardsTitle setOfCardsIsDone setOfCardsStudyTime totalCardsDisplayed
	 *	lastTimeSetViewed lastTimeSetMarkedDone markedDoneSetCounter
	 */
	public void testLoadSetMetadata() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		SetOfCards soc = socdao.loadSetMetadata(1);
		assertNotNull("set null", soc);
		//check if the recovered data from the set is correct
		assertEquals("value is not equal","Semantic primitives", soc.getTitle());
		assertTrue("set should not be done", !soc.isDone());
		assertEquals("value is not equal",10000L, soc.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",0, soc.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, soc.getLastTimeViewed());
		assertEquals("value is not equal",10000L, soc.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, soc.getMarkedDoneCounter());
	}

	public void testGetRecordCount() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		int recordCount = socdao.getRecordCount();
		// the record store should have 2 records
		assertEquals(2, socdao.getRecordCount()); 	
	}
	
	public void testResetState() throws RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		//assert that the recordstore is empty		
		assertEquals(0, socdao.getRecordCount()); 
	}

	public void testDummy() {
		assertTrue(true);
	}
		
	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsDAOTest("testLoadState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadState(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testSaveState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testSaveState(); 
			} 
		}));

		testsuite.addTest(new SetOfCardsDAOTest("testLoadCard", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadCard(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetMetadata", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetMetadata(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testGetRecordCount", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testGetRecordCount(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testResetState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testResetState(); 
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
