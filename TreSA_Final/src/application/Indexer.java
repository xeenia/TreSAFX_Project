package application;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiBits;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class Indexer extends PreProcessoring{
	private IndexWriter writer;
	
	public Indexer(String indexDirectoryPath) throws IOException  {
		//this directory will contain the indexes
		 Path indexPath = Paths.get(indexDirectoryPath);
		 if(!Files.exists(indexPath)) {
			 Files.createDirectory(indexPath);
		 }
		 //Path indexPath = Files.createTempDirectory(indexDirectoryPath);
		 Directory indexDirectory = FSDirectory.open(indexPath);
		 //create the indexer
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
		indexingInfo.setText(previousIndexingInfo+ "Indexing " + file.getName()+"\n");
		writer.addDocument(getDocument(file));
	}
	
	public void createIndex(String dataDirPath, FileFilter filter, ArrayList<String> articles,TextArea indexingInfo) throws IOException {
		String previous = indexingInfo.getText();
		File[] indexfiles = new File(LuceneConstants.INDEX_DIR).listFiles();
		File file=null;
		for(String str:articles) {
			file = new File(dataDirPath+"\\"+str);
			if(file.exists()) {
				TopDocs hits=null;
				if(indexfiles.length!=1) {
					Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
					 hits = searcher.getHits(str);
					 searcher.close();
				}else {
					indexFile(file,indexingInfo);
					continue;
				}
				if(hits.totalHits.toString().contains("1")) {
					previous = indexingInfo.getText();
					indexingInfo.setText(previous+"The file: "+str+" already exist."+"\n");
				}else {
					indexFile(file,indexingInfo);
				}
			}else{
				previous = indexingInfo.getText();
				indexingInfo.setText(previous+"File <<"+str+">> not found in Data Folder.\n");
			}
			
		}
		previous = indexingInfo.getText();
		indexingInfo.setText(previous+writer.numRamDocs() + " File(s) indexed\n");
		
	}
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
	
	
	
}
