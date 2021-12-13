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
	@FXML private Button b_boolean;
	@FXML private Button b_vector;
	
	@FXML private void phrasesButton(ActionEvent event) throws IOException{
		Parent page = FXMLLoader.load(getClass().getResource("SearchUI.fxml"));
		Scene scene = new Scene(page);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML public void backButton(ActionEvent event) throws IOException{
		Parent page = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
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
}