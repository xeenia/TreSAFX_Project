package application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	Query query;
	
	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	public TopDocs search(int choice, String input, String fieldType) throws IOException, ParseException {
		String searchQuery = input;
		
		switch(choice) {
			case 1:
				QueryParser queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
				query = queryParser.parse(searchQuery);
				
				//System.out.println("query: "+ query.toString());
				return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
			case 2:
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
			case 3:
				PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
				List<String> arraySearchQueryPhrase = stringToArrayList(searchQuery);
				for(String s:arraySearchQueryPhrase) {
					phraseQuery.add(new Term(fieldType,s));
				}
				return indexSearcher.search(phraseQuery.build(), LuceneConstants.MAX_SEARCH);
			case 4:
				PhraseQuery.Builder fieldquery = new PhraseQuery.Builder();
				List<String> arraySearchQueryPhrase1 = stringToArrayList(input);
				for(String s:arraySearchQueryPhrase1) {
					fieldquery.add(new Term(fieldType,s));
				}
				return indexSearcher.search(fieldquery.build(), LuceneConstants.MAX_SEARCH);
		}
		
		return null;
	}
	public String getHits(String filename) throws IOException {
		PhraseQuery.Builder fieldQuery = new PhraseQuery.Builder();
		String path = LuceneConstants.DATA_DIR+filename;
		fieldQuery.add(new Term(LuceneConstants.FILE_PATH,path));
		TopDocs hits = indexSearcher.search(fieldQuery.build(), LuceneConstants.MAX_SEARCH);
		
		return hits.scoreDocs.toString();
	}
	public TopDocs searchByField(String fieldType, String query) throws IOException {
		PhraseQuery.Builder fieldquery = new PhraseQuery.Builder();
		List<String> arraySearchQueryPhrase = stringToArrayList(query);
		for(String s:arraySearchQueryPhrase) {
			fieldquery.add(new Term(fieldType,s));
		}
		return indexSearcher.search(fieldquery.build(), LuceneConstants.MAX_SEARCH);
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
	
	public String logicReplacer(String s) {
		if(s.contains("&&")) { s.replace("&&", "AND"); }
		if(s.contains("||")) { s.replace("||", "OR"); }
		if(s.contains("^"))  { s.replace("^" , "NOT"); }
		return s;
	}
	
}