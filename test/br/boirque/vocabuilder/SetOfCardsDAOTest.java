package br.boirque.vocabuilder;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

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

	public void testLoadSet() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		SetOfCards soc = socdao.LoadSet();
		assertNotNull(soc);
	}

	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
//		socdao.resetState();
		socdao.SaveState(setOfCards);
		SetOfCards soc = socdao.LoadSet();
		assertNotNull("set null", soc);
		//check if the recovered data from the set is correct
		assertTrue("set should not be done", !soc.isDone());
		assertEquals("value is not equal","finnish", soc.getSideOneTitle());
		assertEquals("value is not equal","Semantic primitives", soc.getTitle());
		assertEquals("value is not equal",10000L, soc.getTotalStudiedTimeInMiliseconds());
		//recover the flash cards from the set and check if the data is 
		//correct
		Vector flashCards = soc.getFlashCards();
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("value is not equal","Auto", flashCard1.getSideOne());
		assertTrue("should be done", flashCard1.isDone());
		FlashCard flashCard2 = ((FlashCard)(flashCards.elementAt(1)));
		assertEquals("value is not equal","egg", flashCard2.getSideTwo());
		//assert that this card is NOT done
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
