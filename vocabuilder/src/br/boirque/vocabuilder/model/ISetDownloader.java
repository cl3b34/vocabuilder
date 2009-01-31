package br.boirque.vocabuilder.model;

import java.util.Vector;

/**
 * Interface for classes that download lists of flashcards from the web
 * 
 * @author cleber.goncalves
 * 
 */
public interface ISetDownloader {

	/**
	 * Returns a list of the existing set categories
	 * 
	 * @since 1.8
	 * @author cleber.goncalves
	 * @return Vector containing the names of all available categories
	 */
	Vector listCategories();

	/**
	 * Returns a list of the sets available for download within a certain
	 * category
	 * 
	 * @since 1.8
	 * @author cleber.goncalves
	 * @return Vector containing the names of all sets available for download
	 * @param category
	 *            The name of the category from which to list the availabe sets
	 */
	Vector listDownloadableSets(String category);

	/**
	 * @param setName
	 *            name or id of the set to download
	 * @return a SetOfCards containing cards from the downloaded set
	 * @author cleber.goncalves
	 * @since 1.8
	 */
	SetOfCards downloadSet(String setName);

}
