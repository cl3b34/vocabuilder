package br.boirque.vocabuilder.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import br.boirque.vocabuilder.model.FlashCard;
import br.boirque.vocabuilder.model.RecordStoreFactory;
import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAOV4Impl;
import br.boirque.vocabuilder.model.SetOfCardsLoader;
import br.boirque.vocabuilder.util.TestConstants;

import j2meunit.framework.*;

/**
 * Tests the functionality and performance of the loading system
 * 
 * @author cleber.goncalves
 * 
 */
public class SetOfCardsDAOV4ImplTest extends TestCase implements TestConstants {

	SetOfCards setOfCards;

	/**
	 * Default constructor
	 */
	public SetOfCardsDAOV4ImplTest() {
	}

	/**
	 * @param sTestName
	 *            The name of the test being called
	 * @param rTestMethod
	 *            A reference to the method to be called This seems to be
	 *            required by J2MEUnit
	 */
	public SetOfCardsDAOV4ImplTest(String sTestName, TestMethod rTestMethod) {
		super(sTestName, rTestMethod);
	}

	protected void setUp() throws Exception {
		SetOfCardsLoader socl = new SetOfCardsLoader();
		setOfCards = socl.loadSet(TXTSETTOLOAD);
		// set some values to the set
		setOfCards.setTotalStudiedTimeInMiliseconds(10000L);
		setOfCards.setTotalNumberOfDisplayedCards(20);
		setOfCards.setLastTimeViewed(20L);
		setOfCards.setLastTimeMarkedDone(10000L);
		setOfCards.setMarkedDoneCounter(100);
		setOfCards.setTotalNumberOfCardsMarkedDone(200);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ISetOfCardsDAO socd = new SetOfCardsDAOV4Impl(setOfCards.getSetName());
		socd.resetSetState();
		setOfCards = null;
		socd = null;
		System.gc();
	}

