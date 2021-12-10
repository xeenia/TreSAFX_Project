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

public class SearchUIController {

	@FXML
	private Button b_back;
	@FXML private Button b_byField;
	
	//Going back to main UI if I click the TreSA logo
	public void backButton(ActionEvent event) throws IOException{
		Parent searchPage = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
	
	@FXML private void byFieldButton(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("ByFieldUI.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);	
	    //SettingsController settingsController = loader.getController();
	    //settingsController.showFiles();
		Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		settingStage.setScene(scene);
		settingStage.show();
	}
}
