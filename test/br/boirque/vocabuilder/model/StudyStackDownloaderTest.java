package br.boirque.vocabuilder.model;

import br.boirque.vocabuilder.TestSkeleton;
import j2meunit.framework.*;


public class StudyStackDownloaderTest extends TestCase {
	/**
	 * Default constructor. Required by j2meunit
	 */
	public StudyStackDownloaderTest() {
		
	}

	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public StudyStackDownloaderTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testDownloadSet() {
		StudyStackDownloader ssd = new StudyStackDownloader();
		ssd.downloadSet("1");
	}

	public void testListCategories() {
		fail("Not yet implemented");
	}

	public void testListDownloadableSets() {
		fail("Not yet implemented");
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();
		
		testsuite.addTest(new StudyStackDownloaderTest("testDownloadSet", new TestMethod(){ 
			public void run(TestCase tc){
				((StudyStackDownloaderTest) tc).testDownloadSet(); 
			} 
		}));

		return testsuite;
	}
	
}
