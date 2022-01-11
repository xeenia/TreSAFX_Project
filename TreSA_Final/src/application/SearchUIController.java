package application;

import java.io.File;
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
	@FXML private TextField tf_articleName;
	@FXML private TextField tf_k_num;
	
	@FXML private Button b_clear;
	@FXML private Button b_search;
	@FXML private Button b_back;
	@FXML private Button b_showedDocBackButton;
	
	@FXML protected ToggleButton tb_phrases;
	@FXML protected ToggleButton tb_vector;
	@FXML protected ToggleButton tb_field;
	@FXML protected ToggleButton tb_boolean;
	@FXML protected ToggleButton tb_TopK;
	
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
	
	// Go back to the main scene when the logo is clicked
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
			clearButtonFunc();
		}else if(tb_boolean.isSelected()) {
			tx_booleanModel.setText("");
		}else {
			tf_articleName.setText("");
			tf_k_num.setText("");
		}
	}
	
	private void clearButtonFunc() {
		tf_fieldTitle.setText("");
		tf_fieldPeople.setText("");
		tf_fieldPlaces.setText("");
		tf_fieldContents.setText("");
	}

	// Choosing a button, the current search option will become editable and an example will appear
	@FXML private void phrasesToddlerButton(ActionEvent event) {
		optionSelected(1);
	}
	
	@FXML private void vectorToddlerButton(ActionEvent event) {
		optionSelected(1);
	}
	
	@FXML private void fieldsToddlerButton(ActionEvent event) {
		optionSelected(2);
	}
	
	@FXML private void booleanToddlerButton(ActionEvent event) {
		optionSelected(3);
	}
	@FXML private void topKToddlerButton(ActionEvent event) {
		optionSelected(4);
	}
	
	// Setting disability for search option, epending on what has been selected
	protected void optionSelected(int choice) {
		if(choice == 1) {
			// textField for phrase or vector is editable, and the others are not editable
			setDisability(1,false);
			setDisability(2,true);
			setDisability(3,true);
			setDisability(4,true);
		}else if(choice == 2) {
			// textFields for field search is editable
			setDisability(1,true);
			setDisability(2,false);
			setDisability(3,true);
			setDisability(4,true);
		}else if(choice==3){			
			// textArea go boolean is editable
			setDisability(1,true);
			setDisability(2,true);
			setDisability(3,false);
			setDisability(4,true);
		}else {
			// textfields top-k is editable
			setDisability(1,true);
			setDisability(2,true);
			setDisability(3,true);
			setDisability(4,false);
		}
	}
	
	private void setDisability(int choice, Boolean bl) {
		switch(choice) {
			case 1:
				tf_search.setDisable(bl);
				break;
			case 2:
				setDisabilityFunc(1,bl);
				setDisabilityFunc(2,bl);
				break;
			case 3:
				tx_booleanModel.setDisable(bl);
				break;
			case 4:
				tf_articleName.setDisable(bl);
				tf_k_num.setDisable(bl);
		}
	}
	
	private void setDisabilityFunc(int choice, Boolean bl) {
		// For fields
		if(choice == 1) {
			tf_fieldTitle.setDisable(bl);
			tf_fieldPeople.setDisable(bl);
			tf_fieldPlaces.setDisable(bl);
			tf_fieldContents.setDisable(bl);
		}
		// For labels
		if(choice == 2) {
			l_title.setDisable(bl);
			l_people.setDisable(bl);
			l_places.setDisable(bl);
			l_contents.setDisable(bl);
		}
	}
	
	@FXML private void searchButton(ActionEvent event) throws IOException, ParseException{
		
		/* In class Searcher -> method search -> parameter choice
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
			if(!(tf_fieldTitle.getText().isBlank()) ||
			   !(tf_fieldPeople.getText().isBlank())||
			   !(tf_fieldPlaces.getText().isBlank())||
			   !(tf_fieldContents.getText().isBlank())
			){
				if(!(tf_fieldTitle.getText().isBlank())) {
					search(tf_fieldTitle.getText().toLowerCase(),LuceneConstants.TITLE,4);
				}
				if(!(tf_fieldPeople.getText().isBlank())) {
					search(tf_fieldPeople.getText().toLowerCase(), LuceneConstants.PEOPLE,4);
				}
				if(!(tf_fieldPlaces.getText().isBlank())) {
					search(tf_fieldPlaces.getText().toLowerCase(), LuceneConstants.PLACES,4);
				}
				if(!(tf_fieldContents.getText().isBlank())) {
					search(tf_fieldContents.getText().toLowerCase(),LuceneConstants.BODY,4);
				}
			}
			else {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must select at least one field.");
			}
			
		}else if(tb_boolean.isSelected()) {
			String isBlank = tx_booleanModel.getText().toLowerCase();
			if(isBlank.length()!=0) {
				l_errorMessage.setVisible(false);
				search(isBlank,LuceneConstants.CONTENTS,2);
			}else {
				l_errorMessage.setVisible(true);
			}
		}else if(tb_TopK.isSelected()) {
			StringBuilder articleNameString = new StringBuilder();
			articleNameString.append(tf_articleName.getText());
			String kStr = tf_k_num.getText();
			int k;
			try {
				k=Integer.valueOf(kStr);
				articleNameString.append(" "+k);
				search(articleNameString.toString(),LuceneConstants.FILE_NAME,5);
			}catch(NumberFormatException e) {l_errorMessage.setText("You must write a number.");}
		}
	}
	
	public void search(String searchQuery, String fieldType, int choice) throws IOException, ParseException {
		 l_errorMessage.setVisible(false);
		 if(!lv_showedDocs.isVisible()) {
		 	 showDocVisible(false);
		 }
		 Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
		 long startTime = System.currentTimeMillis();
		 TopDocs hits;
		 String[] str = new String[2];
		 str[1]="1";
		 if(choice==5) {
			 str = searchQuery.split(" ");
			 hits = searcher.search(choice, str[0],fieldType);
		 }else {
			// Searching query
			  hits = searcher.search(choice, searchQuery,fieldType);
		 }
		 
		 long endTime = System.currentTimeMillis();
		 DocumentFromSearch document;
		 l_hits.setText(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		 l_hits.setVisible(true);
		 
		 // Clear the listview of showed docs in order not to show duplications
		 lv_showedDocs.getItems().clear();
		 boolean articleComp = false;
		 // This "If" expression: If the user selected option 5 (article comparison) enter first option 
		 if(choice==5) {
			 	// Prints from higher to lower scores for article comparison
				int n = 0;
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					if(n==Integer.valueOf(str[1])+1) {
						break;
					}
					if (n == 0) {
						n++;
						continue;
					}
					Document doc = searcher.getDocument(scoreDoc);
					
					document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_PATH), searchQuery,"top");
					document.scoreDoc=scoreDoc;
					showDocuments(document, searchQuery,fieldType); 
					n++;
				}
			}
		 else {
			 for(ScoreDoc scoreDoc : hits.scoreDocs) {
				 Document doc = searcher.getDocument(scoreDoc);
				 document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_PATH), searchQuery,fieldType);
				 showDocuments(document, searchQuery,fieldType); 
			 }
		 }
		 searcher.close();
	}
	
	public void setListView() {
		lv_showedDocs = new ListView<>();	
	}
	
	// When the query is transfered then we search it
	public void transferQuery(String[] query,int choice) throws IOException, ParseException {
		switch(choice) {
			case 1:
				tf_search.setText(query[0]);
				search(query[0],LuceneConstants.CONTENTS,1);
				break;
			case 2:
				tx_booleanModel.setText(query[0]);
				search(query[0],LuceneConstants.CONTENTS,2);
				break;
			case 3:
				tf_search.setText(query[0]);
				search(query[0],LuceneConstants.CONTENTS,3);
				break;
			case 4:
				if(!query[0].isBlank()) {
					tf_fieldTitle.setText(query[0]);
					search(query[0],LuceneConstants.TITLE,4);
				}
				if(!query[1].isBlank()) {
					tf_fieldPeople.setText(query[1]);
					search(query[1], LuceneConstants.PEOPLE,4);
				}
				if(!query[2].isBlank()) {
					tf_fieldPlaces.setText(query[2]);
					search(query[2], LuceneConstants.PLACES,4);
				}
				if(!query[3].isBlank()) {
					tf_fieldContents.setText(query[3]);
					search(query[3],LuceneConstants.BODY,4);
				}
				break;
			case 5:
				tf_articleName.setText(query[0]);
				tf_k_num.setText(query[1]);
				search(query[0]+" "+query[1],LuceneConstants.FILE_NAME,5);
		}
		
	}
	
	// Go back to the listview of documents
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
		if(fieldtype.contains(LuceneConstants.FILE_NAME)) {
			// In order to see the context of a document we need to click on title
			Hyperlink link = makeHyperLink(document.getTitle(),document,searchQuery);
			StringBuilder str = new StringBuilder();
			str.append(document.getfilename());
			str = str.delete(0, str.indexOf("Article"));
			Text text2 = new Text(str.toString());
			// Calling showQuery method in order to show where the query was found
			Text text3 = new Text();
			TextFlow flow = new TextFlow();
			Text bold = new Text();
			bold.setStyle("-fx-font-weight: bold;");
			bold.setText("Score: ");
			text3.setText(String.valueOf(document.scoreDoc.score));
			flow.getChildren().addAll(bold,text3);
			VBox vbox = new VBox();
			vbox.getChildren().addAll(link,text2,flow);
			
			// Putting the doc in listview
			lv_showedDocs.getItems().add(vbox);
		}else {
		// In order to see the context of a document we need to click on title
		Hyperlink link = makeHyperLink(document.getTitle(),document,searchQuery);
		Text text2 = new Text(document.getPerson()+", "+document.getPlace());
		// Calling showQuery method in order to show where the query was found
		TextFlow text3 = showQuery(searchQuery,document,fieldtype);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(link,text2,text3);
		
		// Putting the doc in listview
		lv_showedDocs.getItems().add(vbox);	}
	}
	
	// Hiding the listview and showing the context of the specific doc we selected
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
		String line="";
		if(!fieldtype.contains(LuceneConstants.FILE_NAME)) {
			line = doc.getQueryAppereanceLine(fieldtype);
		}else {
			line="yaaa ";
		}
		String lowerCaseLine = line.toLowerCase();
		String[] splittedQuery= {""};
		// Splitting the query if it's from boolean model
		if(query.contains("&&")||query.contains("||")) {
			splittedQuery = query.split(" ");
		}
		String secondPart="";
		String firstPart="";
		// Making the query red
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
