package ie.icd.irws.assignment1;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchFile {

	private int HITS_PER_PAGE = 1000;
	
	/**
	 * @param indexFile
	 * @param queryList
	 * @param tempAnalyzer
	 * @param tempSimilarity
	 * @param hpp
	 * @param resultFilePath
	 * @return
	 * @throws ParseException
	 */
	public Map<String, List<String>> searchQueries(Path indexFile, List<Map<String, String>> queryList, String tempAnalyzer, String tempSimilarity, String hpp, Path resultFilePath) throws ParseException {
		
		Map<String, List<String>> resultDictionary = new HashMap<String, List<String>>();
		try {
			try {
				HITS_PER_PAGE = Integer.parseInt(hpp);
			}
			catch (NumberFormatException e) {}
			
			//System.out.print("hits per page:: " + hpp +"..." );

			// Create analyzer
			Analyzer idxAnalyzer = null;	
			if (tempAnalyzer.equals("WhiteSpaceAnalyzer")) 
				idxAnalyzer = new WhitespaceAnalyzer();
			else if (tempAnalyzer.equals("Simple")) 
				idxAnalyzer = new SimpleAnalyzer();
			else if (tempAnalyzer.equals("Keyword")) 
				idxAnalyzer = new KeywordAnalyzer();
			else if (tempAnalyzer.equals("Standard")) 
				idxAnalyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
			else if (tempAnalyzer.equals("English"))				
				idxAnalyzer = new EnglishAnalyzer();

			// Read index from disk
			Directory directory = FSDirectory.open(indexFile);
			DirectoryReader idxReader = DirectoryReader.open(directory);
			
			// Create an index searcher from Similarity
			IndexSearcher idxSearcher = new IndexSearcher(idxReader);
			if (tempSimilarity.equals("LMDirichlet")) 
				idxSearcher.setSimilarity(new LMDirichletSimilarity());
			else if (tempSimilarity.equals("TFIDF")) 
				idxSearcher.setSimilarity(new ClassicSimilarity());
			else if (tempSimilarity.equals("BM25")) 				
				idxSearcher.setSimilarity(new BM25Similarity());
			
			List<String> searchResultData = new ArrayList<String>();

			// Read all queries and retrieve relevant documents
			//System.out.println("...Searching queries using CRAN index file"); 
			for (int i = 0; i < queryList.size(); i++) {				
				Map<String, String> curQueryMap = queryList.get(i);
				MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
						new String[] {"Title", "Locations", "Authors", "Abstract"}, idxAnalyzer);
				Query curQuery = queryParser.parse(curQueryMap.get("Query"));
				
				// Search
				TopDocs topDocs = idxSearcher.search(curQuery, HITS_PER_PAGE);
				ScoreDoc[] topHits = topDocs.scoreDocs;
				
				//System.out.println(i+1 +"...Search top hits:"+topHits.length);
				if(i%5==0) System.out.print(".");
				// Show search results
				List<String> curResultList = new ArrayList<String>();
		        //System.out.println("Found " + hits.length + " hits.");
				
		        for(int j = 0; j < topHits.length; j++) {		        	
		        	int docId = topHits[j].doc;
		            Document doc = idxSearcher.doc(docId);		         
		            curResultList.add(doc.get("ID"));
		            
		            searchResultData.add(curQueryMap.get("QueryNo") + " 0 " + doc.get("ID") + " 0 " + topHits[j].score + " STANDARD");
		        }
		        resultDictionary.put(Integer.toString(i + 1), curResultList);
			}
			
			// Create directory if it does not exist
			File resultDir = new File("searchResult");
			if (!resultDir.exists()) resultDir.mkdir();
			
			Files.write(resultFilePath, searchResultData, Charset.forName("UTF-8"));
			System.out.print("\nSearching done!...");

		}
		catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		return resultDictionary;
	}
}
