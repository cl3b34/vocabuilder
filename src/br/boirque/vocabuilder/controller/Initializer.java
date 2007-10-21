package br.boirque.vocabuilder.controller;

import java.io.IOException;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import br.boirque.vocabuilder.model.SetOfCards;
import br.boirque.vocabuilder.model.SetOfCardsDAO;
import br.boirque.vocabuilder.model.SetOfCardsLoader;

/**
 * Takes care of all the initialization tasks
 * @author cleber.goncalves
 *
 */
public class Initializer {
	
	public void initializeApp() throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException{
		//Load the set from any available media (defined in the loader)
		SetOfCardsLoader socl = new SetOfCardsLoader();
		SetOfCards soc = socl.loadSet();
		
		//save the loaded set into a local recordStore
		SetOfCardsDAO socdao = new SetOfCardsDAO();
		socdao.saveState(soc);			
	}
}
