package application;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainUIController {
	@FXML private Button b_search;
	@FXML private Button b_advance;
	@FXML private Button b_settings;
	@FXML private TextField tf_search;
	@FXML private Label errorLabel;

	public void searchButton(ActionEvent event) throws IOException, ParseException{
		String str = tf_search.getText();
		errorLabel.setVisible(false);
		if(!str.isBlank()) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SearchUI.fxml"));
			Parent searchPage = loader.load();
			Scene searchScene = new Scene(searchPage);
			SearchUIController controller = loader.getController();
			controller.transferQuery(tf_search.getText());	
			Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			searchStage.setScene(searchScene);
			searchStage.show();
		}else {
			errorLabel.setVisible(true);
		}
	}

	public void advanceButton(ActionEvent event) {
		
	}

	public void settingsButton(ActionEvent event) throws IOException {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Settings.fxml"));
		Parent settingPage = loader.load();
		Scene settingScene = new Scene(settingPage);	
	    SettingsController settingsController = loader.getController();
	    settingsController.showFiles();
		Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		settingStage.setScene(settingScene);
		settingStage.show();
	}
	

	
}
