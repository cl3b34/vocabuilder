package br.boirque.vocabuilder.model;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.io.IOException;
import java.util.Vector;

import br.boirque.vocabuilder.util.VocaUtil;

public class PropertiesLoaderTest extends TestCase {

	private static final String TEST_PROPERTIES_FILENAME = "testProperties.txt";
	/**
	 * Max loading time 0.5 second
	 */
	private static final long MAXLOADINGTIME = 500L;

	/**
	 * default constructor
	 */
	public PropertiesLoaderTest() {
	}

	/**
	 * required by J2ME unit
	 * 
	 * @param name
	 * @param testMethod
	 */
	public PropertiesLoaderTest(String name, TestMethod testMethod) {
		super(name, testMethod);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testLoadPropertie() throws IOException {
		PropertiesLoader ploader = new PropertiesLoader();
		long startTime = System.currentTimeMillis();
		Vector props = ploader.loadProperties(TEST_PROPERTIES_FILENAME);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		assertTrue("Props load:" + VocaUtil.milisecondsToSeconds(loadingTime),
				loadingTime < MAXLOADINGTIME);
		System.out.println("Props load: "
				+ VocaUtil.milisecondsToSeconds(loadingTime));
		assertNotNull(props);
		assertTrue(props.size() > 0);
		// check if all the properties are populated with something
		for (int i = 0; i < props.size(); i++) {
			Property prop = (Property)props.elementAt(i);
			assertNotNull(prop);
			String propName = prop.getName();
			assertNotNull(propName);
			assertTrue(propName.length() > 0);
			String propValue = prop.getValue();
			assertTrue(propValue.length() > 0);
		}

	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new PropertiesLoaderTest("testLoadPropertie",
				new TestMethod() {
					public void run(TestCase tc) throws IOException {
						((PropertiesLoaderTest) tc).testLoadPropertie();
					}
				}));

		return testsuite;
	}
}
