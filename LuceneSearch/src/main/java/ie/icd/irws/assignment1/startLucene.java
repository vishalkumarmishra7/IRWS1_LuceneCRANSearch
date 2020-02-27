package ie.icd.irws.assignment1;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

public class startLucene {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("<<< Program Running time is approx: 102.22100 seconds >>>");
		// Set required Data
        int SETUP_LIST_SIZE = 12;
		String cranDir = "database/cran.tar/";
		String cranFileData = "cran.all.1400";
		
		String indexDir = "indexDir/";
		Path indexFilePath = Paths.get(indexDir+"/cran.index");
		String hpp = "1000";
		String queryFile = "cran.qry";
		String resultDir = "searchResult/";
		
		String tempAnalyzer = null;
		String tempSimilarity = null;
		
		System.out.println("CRAN Directory set as:: " + cranDir+"\n");
		//Parse CRAN 1400 files and store in List of Map
		FileUtils fileData1 = new FileUtils();
		List<Map<String, String>> parsedCranFileList = fileData1.databaseParser(cranDir,cranFileData);
		
		//Parse CRAN queries and store in List of Map
		FileUtils fileData2 = new FileUtils();
		List<Map<String, String>> queryList = fileData2.parseQuery(cranDir, queryFile);
			
		//Setup Analyzer and Search Similarity to be used
	    String[ ][ ] setupList = new String[SETUP_LIST_SIZE][2];      
	    
	    setupList[0][0] = "English";         setupList[0][1] = "BM25";
	    setupList[1][0] = "English";         setupList[1][1] = "LMDirichlet";
	    setupList[2][0] = "English";         setupList[2][1] = "TFIDF";
	    
	    setupList[3][0] = "Standard";         setupList[3][1] = "BM25";
	    setupList[4][0] = "Standard";         setupList[4][1] = "LMDirichlet";
	    setupList[5][0] = "Standard";         setupList[5][1] = "TFIDF";
	    
	    setupList[6][0] = "Simple";         setupList[6][1] = "BM25";
	    setupList[7][0] = "Simple";         setupList[7][1] = "LMDirichlet";
	    setupList[8][0] = "Simple";         setupList[8][1] = "TFIDF";
	    
	    setupList[9][0] = "WhiteSpaceAnalyzer";         setupList[9][1] = "BM25";
	    setupList[10][0] = "WhiteSpaceAnalyzer";         setupList[10][1] = "LMDirichlet";
	    setupList[11][0] = "WhiteSpaceAnalyzer";         setupList[11][1] = "TFIDF";
	    
	    //Perform Indexing and Searching as per Setup
        for (int i = 0; i < SETUP_LIST_SIZE ; i++){
        	tempAnalyzer = setupList[i][0];
        	tempSimilarity = setupList[i][1];
            System.out.println(i+1+". Using Analyzer=["+tempAnalyzer+"] Similarity=["+tempSimilarity+"]");

    		////Delete Index
    		System.out.println(i+1+".1 Delete previous index files(if exist).");
    		fileData1.deleteSearchDir(new File(indexDir));
    		
    		////Index File
    		System.out.print(i+1+".2 Indexing database files...");
    		IndexFile idxFiles = new IndexFile();
    		idxFiles.createCranIndex(indexFilePath, parsedCranFileList, tempAnalyzer, tempSimilarity);
    		System.out.println("Indexes saved in:: " + indexFilePath);
            
    		////Search Query
    		System.out.print(i+1+".3 Searching CRAN 225-Queries...");
    		Path resultFilePath = Paths.get(resultDir+"result_"+tempAnalyzer+"_"+tempSimilarity+".txt");
    		SearchFile searchFiles = new SearchFile();
    		try {
    			searchFiles.searchQueries(indexFilePath, queryList, tempAnalyzer, tempSimilarity, hpp, resultFilePath);
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
			System.out.println("Search results written to:: "+resultFilePath+ "  (use in TREC Eval)"+"\n");

    		
        }  
        System.out.println("\nAll Done!!\n");  

        long end = System.currentTimeMillis();

        NumberFormat secFormatter = new DecimalFormat("#0.00000");
        System.out.println("Program Running time is " + secFormatter.format((end - start) / 1000d) + " seconds");

	}

}
