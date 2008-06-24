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
							if(i % 400 > 0){
								if(limitChar.size() !=0 && !limitChar.equalsIgnoreCase(currentChar) ||i == lines.size()-1){
									//	println "trocou " +limitChar + " " + currentChar
									println "ending list ${filenameBase} at index ${i}"
									
									String filename = filenameBase+ "_"+ firstCharOfList + limitChar + ".txt"
									title = "#setName = " + filename
									meta = title + "\n" + sideOne + "\n" + sideTwo + "\n"
								
									String savingPath = file.getParent() + File.separator + filename
									String contents = meta + splittedList
									
									//save
									saveFile(contents, savingPath)
									
									//append this file to the properties
									def propertiesFile = file.getParent() + File.separator + "properties.txt"
									appendFile("defaultset="+filename + "\n" , propertiesFile)
									
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
								println "limit reached, look for next char after: " + limitChar
							}
						}
					}
					//saves the last file
					
					
				}
				
			}
		}
	}
	
	void saveFile(String contents, String savingPath){
		println "Saving" + savingPath
		println()
		File fileToSave = new File(savingPath)
		fileToSave.write(contents)		
	}
	
	
	void appendFile(String contents, String savingPath){
		println "Appending" + savingPath
		println()
		File fileToSave = new File(savingPath)
		fileToSave.append(contents)		
	}
	
	//remove duplicated words
	String removeDuplicates(String listToClean){
		println "Removing duplicates "
		println "before: " + listToClean
		
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
				println "found repeated: " + word + " " +counter + " times"
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
		println "after: " + cleanedList
		println()
		return cleanedList
	}
	
}