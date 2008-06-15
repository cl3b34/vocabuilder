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

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestSuite;


public class TestAll extends TestCase
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * TestOne constructor comment.
	 */
	public TestAll()
	{
		super("null");
	}

	public TestAll(String name)
	{
		super(name);
	}

	//~ Methods ----------------------------------------------------------------

	public static void main(String[] args)
	{
		String[] runnerArgs = new String[] { "j2meunit.examples.TestAll" };
		j2meunit.textui.TestRunner.main(runnerArgs);
	}

	public Test suite()
	{
		TestSuite suite = new TestSuite();

		suite.addTest(new TestOne().suite());
		suite.addTest(new TestTwo().suite());

		return suite;
	}
}
