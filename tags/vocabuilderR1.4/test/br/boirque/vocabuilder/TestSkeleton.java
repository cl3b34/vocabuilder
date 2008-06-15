package br.boirque.vocabuilder;

import j2meunit.framework.*;

public class TestSkeleton extends TestCase {

	/**
	 * Default constructor
	 */
	public TestSkeleton() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public TestSkeleton(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDummy() {
		assertTrue(true);
	}
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
		testsuite.addTest(new TestSkeleton("testDummy", new TestMethod(){ 
			public void run(TestCase tc){
				((TestSkeleton) tc).testDummy(); 
			} 
		}));

		return testsuite;
	}
}
