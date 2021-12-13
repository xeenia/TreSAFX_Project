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
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShowArticleController {

	@FXML private Button b_back;
	@FXML private Button b_byField;
	@FXML private TextArea ta_search;
	@FXML private Button b_search;
	@FXML private Label errorLabel;
	@FXML private Button b_boolean;
	@FXML private Button b_vector;
	@FXML private Button b_backLogo;
	@FXML private Text title;
	@FXML private Text person;
	@FXML private Text place;
	@FXML private Text content;
	
	
	//Going back to main UI if I click the TreSA logo
	public void backButtonLogo(ActionEvent event) throws IOException{
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
		Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		settingStage.setScene(scene);
		settingStage.show();
	}
	@FXML private void searchButton(ActionEvent event) throws IOException, ParseException {
		String str = ta_search.getText();
		if(!str.isBlank()) {
			errorLabel.setVisible(false);
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SearchUI.fxml"));
			Parent page = loader.load();
			Scene scene = new Scene(page);	
		    SearchUIController controller = loader.getController();
		    controller.search(str);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		}else {
			errorLabel.setVisible(true);
		}
	}
	public void setTextAreaSearchedQuery(String str) {
		ta_search.setText(str);
	}
	@FXML private void backButton(ActionEvent event) throws IOException, ParseException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("SearchUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
		SearchUIController controller = loader.getController();
	    controller.transferQuery(ta_search.getText());
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	public void showDetailedDocument(DocumentFromSearch document) {
		title.setText(document.getTitle());
		person.setText(document.getPerson());
		place.setText(document.getPlace());
		content.setText(document.getContent());
	}
	
}


