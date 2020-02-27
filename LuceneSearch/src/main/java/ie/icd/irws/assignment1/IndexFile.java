package ie.icd.irws.assignment1;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class IndexFile {


	/**
	 * @param indexFilePath
	 * @param parsedCranFileList
	 * @param tempAnalyzer
	 * @param searchSimilarity
	 */
	public void createCranIndex(Path indexFilePath, List<Map<String, String>> parsedCranFileList, String tempAnalyzer, String searchSimilarity) {
		
		try {
			
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
			
			// Save index on disk
			Directory idxDirectory = FSDirectory.open(indexFilePath);
			
			// Create index writer
			//// Create Index Writer Config
			IndexWriterConfig idxWriterConfig = new IndexWriterConfig(idxAnalyzer);
			idxWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

			//// Set Similarity type
			if (searchSimilarity.equals("LMDirichlet")) 
				idxWriterConfig.setSimilarity(new LMDirichletSimilarity());
			else if (searchSimilarity.equals("TFIDF")) 
				idxWriterConfig.setSimilarity(new ClassicSimilarity());
			else if (searchSimilarity.equals("BM25")) 				
				idxWriterConfig.setSimilarity(new BM25Similarity());

			IndexWriter idxWriter = new IndexWriter(idxDirectory, idxWriterConfig);
			
			// Add files to index
			//System.out.println("...Adding Files to index"); 
			for (int i = 0; i < parsedCranFileList.size(); i++)
				addCranFiles(idxWriter, parsedCranFileList.get(i));
			
			System.out.print("Indexing done!!...");
			idxWriter.close();
			idxDirectory.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	

	/**
	 * @param idxWriter
	 * @param dictionary
	 * @throws IOException
	 */
	private void addCranFiles(IndexWriter idxWriter, Map<String, String> dictionary) throws IOException {
		
		Document doc = new Document();
		doc.add(new StringField("ID", dictionary.get("ID"), Field.Store.YES));
		doc.add(new TextField("Title", dictionary.get("Title"), Field.Store.YES));
		doc.add(new TextField("Locations", dictionary.get("Locations"), Field.Store.YES));
		doc.add(new TextField("Authors", dictionary.get("Authors"), Field.Store.YES));
		doc.add(new TextField("Abstract", dictionary.get("Abstract"), Field.Store.YES));
		idxWriter.addDocument(doc);
	}

}
