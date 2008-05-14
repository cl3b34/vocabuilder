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
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsLoader;

import j2meunit.framework.*;


/**
 * Tests the functionality and performance of the loading system
 * The list of cards used must be:
 * "/Finnish/longlist_fin_eng.txt"
 * @author cleber.goncalves
 *
 */
public class SetOfCardsDAOTest extends TestCase {

	String setToLoad = "/Finnish/longlist_fin_eng.txt";
	SetOfCards setOfCards;
	
	private static final long LOADSETMAXTIME = 2000L;
	private static final long SAVESETMAXTIME = 5000L;
	private static final long SAVECARDMAXTIME = 300L;
	private static final long LOADCARDMAXTIME = SAVECARDMAXTIME;
	private static final long ADDMAXTIME = SAVECARDMAXTIME;
	private static final long UPDATEMAXTIME = SAVECARDMAXTIME;
	private static final long COUNTMAXTIME = SAVECARDMAXTIME;
	private static final long RESETMAXTIME = SAVECARDMAXTIME;
	
	private static final int TOTALOFCARDS = 1827;
	private static final int METADATARECORD = 2;
	private static final int FILEFORMATRECORD = 1;
	
	/**
	 * Default constructor
	 */
	public SetOfCardsDAOTest() {
	}
	
	/**
	 * @param sTestName The name of the test being called
	 * @param rTestMethod A reference to the method to be called
	 * This seems to be required by J2MEUnit
	 */
	public SetOfCardsDAOTest(String sTestName, TestMethod rTestMethod)
	{
		super(sTestName, rTestMethod);
	}


	protected void setUp() throws Exception {	
		SetOfCardsLoader socl = new SetOfCardsLoader();
		setOfCards = socl.loadSet(setToLoad);
		//set some values to the set
		setOfCards.setTotalStudiedTimeInMiliseconds(10000L);
		setOfCards.setTotalNumberOfDisplayedCards(20);
		setOfCards.setLastTimeViewed(20L);
		setOfCards.setLastTimeMarkedDone(10000L);
		setOfCards.setMarkedDoneCounter(100);
		setOfCards.setTotalNumberOfCardsMarkedDone(200);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		setOfCards = null;
		setToLoad = null;
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd = null;
		System.gc();
	}
	
	public void testAddFileFormatVersionNumber() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		long startTime = System.currentTimeMillis();
		socdao.addFileFormatVersionNumber(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS AddFileFormatVersionNumber time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddFFormat:" + milisecondsToSeconds(loadingTime), loadingTime < ADDMAXTIME);
	}

