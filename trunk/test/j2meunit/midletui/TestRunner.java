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
package j2meunit.midletui;

import j2meunit.framework.AssertionFailedError;
import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestFailure;
import j2meunit.framework.TestListener;
import j2meunit.framework.TestResult;
import j2meunit.framework.TestSuite;
import j2meunit.util.StringUtil;
import j2meunit.util.Version;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


/********************************************************************
 * A TestRunner that runs as a MIDlet. It can be used in two ways:
 * 
 * <ul>
 * <li>
 * By subclassing and implementing startApp() which then calls start() with the
 * test classes to run as the parameter, or
 * </li>
 * <li>
 * By invoking it directly after setting the property J2MEUnitTestClasses with
 * the names of the test classes (can be set in the JAD file)
 * </li>
 * </ul>
 * 
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public class TestRunner extends MIDlet implements TestListener
{
	//~ Static fields/initializers ----------------------------------------------

	protected static TestRunner theInstance = null;

	//~ Instance fields ---------------------------------------------------------

	protected Gauge		  aProgressBar;
	protected List		  aResultsList;
	protected PrintStream aWriter	    = System.out;
	protected StringItem  aErrorInfo;
	protected StringItem  aFailureInfo;
	protected TestResult  aResult;
	protected boolean     bScreenOutput = true;
	protected boolean     bTextOutput   = true;
	protected int		  nCount;

	//~ Constructors ------------------------------------------------------------

	/***************************************
	 * Creates a new TestRunner object.
	 */
	public TestRunner()
	{
		if (theInstance != null)
			throw new RuntimeException("Only one MIDlet instance allowed!");

		theInstance = this;
	}

	//~ Methods -----------------------------------------------------------------

	/***************************************
	 * To return the current TestRunner instance. This is needed to determine
	 * the current Display and is only valid after the constructor of
	 * TestRunner has been invoked.
	 *
	 * @return
	 */
	public static TestRunner getInstance()
	{
		return theInstance;
	}

	/***************************************
	 * To set the output mode(s) for this TestRunner.
	 *
	 * @param bScreen If TRUE, output will be written to an LCDUI screen
	 * @param bText If TRUE, output will be written to a PrintStream (System.out
	 * 		  by default)
	 */
	public void setOutputMode(boolean bScreen, boolean bText)
	{
		bScreenOutput = bScreen;
		bTextOutput   = bText;
	}

	/***************************************
	 * Set the output stream.
	 *
	 * @param aStream A PrintStream to be used for output.
	 */
	public void setWriter(PrintStream aStream)
	{
		aWriter = aStream;
	}

	/***************************************
	 * Get the output stream (defaults to System.out).
	 *
	 * @return A PrintStream for output
	 */
	public PrintStream getWriter()
	{
		return aWriter;
	}

	/***************************************
	 * TestListener.addError() - will print 'E' to System.out.
	 *
	 * @param test The test that failed
	 * @param t The Exception that caused the error
	 */
	public synchronized void addError(Test test, Throwable t)
	{
		System.out.print("E");
	}

	/***************************************
	 * TestListener.addError() - will print 'F' to System.out.
	 *
	 * @param test The test that failed
	 * @param e The AssertionFailedError that caused the failure
	 */
	public synchronized void addFailure(Test test, AssertionFailedError e)
	{
		System.out.print("F");
	}

	/***************************************
	 * Add a string to the result output.
	 *
	 * @param sText The text to add
	 */
	public void addToResultsList(String sText)
	{
		if (bScreenOutput)
			getResultsList().append(sText, null);

		if (bTextOutput)
			aWriter.println(sText);
	}

	/***************************************
	 * Add a string to the result output.
	 *
	 * @param t The text to add
	 */
	public void addToResultsList(Throwable t)
	{
		if (bScreenOutput)
		{
			String sMsg = ((t.getMessage() != null) ? t.getMessage()
													: t.getClass().getName());
			getResultsList().append(sMsg, null);
		}

		if (bTextOutput)
			t.printStackTrace();
	}

	/***************************************
	 * TestListener.endTest()
	 *
	 * @param test The test that finished
	 */
	public void endTest(Test test)
	{
		if (aProgressBar != null)
		{
			aProgressBar.setValue(aProgressBar.getValue() + 1);
			aFailureInfo.setText(Integer.toString(aResult.failureCount()));
			aErrorInfo.setText(Integer.toString(aResult.errorCount()));
		}
	}

	/***************************************
	 * TestListener.endTestStep()
	 *
	 * @param test The test of which a step has finished
	 */
	public void endTestStep(Test test)
	{
		if (aProgressBar != null)
			aProgressBar.setValue(aProgressBar.getValue() + 1);
	}

	/***************************************
	 * Prints errors and failures to the standard output
	 *
	 * @param result The test results
	 */
	public synchronized void print(TestResult result)
	{
		printHeader(result);
		printErrors(result);
		printFailures(result);
		printFooter();
	}

	/***************************************
	 * Prints the errors to the standard output
	 *
	 * @param result The test result containing the errors
	 */
	public void printErrors(TestResult result)
	{
		if (result.errorCount() != 0)
		{
			if (result.errorCount() == 1)
				addToResultsList("There was " + result.errorCount() + " error:");
			else
				addToResultsList("There were " + result.errorCount() +
								 " errors:");

			int i = 1;

			for (Enumeration e = result.errors(); e.hasMoreElements(); i++)
			{
				TestFailure failure = (TestFailure) e.nextElement();

				addToResultsList(i + ") " + failure.failedTest());

				if (failure.thrownException() != null)
					addToResultsList(failure.thrownException());
			}
		}
	}

	/***************************************
	 * Prints failures to the standard output
	 *
	 * @param result The test result containing the failures
	 */
	public void printFailures(TestResult result)
	{
		if (result.failureCount() != 0)
		{
			if (result.failureCount() == 1)
				addToResultsList("There was " + result.failureCount() +
								 " failure:");
			else
				addToResultsList("There were " + result.failureCount() +
								 " failures:");

			int i = 1;

			for (Enumeration e = result.failures(); e.hasMoreElements(); i++)
			{
				TestFailure failure = (TestFailure) e.nextElement();

				addToResultsList(i + ") " + failure.failedTest());

				if (failure.thrownException() != null)
					addToResultsList(failure.thrownException());
			}
		}
	}

	/***************************************
	 * Prints the footer
	 */
	public void printFooter()
	{
		addToResultsList("J2ME Unit " + Version.id());
		addToResultsList("Original Version by RoleModel Software, Inc.");
		addToResultsList("Original JUnit by Kent Beck and Erich Gamma");
	}

	/***************************************
	 * Prints the header of the report
	 *
	 * @param result DOCUMENT ME!
	 */
	public void printHeader(TestResult result)
	{
		if (result.wasSuccessful())
		{
			addToResultsList("OK");
			addToResultsList(" (" + result.runCount() + " tests)");
		}
		else
		{
			addToResultsList("FAILURES");
			addToResultsList("Test Results:");
			addToResultsList("Run: " + result.runCount());
			addToResultsList("Failures: " + result.failureCount());
			addToResultsList("Errors: " + result.errorCount());
		}
	}

	/***************************************
	 * To display the result of the test in a javax.microedition.lcdui.List
	 * screen.
	 */
	public void showResult()
	{
		if (bScreenOutput)
			Display.getDisplay(this).setCurrent(getResultsList());
	}

	/***************************************
	 * TestListener.startTest() - will print '.' to te System.out.
	 *
	 * @param test The test that started
	 */
	public synchronized void startTest(Test test)
	{
		System.out.print(".");
	}

	/***************************************
	 * Returns the javax.microedition.lcdui.List instance to append the result
	 * data to. Will be created if it doesn't exist.
	 *
	 * @return A javax.microedition.lcdui.List object
	 */
	protected List getResultsList()
	{
		if (aResultsList == null)
			aResultsList = new List("J2ME Unit", List.IMPLICIT);

		return aResultsList;
	}

	/***************************************
	 * Builds a test suite from all test case classes in a string array.
	 *
	 * @param rTestCaseClasses A string array containing the test case class
	 * 		  names
	 *
	 * @return A test suite containing all tests
	 */
	protected Test createTestSuite(String[] rTestCaseClasses)
	{
		if (rTestCaseClasses.length < 1)
		{
			System.out.println("Usage: TestRunner testCaseName1 [... testCaseNameN]");
			System.exit(-1);
		}

		TestSuite aSuite = new TestSuite();

		for (int i = 0; i < rTestCaseClasses.length; i++)
		{
			try
			{
				String   sClass = rTestCaseClasses[i];
				TestCase aCase = (TestCase) Class.forName(sClass).newInstance();
				aSuite.addTest(aCase.suite());
			}
			catch (Exception e)
			{
				System.out.println("Access to TestCase " + rTestCaseClasses[i] +
								   " failed: " + e.getMessage() + " - " +
								   e.getClass().getName());
			}
		}

		return aSuite;
	}

	/***************************************
	 * Empty implementation of MIDlet.destroyApp.
	 *
	 * @param bUnconditional Not needed here
	 */
	protected void destroyApp(boolean bUnconditional)
	{
	}

	/***************************************
	 * Will run all tests in the given test suite.
	 *
	 * @param suite The test suite to run
	 */
	protected void doRun(Test suite)
	{
		aResult = new TestResult();
		aResult.addListener(this);

		long startTime = System.currentTimeMillis();
		suite.run(aResult);

		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		addToResultsList("Time: " + StringUtil.elapsedTimeAsString(runTime));
		print(aResult);
	}

	/***************************************
	 * Empty implementation of MIDlet.pauseApp.
	 */
	protected void pauseApp()
	{
	}

	/***************************************
	 * Starts a test run. processes the command line arguments and creates a
	 * test suite from it. The test suite will then be run in a separate thread
	 * to allow the progress bar to be updated. After all tests have run the
	 * result screen will be displayed.
	 * 
	 * <p>
	 * The only thing that a sub
	 * </p>
	 *
	 * @param rTestCaseClasses The names of the test case classes
	 */
	protected void start(String[] rTestCaseClasses)
	{
		final Test    aTestSuite = createTestSuite(rTestCaseClasses);
		final Display rDisplay = Display.getDisplay(this);
		Form		  aForm    = new Form("TestRunner");

		nCount = aTestSuite.countTestSteps();

		aProgressBar = new Gauge(null, false, nCount, 0);
		aFailureInfo = new StringItem("Failures:", "0");
		aErrorInfo   = new StringItem("Errors:", "0");

		aForm.append("Testing...");
		aForm.append(aProgressBar);
		aForm.append(aFailureInfo);
		aForm.append(aErrorInfo);
		rDisplay.setCurrent(aForm);

		new Thread()
		{
			public void run()
			{
				try
				{
					doRun(aTestSuite);
					showResult();
				}
				catch (Exception e)
				{
					System.out.println("Exception while running test: " + e);
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/***************************************
	 * Default implementation that reads the attribute J2MEUnitTestClasses with
	 * getAppProperty and invokes the method start() with the result string.
	 * Therefore, if this string contains the space-separated list of test
	 * classes to run it is not necessary to subclass midletui.TestRunner.
	 */
	protected void startApp() throws MIDletStateChangeException
	{
		try
		{
			String sTestClasses = getAppProperty("J2MEUnitTestClasses");

			System.out.println("Testing: " + sTestClasses);
			start(new String[] { sTestClasses });
		}
		catch (Exception e)
		{
			System.out.println("Exception while setting up tests: " + e);
			e.printStackTrace();
		}
	}
}
