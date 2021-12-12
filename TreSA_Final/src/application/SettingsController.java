package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class SettingsController {
	
	@FXML private TextField tf_addingDocs;
	@FXML private TextField tf_deletingDocs;
	@FXML private Button b_back;
	@FXML private Button b_addingDocs;
	@FXML private Button b_deletingDocs;	
	@FXML private Text t_add;
	@FXML private Text t_delete;
	@FXML private Text t_indexInfo;
	@FXML private TextArea ta_documents;
	@FXML private TextArea ta_docsInIndexer;
	
	static String indexDir = LuceneConstants.INDEX_DIR;
	static String dataDir = LuceneConstants.DATA_DIR;
	Indexer indexer;
	IndexWriter writer;

	// Showing the file names in text area that exist in Data folder
	public void showFiles() {
		File[] files = new File(LuceneConstants.DATA_DIR).listFiles();
		String fileNames="";
		for (File file : files) 	{
		    fileNames = fileNames.concat(file.getName()+"\n");
			ta_documents.setText(fileNames);
		}
	}
	
	// Going back to main UI when the back button is clicked
	@FXML private void backButton(ActionEvent event) throws IOException {
		Parent searchPage = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
	
	// Taking the documents and send them to Indexer
	@FXML private void addingDocsButton(MouseEvent event) throws IOException {
		ArrayList<String> documents = takeTheDocs(tf_addingDocs, t_add);
		if(!documents.isEmpty())
			createIndex(documents);	
	}
	
	// Taking the documents and delete them from Indexer
	@FXML private void deletingDocsButton(MouseEvent event) throws IOException {
		ArrayList<String> documents = takeTheDocs(tf_deletingDocs,t_delete);
		if(!documents.isEmpty()) {
			deleteSpecificDocs(documents);
		}
	}
	
	private boolean checkTheFileName(String article) {
		String[] splitted = article.split(" ");
		for(String fileName : splitted) {
			if(fileName.startsWith("Article")&&fileName.endsWith(".txt")) {
				String num = fileName.replace("Article", "");
				num = num.replace(".txt", "");
			    try {
			        int isNumber = Integer.parseInt(num);
			    } catch (NumberFormatException nfe) {
			        return false;
			    }
			    return true;
			}else {
				return false;
			}
		}
		return true;
	}
	
	private ArrayList<String> takeTheDocs(TextField textField, Text errorText) {
		String documentsFromTF = textField.getText();
		ArrayList<String> documents = new ArrayList<String>();
		String[] splitted = documentsFromTF.split(" ");
		for(String str : splitted) {
			if(checkTheFileName(str)) {
				documents.add(str);
				if(errorText.isVisible()) {
					errorText.setVisible(false);
				}
			}else {
				errorText.setText("Incorrect file name: "+ str);
				errorText.setVisible(true);
			}
		}
		textField.setText("");
		return documents;
	}

	private void createIndex(ArrayList<String> articles) throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		ta_docsInIndexer.setText("");
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter(), 1,ta_docsInIndexer); // Creates an Index with all Data
		long endTime = System.currentTimeMillis();		
		indexer.close();
		t_indexInfo.setText(numIndexed + " File(s) indexed, time taken: " + (endTime-startTime)+" ms");
		if(!t_indexInfo.isVisible()) {
			t_indexInfo.setVisible(true);
		}
	}

	public void deleteSpecificDocs(ArrayList<String> documents) throws IOException {
		Path path = Paths.get(LuceneConstants.INDEX_DIR);
     	Directory directory = FSDirectory.open(path);
     	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(directory,config);
		for(String article : documents){
			writer.deleteDocuments(new Term(LuceneConstants.FILE_NAME,article));
			writer.commit();
		}
		t_indexInfo.setText("index contains deleted files: "+writer.hasDeletions());
		if(!t_indexInfo.isVisible()) {
			t_indexInfo.setVisible(true);
		}
		writer.close();
	}

}


