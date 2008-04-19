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
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(soc);
		System.gc();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		soc = null;
		setToLoad = null;
		System.gc();
	}

	public void testTextFileLoaderPerformance() throws IOException{
		long startTime = System.currentTimeMillis();		
		SetOfCardsLoader socl = new SetOfCardsLoader();
		soc = socl.loadSet(setToLoad);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("Text file Loading time: " + loadingTime);
		assertNotNull("Set of cards was null", soc);
		// loading time must be under 5s
		assertTrue("Text file loading time is too high", loadingTime < 5000L);
	}
	
	public void testSaveStatePerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		long startTime = System.currentTimeMillis();		
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.saveState(soc);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("RMS saving time: " + savingTime);
		assertNotNull("Set of cards was null", soc);
		// time must be under 5s
		assertTrue("RMS saving time is too high", savingTime < 5000L);
	}	
	
	public void testLoadStatePerformance() throws IOException, InvalidRecordIDException, RecordStoreException{
		long startTime = System.currentTimeMillis();
		SetOfCardsDAO socd = new SetOfCardsDAO();
		soc = socd.loadState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading time: " + loadingTime);
		assertNotNull("Set of cards was null", soc);
		// loading time must be under 5s
		assertTrue("RMS loading time is too high", loadingTime < 5000L);
	}	
	
	public void testLoadCardPerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		long startTime = System.currentTimeMillis();
		SetOfCardsDAO socd = new SetOfCardsDAO();
		FlashCard card = socd.loadCard(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading 1 card time: " + loadingTime);
		assertNotNull("Set of cards was null", soc);
		// loading time must be under 100ms
		assertTrue("RMS loading 1 card time is too high", loadingTime < 100L);
	}
	
	public void testLoadSetMetadataPerformance() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		long startTime = System.currentTimeMillis();		
		SetOfCardsDAO socd = new SetOfCardsDAO();
		SetOfCards socMeta = socd.loadSetMetadata(2);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading set metadata time: " + loadingTime);
		assertNotNull("Set of cards was null", soc);
		// loading time must be under 100ms
		assertTrue("RMS loading set metadata time is too high", loadingTime < 100L);
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
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
