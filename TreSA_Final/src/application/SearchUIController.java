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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SearchUIController {
	BooleanFields booleanFields;
	@FXML private TextField tf_search;
	@FXML private TextField tf_fieldTitle;
	@FXML private TextField	tf_fieldPeople;
	@FXML private TextField	tf_fieldPlaces;
	@FXML private TextField	tf_fieldContents;
	
	@FXML private Button b_clear;
	@FXML private Button b_search;
	@FXML private Button b_booleanAdd;
	@FXML private Button b_booleanDelete;
	@FXML private Button b_back;
	
	@FXML protected ToggleButton tb_phrases;
	@FXML protected ToggleButton tb_vector;
	@FXML protected ToggleButton tb_field;
	@FXML protected ToggleButton tb_boolean;
	
	@FXML private ListView<VBox> lv_showedDocs;
	@FXML private ListView<HBox> lv_booleanModel;
	
	@FXML Label l_title;
	@FXML Label l_people;
	@FXML Label l_places;
	@FXML Label l_contents;
	@FXML Label l_errorMessage;
	@FXML Label l_hits;
	
	@FXML private Text t_title;
	@FXML private Text t_people;
	@FXML private Text t_places;
	@FXML private Text t_contents;
	
	@FXML private Label l_selectedDocPeople;
	@FXML private Label l_selectedDocPlaces;
	@FXML private Button b_showedDocBackButton;
	
	@FXML private void goBackLogo(ActionEvent event) throws IOException {
		Parent page = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene scene = new Scene(page);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	protected void optionSelected(int choice) {
		if(choice==1) {
			setDisability(1,false);
			setDisability(2,true);
			setDisability(3,true);
		}else if(choice==2) {
			setDisability(1,true);
			setDisability(2,false);
			setDisability(3,true);
		}else {			
			setDisability(1,true);
			setDisability(2,true);
			setDisability(3,false);
		}
	}
	@FXML private void phrasesToddlerButton(ActionEvent event) {
		optionSelected(1);
	}
	@FXML private void vectorToddlerButton(ActionEvent event) {
		optionSelected(1);
		
	}
	@FXML private void booleanToddlerButton(ActionEvent event) {
		optionSelected(3);
	}
	@FXML private void fieldsToddlerButton(ActionEvent event) {
		optionSelected(2);
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
				lv_booleanModel.setDisable(bl);
				b_booleanAdd.setDisable(bl);
				b_booleanDelete.setDisable(bl);
				break;
		}
	}
	@FXML private void searchButton(ActionEvent event) throws IOException, ParseException{
		if(tb_phrases.isSelected()) {
			String str = tf_search.getText().toLowerCase();
			if(!str.isBlank()) {
				lv_showedDocs.getItems().clear();
				l_errorMessage.setVisible(false);
				search(str,LuceneConstants.CONTENTS,3);
			}else {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must search something.");
			}
		}else if(tb_vector.isSelected()) {
			String str = tf_search.getText();
			if(!str.isBlank()) {
				l_errorMessage.setVisible(false);
			}else {
				l_errorMessage.setVisible(true);
			}
		}else if(tb_field.isSelected()) {
			lv_showedDocs.getItems().clear();
			Boolean allFieldsAreBlank=true;;
			if(!(tf_fieldTitle.getText().isBlank())) {
				allFieldsAreBlank=false;
				search(tf_fieldTitle.getText().toLowerCase(),LuceneConstants.TITLE,4);
			}
			if(!(tf_fieldPeople.getText().isBlank())) {
				allFieldsAreBlank=false;
				search(tf_fieldPeople.getText().toLowerCase(), LuceneConstants.PEOPLE,4);
			}
			if(!(tf_fieldPlaces.getText().isBlank())) {
				allFieldsAreBlank=false;
				search(tf_fieldPlaces.getText().toLowerCase(), LuceneConstants.PLACES,4);
			}
			if(!(tf_fieldContents.getText().isBlank())) {
				allFieldsAreBlank=false;
				search(tf_fieldContents.getText().toLowerCase(),LuceneConstants.CONTENTS,4);
			}
			if(allFieldsAreBlank) {
				l_errorMessage.setVisible(true);
				l_errorMessage.setText("You must search atleast one field.");
			}
		}else if(tb_boolean.isSelected()) {
			String str = "idk";
			if(!str.isBlank()) {
				l_errorMessage.setVisible(false);
				search(str,LuceneConstants.CONTENTS,4);
			}else {
				l_errorMessage.setVisible(true);
			}
		}
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
			
		}
	}
	public void setListView() {
		lv_showedDocs = new ListView<>();	
	}
	public void transferQuery(String query) throws IOException, ParseException {
		tf_search.setText(query);
		search(query,LuceneConstants.CONTENTS,3);
	}
	public void search(String searchQuery, String fieldType, int choice) throws IOException, ParseException {
		l_errorMessage.setVisible(false);
		if(!lv_showedDocs.isVisible()) {
			showDocVisible(false);
		}
		 Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
		 long startTime = System.currentTimeMillis();
		 TopDocs hits = searcher.search(choice, searchQuery,fieldType);
		 long endTime = System.currentTimeMillis();
		 DocumentFromSearch document;
		 l_hits.setText(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		 l_hits.setVisible(true);
		 for(ScoreDoc scoreDoc : hits.scoreDocs) {
			 Document doc = searcher.getDocument(scoreDoc);
			 document = new DocumentFromSearch(doc.get(LuceneConstants.FILE_PATH), searchQuery);
			 showDocuments(document, searchQuery,fieldType); 
		 }
		 	searcher.close();
   }
 	
 	public void setBooleanListView() {
		booleanFields = new BooleanFields();
		HBox hbox = new HBox();
		hbox.getChildren().addAll(booleanFields.getTextField1(),booleanFields.getLogicalButton(),booleanFields.getTextField2());
		lv_booleanModel.getItems().add(hbox);
	}
	@FXML private void addBoolean(ActionEvent event) {
		if(lv_booleanModel.getItems().size()<7) {
			booleanFields = new BooleanFields();
			HBox hbox = new HBox();
			hbox.getChildren().addAll(booleanFields.getTextField1(),booleanFields.getLogicalButton(),booleanFields.getTextField2());
			lv_booleanModel.getItems().add(hbox);
		}
		
	}
	@FXML private void removeBoolean(ActionEvent event) {
		if(lv_booleanModel.getItems().size()!=1)
			lv_booleanModel.getItems().remove(lv_booleanModel.getItems().size()-1);
	}	

	@FXML private void showedDocBackButton(ActionEvent event) throws IOException, ParseException {
		showDocVisible(false);
	}
	private void showDocVisible(Boolean bl) {
		lv_showedDocs.setVisible(!bl);
		t_title.setVisible(bl);
		t_people.setVisible(bl);
		t_places.setVisible(bl);
		t_contents.setVisible(bl);
		b_showedDocBackButton.setVisible(bl);
		l_selectedDocPeople.setVisible(bl);
		l_selectedDocPlaces.setVisible(bl);
	}
	public void showDetailedDocument(DocumentFromSearch document) {
		showDocVisible(true);
		t_title.setText(document.getTitle());
		t_people.setText(document.getPerson());
		t_places.setText(document.getPlace());
		t_contents.setText(document.getContent());
	}
	
	
	public void showDocuments(DocumentFromSearch document, String searchQuery,String fieldtype){
		Hyperlink link = makeHyperLink(document.getTitle(),document,searchQuery);
		Text text2 = new Text(document.getPerson()+", "+document.getPlace());
		TextFlow text3 = showQuery(searchQuery,document,fieldtype);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(link,text2,text3);
		lv_showedDocs.getItems().add(vbox);	
	}
	
	private TextFlow showQuery(String query,DocumentFromSearch doc,String fieldtype) {
		
		TextFlow textFlow = new TextFlow();
		String line = doc.getQueryAppereanceLine(fieldtype);
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
	
	private Hyperlink makeHyperLink(String title, DocumentFromSearch document, String searchQuery) {
		Hyperlink link = new Hyperlink();
		link.setStyle("-fx-font-size: 16");
		link.setText(title);
		link.setOnAction((e)->{
			showDetailedDocument(document);
			showDocVisible(true);
		});
		return link;
	}
}