	public void testLoadFileFormatVersionNumber() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV3(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		int versionNumber = socd.loadFileFormatVersionNumber(FILEFORMATRECORD);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS LoadFileFormatVersion time: " + milisecondsToSeconds(loadingTime));
		assertEquals(3, versionNumber);
		// loading time must be under 300ms
		assertTrue("RMS LoadFFormat:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
	}

	/*
	 * Tests if the set metadata is correctly recovered 
	 */
	public void testLoadSetMetadataV4() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();	
		SetOfCards setOrig = socd.saveSetOfCardsV3(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();		
		SetOfCards setOfCardsMeta = socd.loadSetMetadataV4(METADATARECORD);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadMeta time: " + milisecondsToSeconds(loadingTime));
		// under 300ms
		assertTrue("RMS loadMeta:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
		//check if the recovered data from the set is correct
		assertEquals("wrong card amount\n",TOTALOFCARDS + 2, socd.getRecordCount());
		validateSet(setOfCardsMeta);
		assertEquals("value is not equal",setOrig.getSetId(), setOfCardsMeta.getSetId());
	}

	/*
	 *
	 *This is the old format of metadata
	 */
	public void testLoadSetMetadataV2() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();	
		socd.saveSetOfCardsV2(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();		
		SetOfCards setOfCardsMeta = socd.loadSetMetadataV2(1);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadMetaV2 time: " + milisecondsToSeconds(loadingTime));
		// under 300ms
		assertTrue("RMS loadMetaV2:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
		//check if the recovered data from the set is correct
		assertEquals("wrong card amount\n",TOTALOFCARDS, socd.getRecordCount());
		validateSet(setOfCardsMeta);	
	}

	public void testLoadState() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV2(setOfCards);
		//measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadSetOfCardsV2();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("LoadSetOfCardsV2 time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 5s
		assertTrue("RMS loadSetOfCardsV2:" + milisecondsToSeconds(loadingTime), loadingTime < LOADSETMAXTIME);
		//validate the data loaded
		validateSet(setOfCards);
		validateCards(setOfCards.getFlashCards());
		assertEquals("wrong card amount\n",TOTALOFCARDS, setOfCards.getFlashCards().size());	
	}	
	
	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		long startTime = System.currentTimeMillis();		
		socd.saveSetOfCardsV2(setOfCards);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("saveSetOfCardsV2 time: " + milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// time must be under 5s
		assertTrue("saveSetOfCardsV2:" + milisecondsToSeconds(savingTime), savingTime < SAVESETMAXTIME);	
		
		//check if the recovered data is correct
		SetOfCards soc = socd.loadSetOfCardsV2();
		validateSet(soc);
		validateCards(soc.getFlashCards());		
	}
	
	public void testLoadCardV2() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV2(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		// with the old format, the first card is in record 1
		FlashCard card = socd.loadCardV2(1);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadCardV2 time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 300ms
		assertTrue("RMS loadCardV2:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
		assertEquals("wrong card amount\n",TOTALOFCARDS, socd.getRecordCount());	
		validateFirstCard(card);
	}

	public void testLoadCardV3() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV3(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		//First card in the list is in position 3...
		FlashCard card = socd.loadCardV3(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadCardV3 time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 300ms
		assertTrue("RMS loadCardV3:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
		assertEquals("wrong card amount\n",TOTALOFCARDS+2, socd.getRecordCount());
		validateFirstCard(card);
	}	
	
	
	public void testAddCard() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(3);
		socdao.addSetMetadataV4(setOfCards);
		FlashCard card = new FlashCard("parta","suomi","beard","english",true,"woman hates",999,65,98877L,93883L,0);
		long startTime = System.currentTimeMillis();
		card = socdao.addCard(card);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS AddCard time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddCard:" + milisecondsToSeconds(loadingTime), loadingTime < ADDMAXTIME);
		//recover it and verify
		FlashCard cardNew = socdao.loadCard(card.getCardId(),null);
		validateCardAgainstOldCard(cardNew, card);
	}

	/**
	 * tests if the card is updated correctly
	 * Original values:
	 * "aalto","FIN","fluid, liquid, wave","ENG",false,null
	 * @throws RecordStoreException 
	 * @throws IOException 
	 * @throws InvalidRecordIDException 
	 * @throws RecordStoreNotOpenException 
	 */
	public void testUpdateCard() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO setOfCardsdao = new SetOfCardsDAO();
		int cardId = 3;
		setOfCardsdao.resetState();
		setOfCardsdao.saveSetOfCardsV3(setOfCards);		
		FlashCard card = setOfCardsdao.loadCard(cardId, null);
		card.setSideOne("viini");
		card.setSideOneTitle("Suomi");
		card.setSideTwo("vodka");
		card.setSideTwoTitle("english");
		card.setDone(false);
		card.setTip("Makes you crazy");
		card.setCardId(cardId);
		long startTime = System.currentTimeMillis();
		setOfCardsdao.updateCard(card);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS UpdateCard time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS UpdateCard:" + milisecondsToSeconds(loadingTime), loadingTime < UPDATEMAXTIME);	
		
		//recover it and verify
		FlashCard cardNew = setOfCardsdao.loadCard(cardId,null);
		validateCardAgainstOldCard(cardNew, card);
	}

	public void testSaveSetOfCards() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		long startTime = System.currentTimeMillis();		
		socd.saveSetOfCardsV3(setOfCards);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("RMS saveSet time: " + milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",TOTALOFCARDS+2, socd.getRecordCount());
		// time must be under 5s
		assertTrue("RMS saveSet:" + milisecondsToSeconds(savingTime), savingTime < SAVESETMAXTIME);

		//check if the recovered data from the set is correct
		SetOfCards soc = socd.loadSetOfCardsV3();
		validateSet(soc);
		validateCards(soc.getFlashCards());
	}	
	
	
	public void testLoadSetOfCards() throws RecordStoreNotFoundException, RecordStoreException, IOException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV3(setOfCards);
		//measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadSetOfCardsV3();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadSet time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 5s
		assertTrue("RMS loadSet:" + milisecondsToSeconds(loadingTime), loadingTime < LOADSETMAXTIME);
		validateSet(setOfCards);
		assertEquals("wrong card amount\n",TOTALOFCARDS+2, socd.getRecordCount());
	}

	public void testGetRecordCount() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.saveSetOfCardsV3(setOfCards);		
		long startTime = System.currentTimeMillis();
		int recordCount = socdao.getRecordCount();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS getRecordCount time: " + milisecondsToSeconds(loadingTime));
		// the record store should have 1827 records + 1 metadata + 1 file format version
		assertEquals(TOTALOFCARDS+2, recordCount); 	
		assertTrue("RMS getRecordCount:" + milisecondsToSeconds(loadingTime), loadingTime < COUNTMAXTIME);
	}

	public void testAddSetMetadata() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(3);
		SetOfCards soc;
		long startTime = System.currentTimeMillis();
		soc = socdao.addSetMetadataV4(setOfCards);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS AddSetMetadata time: " + milisecondsToSeconds(loadingTime));

		assertTrue("RMS AddMeta:" + milisecondsToSeconds(loadingTime), loadingTime < ADDMAXTIME);

		//recover it and verify
		SetOfCards socNew = socdao.loadSetMetadataV4(soc.getSetId());
		validateSetAgainstOldSet(soc, socNew);	
	}
	
