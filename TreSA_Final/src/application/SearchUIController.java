package application;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SearchUIController {
	@FXML private TextField tf_search;
	@FXML private TextField tf_fieldTitle;
	@FXML private TextField	tf_fieldPeople;
	@FXML private TextField	tf_fieldPlaces;
	@FXML private TextField	tf_fieldContents;
	
	@FXML private Button b_clear;
	@FXML private Button b_search;
	@FXML private Button b_back;
	@FXML private Button b_showedDocBackButton;
	
	@FXML protected ToggleButton tb_phrases;
	@FXML protected ToggleButton tb_vector;
	@FXML protected ToggleButton tb_field;
	@FXML protected ToggleButton tb_boolean;
	
	@FXML private ListView<VBox> lv_showedDocs;
	
	@FXML Label l_title;
	@FXML Label l_people;
	@FXML Label l_places;
	@FXML Label l_contents;
	@FXML Label l_errorMessage;
	@FXML Label l_hits;
	@FXML Label l_example;
	
	@FXML private Text t_title;
	@FXML private Text t_people;
	@FXML private Text t_places;
	@FXML private Text t_contents;
	
	@FXML private ScrollPane scroll;
	@FXML private VBox contentvbox;
	@FXML private TextArea tx_booleanModel;
	//go back to the main scene when the logo is selected
	@FXML private void goBackLogo(ActionEvent event) throws IOException {
		Parent page = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene scene = new Scene(page);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	@FXML private void clearButton(ActionEvent event) {
		if(tb_phrases.isSelected()||tb_vector.isSelected()) {
			tf_search.setText("");
		}else if(tb_field.isSelected()) {
			tf_fieldTitle.setText("");
			tf_fieldPeople.setText("");
			tf_fieldPlaces.setText("");
			tf_fieldContents.setText("");
		}else if(tb_boolean.isSelected()) {
			tx_booleanModel.setText("");
		}
	}
	//setting disability for searches option, depending on what we choose
	protected void optionSelected(int choice) {
		if(choice==1) {
			//textField for phrase or vector is editable, and the others are not editable
			setDisability(1,false);
			setDisability(2,true);
			setDisability(3,true);
		}else if(choice==2) {
			//textFields for field search is editable
			setDisability(1,true);
			setDisability(2,false);
			setDisability(3,true);
		}else {			
			//textArea go boolean is editable
			setDisability(1,true);
			setDisability(2,true);
			setDisability(3,false);
		}
	}
	private void setDisability(int choice, Boolean bl) {
		switch(choice) {
			case 1:
				tf_search.setDisable(bl);
				break;
			case 2:
				tf_fieldTitle.setDisable(bl);
				tf_fieldPeople.setDisable(bl);
				tf_fieldPlaces.setDisable(bl);
				tf_fieldContents.setDisable(bl);
				
				l_title.setDisable(bl);
				l_people.setDisable(bl);
				l_places.setDisable(bl);
				l_contents.setDisable(bl);
				break;
			case 3:
				tx_booleanModel.setDisable(bl);
				break;
		}
	}
	//when i choose a button the current search option will become editable and an example will appear
	@FXML private void phrasesToddlerButton(ActionEvent event) {
		optionSelected(1);
		//l_example.setVisible(true);
		//l_example.setText("Example: What Comissaria said");
	}
	@FXML private void vectorToddlerButton(ActionEvent event) {
		optionSelected(1);
		//l_example.setVisible(true);
	//	l_example.setText("Example: cocoa");
	}
	@FXML private void booleanToddlerButton(ActionEvent event) {
		optionSelected(3);
		//l_example.setVisible(true);
		//l_example.setText("Example: cocoa && bahia"+"\n\n&&: Logical AND\n||: Logical OR\n^: Logical NOT");
	}
	@FXML private void fieldsToddlerButton(ActionEvent event) {
		optionSelected(2);
		//l_example.setVisible(true);
		//l_example.setText("Search something specific.");
	}
	
	@FXML private void searchButton(ActionEvent event) throws IOException, ParseException{
		/* in class Searcher -> method search -> parameter choice
		 * 1 -> Vector Search
		 * 2-> Boolean Search
		 * 3-> Phrase Search
		 * 4-> Field Search
		*/
		if(tb_phrases.isSelected()) {
			String str = tf_search.getText().toLowerCase();
			if(!str.isBlank()) {
				lv_showedDocs.getItems().clear();
				l_errorMessage.setVisible(false);
				//search query
				search(str,LuceneConstants.CONTENTS,3);
			}else {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must search something.");
			}
		}else if(tb_vector.isSelected()) {
			String str = tf_search.getText().toLowerCase();
			if(!str.isBlank()) {
				l_errorMessage.setVisible(false);
				//search query
				search(tf_search.getText().toLowerCase(),LuceneConstants.CONTENTS,1);
			}else {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must search something.");
			}
		}else if(tb_field.isSelected()) {
			lv_showedDocs.getItems().clear();
			Boolean allFieldsAreBlank=true;;
			if(!(tf_fieldTitle.getText().isBlank())) {
				allFieldsAreBlank=false;
				//search query
				search(tf_fieldTitle.getText().toLowerCase(),LuceneConstants.TITLE,4);
			}
			if(!(tf_fieldPeople.getText().isBlank())) {
				allFieldsAreBlank=false;
				//search query
				search(tf_fieldPeople.getText().toLowerCase(), LuceneConstants.PEOPLE,4);
			}
			if(!(tf_fieldPlaces.getText().isBlank())) {
				allFieldsAreBlank=false;
				//search query
				search(tf_fieldPlaces.getText().toLowerCase(), LuceneConstants.PLACES,4);
			}
			if(!(tf_fieldContents.getText().isBlank())) {
				allFieldsAreBlank=false;
				//search query
				search(tf_fieldContents.getText().toLowerCase(),LuceneConstants.BODY,4);
			}
			if(allFieldsAreBlank) {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must search atleast one field.");
			}
		}else if(tb_boolean.isSelected()) {
			String isBlank = tx_booleanModel.getText().toLowerCase();
			if(isBlank.length()!=0) {
				l_errorMessage.setVisible(false);
				search(isBlank,LuceneConstants.CONTENTS,2);
			}else {
				l_errorMessage.setVisible(true);
			}
		}
	}
	
	public void search(String searchQuery, String fieldType, int choice) throws IOException, ParseException {
		 l_errorMessage.setVisible(false);
	
		 if(!lv_showedDocs.isVisible()) {
		 	 showDocVisible(false);
		 }
		 Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
		 long startTime = System.currentTimeMillis();
		 //we search the query
		 TopDocs hits = searcher.search(choice, searchQuery,fieldType);
		 long endTime = System.currentTimeMillis();
		 DocumentFromSearch document;
		 l_hits.setText(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		 l_hits.setVisible(true);
		 //we clear the listview of showed docs in order to not show dublicate
		 lv_showedDocs.getItems().clear();
		 boolean articleComp = false;
		 // This "If" expression: If the user selected option 5 (article comparison) enter first option 
		 if(articleComp) {
			 	// Prints from higher to lower scores for article comparison
				int n = 0;
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					if (n == 0) {
						n++;
						continue;
					}
					Document doc = searcher.getDocument(scoreDoc);
					
					document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_NAME), searchQuery,fieldType);
					showDocuments(document, searchQuery,fieldType); 
					n++;
				}
			}
		 else {
			 for(ScoreDoc scoreDoc : hits.scoreDocs) {
				 Document doc = searcher.getDocument(scoreDoc);
				 //we make an object for every document that we found and we take the context from the file in Data Folder
				 document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_PATH), searchQuery,fieldType);
				 //we put the document in listView
				 showDocuments(document, searchQuery,fieldType); 
			 }
		 }
		 searcher.close();
  }

	public void setListView() {
		lv_showedDocs = new ListView<>();	
	}
	//when the query is transfered the we search it
	public void transferQuery(String query) throws IOException, ParseException {
		tf_search.setText(query);
		search(query,LuceneConstants.CONTENTS,3);
	}
	
	//go back to the listview of documents
	@FXML private void showedDocBackButton(ActionEvent event) throws IOException, ParseException {
		if(lv_showedDocs.isVisible()) {
			Parent page = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
			Scene scene = new Scene(page);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		}else {
			showDocVisible(false);
		}
		
	}

	public void showDocuments(DocumentFromSearch document, String searchQuery,String fieldtype){
		//in order to see the context of a document we need to click on title
		Hyperlink link = makeHyperLink(document.getTitle(),document,searchQuery);
		//person,place 
		Text text2 = new Text(document.getPerson()+", "+document.getPlace());
		//we call showQuery method in order to show where the query was found
		TextFlow text3 = showQuery(searchQuery,document,fieldtype);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(link,text2,text3);
		//we put the doc in listview
		lv_showedDocs.getItems().add(vbox);	
	}
	//we hide the listview and we show the context of the specific doc we selected
	private void showDocVisible(Boolean bl) {
		lv_showedDocs.setVisible(!bl);
		scroll.setVisible(bl);
		t_title.setVisible(bl);
		t_people.setVisible(bl);
		t_places.setVisible(bl);
		t_contents.setVisible(bl);
	}
	
	public void showDetailedDocument(DocumentFromSearch document) {
		showDocVisible(true);
		t_title.setText(document.getTitle());
		t_people.setText(document.getPerson());
		t_places.setText(document.getPlace());
		t_contents.setText(document.getContent());
	}
	
	private TextFlow showQuery(String query,DocumentFromSearch doc,String fieldtype) {
		
		TextFlow textFlow = new TextFlow();
		String line = doc.getQueryAppereanceLine(fieldtype);
		String lowerCaseLine = line.toLowerCase();
		String[] splittedQuery= {""};
		//we need to split the query if it's from boolean model
		if(query.contains("&&")||query.contains("||")) {
			splittedQuery = query.split(" ");
		}
		String secondPart="";
		String firstPart="";
		//we make the query red
		if(splittedQuery.length==1) {
			secondPart = line.substring(lowerCaseLine.indexOf(query));
			secondPart = secondPart.substring(query.length());
			firstPart = line.substring(0,lowerCaseLine.indexOf(query));
		}else {
			for(int i=0; i<splittedQuery.length;i++) {
				if(lowerCaseLine.contains(splittedQuery[i])) {
					secondPart = line.substring(lowerCaseLine.indexOf(splittedQuery[i]));
					secondPart = secondPart.substring(splittedQuery[i].length());
					firstPart = line.substring(0,lowerCaseLine.indexOf(splittedQuery[i]));
					query=splittedQuery[i];
					break;
				}
			}
		}
		
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
	
	private Hyperlink makeHyperLink(String title, DocumentFromSearch document, String searchQuery) {
		Hyperlink link = new Hyperlink();
		link.setStyle("-fx-font-size: 16; -fx-text-fill: #362222; -fx-font-weight: bold;");
		link.setText(title);
		link.setOnAction((e)->{
			showDetailedDocument(document);
			showDocVisible(true);
		});
		return link;
	}
}
