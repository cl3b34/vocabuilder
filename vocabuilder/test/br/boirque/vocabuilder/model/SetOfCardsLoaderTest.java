package br.boirque.vocabuilder.model;

import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import br.boirque.vocabuilder.util.TestConstants;
import br.boirque.vocabuilder.util.VocaUtil;
import j2meunit.framework.*;

public class SetOfCardsLoaderTest extends TestCase{
	
	/**
	 * Default constructor
	 */
	public SetOfCardsLoaderTest() {
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
		SetOfCards soc = socl.loadSet(TestConstants.TXTSETTOLOAD);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		assertEquals("wrong card amount\n",TestConstants.TOTALOFCARDS, soc.getFlashCards().size());
		assertNotNull("Null Set of cards", soc);
		assertTrue("Text load:" + VocaUtil.milisecondsToSeconds(loadingTime), loadingTime < TestConstants.MAXLOADINGTIME);
		System.out.println("TxtSetLoad: " + VocaUtil.milisecondsToSeconds(loadingTime));
	}
	
	
	public void testTextFileLoader() throws IOException{
		SetOfCardsLoader socl = new SetOfCardsLoader();
		SetOfCards soc = socl.loadSet(TestConstants.TXTSETTOLOAD);
		assertNotNull(soc);
		Vector cards = soc.getFlashCards();
		System.out.println("Number of Cards: " + cards.size());
		assertNotNull(cards);
		// check the set name
		String setName = soc.getSetName();
		assertEquals(TestConstants.RMSSETNAME, setName);
		
		//check the first card
		FlashCard firstCard = (FlashCard) cards.elementAt(0);
		assertNotNull(firstCard);
		String firstWordFirstCard = firstCard.getSideOne();
		assertEquals(TestConstants.FIRSTWORDONFIRSTCARD, firstWordFirstCard);
		String secondWordFirstCard = firstCard.getSideTwo();
		assertEquals(TestConstants.SECONDWORDONFIRSTCARD, secondWordFirstCard);
		assertTrue(false == firstCard.isDone());
		String sideOneTitleFirstCard = firstCard.getSideOneTitle();
		assertEquals(TestConstants.SIDEONETITLE, sideOneTitleFirstCard);
		String sideTwoTitleFirstCard = firstCard.getSideTwoTitle();
		assertEquals(TestConstants.SIDETWOTITLE, sideTwoTitleFirstCard);
		
		//check the second card
		FlashCard secondCard = (FlashCard) cards.elementAt(1);
		assertNotNull(secondCard);
		String firstWordsecondCard = secondCard.getSideOne();
		assertEquals(TestConstants.FIRSTWORDONSECONDCARD, firstWordsecondCard);
		String secondWordsecondCard = secondCard.getSideTwo();
		assertEquals(TestConstants.SECONDWORDONSECONDCARD, secondWordsecondCard);
		assertTrue(false == secondCard.isDone());
		String sideOneTitleSecondCard = secondCard.getSideOneTitle();
		assertEquals(TestConstants.SIDEONETITLE, sideOneTitleSecondCard);
		String sideTwoTitleSecondCard = secondCard.getSideTwoTitle();
		assertEquals(TestConstants.SIDETWOTITLE, sideTwoTitleSecondCard);
		
		// check the last card
		FlashCard lastCard = (FlashCard) cards.lastElement();
		assertNotNull(lastCard);
		String firstWordLastCard = lastCard.getSideOne();
		assertEquals(TestConstants.FIRSTWORDONLASTCARD, firstWordLastCard);
		String secondWordLastCard = lastCard.getSideTwo();
		assertEquals(TestConstants.SECONDWORDONLASTCARD, secondWordLastCard);
		assertTrue(false == lastCard.isDone());
		String sideOneTitleLastCard = lastCard.getSideOneTitle();
		assertEquals(TestConstants.SIDEONETITLE, sideOneTitleLastCard);
		String sideTwoTitleLastCard = lastCard.getSideTwoTitle();
		assertEquals(TestConstants.SIDETWOTITLE, sideTwoTitleLastCard);
		
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
