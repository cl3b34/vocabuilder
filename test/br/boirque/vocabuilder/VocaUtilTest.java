package br.boirque.vocabuilder;

import br.boirque.vocabuilder.util.VocaUtil;
import j2meunit.framework.*;

public class VocaUtilTest extends TestCase {

	/**
	 * Default constructor
	 */
	public VocaUtilTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public VocaUtilTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetStudyTimeAsString() {
		VocaUtil vu = new VocaUtil();
		assertEquals("1 sec", vu.getStudyTimeAsString(1000L)); 
		assertEquals("30 sec", vu.getStudyTimeAsString(30900L)); // 30s 
		assertEquals("7min 0sec", vu.getStudyTimeAsString(420000L)); 
		assertEquals("59min 0sec", vu.getStudyTimeAsString(3540000L)); 
		assertEquals("1hr 13min", vu.getStudyTimeAsString(4380000L)); // 1h 13min
		assertEquals("1 day 0hr 00min", vu.getStudyTimeAsString(86400000L));
		assertEquals("1 day 1hr 13min", vu.getStudyTimeAsString(90780000L)); // 1 day 1hr 13min
	}
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
		testsuite.addTest(new VocaUtilTest("testGetStudyTimeAsString", new TestMethod(){ 
			public void run(TestCase tc){
				((VocaUtilTest) tc).testGetStudyTimeAsString(); 
			} 
		}));

		return testsuite;
	}
}
