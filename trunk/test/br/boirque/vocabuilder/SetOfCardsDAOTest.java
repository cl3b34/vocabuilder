package br.boirque.vocabuilder;

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
	
	public void testLoadState() throws InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(setOfCards);
		//measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading state time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null set of cards", setOfCards);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 5s
		assertTrue("RMS load:" + milisecondsToSeconds(loadingTime), loadingTime < 5000L);		
	}	
	
	public void testSaveState() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		long startTime = System.currentTimeMillis();		
		socd.saveState(setOfCards);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("RMS save state time: " + milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// time must be under 5s
		assertTrue("RMS save:" + milisecondsToSeconds(savingTime), savingTime < 5000L);	
		
		//check if the recovered data from the set is correct
		SetOfCards soc = socd.loadState();
		assertNotNull("set null", soc);
		assertTrue("set should not be done", !soc.isDone());
		assertEquals("wrong title","longlist_fi_en", soc.getTitle());
		assertEquals("wrong total time",10000L, soc.getTotalStudiedTimeInMiliseconds());
		//recover the flash cards from the set and check
		// if the data is matches the original
		Vector flashCards = soc.getFlashCards();
		
		//Card 1
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("value is not equal","aalto", flashCard1.getSideOne());
		assertEquals("value is not equal","FIN", flashCard1.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", flashCard1.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard1.getSideTwoTitle());
		assertTrue("should not be done", !flashCard1.isDone());
		
		// last card
		FlashCard flashCard2 = ((FlashCard)(flashCards.lastElement()));
		assertEquals("value is not equal","öljy", flashCard2.getSideOne());
		assertEquals("value is not equal","FIN", flashCard2.getSideOneTitle());
		assertEquals("value is not equal","oil", flashCard2.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard2.getSideTwoTitle());
		assertTrue("should not be done", !flashCard2.isDone());
	}
	
	public void testLoadCardV2() throws IOException, RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveState(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCardV2(1);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading cardv2 time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null card", card);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// loading time must be under 300ms
		assertTrue("RMS loadCardV2:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		
		// with the old format, the first card is in record 1
		assertEquals("value is not equal","aalto", card.getSideOne());
		assertEquals("value is not equal","FIN", card.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", card.getSideTwo());
		assertEquals("value is not equal","ENG", card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());	
	}
	
	public void testLoadCardV3() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCards(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		FlashCard card = socd.loadCardV3(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading cardV3 time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null card", card);
		assertEquals("wrong card amount\n",1829, socd.getRecordCount());
		// loading time must be under 300ms
		assertTrue("RMS loadCardV3:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);

		//First card in the list is in position 3...
		assertEquals("value is not equal","aalto", card.getSideOne());
		assertEquals("value is not equal","FIN", card.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", card.getSideTwo());
		assertEquals("value is not equal","ENG", card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}
	
	
	
	public void testLoadFileFormatVersionNumber() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCards(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();
		int versionNumber = socd.loadFileFormatVersionNumber(1);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading file format version time: " + milisecondsToSeconds(loadingTime));
		assertEquals(3, versionNumber);
		// loading time must be under 300ms
		assertTrue("RMS LoadFileFormatVersion:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
	}
	
	/*
	 * setOfCardsTitle setOfCardsIsDone setOfCardsStudyTime totalCardsDisplayed
	 *	lastTimeSetViewed lastTimeSetMarkedDone markedDoneSetCounter
	 */
	public void testLoadSetMetadata() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException  {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();	
		SetOfCards setOrig = socd.saveSetOfCards(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();		
		SetOfCards setOfCardsMeta = socd.loadSetMetadata(2);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading set metadata time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null card metadata", setOfCardsMeta);
		assertEquals("wrong card amount\n",1829, socd.getRecordCount());
		// under 300ms
		assertTrue("RMS loadMeta:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		//check if the recovered data from the set is correct
		assertEquals("wrong title","longlist_fi_en", setOfCardsMeta.getTitle());
		assertTrue("set should not be done", !setOfCardsMeta.isDone());		
		assertEquals("value is not equal",10000L, setOfCardsMeta.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",20, setOfCardsMeta.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, setOfCardsMeta.getLastTimeViewed());
		assertEquals("value is not equal",10000L, setOfCardsMeta.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, setOfCardsMeta.getMarkedDoneCounter());
		assertEquals("value is not equal",setOrig.getSetId(), setOfCardsMeta.getSetId());
	}
	
	public void testSaveSetOfCards() throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		long startTime = System.currentTimeMillis();		
		socd.saveSetOfCards(setOfCards);		
		long endTime = System.currentTimeMillis();
		long savingTime = endTime -startTime;
		System.out.println("RMS save set time: " + milisecondsToSeconds(savingTime));
		assertTrue("Empty Saved set", socd.getRecordCount() > 0);
		assertEquals("wrong card amount\n",1829, socd.getRecordCount());
		// time must be under 5s
		assertTrue("RMS saveSet:" + milisecondsToSeconds(savingTime), savingTime < 5000L);

		//check if the recovered data from the set is correct
		SetOfCards soc = socd.loadSetOfCards();
		assertEquals("wrong title","longlist_fi_en", soc.getTitle());
		assertTrue("set should not be done", !soc.isDone());		
		assertEquals("value is not equal",10000L, soc.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",20, soc.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, soc.getLastTimeViewed());
		assertEquals("value is not equal",10000L, soc.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, soc.getMarkedDoneCounter());
		//recover the flash cards from the set and check
		// if the data matches the original
		Vector flashCards = soc.getFlashCards();
		//Card 1
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("value is not equal","aalto", flashCard1.getSideOne());
		assertEquals("value is not equal","FIN", flashCard1.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", flashCard1.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard1.getSideTwoTitle());
		assertTrue("should not be done", !flashCard1.isDone());
		
		// last card
		FlashCard flashCard2 = ((FlashCard)(flashCards.lastElement()));
		assertEquals("value is not equal","öljy", flashCard2.getSideOne());
		assertEquals("value is not equal","FIN", flashCard2.getSideOneTitle());
		assertEquals("value is not equal","oil", flashCard2.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard2.getSideTwoTitle());
		assertTrue("should not be done", !flashCard2.isDone());
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
		setOfCardsdao.saveSetOfCards(setOfCards);		
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
		System.out.println("RMS update card time: " + milisecondsToSeconds(loadingTime));

		//recover it and verify
		FlashCard cardNew = setOfCardsdao.loadCard(cardId,null);
		assertEquals(cardNew.getSideOne(), "viini");
		assertEquals(cardNew.getSideOneTitle(), "Suomi");
		assertEquals(cardNew.getSideTwo(), "vodka");
		assertEquals(cardNew.getSideTwoTitle(), "english");
		assertTrue(cardNew.isDone() == false);
		assertEquals(cardNew.getTip(), "Makes you crazy");
		assertEquals(cardNew.getCardId(), cardId);
		assertTrue("RMS UpdateCard:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
	}

	public void testAddCard() throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(3);
		socdao.addSetMetadata(setOfCards);
		FlashCard card = new FlashCard("parta","suomi","beard","english",true,"woman hates",999,65,98877L,93883L,0);
		long startTime = System.currentTimeMillis();
		card = socdao.addCard(card);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS add card time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddCard:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		//recover it and verify
		FlashCard cardNew = socdao.loadCard(card.getCardId(),null);
		assertEquals(cardNew.getSideOne(), card.getSideOne());
		assertEquals(cardNew.getSideOneTitle(), card.getSideOneTitle());
		assertEquals(cardNew.getSideTwo(), "beard");
		assertEquals(cardNew.getSideTwoTitle(), "english");
		assertTrue(cardNew.isDone() == card.isDone());
		assertEquals(cardNew.getTip(), card.getTip());
		assertTrue(cardNew.getCardId()== card.getCardId());
		assertTrue(cardNew.getLastTimeMarkedDone()== card.getLastTimeMarkedDone());
		assertTrue(cardNew.getMarkedDoneCounter()== card.getMarkedDoneCounter());
		assertTrue(cardNew.getLastTimeViewed()== card.getLastTimeViewed());
		assertTrue(cardNew.getViewedCounter()== card.getViewedCounter());		
	}
	
	public void testGetRecordCount() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.saveSetOfCards(setOfCards);		
		long startTime = System.currentTimeMillis();
		int recordCount = socdao.getRecordCount();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS get record count time: " + milisecondsToSeconds(loadingTime));
		// the record store should have 1827 records + 1 metadata + 1 file format version
		assertEquals(1829, recordCount); 	
		assertTrue("RMS getRecordCount:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);

	}

	public void testAddSetMetadata() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(3);
		SetOfCards soc = new SetOfCards("jet set", true, 999L, null, 0, 993L,88L, 2);
		long startTime = System.currentTimeMillis();
		soc = socdao.addSetMetadata(soc);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS add metadata time: " + milisecondsToSeconds(loadingTime));

		assertTrue("RMS AddSetMetadata:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);

		//recover it and verify
		SetOfCards socNew = socdao.loadSetMetadata(soc.getSetId());
		assertEquals(socNew.getTitle(), soc.getTitle());
		assertTrue(socNew.isDone() == soc.isDone());
		assertTrue(socNew.getTotalStudiedTimeInMiliseconds() == 999L);
		assertTrue(socNew.getTotalNumberOfDisplayedCards() == soc.getTotalNumberOfDisplayedCards());
		assertTrue(socNew.getLastTimeViewed()== soc.getLastTimeViewed());
		assertTrue(socNew.getLastTimeMarkedDone()== soc.getLastTimeMarkedDone());
		assertTrue(socNew.getMarkedDoneCounter()== soc.getMarkedDoneCounter());
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
		setOfCards = socdao.saveSetOfCards(setOfCards);
		//convention for the set meta data = 2nd record
		SetOfCards soc = socdao.loadSetMetadata(setOfCards.getSetId());
		//modify it
		soc.setTitle("turbo set");
		soc.setDone(false);
		soc.setTotalStudiedTimeInMiliseconds(9L);
		soc.setTotalNumberOfDisplayedCards(983);
		soc.setLastTimeViewed(444L);
		soc.setLastTimeMarkedDone(867756L);
		soc.setMarkedDoneCounter(328838);
		long startTime = System.currentTimeMillis();
		socdao.updateSetMetadata(soc);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS update metadata time: " + milisecondsToSeconds(loadingTime));

		assertTrue("RMS updateSetMetadata:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		
		//recover it and verify
		SetOfCards socNew = socdao.loadSetMetadata(soc.getSetId());
		assertEquals("Title differ", socNew.getTitle(), soc.getTitle());
		assertTrue("done differ", socNew.isDone() == soc.isDone());
		assertTrue("studied time differ",socNew.getTotalStudiedTimeInMiliseconds() == 9L);
		assertTrue("number of displayed",socNew.getTotalNumberOfDisplayedCards() == soc.getTotalNumberOfDisplayedCards());
		assertTrue("last time view",socNew.getLastTimeViewed()== soc.getLastTimeViewed());
		assertTrue("last time done",socNew.getLastTimeMarkedDone()== soc.getLastTimeMarkedDone());
		assertTrue("done counter",socNew.getMarkedDoneCounter()== soc.getMarkedDoneCounter());
	}	
	
	public void testResetState() throws RecordStoreNotFoundException, RecordStoreException{
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		long startTime = System.currentTimeMillis();
		socdao.resetState();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS reset state time: " + milisecondsToSeconds(loadingTime));

		//assert that the recordstore is empty		
		assertEquals(0, socdao.getRecordCount());
		assertTrue("RMS resetState:" + milisecondsToSeconds(loadingTime), loadingTime < 50L);
	}	
	
	public void testLoadSetOfCards() throws RecordStoreNotFoundException, RecordStoreException, IOException{
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();
		socd.saveSetOfCards(setOfCards);
		//measure the performance
		long startTime = System.currentTimeMillis();
		setOfCards = socd.loadSetOfCards();
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading set time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null set of cards", setOfCards);
		assertEquals("wrong card amount\n",1829, socd.getRecordCount());
		// loading time must be under 5s
		assertTrue("RMS loadSet:" + milisecondsToSeconds(loadingTime), loadingTime < 5000L);
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
		System.out.println("RMS loading setV4 time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null set of cards", setOfCards);
		assertEquals("wrong card amount\n",1827, setOfCards.getFlashCards().size());
		// loading time must be under 5s
		assertTrue("RMS loadSetV4:" + milisecondsToSeconds(loadingTime), loadingTime < 5000L);
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
		System.out.println("RMS loading cardV4 time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null card", card);
		// loading time must be under 300ms
		assertTrue("RMS loadCardV4:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);

		//First card in the list is in position 3...
		assertEquals("value is not equal","aalto", card.getSideOne());
		assertEquals("value is not equal","FIN", card.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", card.getSideTwo());
		assertEquals("value is not equal","ENG", card.getSideTwoTitle());
		assertTrue("should not be done", !card.isDone());
	}


	public void testAddFileFormatVersionNumber() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		long startTime = System.currentTimeMillis();
		socdao.addFileFormatVersionNumber(3);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS add fileFormatVersion time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddFileFormatVersion:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
	}

	/*
	 * setOfCardsTitle setOfCardsIsDone setOfCardsStudyTime totalCardsDisplayed
	 *	lastTimeSetViewed lastTimeSetMarkedDone markedDoneSetCounter
	 *
	 *This is the old format of metadata
	 */
	public void testLoadSetMetadataV2() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socd = new SetOfCardsDAO();
		socd.resetState();	
		socd.saveState(setOfCards);
		//measure performance
		long startTime = System.currentTimeMillis();		
		SetOfCards setOfCardsMeta = socd.loadSetMetadataV2(1);
		long endTime = System.currentTimeMillis();
		long loadingTime = endTime -startTime;
		System.out.println("RMS loading set metadataV2 time: " + milisecondsToSeconds(loadingTime));
		assertNotNull("Null set metadata", setOfCardsMeta);
		assertEquals("wrong card amount\n",1827, socd.getRecordCount());
		// under 300ms
		assertTrue("RMS loadMetaV2:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		//check if the recovered data from the set is correct
		assertEquals("wrong title","longlist_fi_en", setOfCardsMeta.getTitle());
		assertTrue("set should not be done", !setOfCardsMeta.isDone());		
		assertEquals("value is not equal",10000L, setOfCardsMeta.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",20, setOfCardsMeta.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, setOfCardsMeta.getLastTimeViewed());
		assertEquals("value is not equal",10000L, setOfCardsMeta.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, setOfCardsMeta.getMarkedDoneCounter());
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
		assertTrue("RMS saveSet:" + milisecondsToSeconds(savingTime), savingTime < 5000L);

		//check if the recovered data from the set is correct
		SetOfCards soc = socd.loadSetOfCardsV4();
		assertEquals("wrong card amount\n",1827, soc.getFlashCards().size());
		assertEquals("wrong title","longlist_fi_en", soc.getTitle());
		assertTrue("set should not be done", !soc.isDone());		
		assertEquals("value is not equal",10000L, soc.getTotalStudiedTimeInMiliseconds());
		assertEquals("value is not equal",20, soc.getTotalNumberOfDisplayedCards());
		assertEquals("value is not equal",20L, soc.getLastTimeViewed());
		assertEquals("value is not equal",10000L, soc.getLastTimeMarkedDone());
		assertEquals("value is not equal",100, soc.getMarkedDoneCounter());
		//recover the flash cards from the set and check
		// if the data matches the original
		Vector flashCards = soc.getFlashCards();
		//Card 1
		FlashCard flashCard1 = ((FlashCard)(flashCards.elementAt(0)));
		assertEquals("value is not equal","aalto", flashCard1.getSideOne());
		assertEquals("value is not equal","FIN", flashCard1.getSideOneTitle());
		assertEquals("value is not equal","fluid, liquid, wave", flashCard1.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard1.getSideTwoTitle());
		assertTrue("should not be done", !flashCard1.isDone());
		
		// last card
		FlashCard flashCard2 = ((FlashCard)(flashCards.lastElement()));
		assertEquals("value is not equal","öljy", flashCard2.getSideOne());
		assertEquals("value is not equal","FIN", flashCard2.getSideOneTitle());
		assertEquals("value is not equal","oil", flashCard2.getSideTwo());
		assertEquals("value is not equal","ENG", flashCard2.getSideTwoTitle());
		assertTrue("should not be done", !flashCard2.isDone());

	}

	public void testAddCardV4() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException {
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.resetState();
		socdao.addFileFormatVersionNumber(4);
		socdao.addSetMetadata(setOfCards);
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
		System.out.println("RMS add cardV4 time: " + milisecondsToSeconds(loadingTime));
		assertTrue("RMS AddCardV4:" + milisecondsToSeconds(loadingTime), loadingTime < 300L);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(
				recordStore.getRecord(3));
		DataInputStream inputStream = new DataInputStream(bais);
		
		//recover it and verify
		FlashCard cardNew = socdao.loadCardV4(inputStream);
		assertEquals(cardNew.getSideOne(), card.getSideOne());
		assertEquals(cardNew.getSideOneTitle(), card.getSideOneTitle());
		assertEquals(cardNew.getSideTwo(), "beard");
		assertEquals(cardNew.getSideTwoTitle(), "english");
		assertTrue(cardNew.isDone() == card.isDone());
		assertEquals(cardNew.getTip(), card.getTip());
		assertTrue(cardNew.getCardId()== card.getCardId());
		assertTrue(cardNew.getLastTimeMarkedDone()== card.getLastTimeMarkedDone());
		assertTrue(cardNew.getMarkedDoneCounter()== card.getMarkedDoneCounter());
		assertTrue(cardNew.getLastTimeViewed()== card.getLastTimeViewed());
		assertTrue(cardNew.getViewedCounter()== card.getViewedCounter());
	}
	
	
	
	private String milisecondsToSeconds(long timeToConvert) {
		if (timeToConvert < 1000L){
			return timeToConvert + "ms";
		}
		return timeToConvert/1000L + "s";		
	}
	
			
	public Test suite() {
		TestSuite testsuite = new TestSuite();

		testsuite.addTest(new SetOfCardsDAOTest("testLoadState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadState(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testSaveState", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testSaveState(); 
			} 
		}));

		testsuite.addTest(new SetOfCardsDAOTest("testLoadCardV2", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadCardV2(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadCardV3", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadCardV3(); 
			} 
		}));
				
		testsuite.addTest(new SetOfCardsDAOTest("testLoadFileFormatVersionNumber", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadFileFormatVersionNumber(); 
			} 
		}));		
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetMetadata", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetMetadata(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testSaveSetOfCards", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testSaveSetOfCards(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testUpdateCard", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
				((SetOfCardsDAOTest) tc).testUpdateCard(); 
			} 
		}));
		
		testsuite.addTest(new SetOfCardsDAOTest("testAddCard", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, InvalidRecordIDException, IOException, RecordStoreException{
				((SetOfCardsDAOTest) tc).testAddCard(); 
			} 
		}));		
		
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
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetOfCards", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetOfCards(); 
			} 
		}));
		
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
		
		testsuite.addTest(new SetOfCardsDAOTest("testLoadSetMetadataV2", new TestMethod(){ 
			public void run(TestCase tc) throws RecordStoreNotOpenException, RecordStoreFullException, IOException, RecordStoreException {
				((SetOfCardsDAOTest) tc).testLoadSetMetadataV2(); 
			} 
		}));
		
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
