package application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
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
	
	public TopDocs search(int choice, Scanner input) throws IOException, ParseException {
		Scripts.Script(13);
		if(choice == 2 ) { Scripts.Script(19); }
		input.nextLine();
		String searchQuery = input.nextLine();
		
		switch(choice) {
			case 1:
				QueryParser queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
				query = queryParser.parse(searchQuery);
				
				System.out.println("query: "+ query.toString());
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
							query = new TermQuery(new Term(LuceneConstants.CONTENTS, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.MUST);
							prevAND = false;
						}
						else if(prevOR) {
							query = new TermQuery(new Term(LuceneConstants.CONTENTS, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.SHOULD);
							prevOR = false;
						}
						else if(prevNOT) {
							query = new TermQuery(new Term(LuceneConstants.CONTENTS, arraySearchQueryBoolean.get(i)));
							booleanQuery.add(query, BooleanClause.Occur.MUST_NOT);
							prevNOT = false;
						}
						else {
							query = new TermQuery(new Term(LuceneConstants.CONTENTS, arraySearchQueryBoolean.get(i)));
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
					phraseQuery.add(new Term(LuceneConstants.CONTENTS,s));
				}
				
				return indexSearcher.search(phraseQuery.build(), LuceneConstants.MAX_SEARCH);
		}
		
		return null;
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