package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;

import javafx.scene.control.TextArea;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	Query query;
	QueryParser queryParser;
	
	public void printDocuments(TextArea ta) throws IOException {
		String previous = "";
		Path indexPath = Paths.get(LuceneConstants.INDEX_DIR);
		Directory directory = FSDirectory.open(indexPath);
		IndexReader indexReader = DirectoryReader.open(directory);
			Bits liveDocs = MultiBits.getLiveDocs(indexReader);
			for (int i = 0; i < indexReader.maxDoc(); i++) {
				if (liveDocs != null && !liveDocs.get(i))
					continue;
				
				Document doc = indexReader.document(i);
				ta.setText(previous + doc.get(LuceneConstants.FILE_NAME)+"\n");
				previous = ta.getText();
			}
	}
	
	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	public TopDocs search(int choice, String input, String fieldType) throws IOException, ParseException {
		String searchQuery = input;
		
		switch(choice) {
			case 1: //vector
				queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
				query = queryParser.parse(searchQuery);
				return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			case 2: //boolean
				BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
				List<String> arraySearchQueryBoolean = stringToArrayList(searchQuery);
				Query query;
				
				boolean prevAND = false, prevOR = false, prevNOT = false;
				for(int i=0; i<arraySearchQueryBoolean.size(); i++) {
					if(arraySearchQueryBoolean.get(i).contains("&&"))      { prevAND = true; }				
					else if(arraySearchQueryBoolean.get(i).contains("||")) { prevOR = true; }
					else if(arraySearchQueryBoolean.get(i).contains("^"))  { prevNOT= true; }
					else {
						if(prevAND) {
							query = new TermQuery(new Term(fieldType, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.MUST);
							prevAND = false;
						}
						else if(prevOR) {
							query = new TermQuery(new Term(fieldType, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.SHOULD);
							prevOR = false;
						}
						else if(prevNOT) {
							query = new TermQuery(new Term(fieldType, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.MUST_NOT);
							prevNOT = false;
						}
						else {
							query = new TermQuery(new Term(fieldType, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.MUST);
							if(i!=0){
								booleanQuery.add(query, BooleanClause.Occur.MUST);
							}
							else if(i==0 && arraySearchQueryBoolean.get(i+1).contains("||")) {
								booleanQuery.add(query, BooleanClause.Occur.SHOULD);
							}
						}
					}
				}
				searchQuery = logicReplacer(searchQuery);
				return indexSearcher.search(booleanQuery.build(), LuceneConstants.MAX_SEARCH);
			case 3: //phrase
				PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
				List<String> arraySearchQueryPhrase = stringToArrayList(searchQuery);
				for(String s:arraySearchQueryPhrase) {
					phraseQuery.add(new Term(fieldType,s));
				}
				return indexSearcher.search(phraseQuery.build(), LuceneConstants.MAX_SEARCH);
			case 4:	//fields
				queryParser = new QueryParser(fieldType, new StandardAnalyzer()); 
				query = queryParser.parse(searchQuery);
				return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			case 5: //top-k
				TopDocs hits = getHits(searchQuery);
				if (hits.totalHits.toString().contains("1")){
					queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
					// Idea: "searchQuery" variable should become LuceneConstants.CONTENTS of the file we searched
					// (file name is inside searchQuery variable)

					// Take LuceneConstants.CONTENTS of [filename].txt from Index
					String documentContents = documentContext(searchQuery);
					query = queryParser.parse(documentContents);
					return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
				}
		}
		return null;
	}
	
	public TopDocs getHits(String filename) throws IOException {
		PhraseQuery.Builder fieldQuery = new PhraseQuery.Builder();
		String path = LuceneConstants.DATA_DIR+filename;
		fieldQuery.add(new Term(LuceneConstants.FILE_PATH,path));
		TopDocs hits = indexSearcher.search(fieldQuery.build(), LuceneConstants.MAX_SEARCH);
		return hits;
	}
	
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	
	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
	
	public List<String> stringToArrayList(String s) {
		String str[] = s.split(" ");
		List<String> al = new ArrayList<String>();
		al = Arrays.asList(str);
		
		return al;
	}
	
	public String documentContext(String searchQuery) throws IOException {	
		Set<String> h = new HashSet<>() {{ add(LuceneConstants.CONTENTS); }};
		TopDocs hits=getHits(searchQuery);
		Document doc = getDocument(hits.scoreDocs[0]);
		searchQuery = searchQuery.replace("Article","");
		return doc.get(LuceneConstants.CONTENTS);
	}
	
	static long numberOfFiles() throws IOException {
		try (Stream<Path> files = Files.list(Paths.get(LuceneConstants.DATA_DIR))) {
		    return files.count();
		}
	}
	
	public boolean checkExistanceOfFile(String searchQuery) throws IOException { 
		PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
		List<String> arraySearchQueryPhrase = stringToArrayList(searchQuery+".txt");
		for (String s : arraySearchQueryPhrase) {
			phraseQuery.add(new Term(LuceneConstants.FILE_NAME, s));
		}
		TopDocs hits = indexSearcher.search(phraseQuery.build(), LuceneConstants.MAX_SEARCH);
		return !hits.totalHits.toString().contains("0");
	}
	
	public String logicReplacer(String s) {
		if(s.contains("&&")) { s.replace("&&", "AND"); }
		if(s.contains("||")) { s.replace("||", "OR"); }
		if(s.contains("^"))  { s.replace("^" , "NOT"); }
		return s;
	}
	
}