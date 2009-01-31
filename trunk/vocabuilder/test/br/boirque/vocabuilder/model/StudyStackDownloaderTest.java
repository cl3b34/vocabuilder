package br.boirque.vocabuilder.model;

import java.util.Vector;

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

	StudyStackDownloader ssd;
	
	protected void setUp() throws Exception {
		ssd = new StudyStackDownloader();
	}

	protected void tearDown() throws Exception {
	}

	
	public void testDownloadSet() {		
		SetOfCards set = ssd.downloadSet("1");
//		System.out.println(set.getFlashCards());
		assertNotNull("Null set",set);
		assertNotNull("Null cards",set.getFlashCards());
		assertEquals("Wrong card count", 50, set.getTotalNumberOfCards());
	}

	public void testSplitInLines() {
		String set = "word | otherWord | tip\nword1 | otherWord1 | tip1";
		Vector lines = ssd.splitInLines(set);
		System.out.println(lines.size());
		assertEquals(2, lines.size());
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

		testsuite.addTest(new StudyStackDownloaderTest("testSplitInLines", new TestMethod(){ 
			public void run(TestCase tc){
				((StudyStackDownloaderTest) tc).testSplitInLines(); 
			} 
		}));

		
		
		return testsuite;
	}
	
}
