package br.boirque.vocabuilder;


import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestSuite;
import br.boirque.vocabuilder.util.*;
import br.boirque.vocabuilder.model.*;
import br.boirque.vocabuilder.controller.*;
import br.boirque.vocabuilder.view.*;

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
		suite.addTest(new VocaUtilTest().suite());
		suite.addTest(new SetOfCardsLoaderTest().suite());
		suite.addTest(new SetOfCardsDAOTest().suite());
		suite.addTest(new InitializerTest().suite());
		suite.addTest(new RecordStoreFactoryTest().suite());
		return suite;
	}
}

