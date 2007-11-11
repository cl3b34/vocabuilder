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
package j2meunit.examples.midp;

import j2meunit.midletui.TestRunner;

/*************************************************************************
 * Example MIDlet TestRunner that overloads the startApp() method and invokes 
 * start() with the names of the classes to test. This can be achieved also by
 * setting the JAD file attribute J2MEUnitTestClasses with the names of the 
 * classes to test and then invoke the superclass j2meunit.midletui.TestRunner
 * directly without subclassing it. 
 * 
 * @author eso
 */
public class ExampleTestMidlet extends TestRunner
{
	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * ExampleTestRunner constructor comment.
	 */
	public ExampleTestMidlet()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * startApp.
	 */
	protected void startApp()
	{
		start(new String[] { "j2meunit.examples.TestAll" });
	}
}