	/**
	 * Test the update of the set metadata
	 * @throws RecordStoreException 
	 * @throws IOException 
	 * @throws InvalidRecordIDException 
	 * @throws RecordStoreNotOpenException 
	 */
	public void testUpdateSetMetadata() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		setOfCards = socdao.saveSetOfCardsV4(setOfCards);
		//convention for the set meta data = 2nd record
		SetOfCards soc = socdao.loadSetMetadataV4(setOfCards.getSetId());
		//modify it
		soc.setTitle("turbo set");
		soc.setDone(false);
		soc.setTotalStudiedTimeInMiliseconds(9L);
		soc.setTotalNumberOfDisplayedCards(983);
		soc.setLastTimeViewed(444L);
		soc.setLastTimeMarkedDone(867756L);
		soc.setMarkedDoneCounter(328838);
		soc.setTotalNumberOfCards(838);
		soc.setTotalNumberOfCardsMarkedDone(38);
		long startTime = System.currentTimeMillis();
		socdao.updateSetMetadataV4(soc);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS updateSetMetadata time: " + milisecondsToSeconds(loadingTime));

		assertTrue("RMS updateMeta:" + milisecondsToSeconds(loadingTime), loadingTime < UPDATEMAXTIME);
		
		//recover it and verify
		SetOfCards socNew = socdao.loadSetMetadataV4(soc.getSetId());
		validateSetAgainstOldSet(soc, socNew);
	}	
	
	public void testResetState() throws RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		long startTime = System.currentTimeMillis();
		socdao.resetState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS resetState time: " + milisecondsToSeconds(loadingTime));

		//assert that the recordstore is empty		
		assertEquals(0, socdao.getRecordCount());
		assertTrue("RMS resetState:" + milisecondsToSeconds(loadingTime), loadingTime < RESETMAXTIME);
	}	
	
	public void testLoadSetOfCardsV4() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV4(setOfCards);
		//measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadSetOfCardsV4();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadSetV4 time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 5s
		assertTrue("RMS loadSetV4:" + milisecondsToSeconds(loadingTime), loadingTime < LOADSETMAXTIME);
		validateSet(setOfCards);
		validateCards(setOfCards.getFlashCards());
		assertEquals("wrong card amount\n",TOTALOFCARDS, setOfCards.getFlashCards().size());	
	}

	
	public void testLoadCardV4() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCardsV4(setOfCards);
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore recordStore = factory.getStoreInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(
				recordStore.getRecord(3));
		DataInputStream inputStream = new DataInputStream(bais);
		//measure performance
		long startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCardV4(inputStream);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loadCardV4 time: " + milisecondsToSeconds(loadingTime));
		// loading time must be under 300ms
		assertTrue("RMS loadCardV4:" + milisecondsToSeconds(loadingTime), loadingTime < LOADCARDMAXTIME);
		validateFirstCard(card);
	}


	public void testSaveSetOfCardsV4() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		long startTime = System.currentTimeMillis();		
		socd.saveSetOfCardsV4(setOfCards);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("RMS saveSetV4 time: " + milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		// time must be under 5s
		assertTrue("RMS saveSetV4:" + milisecondsToSeconds(savingTime), savingTime < SAVESETMAXTIME);

		//check if the recovered data from the set is correct
		SetOfCards soc = socd.loadSetOfCardsV4();
		validateSet(soc);
		validateCards(soc.getFlashCards());
	}

	public void testAddCardV4() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(4);
		socdao.addSetMetadataV4(setOfCards);
		FlashCard card = new FlashCard("parta","suomi","beard","english",true,"woman hates",999,65,98877L,93883L,0);
		
		RecordStoreFactory factory = RecordStoreFactory.getFactory();
		RecordStore recordStore = factory.getStoreInstance();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(baos);	
		
		long startTime = System.currentTimeMillis();
		outputStream = socdao.addCardV4(card, outputStream);
		byte[] b = baos.toByteArray();
		int recordLength = b.length;
		recordStore.addRecord(b, 0, recordLength);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS AddCardV4 time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddCardV4:" + milisecondsToSeconds(loadingTime), loadingTime < ADDMAXTIME);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(
				recordStore.getRecord(3));
		DataInputStream inputStream = new DataInputStream(bais);
		
		//recover it and verify
		FlashCard cardNew = socdao.loadCardV4(inputStream);
		validateCardAgainstOldCard(cardNew, card);
	}
	
	private void validateCardAgainstOldCard(FlashCard newCard, FlashCard oldCard) {
		assertEquals(newCard.getSideOne(), oldCard.getSideOne());
		assertEquals(newCard.getSideOneTitle(), oldCard.getSideOneTitle());
		assertEquals(newCard.getSideTwo(), oldCard.getSideTwo());
		assertEquals(newCard.getSideTwoTitle(), oldCard.getSideTwoTitle());
		assertTrue(newCard.isDone() == oldCard.isDone());
		assertEquals(newCard.getTip(), oldCard.getTip());
		assertTrue(newCard.getCardId()== oldCard.getCardId());
		assertTrue(newCard.getLastTimeMarkedDone()== oldCard.getLastTimeMarkedDone());
		assertTrue(newCard.getMarkedDoneCounter()== oldCard.getMarkedDoneCounter());
		assertTrue(newCard.getLastTimeViewed()== oldCard.getLastTimeViewed());
		assertTrue(newCard.getViewedCounter()== oldCard.getViewedCounter());
	}
	
	private void validateCards(Vector flashCards) {
		//Card 1
		FlashCard card1 = ((FlashCard)(flashCards.elementAt(0)));
		validateFirstCard(card1);
		
		// last card
		FlashCard card2 = ((FlashCard)(flashCards.lastElement()));
		validateLastCard(card2);
	}
	
	private void validateFirstCard(FlashCard card){
		assertNotNull("Null card", card);
		assertEquals("value is not equal","aalto", card.getSideOne());
		assertEquals("value is not equal","FIN", card.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", card.getSideTwo());
		assertEquals("value is not equal","ENG", card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}
	
	private void validateLastCard(FlashCard card){
		assertNotNull("Null card", card);
		assertEquals("value is not equal","öljy", card.getSideOne());
		assertEquals("value is not equal","FIN", card.getSideOneTitle());
		assertEquals("value is not equal","oil", card.getSideTwo());
		assertEquals("value is not equal","ENG", card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}
	
	private void validateSet(SetOfCards set) {
		assertNotNull("Null card metadata", set);
		assertEquals("wrong title","longlist_fi_en", set.getTitle());
		assertTrue("set should not be done", !set.isDone());		
		assertEquals("value is not equal",10000L, set.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",20, set.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, set.getLastTimeViewed());
		assertEquals("value is not equal",10000L, set.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, set.getMarkedDoneCounter());	
		assertEquals("value is not equal",TOTALOFCARDS, set.getTotalNumberOfCards());	
		assertEquals("value is not equal",200, set.getTotalNumberOfCardsMarkedDone());	
	}
	
	private void validateSetAgainstOldSet(SetOfCards oldSet, SetOfCards newSet) {
		assertNotNull("Null oldcard metadata", oldSet);
		assertNotNull("Null newcard metadata", newSet);
		assertEquals("Title differ",newSet.getTitle(), oldSet.getTitle());
		assertTrue("done differ",newSet.isDone() == oldSet.isDone());
		assertTrue("studied time differ",newSet.getTotalStudiedTimeInMiliseconds() == oldSet.getTotalStudiedTimeInMiliseconds());
		assertTrue("number of displayed",newSet.getTotalNumberOfDisplayedCards() == oldSet.getTotalNumberOfDisplayedCards());
		assertTrue("last time view",newSet.getLastTimeViewed()== oldSet.getLastTimeViewed());
		assertTrue("last time done",newSet.getLastTimeMarkedDone()== oldSet.getLastTimeMarkedDone());
		assertTrue("done counter",newSet.getMarkedDoneCounter()== oldSet.getMarkedDoneCounter());
		assertTrue("card total",newSet.getTotalNumberOfCards()== oldSet.getTotalNumberOfCards());
		assertTrue("card done total",newSet.getTotalNumberOfCardsMarkedDone()== oldSet.getTotalNumberOfCardsMarkedDone());
	}
	
	private String milisecondsToSeconds(long timeToConvert) {
		if (timeToConvert < 1000L){
			return timeToConvert + "ms";
		}
		return timeToConvert/1000L + "s";		
	}
	
			
	public Test suite() {
		TestSuite testsuite = new TestSuite();

//		testsuite.addTest(new SetOfCardsDAOTest("testLoadState", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testLoadState(); 
//			} 
//		}));
//		
//		testsuite.addTest(new SetOfCardsDAOTest("testSaveState", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testSaveState(); 
//			} 
//		}));
//
//		testsuite.addTest(new SetOfCardsDAOTest("testLoadCardV2", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testLoadCardV2(); 
//			} 
//		}));
//		
//		testsuite.addTest(new SetOfCardsDAOTest("testLoadCardV3", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testLoadCardV3(); 
//			} 
//		}));
//				
		testsuite.addTest(new SetOfCardsDAOTest("testLoadFileFormatVersionNumber", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadFileFormatVersionNumber(); 
			} 
		}));		
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetMetadata", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetMetadataV4(); 
			} 
		}));
		
