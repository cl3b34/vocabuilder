package br.boirque.vocabuilder.controller;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import br.boirque.vocabuilder.util.TestConstants;

public class InitializerTest extends TestCase implements TestConstants {

	/**
	 * Required by J2MEUnit
	 */
	public InitializerTest() {
	}

	/**
	 * @param sTestName
	 *            The name of the test being called
	 * @param rTestMethod
	 *            A reference to the method to be called This seems to be
	 *            required by J2MEUnit
	 */
	public InitializerTest(String sTestName, TestMethod rTestMethod) {
		super(sTestName, rTestMethod);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
		Initializer init = new Initializer();
		init.resetState(RMSSETNAME);
	}

	public void testSaveState() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(TXTSETTOLOAD);
		boolean saved = init.saveState(soc);
		assertTrue(saved);
	}

	public void testLoadSet() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(TXTSETTOLOAD);
		init.saveState(soc);
		SetOfCards socNew = init.loadSet(soc.getSetName());
		assertNotNull(socNew);
	}

	public void testResetState() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(TXTSETTOLOAD);
		init.saveState(soc);
		String setName = soc.getSetName();
		SetOfCards socNew = init.loadSet(setName);
		assertNotNull("Null set", socNew);
		init.resetState(setName);
		int recordCount = init.getCardCount(setName);
		assertEquals("Didn't reset", 0, recordCount);
	}

	public void testGetRecordCount() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(TXTSETTOLOAD);
		init.resetState(soc.getSetName());
		int recordCount = init.getCardCount(soc.getSetName());
		assertEquals("Wrong record count", 0, recordCount);
	}
	
	public void testLoadDefaultSetNames() {
		Initializer init = new Initializer();
		String[] setNames = init.loadDefaultSetNames();
		assertNotNull(setNames);
		assertEquals(DEFAULTSETCOUNT, setNames.length);
	}
	
	public void testLoadOnProgressSetNames() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(TXTSETTOLOAD);
		init.saveState(soc);
		String[] setNames = init.loadOnProgressSetNames();
		assertNotNull(setNames);
		assertEquals(1, setNames.length);
		assertTrue(RMSSETNAME.equals(setNames[0]));		
	}
	
	public void testLoadUniqueSetNames() throws IOException {
		Initializer init = new Initializer();
		init.deleteSetOfCardsFromRMS(RMSSETNAME);
		String[] uniqueSets = init.loadUniqueSetNames();
		//1st test: No sets in progress.
		assertNotNull(uniqueSets);
		assertEquals("wrong unique count", DEFAULTSETCOUNT, uniqueSets.length);
		//2nd test: progressively save then to RMS
		SetOfCardsLoader socl = new SetOfCardsLoader();
		for(int i=0; i<uniqueSets.length; i++) {
			SetOfCards soc = socl.loadSet(uniqueSets[i]);
			init.saveState(soc);
			//number of unique sets remain intact (same as default sets)
			String[] newUniqueSets = init.loadUniqueSetNames();
			assertNotNull(newUniqueSets);
			assertEquals("wrong NewUnique count", DEFAULTSETCOUNT, newUniqueSets.length);
			//number of default sets remain intact
			String[] newDefaultSets = init.loadDefaultSetNames();
			assertNotNull(newDefaultSets);
			assertEquals("wrong default count",DEFAULTSETCOUNT, newDefaultSets.length);
			//number of RMS sets increases from 1 to the amount of default sets
			String[] newInProgress = init.loadOnProgressSetNames();
			assertNotNull(newInProgress);
			assertEquals("wrong OnProgress count",i+1, newInProgress.length);			
		}
	}
	
	public void testGetNextCard() {
		Initializer init = new Initializer();
		FlashCard c = init.getNextCard();
		assertNotNull(c);
		FlashCard c1 = init.getNextCard();
		assertNotNull(c1);
		assertTrue(!c.equals(c1));
	}
	
	public void testInitializeRandomCardIndex() {
		Initializer init = new Initializer();
		SetOfCards soc = init.loadSet(RMSSETNAME);
		Vector cards = soc.getFlashCards();
		Vector indexes = init.initializeRandomCardIndex(cards,false);
		assertNotNull(indexes);
		// mark the first card as done
		FlashCard c = (FlashCard)cards.elementAt(0);
		c.setDone(true);
		Vector indexesNotDone = init.initializeRandomCardIndex(cards,true);
		assertNotNull(indexes);
		assertTrue(indexes.size() > indexesNotDone.size());
	}
	
	
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new InitializerTest("testLoadSet",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((InitializerTest) tc).testLoadSet();
					}
				}));

		testsuite.addTest(new InitializerTest("testSaveState",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((InitializerTest) tc).testSaveState();
					}
				}));


		testsuite.addTest(new InitializerTest("testResetState",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((InitializerTest) tc).testResetState();
					}
				}));

		testsuite.addTest(new InitializerTest("testGetRecordCount",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((InitializerTest) tc).testGetRecordCount();
					}
				}));
		
		testsuite.addTest(new InitializerTest("testLoadDefaultSetNames",
				new TestMethod() {
					public void run(TestCase tc){
						((InitializerTest) tc).testLoadDefaultSetNames();
					}
				}));
		
		testsuite.addTest(new InitializerTest("testLoadOnProgressSetNames",
				new TestMethod() {
					public void run(TestCase tc){
						((InitializerTest) tc).testLoadOnProgressSetNames();
					}
				}));
		
		testsuite.addTest(new InitializerTest("testLoadUniqueSetNames",
				new TestMethod() {
					public void run(TestCase tc) throws IOException{
						((InitializerTest) tc).testLoadUniqueSetNames();
					}
				}));
		
//		testsuite.addTest(new InitializerTest("testDeleteSetOfCardsFromRMS",
//				new TestMethod() {
//					public void run(TestCase tc) throws IOException{
//						((InitializerTest) tc).testDeleteSetOfCardsFromRMS();
//					}
//				}));
		
		testsuite.addTest(new InitializerTest("testGetNextCard",
				new TestMethod() {
					public void run(TestCase tc) throws IOException{
						((InitializerTest) tc).testGetNextCard();
					}
				}));
		
		
		return testsuite;
	}

}
