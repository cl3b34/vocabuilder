package br.boirque.vocabuilder;


import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestSuite;

public class TestAll extends TestCase
{
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
		String[] runnerArgs = new String[] { "br.boirque.vocabuilder.TestAll" };
		j2meunit.textui.TestRunner.main(runnerArgs);
	}

	public Test suite()
	{
		TestSuite suite = new TestSuite();

		suite.addTest(new SetOfCardsDAOTest().suite());
		//suite.addTest(new TestTwo().suite());

		return suite;
	}
}

