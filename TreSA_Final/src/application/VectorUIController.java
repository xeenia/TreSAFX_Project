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

public class VectorUIController {

	@FXML private Button b_back;
	@FXML private Button b_byField;
	@FXML private TextArea ta_search;
	@FXML private Button b_search;
	@FXML private Label errorLabel;
	@FXML private ListView<VBox> listView;
	private VBox vbox;
	@FXML private Button b_boolean;
	@FXML private Button b_phrases;
	
	
	
	
	
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
		Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		settingStage.setScene(scene);
		settingStage.show();
	}
	@FXML private void booleanButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("BooleanUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
		BooleanUIController controller = loader.getController();
		controller.setListView();
		Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		settingStage.setScene(scene);
		settingStage.show();
	}
	@FXML private void phrasesButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("SearchUI.fxml"));
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
		}else {
			errorLabel.setVisible(true);
		}
	}
	public void setListView() {
		listView = new ListView<>();
		
	}
	
}


