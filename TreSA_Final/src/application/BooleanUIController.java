package application;

import java.io.IOException;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BooleanUIController {

	@FXML private Button b_back;
	@FXML private Button b_byField;
	@FXML private Button b_search;
	@FXML private Label errorLabel;
	@FXML private ListView<HBox> listView;
	private VBox vbox;
	@FXML private Button b_vector;
	@FXML private Button b_addBoolean;
	BooleanFields booleanFields;
	@FXML private Button b_phrases;
	@FXML private Button b_removeBoolean;
	
	
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
	@FXML private void phrasesButton(ActionEvent event) throws IOException{
		Parent page = FXMLLoader.load(getClass().getResource("SearchUI.fxml"));
		Scene scene = new Scene(page);
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
		String str = "idk";
		if(!str.isBlank()) {
			errorLabel.setVisible(false);
			search(str);
		}else {
			errorLabel.setVisible(true);
		}
	}
	private void search(String searchQuery) throws IOException, ParseException {
		
   }
	
	public void setListView() {
		booleanFields = new BooleanFields();
		HBox hbox = new HBox();
		hbox.getChildren().addAll(booleanFields.getTextField1(),booleanFields.getLogicalButton(),booleanFields.getTextField2());
		listView.getItems().add(hbox);
	}
	@FXML public void addMoreBoolean(ActionEvent event) {
		if(listView.getItems().size()<7) {
			booleanFields = new BooleanFields();
			HBox hbox = new HBox();
			hbox.getChildren().addAll(booleanFields.getTextField1(),booleanFields.getLogicalButton(),booleanFields.getTextField2());
			listView.getItems().add(hbox);
		}
		
	}
	@FXML public void removeBoolean(ActionEvent event) {
		if(listView.getItems().size()!=1)
			listView.getItems().remove(listView.getItems().size()-1);
	}	
}


