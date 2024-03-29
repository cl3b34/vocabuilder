package br.boirque.vocabuilder;


import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestSuite;
import br.boirque.vocabuilder.controller.InitializerTest;
import br.boirque.vocabuilder.model.PropertiesLoaderTest;
import br.boirque.vocabuilder.model.RecordStoreFactoryTest;
import br.boirque.vocabuilder.model.SetOfCardsDAOV4ImplTest;
import br.boirque.vocabuilder.model.SetOfCardsLoaderTest;
import br.boirque.vocabuilder.model.StudyStackDownloaderTest;
import br.boirque.vocabuilder.util.VocaUtilTest;

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
		suite.addTest(new InitializerTest().suite());
		suite.addTest(new SetOfCardsLoaderTest().suite());
//		suite.addTest(new PropertiesLoaderTest().suite());
		suite.addTest(new RecordStoreFactoryTest().suite());
		suite.addTest(new SetOfCardsDAOV4ImplTest().suite());
		suite.addTest(new VocaUtilTest().suite());
		suite.addTest(new StudyStackDownloaderTest().suite());
		return suite;
	}
}

