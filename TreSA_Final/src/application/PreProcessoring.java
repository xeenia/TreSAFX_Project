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
import org.apache.lucene.document.TextField;


public class PreProcessoring{
	
	protected Document preProcessing(Document document, File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder bodyBuilder = new StringBuilder(), entireText = new StringBuilder();
		String line;
		String ls = System.getProperty("line.separator");
		int i = 0;
		while ((line = reader.readLine()) != null) {
			if(i>=3) { // taking text from <BODY></BODY>
				bodyBuilder.append(line);
				bodyBuilder.append(ls);
			}
			else {
				if(i == 0) {  // PLACES
					line = removingHTML(line,1);
					System.out.println("Places: "+line);
					document.add(new Field(LuceneConstants.PLACES, line, StringField.TYPE_STORED)); 
				} else if( i == 1) { // PEOPLE
					line = removingHTML(line,2);
					System.out.println("People: "+line);
					document.add(new Field(LuceneConstants.PEOPLE, line, StringField.TYPE_STORED)); 
				} else if( i == 2) { // TITLE
					line = removingHTML(line,3); 
					line = removingPunctuation(line);
					System.out.println("Title: "+line);
					document.add(new Field(LuceneConstants.TITLE, line, StringField.TYPE_STORED));
				}
				entireText.append(line + " ");
			}
			i++;
		}

		// Delete the last new line separator
		bodyBuilder.deleteCharAt(bodyBuilder.length() - 1);
		entireText.deleteCharAt(entireText.length() - 1);
		reader.close();
		
		// BODY
		line = removingHTML(bodyBuilder.toString(),4);
		line = removingPunctuation(line); 
		line = removingStopwords(line);
		entireText.append(line);
		System.out.println("Body: "+line);
		document.add(new Field(LuceneConstants.BODY, line, StringField.TYPE_STORED));

		// For general searches
		document.add(new Field(LuceneConstants.CONTENTS, entireText, TextField.TYPE_STORED));

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
	
	private String removingHTML(String line, int choise) {
		if(choise == 1) {
			line=line.replace("<PLACES>","");
			line=line.replace("</PLACES>","");
		}
		
		if(choise == 2) {
			line=line.replace("<PEOPLE>","");
			line=line.replace("</PEOPLE>","");
		}
		
		if(choise == 3) {
			line=line.replace("<TITLE>","");
			line=line.replace("</TITLE>","");
		}
		
		if(choise == 4) { 
			line=line.replace("<BODY>","");
			line=line.replace("</BODY>",""); 
		}
		
		return line.toLowerCase();
	}
	
	
}

