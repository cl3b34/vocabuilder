package br.boirque.vocabuilder.model;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import j2meunit.framework.*;

public class RecordStoreFactoryTest extends TestCase {

	public RecordStoreFactoryTest() {}

	/**
	 * @param name
	 * @param testMethod
	 * Required by j2meunit
	 */
	public RecordStoreFactoryTest(String name, TestMethod testMethod) {
		super(name, testMethod);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testGetFactory() {
		// factory is a singletown. All generated objects are the same.
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStoreFactory factory1 = RecordStoreFactory.getFactory();
		assertEquals(factory, factory1);
	}

	public void testGetStoreInstance() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore store = factory.getStoreInstance();
		RecordStore store1 = factory.getStoreInstance();
		assertEquals(store, store1);
	}

	
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new RecordStoreFactoryTest("testGetFactory", new TestMethod(){ 
			public void run(TestCase tc) {
				((RecordStoreFactoryTest) tc).testGetFactory(); 
			} 
		}));
	
		testsuite.addTest(new RecordStoreFactoryTest("testGetStoreInstance", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
				((RecordStoreFactoryTest) tc).testGetStoreInstance(); 
			} 
		}));
		return testsuite;
	}
}
