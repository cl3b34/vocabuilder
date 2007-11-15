package br.boirque.vocabuilder;

import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import j2meunit.framework.*;

public class SetOfCardsLoaderTest extends TestCase {

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

	public void testLoadSet(){
		
	}
	
	public void testTextFileLoader() throws IOException{
		SetOfCardsLoader socl = new SetOfCardsLoader();
		SetOfCards soc = socl.loadSet();
		assertNotNull(soc);
		Vector cards = soc.getFlashCards();
		System.out.println("Number of Cards: " + cards.size());
		assertNotNull(cards);
		//check the first card
		FlashCard firstCard = (FlashCard) cards.elementAt(0);
		assertNotNull(firstCard);
		String firstWordFirstCard = firstCard.getSideOne();
		assertEquals("aalto", firstWordFirstCard);
		String secondWordFirstCard = firstCard.getSideTwo();
		assertEquals("fluid, liquid, wave", secondWordFirstCard);
		assertTrue(false == firstCard.isDone());
		
		//check the second card
		FlashCard secondCard = (FlashCard) cards.elementAt(1);
		assertNotNull(secondCard);
		String firstWordsecondCard = secondCard.getSideOne();
		assertEquals("aamiainen", firstWordsecondCard);
		String secondWordsecondCard = secondCard.getSideTwo();
		assertEquals("breakfast", secondWordsecondCard);
		assertTrue(false == secondCard.isDone());
		
		// check the last card
		FlashCard lastCard = (FlashCard) cards.lastElement();
		assertNotNull(lastCard);
		String firstWordLastCard = lastCard.getSideOne();
		assertEquals("öljy", firstWordLastCard);
		String secondWordLastCard = lastCard.getSideTwo();
		assertEquals("oil", secondWordLastCard);
		assertTrue(false == lastCard.isDone());
		
		assertTrue(false == soc.isDone());		
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsLoaderTest("testTextFileLoader", new TestMethod(){ 
			public void run(TestCase tc) throws IOException{
				((SetOfCardsLoaderTest) tc).testTextFileLoader(); 
			} 
		}));
		
		return testsuite;
	}
}
