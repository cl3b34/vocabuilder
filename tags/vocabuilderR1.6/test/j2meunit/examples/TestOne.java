//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is part of J2MEUnit, a Java 2 Micro Edition unit testing framework.
//
// J2MEUnit is free software distributed under the Common Public License (CPL).
// It may be redistributed and/or modified under the terms of the CPL. You 
// should have received a copy of the license along with J2MEUnit. It is also 
// available from the website of the Open Source Initiative at 
// http://www.opensource.org.
//
// J2MEUnit is distributed in the hope that it will be useful, but WITHOUT ANY 
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
// FOR A PARTICULAR PURPOSE.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package j2meunit.examples;

import j2meunit.framework.*;


/********************************************************************
 * Example Testcase 1.
 *
 * @author: Developer
 */
public class TestOne extends TestCase
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Default constructor.
	 */
	public TestOne()
	{
	}

	/***************************************
	 * TestOne constructor comment.
	 *
	 * @param name java.lang.String
	 */
	public TestOne(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * To create the test suite.
	 */
	public Test suite()
	{
		TestSuite aSuite = new TestSuite();
		
		aSuite.addTest(new TestOne("testOne", new TestMethod() 
		{ public void run(TestCase tc) {((TestOne) tc).testOne(); } }));

		aSuite.addTest(new TestOne("testTwo", new TestMethod() 
		{ public void run(TestCase tc) {((TestOne) tc).testTwo(); } }));
		
		return aSuite;
	}

	/***************************************
	 * Test method 1.
	 */
	public void testOne()
	{
		System.out.println("TestOne.testOne()");
		assertTrue("Should be true", false);
	}

	/***************************************
	 * Test method 2.
	 */
	public void testTwo()
	{
		System.out.println("TestOne.testTwo()");
		throw new RuntimeException("Exception");
	}
}
