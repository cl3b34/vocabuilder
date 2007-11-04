package br.boirque.vocabuilder.model;

import java.util.Vector;

/**
 * Loads a SetOfCards from external
 * medium (Http, Bluetooth, IR, flashmemory)
 * @author cleber.goncalves
 *
 */
public class SetOfCardsLoader {

	//this method tries to load a set
	//from whatever medium it can
	public SetOfCards loadSet() {
		SetOfCards setToReturn;
		setToReturn = mockLoader();
		return setToReturn;
	}
	
	/*
	 * Only generates a fake set of cards and returns it
	 */
	private SetOfCards mockLoader() {
		//just return a fake SetOfCards		
		FlashCard mockCard1 = new FlashCard("tuli","fin","fire","eng",false,"burns");
		FlashCard mockCard2 = new FlashCard("veta","fin","water","eng",false,"wets");
		FlashCard mockCard3 = new FlashCard("leipa","fin","bread","eng",false,"is eatable");
		FlashCard mockCard4 = new FlashCard("tietokone","fin","computer","eng",false,"knowledge machine");

		
		Vector cards = new Vector();
		cards.addElement(mockCard1);
		cards.addElement(mockCard2);
		cards.addElement(mockCard3);
		cards.addElement(mockCard4);

		SetOfCards mockSet = new SetOfCards("mockSet",false,2000L,cards);
		return mockSet;
	}
	
	
}
