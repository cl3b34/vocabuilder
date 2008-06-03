package br.boirque.vocabuilder.model;

import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import br.boirque.vocabuilder.util.VocaUtil;
import j2meunit.framework.*;

public class SetOfCardsLoaderTest extends TestCase {

	private static final long MAXLOADINGTIME = 2000L;
	private static final int TOTALOFCARDS = 1827;
	String setToLoad = "/Finnish/longlist_fin_eng.txt";
	
	/**
	 * Default constructor
	 */
	public SetOfCardsLoaderTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public SetOfCardsLoaderTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoadSet() throws IOException{
		long startTime = System.currentTimeMillis();
		SetOfCardsLoader socl = new SetOfCardsLoader();
		SetOfCards soc = socl.loadSet(setToLoad);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		assertEquals("wrong card amount\n",TOTALOFCARDS, soc.getFlashCards().size());
		assertNotNull("Null Set of cards", soc);
		assertTrue("Text load:" + VocaUtil.milisecondsToSeconds(loadingTime), loadingTime < MAXLOADINGTIME);
		System.out.println("TxtSetLoad: " + VocaUtil.milisecondsToSeconds(loadingTime));
	}
	
	
	public void testTextFileLoader() throws IOException{
		SetOfCardsLoader socl = new SetOfCardsLoader();
		SetOfCards soc = socl.loadSet(setToLoad);
		assertNotNull(soc);
		Vector cards = soc.getFlashCards();
		System.out.println("Number of Cards: " + cards.size());
		assertNotNull(cards);
		// check the set name
		String setName = soc.getSetName();
		assertEquals("longlist_fi_en", setName);
		
		//check the first card
		FlashCard firstCard = (FlashCard) cards.elementAt(0);
		assertNotNull(firstCard);
		String firstWordFirstCard = firstCard.getSideOne();
		assertEquals("aalto", firstWordFirstCard);
		String secondWordFirstCard = firstCard.getSideTwo();
		assertEquals("fluid, liquid, wave", secondWordFirstCard);
		assertTrue(false == firstCard.isDone());
		String sideOneTitleFirstCard = firstCard.getSideOneTitle();
		assertEquals("FIN", sideOneTitleFirstCard);
		String sideTwoTitleFirstCard = firstCard.getSideTwoTitle();
		assertEquals("ENG", sideTwoTitleFirstCard);
		
		//check the second card
		FlashCard secondCard = (FlashCard) cards.elementAt(1);
		assertNotNull(secondCard);
		String firstWordsecondCard = secondCard.getSideOne();
		assertEquals("aamiainen", firstWordsecondCard);
		String secondWordsecondCard = secondCard.getSideTwo();
		assertEquals("breakfast", secondWordsecondCard);
		assertTrue(false == secondCard.isDone());
		String sideOneTitleSecondCard = secondCard.getSideOneTitle();
		assertEquals("FIN", sideOneTitleSecondCard);
		String sideTwoTitleSecondCard = secondCard.getSideTwoTitle();
		assertEquals("ENG", sideTwoTitleSecondCard);
		
		// check the last card
		FlashCard lastCard = (FlashCard) cards.lastElement();
		assertNotNull(lastCard);
		String firstWordLastCard = lastCard.getSideOne();
		assertEquals("öljy", firstWordLastCard);
		String secondWordLastCard = lastCard.getSideTwo();
		assertEquals("oil", secondWordLastCard);
		assertTrue(false == lastCard.isDone());
		String sideOneTitleLastCard = lastCard.getSideOneTitle();
		assertEquals("FIN", sideOneTitleLastCard);
		String sideTwoTitleLastCard = lastCard.getSideTwoTitle();
		assertEquals("ENG", sideTwoTitleLastCard);
		
		assertTrue(false == soc.isDone());		
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsLoaderTest("testLoadSet", new TestMethod(){ 
			public void run(TestCase tc) throws IOException{
				((SetOfCardsLoaderTest) tc).testLoadSet(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsLoaderTest("testTextFileLoader", new TestMethod(){ 
			public void run(TestCase tc) throws IOException{
				((SetOfCardsLoaderTest) tc).testTextFileLoader(); 
			} 
		}));
		
		
		
		return testsuite;
	}
}
