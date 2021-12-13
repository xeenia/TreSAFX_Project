package application;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SearchUIController {

	@FXML private Button b_back;
	@FXML private Button b_byField;
	@FXML private TextArea ta_search;
	@FXML private Button b_search;
	@FXML private Label errorLabel;
	@FXML private ListView<VBox> listView;
	private VBox vbox;
	@FXML private Button b_boolean;
	@FXML private Button b_vector;
	@FXML private Label l_hits;
	
	
	
	//Going back to main UI if I click the TreSA logo
	public void backButton(ActionEvent event) throws IOException{
		Parent page = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene scene = new Scene(page);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML private void byFieldButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("ByFieldUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	@FXML private void booleanButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("BooleanUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
		BooleanUIController controller = loader.getController();
		controller.setListView();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	@FXML private void vectorButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("VectorUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	@FXML private void searchButton(ActionEvent event) throws IOException, ParseException {
		String str = ta_search.getText();
		if(!str.isBlank()) {
			listView.getItems().clear();
			errorLabel.setVisible(false);
			search(str);
		}else {
			errorLabel.setVisible(true);
		}
	}
	public void search(String searchQuery) throws IOException, ParseException {
		Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
		 long startTime = System.currentTimeMillis();
		 TopDocs hits = searcher.search(3, searchQuery);
		 long endTime = System.currentTimeMillis();
		 DocumentFromSearch document;
		 l_hits.setText(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		 l_hits.setVisible(true);
		 for(ScoreDoc scoreDoc : hits.scoreDocs) {
			 Document doc = searcher.getDocument(scoreDoc);
			 document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_PATH), searchQuery);
			 showDocuments(document, searchQuery);
			 //System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		 }
		 	searcher.close();
   }
	
	public void transferQuery(String query) throws IOException, ParseException {
		ta_search.setText(query);
		search(query);
	}
	public void setListView() {
		listView = new ListView<>();	
	}
	public void showDocuments(DocumentFromSearch document, String searchQuery){
		Hyperlink link = makeHyperLink(document.getTitle(),document);
		Text text2 = new Text(document.getPerson()+", "+document.getPlace());
		TextFlow text3 = showQuery(searchQuery,document);
		vbox = new VBox();
		vbox.getChildren().addAll(link,text2,text3);
		listView.getItems().add(vbox);	
	}
	
	private TextFlow showQuery(String query,DocumentFromSearch doc) {
		TextFlow textFlow = new TextFlow();
		String line = doc.getQueryAppereanceLine("body");
		String lowerCaseLine = line.toLowerCase();
		
		String secondPart = line.substring(lowerCaseLine.indexOf(query));
		secondPart = secondPart.substring(query.length());
		String firstPart = line.substring(0,lowerCaseLine.indexOf(query));
		Text text1 = new Text();
		Text text2 = new Text();
		Text text3 = new Text();
		
		text1.setText(firstPart);
		text2.setText(query);
		text2.setFill(Color.RED);
		text3.setText(secondPart);
		textFlow.getChildren().addAll(text1,text2,text3);
		return textFlow;
	}
	
 	private Hyperlink makeHyperLink(String title, DocumentFromSearch document) {
		Hyperlink link = new Hyperlink();
		link.setStyle("-fx-font-size: 16");
		link.setText(title);
		link.setOnAction((e)->{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ShowArticle.fxml"));
			Parent page=null;
				try {
					page = loader.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			Scene scene = new Scene(page);	
		    ShowArticleController controller = loader.getController();
		    controller.showDetailedDocument(document);
		    controller.setTextAreaSearchedQuery(ta_search.getText());
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		});
		return link;
	}
}


