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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainUIController {
	@FXML private Button b_search;
	@FXML private Button b_advance;
	@FXML private Button b_settings;
	@FXML private TextField tf_search;
	@FXML private Label errorLabel;
	@FXML private RadioButton rb_byFields;
	@FXML private RadioButton rb_boolean;
	@FXML private RadioButton rb_vector;
	@FXML private Button b_go;
	
	
	@FXML public void goButton(ActionEvent event) throws IOException, ParseException {
		FXMLLoader loader = new FXMLLoader();
		Parent searchPage;
		loader.setLocation(getClass().getResource("SearchDocumentsUI.fxml"));
		searchPage = loader.load();
		SearchUIController controller = loader.getController();
		if(rb_byFields.isSelected()) {
			controller.optionSelected(2);	
			controller.setBooleanListView();
			controller.tb_field.setSelected(true);
		}else if(rb_boolean.isSelected()) {
			controller.optionSelected(3);
			controller.setBooleanListView();
			controller.tb_boolean.setSelected(true);
		}else {
			controller.optionSelected(1);
			controller.tb_vector.setSelected(true);
			controller.setBooleanListView();
			controller.transferQuery(tf_search.getText());	
			
		}	
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
		
	}
	public void searchButton(ActionEvent event) throws IOException, ParseException{
		String str = tf_search.getText();
		errorLabel.setVisible(false);
		if(!str.isBlank()) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SearchDocumentsUI.fxml"));
			Parent searchPage = loader.load();
			Scene searchScene = new Scene(searchPage);
			SearchUIController controller = loader.getController();	
			controller.optionSelected(1);
			controller.setBooleanListView();
			controller.transferQuery(tf_search.getText());
			Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			searchStage.setScene(searchScene);
			searchStage.show();
		}else {
			advanceVisible(false);
			errorLabel.setVisible(true);
		}
	}
	private void advanceVisible(Boolean bl) {
		rb_byFields.setVisible(bl);
		rb_boolean.setVisible(bl);
		rb_vector.setVisible(bl);
		b_go.setVisible(bl);
	}
	@FXML private void advanceButton(ActionEvent event) {
		errorLabel.setVisible(false);
		advanceVisible(b_go.isVisible()?false:true);
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
