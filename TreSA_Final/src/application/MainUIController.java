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

public class MainUIController {
	@FXML
	private Button b_search;
	@FXML
	private Button b_advance;
	@FXML
	private Button b_settings;

	public void searchButton(ActionEvent event) throws IOException{
		Parent searchPage = FXMLLoader.load(getClass().getResource("SearchUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
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
