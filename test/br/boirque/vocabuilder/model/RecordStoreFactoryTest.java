package br.boirque.vocabuilder.model;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

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
		RecordStore store = factory.getStoreInstance("test_store");
		RecordStore store1 = factory.getStoreInstance("test_store");
		assertSame("stores differ",store, store1);
		RecordStore store2 = factory.getStoreInstance("another_store");
		try {
			assertSame("objects differ", store, store2);
			fail("should be different");
		}catch(AssertionFailedError e) {
			//should throw this exception
		}
	}

	public void testCloseStoreInstance() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException  {
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore store = factory.getStoreInstance("test_store");
		RecordStore store1 = factory.getStoreInstance("test_store");
		assertSame(store, store1);
		factory.closeStoreInstance("test_store");
		try {
			store.getName();
			fail("Store should be closed by now");
		} catch (RecordStoreNotOpenException e) {
		}
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
		
		
		
		testsuite.addTest(new RecordStoreFactoryTest("testCloseStoreInstance", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException {
				((RecordStoreFactoryTest) tc).testCloseStoreInstance(); 
			} 
		}));
		
		return testsuite;
	}
}
