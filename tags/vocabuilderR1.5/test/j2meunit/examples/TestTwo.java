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
 * Insert the type's description here. Creation date: (11/1/00 3:54:43 PM)
 *
 * @author: Developer
 */
public class TestTwo extends TestCase
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * TestOne constructor comment.
	 */
	public TestTwo()
	{
		super("null");
	}

	/***************************************
	 * TestOne constructor comment.
	 *
	 * @param name java.lang.String
	 */
	public TestTwo(String sName, TestMethod rTestMethod)
	{
		super(sName, rTestMethod);
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * Create the test suite.
	 */
	public Test suite()
	{
		return new TestSuite(new TestTwo("testOne", new TestMethod() 
		{	public void run(TestCase tc)
			{ ((TestTwo) tc).testOne(); }
		}));
	}

	/***************************************
	 * Insert the method's description here. Creation date: (11/1/00 4:02:35
	 * PM)
	 */
	public void testOne()
	{
		System.out.println("TestTwo.testOne()");
		assertEquals("These #'s should equal", 3, 4);
	}

}
