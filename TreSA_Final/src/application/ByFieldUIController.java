package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ByFieldUIController {

	
	@FXML private Button b_phrases;
	@FXML private Button b_back;
	
	@FXML private void phrasesButton(ActionEvent event) throws IOException{
		Parent searchPage = FXMLLoader.load(getClass().getResource("SearchUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
	
	@FXML public void backButton(ActionEvent event) throws IOException{
		Parent searchPage = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
}