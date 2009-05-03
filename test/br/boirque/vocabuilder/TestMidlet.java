/*************************************************************************
 * Example MIDlet TestRunner that overloads the startApp() method and invokes 
 * start() with the names of the classes to test. This can be achieved also by
 * setting the JAD file attribute J2MEUnitTestClasses with the names of the 
 * classes to test and then invoke the superclass j2meunit.midletui.TestRunner
 * directly without subclassing it. 
 * 
 * @author eso
 */
package br.boirque.vocabuilder;
import j2meunit.midletui.TestRunner;

public class TestMidlet extends TestRunner {

	/***************************************
	 * ExampleTestRunner constructor comment.
	 */
	public TestMidlet()
	{
	}

	//~ Methods ----------------------------------------------------------------

	/***************************************
	 * startApp.
	 */
	protected void startApp()
	{
		start(new String[] { "br.boirque.vocabuilder.TestAll" });
	}
}
