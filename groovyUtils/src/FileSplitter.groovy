/**
 * My very first Groovy script.
 * It is meant to divide very long lists of words
 * into smaller ones (around 400 words each).
 * The amount of cards varies in each set in order
 * to have all words starting with a particular letter
 * in the same set.
 * 
 * As you can see bellow, this is not an example
 * of good programming practices or 'groovyness' 
 * but it got the job done. :)
 * 
 */

class FileSplitter{
	
	static void main(args){
		FileSplitter splitter = new FileSplitter()
		splitter.splitByLetter()
		
	}
	
	void splitByLetter(){
		def propertiesFileNames = ""
		new File(".").eachFileRecurse{file ->
			if(file.isFile()){
				if(file.name.startsWith("longlist") && file.name.endsWith(".txt")){
					List lines = file.readLines()
					List finalLists = new ArrayList()
					def meta = "";
					def filenameBase = "";
					def title = ""
					def sideOne =""
					def sideTwo =""
					def firstCharOfList = ""
					def limitChar = ""
					def currentChar = ""
					def splittedList = ""
					def propertiesTxt = ""
					lines.eachWithIndex{line,i ->
							if(line.startsWith("#")){
								if(i==0){
									//parse the file name
									def underscore = line.indexOf("_")
									def lang = line.substring(underscore,line.size())
									filenameBase = "words" + lang
								}
								if(i==1){
									sideOne = line
								}
								if(i==2){
									sideTwo = line
								}				
							}else{
								line = removeDuplicates(line)
								//the first letter for the filename ending
								if(i==3){firstCharOfList = line.getAt(0)} 
								currentChar = line.getAt(0)
								//we want around 400 words per file
								if(i % 50 > 0){
									//if we are close to the end of the list, just append it to the previous
									if((lines.size() - i) < 20 && limitChar != "") {
										println "lines size:" + lines.size() + "index: " + i
										limitChar = ""
									}
									//save 
									if(limitChar.size() !=0 && !limitChar.equalsIgnoreCase(currentChar) ||i == lines.size()-1){
										if(i == lines.size()-1){
											//save the last line of the file...
											splittedList = splittedList + "\n" + line
										}
										//	println "trocou " +limitChar + " " + currentChar
										println "ending list ${filenameBase} at index ${i}"
										String setName = filenameBase+ "_"+ firstCharOfList + limitChar
										setName = setName.toLowerCase()
										
										title = "#setName = " + setName
										meta = title + "\n" + sideOne + "\n" + sideTwo + "\n"
									
										String filename =  setName + ".txt"										
										String savingPath = file.getParent() + File.separator + filename
										String contents = meta + splittedList
										
										//save
										saveFile(contents, savingPath)
										
										//append this file to the properties
										propertiesTxt += "defaultset="+ filename + "\n"
										//zeroe the vars
										firstCharOfList = currentChar
										limitChar = ""
										//save the newly loaded line to the next file
										splittedList = line;
	
									}else{
										if(splittedList.size()==0){
											splittedList = line
										}else{							
											splittedList = splittedList + "\n" + line
										}
									}
									
								}else{
			//						400 cards, when the char the word starts changes, finish and save
									limitChar = line.getAt(0)
									splittedList = splittedList + "\n" + line
//									println "limit reached, look for next char after: " + limitChar
								}
							}
					}
					println "Finished file " + file.name
					def propertiesFile = file.getParent() + File.separator + "properties.txt"
					appendFile(propertiesTxt , propertiesFile)
				}
			}
		}
	}
	
	void saveFile(String contents, String savingPath){
		println "Saving" + savingPath
//		println()
		File fileToSave = new File(savingPath)
		fileToSave.write(contents)		
	}
	
	
	void appendFile(String contents, String savingPath){
		println "Saving properties" + savingPath
		println()
		File fileToSave = new File(savingPath)
		fileToSave.write(contents)		
	}
	
	//remove duplicated words
	String removeDuplicates(String listToClean){
//		println "Removing duplicates "
//		println "before: " + listToClean
		
		List line = listToClean.tokenize("=")
		String wordLeft = line.getAt(0)
		String wordsRight = ""
		List translations = line. getAt(1).tokenize(",")
		List clean = translations
		for(int i=0; i< translations.size(); i++){
			def counter = 0;
			def word = translations.getAt(i)
			for(int z=0; z< translations.size(); z++){
				if(word.equalsIgnoreCase(translations.getAt(z))){
					counter++;
				}
			}
			if(counter > 1){
//				println "found repeated: " + word + " " +counter + " times"
				clean.remove(word)
			}
		}
		for(int i=0; i<clean.size(); i++){
			if(i==clean.size()-1){
			wordsRight = wordsRight + clean.getAt(i)
			}else{
				wordsRight = wordsRight + clean.getAt(i) + ","
			}
		}
		def cleanedList = wordLeft + "=" + wordsRight
//		println "after: " + cleanedList
//		println()
		return cleanedList
	}
	
}