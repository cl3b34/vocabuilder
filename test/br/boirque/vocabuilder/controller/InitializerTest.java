package br.boirque.vocabuilder.controller;

import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAOTest;
import j2meunit.framework.*;

public class InitializerTest extends TestCase {
	
	
	
	/**
	 * Required by J2MEUnit
	 */
	public InitializerTest() {}

	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public InitializerTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testInitializeApp() {
		Initializer init = new Initializer();
		SetOfCards soc = init.initializeApp();
		assertNotNull(soc);
	}
	
	public void testSaveState() {
		Initializer init = new Initializer();
		SetOfCards soc = init.initializeApp();
		boolean saved = init.saveState(soc);
		assertTrue(saved);
	}	
	
	public void testLoadState() {
		Initializer init = new Initializer();
		SetOfCards soc = init.initializeApp();
		init.saveState(soc);
		SetOfCards socNew = init.loadState();
		assertNotNull(socNew);
	} 
	
	public void testResetState() {
		Initializer init = new Initializer();
		SetOfCards soc = init.initializeApp();
		init.saveState(soc);
		SetOfCards socNew = init.loadState();
		assertNotNull(socNew);
		init.resetState();
		int recordCount = init.getRecordCount();
		assertEquals("Didn't reset", 0, recordCount);
	}

	public void testGetRecordCount() {
		Initializer init = new Initializer();
		int recordCount = init.getRecordCount();
		assertEquals("Wrong record count", 0, recordCount);
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new InitializerTest("testInitializeApp", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((InitializerTest) tc).testInitializeApp(); 
			} 
		}));
		
		testsuite.addTest(new InitializerTest("testSaveState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((InitializerTest) tc).testSaveState(); 
			} 
		}));
		
		testsuite.addTest(new InitializerTest("testInitializeApp", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((InitializerTest) tc).testInitializeApp(); 
			} 
		}));
		
		testsuite.addTest(new InitializerTest("testResetState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((InitializerTest) tc).testResetState(); 
			} 
		}));
		
		testsuite.addTest(new InitializerTest("testGetRecordCount", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((InitializerTest) tc).testGetRecordCount(); 
			} 
		}));
	
		
		return testsuite;
	}
	
}
