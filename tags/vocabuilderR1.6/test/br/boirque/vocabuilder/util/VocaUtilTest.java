package br.boirque.vocabuilder.util;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.util.TimeZone;

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
	
	public void testGetLastTimeViewedAsString() {
		VocaUtil vu = new VocaUtil();
		//this was supposed to be 18/04/2008 23:11 but for some
		//reason the default time zone on the emulator is UTC
		// (-3 h) so it evaluates to 18/04/2008 20:11
		// should work on the handset
		String timezoneID = (TimeZone.getDefault()).getID();
		assertEquals("Wrong last time view format.\nCheck Timezone:" + timezoneID ,"18/4/2008 23:11", vu.getLastTimeViewedAsString(1208549471078L)); 
	}
	
	
	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
		testsuite.addTest(new VocaUtilTest("testGetStudyTimeAsString", new TestMethod(){ 
			public void run(TestCase tc){
				((VocaUtilTest) tc).testGetStudyTimeAsString(); 
			} 
		}));
		
		testsuite.addTest(new VocaUtilTest("testGetLastTimeViewedAsString", new TestMethod(){ 
			public void run(TestCase tc){
				((VocaUtilTest) tc).testGetLastTimeViewedAsString(); 
			} 
		}));

		return testsuite;
	}
}
