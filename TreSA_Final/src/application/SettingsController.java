package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsController {
	@FXML private TextField tf_addingDocs;
	@FXML private TextField tf_deletingDocs;
	@FXML private TextField tf_editFile;
	@FXML private TextField tf_title;
	@FXML private TextField tf_place;
	@FXML private TextField tf_people;
	
	@FXML private TextArea ta_status;
	@FXML private TextArea ta_content;
	@FXML private TextArea ta_indexer;
	
	@FXML private Button b_back;
	@FXML private Button b_addingDocs;
	@FXML private Button b_deletingDocs;
	@FXML private Button b_editFile;
	@FXML private Button b_clear;
	@FXML private Button b_save;
	@FXML private Button b_cancel;
	@FXML private Button b_delete_all;
	@FXML private Button b_addFileExplorer;
	
	@FXML private Label l_title;
	@FXML private Label l_people;
	@FXML private Label l_place;
	@FXML private Label l_content;
	
	@FXML private HBox hbox;
		
	static String indexDir = LuceneConstants.INDEX_DIR;
	static String dataDir = LuceneConstants.DATA_DIR;
	Indexer indexer;
	IndexWriter writer;
	
	@FXML public void showDocsInIndexer() throws IOException {
		print();
	}
	 
	private void print() throws IOException {
		Searcher seacrher = new Searcher(indexDir);
		ta_indexer.setText("");
		seacrher.printDocuments(ta_indexer);
	}
	
	@FXML private void deleteAllButton(ActionEvent event) throws IOException {
			Path path = Paths.get(LuceneConstants.INDEX_DIR);
	     	Directory directory = FSDirectory.open(path);
	     	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	     	writer = new IndexWriter(directory,config);
		try {
            writer.deleteAll();
            writer.commit();
            writer.close();
            ta_status.setText("All documents have been deleted.");
            print();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@FXML  private void addFileExplorerButton(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose an article");
		Stage stage = (Stage) hbox.getScene().getWindow();
		List<File> files = fileChooser.showOpenMultipleDialog(stage);	
		StringBuilder builder = new StringBuilder();
		if(files!=null) {
			ArrayList<String> documents= new ArrayList();
			for(File file : files) {
				builder.append(file);
				builder = builder.delete(0,builder.indexOf("Article"));
				documents.add(builder.toString());
				builder.delete(0, builder.length());
			}
			createIndex(documents);
		}
		print();
	}
	
	// Going back to main UI when the back button its clicked
	@FXML private void backButton(ActionEvent event) throws IOException {
		Parent searchPage = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
	
	// Taking the documents and send them to Indexer
	@FXML private void addingDocsButton(ActionEvent event) throws IOException {
		ta_status.setText("");
		ArrayList<String> documents = takeTheDocs(tf_addingDocs );
		
		if(!documents.isEmpty())
			createIndex(documents);	
		print();
	}
	
	// Taking the documents and delete them from Indexer
	@FXML private void deletingDocsButton(ActionEvent event) throws IOException {
		ArrayList<String> documents = takeTheDocs(tf_deletingDocs);
		if(!documents.isEmpty()) {
			deleteSpecificDocs(documents);
		}
		print();
	}

	private boolean checkTheFileName(String article) {
		String[] splitted = article.split(" ");
		for(String fileName : splitted) {
			if(fileName.startsWith("Article")&&fileName.endsWith(".txt")) {
				String num = fileName.replace("Article", "");
				num = num.replace(".txt", "");
			    try {
			        int isNumber = Integer.parseInt(num);
			        if(isNumber<0) return false;
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

	private ArrayList<String> takeTheDocs(TextField textField) {
		String documentsFromTF = textField.getText();
		ArrayList<String> documents = new ArrayList<String>();
		String[] splitted = documentsFromTF.split(" ");
		for(String str : splitted) {
			if(checkTheFileName(str)) {
				documents.add(str);
			}else {
				String str1 = ta_status.getText();
				if(str1.isEmpty()) ta_status.setText("Incorrect file name: "+ str);
				else ta_status.setText(str1+"\n"+"Incorrect file name: "+ str);
			}
		}
		textField.setText("");
		return documents;
	}

	private void createIndex(ArrayList<String> articles) throws IOException {
		indexer = new Indexer(indexDir); 
		indexer.createIndex(dataDir, new TextFileFilter(),articles,ta_status);	
		indexer.close();
	}

	public void deleteSpecificDocs(ArrayList<String> documents) throws IOException {
		Path path = Paths.get(LuceneConstants.INDEX_DIR);
     	Directory directory = FSDirectory.open(path);
     	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(directory,config);
		for(String article : documents){
			writer.deleteDocuments(new Term(LuceneConstants.FILE_NAME,article));
			writer.commit();
			ta_status.setText("The document <<"+article+">> has been deleted.");
		}
		writer.close();
		print();
	}
	
	@FXML void editSpecificDoc(ActionEvent event) throws IOException {
		ta_status.setText("");
		Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
		String file = tf_editFile.getText();
		Boolean flag=false;
		String[] fileName = file.split(" ");
		if(checkTheFileName((fileName.length>1)?"fdsfds":file)) {
			TopDocs hits = searcher.getHits(file);
			if(hits.totalHits.toString().contains("1")) {				
				ta_status.setText("The file "+file+" exist in Indexer.");
				}
				File filename = new File(LuceneConstants.DATA_DIR+file);
				if(filename.exists()) {
					setDisability(false);
					BufferedReader reader = new BufferedReader(new FileReader(filename));
					String line=null;
					while((line = reader.readLine()) != null) {
						if(line.contains("<TITLE>")) {
							line = line.replace("<TITLE>", "");
							line = line.replace("</TITLE>", "");
							tf_title.setText(line);
						}
						if(line.contains("<PEOPLE>")) {
							line = line.replace("<PEOPLE>", "");
							line = line.replace("</PEOPLE>", "");
							tf_people.setText(line);
						}
						if(line.contains("<PLACES>")) {
							line = line.replace("<PLACES>", "");
							line = line.replace("</PLACES>", "");
							tf_place.setText(line);
						}
						if(line.contains("<BODY>")||flag) {
							flag=true;
							line = line.replace("<BODY>", "");
							line = line.replace("</BODY>", "");
							String str = ta_content.getText();
							if(str.isEmpty())
								ta_content.setText(str+line);
							else {
								ta_content.setText(str+"\n"+line);
							}
						}
					}	
					reader.close();
				}else {
					ta_status.setText("This file does not exist in Data Folder.");
				}
		}else {
			String str1 = ta_status.getText();
			if(fileName.length>1) {
				if(str1.isEmpty()) ta_status.setText("You need to write 1 file: "+ file);
				else ta_status.setText(str1+"\n"+"You need to write 1 file: "+ file);
			}else {
				if(str1.isEmpty()) ta_status.setText("Incorect file name: "+ file);
				else ta_status.setText(str1+"\n"+"Incorrect file name: "+ file);
			}
			
		}
		searcher.close();
	}
	
	private void clear() {
		tf_title.setText("");
		tf_people.setText("");
		tf_place.setText("");
		ta_content.setText("");
	}
	
	@FXML private void cancelButton(ActionEvent event) {
		setDisability(true);
		ta_status.setText("");
		clear();
	}
	
	@FXML private void saveButton(ActionEvent event) throws IOException {
		if(tf_title.getText().isEmpty()||ta_content.getText().isEmpty()) {
			ta_status.setText("You must write something in both Title field and Content Field.");
		}else {
			File file = new File(LuceneConstants.DATA_DIR+tf_editFile.getText());
			ArrayList<String> arr = new ArrayList();
			Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
			TopDocs hits = searcher.getHits(tf_editFile.getText());
			if(hits.totalHits.toString().contains("1")) {
				arr.add(tf_editFile.getText());
				deleteSpecificDocs(arr);
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("<PLACES>"+tf_place.getText()+"</PLACES>"+"\n");
			writer.write("<PEOPLE>"+tf_people.getText()+"</PEOPLE>"+"\n");
			writer.write("<TITLE>"+tf_title.getText()+"</TITLE>"+"\n");
			writer.write("<BODY>"+ta_content.getText());
			writer.write("</BODY>");
			writer.close();
			clear();
			tf_editFile.setText("");
			if(hits.totalHits.toString().contains("1")) createIndex(arr);
			searcher.close();	
		}
	}
	
	private void setDisability(boolean bl) {
		tf_title.setDisable(bl);
		tf_people.setDisable(bl);
		tf_place.setDisable(bl);
		ta_content.setDisable(bl);
		b_save.setDisable(bl);
		l_title.setDisable(bl);
		l_people.setDisable(bl);
		l_place.setDisable(bl);
		l_content.setDisable(bl);
		b_cancel.setDisable(bl);
	}
	
	@FXML private void clearButton(ActionEvent event) {
		clear();
	}

}