//		testsuite.addTest(new SetOfCardsDAOTest("testSaveSetOfCards", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testSaveSetOfCards(); 
//			} 
//		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testUpdateCard", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
				((SetOfCardsDAOTest) tc).testUpdateCard(); 
			} 
		}));
		
//		testsuite.addTest(new SetOfCardsDAOTest("testAddCard", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
//				((SetOfCardsDAOTest) tc).testAddCard(); 
//			} 
//		}));		
		
		testsuite.addTest(new SetOfCardsDAOTest("testGetRecordCount", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testGetRecordCount(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testAddSetMetadata", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
				((SetOfCardsDAOTest) tc).testAddSetMetadata(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testUpdateSetMetadata", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
				((SetOfCardsDAOTest) tc).testUpdateSetMetadata(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testResetState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testResetState(); 
			} 
		}));
		
//		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetOfCards", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testLoadSetOfCards(); 
//			} 
//		}));
//		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetOfCardsV4", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetOfCardsV4(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadCardV4", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadCardV4(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testAddFileFormatVersionNumber", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testAddFileFormatVersionNumber(); 
			} 
		}));
		
//		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetMetadataV2", new TestMethod(){ 
//			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
//				((SetOfCardsDAOTest) tc).testLoadSetMetadataV2(); 
//			} 
//		}));
//		
		testsuite.addTest(new SetOfCardsDAOTest("testSaveSetOfCardsV4", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testSaveSetOfCardsV4(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testAddCardV4", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testAddCardV4(); 
			} 
		}));
		
		
		return testsuite;
	}
}