	public void testAddCardV4() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAOV4Impl socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.resetSetState();
		socdao.addFileFormatVersionNumber(4);
		socdao.addSetMetadata(setOfCards);
		FlashCard card = new FlashCard("parta", "suomi", "beard", "english",
				true, "woman hates", 999, 65, 98877L, 93883L, 0);

		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore recordStore = factory.getStoreInstance(setOfCards
				.getSetName());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);

		long startTime = System.currentTimeMillis();
		outputStream = socdao.addCardV4(card, outputStream);
		byte[] b = baos.toByteArray();
		int recordLength = b.length;
		recordStore.addRecord(b, 0, recordLength);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS AddCardV4 time: "
				+ milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddCardV4:" + milisecondsToSeconds(loadingTime),
				loadingTime < ADDMAXTIME);

		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(3));
		DataInputStream inputStream = new DataInputStream(bais);

		// recover it and verify
		FlashCard cardNew = socdao.loadCardV4(inputStream);
		validateCardAgainstOldCard(cardNew, card);
	}

	public void testAddFileFormatVersionNumber()
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException {
		ISetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.resetSetState();
		long startTime = System.currentTimeMillis();
		socdao.addFileFormatVersionNumber(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS AddFileFormatVersionNumber time: "
				+ milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddFFormat:" + milisecondsToSeconds(loadingTime),
				loadingTime < ADDMAXTIME);
	}

	public void testAddSetMetadata() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.resetSetState();
		socdao.addFileFormatVersionNumber(3);
		SetOfCards soc;
		long startTime = System.currentTimeMillis();
		soc = socdao.addSetMetadata(setOfCards);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS AddSetMetadata time: "
				+ milisecondsToSeconds(loadingTime));

		assertTrue("RMS AddMeta:" + milisecondsToSeconds(loadingTime),
				loadingTime < ADDMAXTIME);

		// recover it and verify
		SetOfCards socNew = socdao.loadSetMetadata(soc.getSetId());
		validateSetAgainstOldSet(soc, socNew);
	}

	public void testGetCardCount() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.resetSetState();
		socdao.saveSetOfCards(setOfCards);
		long startTime = System.currentTimeMillis();
		int recordCount = socdao.getCardCount();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS getRecordCount time: "
				+ milisecondsToSeconds(loadingTime));
		// the record store should have 1827 records + 1 metadata + 1 file
		// format version
		assertEquals(TOTALOFCARDS, recordCount);
		assertTrue("RMS getRecordCount:" + milisecondsToSeconds(loadingTime),
				loadingTime < COUNTMAXTIME);
	}

	public void testLoadCard() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAOV4Impl socd = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socd.resetSetState();
		socd.saveSetOfCards(setOfCards);
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore recordStore = factory.getStoreInstance(setOfCards
				.getSetName());
		ByteArrayInputStream bais = new ByteArrayInputStream(recordStore
				.getRecord(3));
		DataInputStream inputStream = new DataInputStream(bais);
		// measure performance
		long startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCardV4(inputStream);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS loadCardV4 time: "
				+ milisecondsToSeconds(loadingTime));
		// loading time must be under 300ms
		assertTrue("RMS loadCardV4:" + milisecondsToSeconds(loadingTime),
				loadingTime < LOADCARDMAXTIME);
		validateFirstCard(card);
	}

	public void testLoadFileFormatVersionNumber()
			throws RecordStoreFullException, RecordStoreNotFoundException,
			RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socd.resetSetState();
		socd.saveSetOfCards(setOfCards);
		// measure performance
		long startTime = System.currentTimeMillis();
		int versionNumber = socd.loadFileFormatVersionNumber(FILEFORMATRECORD);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS LoadFileFormatVersion time: "
				+ milisecondsToSeconds(loadingTime));
		assertEquals(ISetOfCardsDAO.FOURTHFILEFORMAT, versionNumber);
		// loading time must be under 300ms
		assertTrue("RMS LoadFFormat:" + milisecondsToSeconds(loadingTime),
				loadingTime < LOADCARDMAXTIME);
	}

	/*
	 * Tests if the set metadata is correctly recovered
	 */
	public void testLoadSetMetadata() throws RecordStoreNotOpenException,
			InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socd.resetSetState();
		SetOfCards setOrig = socd.saveSetOfCards(setOfCards);
		// measure performance
		long startTime = System.currentTimeMillis();
		SetOfCards setOfCardsMeta = socd.loadSetMetadata(METADATARECORD);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS loadMeta time: "
				+ milisecondsToSeconds(loadingTime));
		// under 300ms
		assertTrue("RMS loadMeta:" + milisecondsToSeconds(loadingTime),
				loadingTime < LOADCARDMAXTIME);
		// check if the recovered data from the set is correct
		assertEquals("wrong card amount\n", TOTALOFCARDS, setOfCardsMeta
				.getTotalNumberOfCards());
		validateSet(setOfCardsMeta);
		assertEquals("value is not equal", setOrig.getSetId(), setOfCardsMeta
				.getSetId());
	}

	public void testLoadSetOfCards() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		ISetOfCardsDAO socd = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socd.resetSetState();
		socd.saveSetOfCards(setOfCards);
		// measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadSetOfCards();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS loadSetV4 time: "
				+ milisecondsToSeconds(loadingTime));
		// loading time must be under 5s
		assertTrue("RMS loadSetV4:" + milisecondsToSeconds(loadingTime),
				loadingTime < LOADSETMAXTIME);
		validateSet(setOfCards);
		validateCards(setOfCards.getFlashCards());
		assertEquals("wrong card amount\n", TOTALOFCARDS, setOfCards
				.getFlashCards().size());
	}

	public void testResetState() throws RecordStoreNotFoundException,
			RecordStoreException {
		ISetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		long startTime = System.currentTimeMillis();
		socdao.resetSetState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS resetState time: "
				+ milisecondsToSeconds(loadingTime));

		// assert that the recordstore is empty
		assertEquals(0, socdao.getRecordCount());
		assertTrue("RMS resetState:" + milisecondsToSeconds(loadingTime),
				loadingTime < RESETMAXTIME);
	}

	public void testSaveSetOfCardsV4() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		ISetOfCardsDAO socd = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socd.resetSetState();
		long startTime = System.currentTimeMillis();
		socd.saveSetOfCards(setOfCards);
		long endTime = System.currentTimeMillis();
		long savingTime = endTime - startTime;
		System.out.println("RMS saveSetV4 time: "
				+ milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getCardCount() > 0);
		// time must be under 5s
		assertTrue("RMS saveSetV4:" + milisecondsToSeconds(savingTime),
				savingTime < SAVESETMAXTIME);

		// check if the recovered data from the set is correct
		SetOfCards soc = socd.loadSetOfCards();
		validateSet(soc);
		validateCards(soc.getFlashCards());
	}

	/**
	 * Test the update of the set metadata
	 * 
	 * @throws RecordStoreException
	 * @throws IOException
	 * @throws InvalidRecordIDException
	 * @throws RecordStoreNotOpenException
	 */
	public void testUpdateSetMetadata() throws RecordStoreNotOpenException,
			InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.resetSetState();
		setOfCards = socdao.saveSetOfCards(setOfCards);
		// convention for the set meta data = 2nd record
		SetOfCards soc = socdao.loadSetMetadata(setOfCards.getSetId());
		// modify it
		soc.setSetName("turbo set");
		soc.setDone(false);
		soc.setTotalStudiedTimeInMiliseconds(9L);
		soc.setTotalNumberOfDisplayedCards(983);
		soc.setLastTimeViewed(444L);
		soc.setLastTimeMarkedDone(867756L);
		soc.setMarkedDoneCounter(328838);
		soc.setTotalNumberOfCards(838);
		soc.setTotalNumberOfCardsMarkedDone(38);
		long startTime = System.currentTimeMillis();
		socdao.updateSetMetadata(soc);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime - startTime;
		System.out.println("RMS updateSetMetadata time: "
				+ milisecondsToSeconds(loadingTime));

		assertTrue("RMS updateMeta:" + milisecondsToSeconds(loadingTime),
				loadingTime < UPDATEMAXTIME);

		// recover it and verify
		SetOfCards socNew = socdao.loadSetMetadata(soc.getSetId());
		validateSetAgainstOldSet(soc, socNew);
	}

	public void testCardToByteArray() {
		// fail("Not yet implemented");
	}

	public void testGetAvailableSets() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.saveSetOfCards(setOfCards);
		assertTrue("Must be at least one set",
				SetOfCardsDAO.getAvailableSets().length > 0);
		assertTrue("Set must exist", isSetAvailable(SetOfCardsDAO
				.getAvailableSets(), setOfCards.getSetName()));
	}

	public void testUpdateCard() {
		// fail("Not yet implemented");
	}

	public void testGetRecordCount() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		int recCount = socdao.getRecordCount();
		assertEquals("incorrect record count", recCount, 0);
		socdao.saveSetOfCards(setOfCards);
		recCount = socdao.getRecordCount();
		assertEquals("incorrect record count", recCount, RECORDCOUNT);
	}

	public void testDeleteSetOfCards() throws RecordStoreFullException,
			RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAOV4Impl(RMSSETNAME);
		socdao.saveSetOfCards(setOfCards);
		assertTrue("Set must exist", isSetAvailable(SetOfCardsDAO
				.getAvailableSets(), setOfCards.getSetName()));
		socdao.deleteSetOfCards(setOfCards.getSetName());
		assertTrue("Set shouldn't exist", !isSetAvailable(SetOfCardsDAO
				.getAvailableSets(), setOfCards.getSetName()));
	}

	private boolean isSetAvailable(String[] availableSets, String setToCheck) {
		for (int i = 0; i < availableSets.length; i++) {
			String setAvailable = availableSets[i];
			if (setAvailable.equals(setToCheck)) {
				return true;
			}
		}
		return false;
	}

	private String milisecondsToSeconds(long timeToConvert) {
		if (timeToConvert < 1000L) {
			return timeToConvert + "ms";
		}
		return timeToConvert / 1000L + "s";
	}

	private void validateCardAgainstOldCard(FlashCard newCard, FlashCard oldCard) {
		assertEquals(newCard.getSideOne(), oldCard.getSideOne());
		assertEquals(newCard.getSideOneTitle(), oldCard.getSideOneTitle());
		assertEquals(newCard.getSideTwo(), oldCard.getSideTwo());
		assertEquals(newCard.getSideTwoTitle(), oldCard.getSideTwoTitle());
		assertTrue(newCard.isDone() == oldCard.isDone());
		assertEquals(newCard.getTip(), oldCard.getTip());
		assertTrue(newCard.getCardId() == oldCard.getCardId());
		assertTrue(newCard.getLastTimeMarkedDone() == oldCard
				.getLastTimeMarkedDone());
		assertTrue(newCard.getMarkedDoneCounter() == oldCard
				.getMarkedDoneCounter());
		assertTrue(newCard.getLastTimeViewed() == oldCard.getLastTimeViewed());
		assertTrue(newCard.getViewedCounter() == oldCard.getViewedCounter());
	}

	private void validateCards(Vector flashCards) {
		// Card 1
		FlashCard card1 = ((FlashCard) (flashCards.elementAt(0)));
		validateFirstCard(card1);

		// last card
		FlashCard card2 = ((FlashCard) (flashCards.lastElement()));
		validateLastCard(card2);
	}

	private void validateFirstCard(FlashCard card) {
		assertNotNull("Null card", card);
		assertEquals("value is not equal", FIRSTWORDONFIRSTCARD, card
				.getSideOne());
		assertEquals("value is not equal", SIDEONETITLE, card.getSideOneTitle());
		assertEquals("value is not equal", SECONDWORDONFIRSTCARD, card
				.getSideTwo());
		assertEquals("value is not equal", SIDETWOTITLE, card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}

	private void validateLastCard(FlashCard card) {
		assertNotNull("Null card", card);
		assertEquals("value is not equal", FIRSTWORDONLASTCARD, card
				.getSideOne());
		assertEquals("value is not equal", SIDEONETITLE, card.getSideOneTitle());
		assertEquals("value is not equal", SECONDWORDONLASTCARD, card
				.getSideTwo());
		assertEquals("value is not equal", SIDETWOTITLE, card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}

	private void validateSet(SetOfCards set) {
		assertNotNull("Null card metadata", set);
		assertEquals("wrong title", RMSSETNAME, set.getSetName());
		assertTrue("set should not be done", !set.isDone());
		assertEquals("value is not equal", 10000L, set
				.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal", 20, set
				.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal", 20L, set.getLastTimeViewed());
		assertEquals("value is not equal", 10000L, set.getLastTimeMarkedDone());
		assertEquals("value is not equal", 100, set.getMarkedDoneCounter());
		assertEquals("value is not equal", TOTALOFCARDS, set
				.getTotalNumberOfCards());
		assertEquals("value is not equal", 200, set
				.getTotalNumberOfCardsMarkedDone());
	}

	private void validateSetAgainstOldSet(SetOfCards oldSet, SetOfCards newSet) {
		assertNotNull("Null oldcard metadata", oldSet);
		assertNotNull("Null newcard metadata", newSet);
		assertEquals("Title differ", newSet.getSetName(), oldSet.getSetName());
		assertTrue("done differ", newSet.isDone() == oldSet.isDone());
		assertTrue("studied time differ", newSet
				.getTotalStudiedTimeInMiliseconds() == oldSet
				.getTotalStudiedTimeInMiliseconds());
		assertTrue("number of displayed", newSet
				.getTotalNumberOfDisplayedCards() == oldSet
				.getTotalNumberOfDisplayedCards());
		assertTrue("last time view", newSet.getLastTimeViewed() == oldSet
				.getLastTimeViewed());
		assertTrue("last time done", newSet.getLastTimeMarkedDone() == oldSet
				.getLastTimeMarkedDone());
		assertTrue("done counter", newSet.getMarkedDoneCounter() == oldSet
				.getMarkedDoneCounter());
		assertTrue("card total", newSet.getTotalNumberOfCards() == oldSet
				.getTotalNumberOfCards());
		assertTrue("card done total",
				newSet.getTotalNumberOfCardsMarkedDone() == oldSet
						.getTotalNumberOfCardsMarkedDone());
	}

	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsDAOV4ImplTest(
				"testLoadFileFormatVersionNumber", new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc)
								.testLoadFileFormatVersionNumber();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testLoadSetMetadata",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testLoadSetMetadata();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testUpdateCard",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							InvalidRecordIDException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testUpdateCard();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testGetCardCount",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testGetCardCount();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testAddSetMetadata",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							InvalidRecordIDException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testAddSetMetadata();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testUpdateSetMetadata",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							InvalidRecordIDException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testUpdateSetMetadata();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testResetState",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testResetState();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testLoadSetOfCards",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testLoadSetOfCards();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testLoadCard",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testLoadCard();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest(
				"testAddFileFormatVersionNumber", new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc)
								.testAddFileFormatVersionNumber();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testSaveSetOfCardsV4",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testSaveSetOfCardsV4();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testAddCardV4",
				new TestMethod() {
					public void run(TestCase tc)
							throws RecordStoreNotOpenException,
							RecordStoreFullException, IOException,
							RecordStoreException {
						((SetOfCardsDAOV4ImplTest) tc).testAddCardV4();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testCardToByteArray",
				new TestMethod() {
					public void run(TestCase tc) {
						((SetOfCardsDAOV4ImplTest) tc).testCardToByteArray();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testGetAvailableSets",
				new TestMethod() {
					public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
						((SetOfCardsDAOV4ImplTest) tc).testGetAvailableSets();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testGetRecordCount",
				new TestMethod() {
					public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
						((SetOfCardsDAOV4ImplTest) tc).testGetRecordCount();
					}
				}));

		testsuite.addTest(new SetOfCardsDAOV4ImplTest("testDeleteSetOfCards",
				new TestMethod() {
					public void run(TestCase tc) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
						((SetOfCardsDAOV4ImplTest) tc).testDeleteSetOfCards();
					}
				}));

		return testsuite;
	}
}
