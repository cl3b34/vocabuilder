package br.boirque.vocabuilder;

import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import j2meunit.framework.*;

/**
 * Tests the performance of the loading system
 * The list of cards used must be:
 * "/Finnish/longlist_fin_eng.txt"
 * @author cleber.goncalves
 *
 */
public class PerformanceTest extends TestCase {
	
	String setToLoad = "/Finnish/longlist_fin_eng.txt";
	SetOfCards soc;

	/**
	 * Default constructor
	 */
	public PerformanceTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public PerformanceTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {
		SetOfCardsLoader socl = new SetOfCardsLoader();
		soc = socl.loadSet(setToLoad);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		soc = null;
		setToLoad = null;
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd = null;
		System.gc();
	}
	
	public void testPerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		// common variables
		SetOfCardsDAO socd = new SetOfCardsDAO();
		long endTime;
		long startTime;
		long timeTaken;
		
		//performance of loading a file 
		startTime = System.currentTimeMillis();		
		SetOfCardsLoader socl = new SetOfCardsLoader();
		soc = socl.loadSet(setToLoad);
		endTime = System.currentTimeMillis();
		timeTaken = endTime -startTime;
		assertEquals("wrong card amount\n",1827, soc.getFlashCards().size());
		assertNotNull("Null Set of cards", soc);
		// loading time must be under 5s
		System.out.println("Text load:" + milisecondsToSeconds(timeTaken));
//		assertTrue("Text load:" + milisecondsToSeconds(timeTaken), timeTaken < 5000L);
		
		// performance of saving to RMS 
		startTime = System.currentTimeMillis();		
		socd.resetState();
		socd.saveState(soc);		
		endTime = System.currentTimeMillis();
		timeTaken = endTime -startTime;
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// time must be under 5s
		System.out.println("RMS save:" + milisecondsToSeconds(timeTaken));
//		assertTrue("RMS save:" + milisecondsToSeconds(timeTaken), timeTaken < 5000L);
		
		//performance of loading from RMS
		startTime = System.currentTimeMillis();
		soc = socd.loadState();
		endTime = System.currentTimeMillis();
		timeTaken = endTime -startTime;
		assertNotNull("Null set of cards", soc);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 5s
		System.out.println("RMS load:" + milisecondsToSeconds(timeTaken));
//		assertTrue("RMS load:" + milisecondsToSeconds(timeTaken), timeTaken < 5000L);
		
		//performance of loading a single card
		startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCard(3);
		endTime = System.currentTimeMillis();
		timeTaken = endTime -startTime;
		assertNotNull("Null card", card);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 300ms
		System.out.println("RMS loadCard:" + milisecondsToSeconds(timeTaken));
//		assertTrue("RMS loadCard:" + milisecondsToSeconds(timeTaken), timeTaken < 300L);
		
		//performance of loading the metadata
		startTime = System.currentTimeMillis();		
		SetOfCards socMeta = socd.loadSetMetadata(2);
		endTime = System.currentTimeMillis();
		timeTaken = endTime -startTime;
		assertNotNull("Null card metadata", socMeta);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// under 300ms
		System.out.println("RMS loadMeta: " + milisecondsToSeconds(timeTaken));
//		assertTrue("RMS loadMeta:" + milisecondsToSeconds(timeTaken), timeTaken < 300L);
	}	
	

	public void testTextFileLoaderPerformance() throws IOException{
		long startTime = System.currentTimeMillis();		
		SetOfCardsLoader socl = new SetOfCardsLoader();
		soc = socl.loadSet(setToLoad);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		assertEquals("wrong card amount\n",1827, soc.getFlashCards().size());
		assertNotNull("Null Set of cards", soc);
		// loading time must be under 5s
		assertTrue("Text load:" + milisecondsToSeconds(loadingTime), loadingTime < 5000L);
	}
	
	public void testSaveStatePerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		long startTime = System.currentTimeMillis();		
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(soc);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// time must be under 5s
		assertTrue("RMS save:" + milisecondsToSeconds(savingTime), savingTime < 5000L);
	}	
	
	public void testLoadStatePerformance() throws IOException, InvalidRecordIDException, RecordStoreException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(soc);
		//measure the performance
		long startTime = System.currentTimeMillis();
		soc = socd.loadState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		assertNotNull("Null set of cards", soc);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 5s
		assertTrue("RMS load:" + milisecondsToSeconds(loadingTime), loadingTime < 5000L);
	}	
	
	public void testLoadCardPerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(soc);
		//measure performance
		long startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCard(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		assertNotNull("Null card", card);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 300ms
		assertTrue("RMS loadCard:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
	}
	
	public void testLoadSetMetadataPerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(soc);
		//measure performance
		long startTime = System.currentTimeMillis();		
		SetOfCards socMeta = socd.loadSetMetadata(2);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading set metadata time: " + loadingTime);
		assertNotNull("Null card metadata", socMeta);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// under 300ms
		assertTrue("RMS loadMeta:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
	}

	private String milisecondsToSeconds(long timeToConvert) {
		if (timeToConvert < 1000L){
			return timeToConvert + "ms";
		}
		return timeToConvert/1000L + "s";
		
	}
	
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
//		testsuite.addTest(new PerformanceTest("testPerformance", new TestMethod(){ 
//			public void run(TestCase tc) throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
//				((PerformanceTest) tc).testPerformance(); 
//			} 
//		}));
		
		testsuite.addTest(new PerformanceTest("testTextFileLoaderPerformance", new TestMethod(){ 
			public void run(TestCase tc) throws IOException{
				((PerformanceTest) tc).testTextFileLoaderPerformance(); 
			} 
		}));

		testsuite.addTest(new PerformanceTest("testLoadStatePerformance", new TestMethod(){ 
			public void run(TestCase tc) throws InvalidRecordIDException, IOException, RecordStoreException{
				((PerformanceTest) tc).testLoadStatePerformance(); 
			} 
		}));
		testsuite.addTest(new PerformanceTest("testSaveStatePerformance", new TestMethod(){ 
			public void run(TestCase tc) throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
				((PerformanceTest) tc).testSaveStatePerformance(); 
			} 
		}));
		testsuite.addTest(new PerformanceTest("testLoadCardPerformance", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, IOException, RecordStoreException{
				((PerformanceTest) tc).testLoadCardPerformance(); 
			} 
		}));
		testsuite.addTest(new PerformanceTest("testLoadSetMetadataPerformance", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, IOException, RecordStoreException{
				((PerformanceTest) tc).testLoadSetMetadataPerformance(); 
			} 
		}));
		
		return testsuite;
	}
}
