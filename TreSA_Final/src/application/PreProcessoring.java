package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

public class PreProcessoring{
	
	protected Document preProcessing(Document document, File file) throws IOException {
		// https://www.journaldev.com/875/java-read-file-to-string
		// Reading file and taking content of fields
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		Field contentField = null;
		
		int i = 0;
		while ((line = reader.readLine()) != null) {
			if(i>=3) { // taking text from <BODY></BODY>
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			else {
				if(i == 0) {  // PLACES
					line = removingHTML(line,1); 
					contentField = new Field(LuceneConstants.PLACES, line, StringField.TYPE_STORED); 
				} else if( i == 1) { // PEOPLE
					line = removingHTML(line,2); 
					contentField = new Field(LuceneConstants.PEOPLE, line, StringField.TYPE_STORED); 
				} else if( i == 2) { // TITLE
					line = removingHTML(line,3); 
					line = removingPunctuation(line); 
					contentField = new Field(LuceneConstants.TITLE, line, StringField.TYPE_STORED); 
				}
				document.add(contentField);
			}
			i++;
		}

		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();
		
		// BODY
		line = removingHTML(stringBuilder.toString(),4); 
		line = removingPunctuation(line); 
		line = removingStopwords(line);
		contentField = new Field(LuceneConstants.BODY, line, StringField.TYPE_STORED);
		
		document.add(contentField);
		
		return document;
	}
	
	private String removingStopwords(String preProcessedContent) {
	    Pattern p = Pattern.compile("\\b(I|this|its|i|ii|iii|vi|iv|x|v|y.....)\\b\\s?");
	    Matcher m = p.matcher(preProcessedContent);
	    preProcessedContent = m.replaceAll("");
	    
		return preProcessedContent;
	}
	
	private String removingPunctuation(String preProcessedContent) {
		preProcessedContent = preProcessedContent.replaceAll("\\p{Punct}", "");
		preProcessedContent = preProcessedContent.replaceAll("", "");
		preProcessedContent = preProcessedContent.replaceAll("reuter", "");
		
		return preProcessedContent;
	}
	
	private String removingHTML(String preProcessedContent, int choise) {
		if(choise == 1) {
			preProcessedContent=preProcessedContent.replace("<PLACES>","");
			preProcessedContent=preProcessedContent.replace("</PLACES>","");
		}
		
		if(choise == 2) {
			preProcessedContent=preProcessedContent.replace("<PEOPLE>","");
			preProcessedContent=preProcessedContent.replace("</PEOPLE>","");
		}
		
		if(choise == 3) {
			preProcessedContent=preProcessedContent.replace("<TITLE>","");
			preProcessedContent=preProcessedContent.replace("</TITLE>","");
		}
		
		if(choise == 4) { 
			preProcessedContent=preProcessedContent.replace("<BODY>","");
			preProcessedContent=preProcessedContent.replace("</BODY>",""); 
		}
		
		return preProcessedContent.toLowerCase();
	}
	
	
}

