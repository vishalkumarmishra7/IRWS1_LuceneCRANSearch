package ie.icd.irws.assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Hello world!
 *
 */
public class FileUtils 
{
		

	/**
	 * @param fileDir
	 * @param dbName
	 * @return
	 */
	public List<Map<String, String>> databaseParser(String fileDir, String dbName) {
		
		List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();		
		try {			
			System.out.print("Parsing CRAN file database:: "+dbName+" ...");
			
			// Creating File operation related objects
			File file = new File(fileDir + dbName);
			FileReader dbReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(dbReader);
			
			// Create Dictionary for storing parsed File data
			Map<String, String> dictionary = new HashMap<String, String>();
			
			String lineId;
			String next = ".I";
			int lineNum = 0;
			
			String id = "";
			String title = "";
			String authors = "";
			String locations = "";
			String abstractInfo = "";
			
			// Loop through each readLine
			while ((lineId = bufferedReader.readLine()) != null) {
				lineNum++;
				String[] words = lineId.split("\\s+");
				switch (words[0]) {	
				// Check the current readLine's tag, save the next tag and perform operations
				case ".I":
					if (next != ".I") showParsingError(lineNum);
					if (lineNum > 1) {
						dictionary.put("ID", id);
						dictionary.put("Abstract", abstractInfo);
						fileList.add(dictionary);
						dictionary = new HashMap<String, String>();
					}
					id = words[1];
					abstractInfo = "";
					next = ".T";
					break;
				case ".T":
					if (next != ".T") showParsingError(lineNum);
					next = ".A";
					break;
				case ".A":
					if (next != ".A") {
						if (next == ".I") break;
						showParsingError(lineNum);
					}
					dictionary.put("Title", title);
					title = "";
					next = ".B";
					break;
					
				case ".B":
					if (next != ".B") {
						
						if (next == ".I") break;
						showParsingError(lineNum);
					}
					dictionary.put("Authors", authors);
					authors = "";
					next = ".W";
					break;
					
				case ".W":
					if (next != ".W") {
						if (next == ".I") break;
						showParsingError(lineNum);
					}
					dictionary.put("Locations", locations);
					locations = "";
					next = ".I";
					break;
					
				default:
					switch(next) {
					// Check the current tag, and perform operations
					case ".A": title += lineId + " "; break;						
					case ".B": authors += lineId + " "; break;						
					case ".W": locations += lineId + " "; break;
					case ".I": abstractInfo += lineId + " "; break;				
					default: showParsingError(lineNum); 
					}
				}
			}

			// Add the last entry
			dictionary.put("ID", id);
			dictionary.put("Abstract", abstractInfo);
			fileList.add(dictionary);
			
			dbReader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return fileList;
	}
	
	/**
	 * @param lineNum
	 */
	private void showParsingError(int lineNum) {
		
		System.out.println("Parsing unsuccessful from readLine " + Integer.toString(lineNum));
	}
	
	/**
	 * @param file
	 */
	public void deleteSearchDir(File file) {
		
		File[] fileData = file.listFiles();
		if (fileData != null) {
			for (File f: fileData) {
				deleteSearchDir(f);
			}
		}
		//System.out.println("...Index deleted at location: " + file);
		file.delete();
	}
	
	/**
	 * @param cranDir
	 * @param queryFile
	 * @return
	 */
	public List<Map<String, String>> parseQuery(String cranDir, String queryFile) {
		
		List<Map<String, String>> queryList = new ArrayList<Map<String, String>>();
		
		try {
		
			System.out.println("Parsing CRAN 225-Queries file:: "+queryFile+"\n");
			
			// Creating File operation related objects
			File qFile = new File(cranDir + queryFile);
			FileReader fReader = new FileReader(qFile);
			BufferedReader bufferedReader = new BufferedReader(fReader);
			
			// Create Dictionary for storing parsed File data
			Map<String, String> qDictionary = new HashMap<String, String>();
			
			
			
			// parse each readLine
			int lineNum = 0;
			int qNumber = 0;
			String readLine;
			String next = ".I";
			String id = "";
			String query = "";
			
			while ((readLine = bufferedReader.readLine()) != null) {
				lineNum++;
				readLine = readLine.replace("?", "");
				String[] words = readLine.split("\\s+");
				
				switch (words[0]) {
					
				// read current line's tag, save next tag and do task
				case ".I":
					if (next != ".I") showParsingError(lineNum);
					if (lineNum > 1) {						
						qDictionary.put("ID", id);
						qDictionary.put("QueryNo", Integer.toString(qNumber));
						qDictionary.put("Query", query);
						queryList.add(qDictionary);
						qDictionary = new HashMap<String, String>();
					}
					id = words[1];
					qNumber++;
					query = "";
					next = ".W";
					break;
				
				case ".W":
					if (next != ".W") showParsingError(lineNum);
					next = ".I";
					break;
					
				default:
					switch(next) {
					// read current tag, and do tasks
					case ".I": query += readLine + " "; break;						
					default: showParsingError(lineNum);
					}
				}
			}
			
			// Also append last read entry
			qDictionary.put("ID", id);
			qDictionary.put("QueryNo", Integer.toString(qNumber));
			qDictionary.put("Query", query);
			queryList.add(qDictionary);
			
			fReader.close();
		}
		catch(IOException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		return queryList;
	}

}
