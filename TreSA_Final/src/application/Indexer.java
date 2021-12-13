package application;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javafx.scene.control.TextArea;

public class Indexer extends PreProcessoring{
	private IndexWriter writer;
	
	public Indexer(String indexDirectoryPath) throws IOException  {
		// This directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		if(!Files.exists(indexPath)) {
			 Files.createDirectory(indexPath);
		}
		Directory indexDirectory = FSDirectory.open(indexPath);
		// Create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(indexDirectory, config);
	}

	private Document getDocument(File file) throws IOException {
		Document document = new Document();
	
		// Index file name
		Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
		
		// Index file path
		Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);
		
		// Index content
		// Processing contents of article and addition to document
		document = preProcessing(document, file);
		
		document.add(fileNameField);
		document.add(filePathField);
		
		return document;
	}
	
	private void indexFile(File file,TextArea indexingInfo) throws IOException {
		String previousIndexingInfo = indexingInfo.getText();
		indexingInfo.setText(previousIndexingInfo+"\n"+ "Indexing " + file.getName());
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	
	public int createIndex(String dataDirPath, FileFilter filter, ArrayList<String> articles,TextArea indexingInfo) throws IOException {

		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) ){			

					for(String str:articles) {
						if(file.getName().contains(str)) {				        	
							indexFile(file,indexingInfo);
							articles.remove(str);
							break;
				        }
					}
			}
		}

		return writer.numRamDocs();
	}
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
}
