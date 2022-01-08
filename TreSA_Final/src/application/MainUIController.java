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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainUIController {
	@FXML private Button b_search;
	@FXML private Button b_advance;
	@FXML private TextField tf_search;
	@FXML private Label errorLabel;
	@FXML private RadioButton rb_byFields;
	@FXML private RadioButton rb_boolean;
	@FXML private RadioButton rb_vector;
	@FXML private Button b_go;
	@FXML private Hyperlink h_settings;
	
	
	public void searchButton(ActionEvent event) throws IOException, ParseException{
		String str = tf_search.getText();
		errorLabel.setVisible(false);
		if(!str.isBlank()) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SearchDocumentsUI.fxml"));
			Parent searchPage = loader.load();
			Scene searchScene = new Scene(searchPage);
			SearchUIController controller = loader.getController();	
			// Making phrases search editable 
			controller.optionSelected(1);
			
			// Transfer the query in the other scene to display it
			controller.transferQuery(tf_search.getText().toLowerCase());
			Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			searchStage.setScene(searchScene);
			searchStage.show();
		}else {
			advanceVisible(false);
			// Error message shows up
			errorLabel.setVisible(true);
		}
	}
	
	@FXML private void settingsHyperlink(MouseEvent event) throws IOException {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Settings.fxml"));
			Parent settingPage;
			settingPage = loader.load();
			Scene settingScene = new Scene(settingPage);	
		    SettingsController settingsController = loader.getController();
		    settingsController.showFiles();
			Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			settingStage.setScene(settingScene);
			settingStage.show();
	}
	
	private void advanceVisible(Boolean bl) {
		rb_byFields.setSelected(true);
		rb_byFields.setVisible(bl);
		rb_boolean.setVisible(bl);
		rb_vector.setVisible(bl);
		b_go.setVisible(bl);
	}
	
	@FXML private void advanceButton(ActionEvent event) {
		errorLabel.setVisible(false);
		advanceVisible(b_go.isVisible()?false:true);
	}
	
	// "Go" button shows up, when the "Advance" button is selected
	@FXML public void goButton(ActionEvent event) throws IOException, ParseException {
		FXMLLoader loader = new FXMLLoader();
		Parent searchPage;
		loader.setLocation(getClass().getResource("SearchDocumentsUI.fxml"));
		searchPage = loader.load();
		SearchUIController controller = loader.getController();
		if(rb_byFields.isSelected()) {
			// "Field search" editable
			controller.optionSelected(2);	
			// Setting selection for the current toggle button
			controller.tb_field.setSelected(true);
		}else if(rb_boolean.isSelected()) {
			// "Boolean search" editable
			controller.optionSelected(3);
			controller.tb_boolean.setSelected(true);
		}else {
			// "Vector search" editable
			controller.optionSelected(1);
			controller.tb_vector.setSelected(true);
			controller.transferQuery(tf_search.getText());	
		}	
		Scene searchScene = new Scene(searchPage);
		Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchStage.setScene(searchScene);
		searchStage.show();
	}
	
}
