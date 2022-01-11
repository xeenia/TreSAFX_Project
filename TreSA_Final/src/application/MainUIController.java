package application;

import java.io.File;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainUIController {
	@FXML private Button b_search;
	@FXML private Button b_advance;
	@FXML private TextField tf_search;
	@FXML private Label errorLabel;
	@FXML private Hyperlink h_settings;
	@FXML private TextField tf_title;
	@FXML private TextField tf_person;
	@FXML private TextField tf_place;
	@FXML private TextField tf_content;
	@FXML private TextField tf_k;
	
	
	public void searchButton(ActionEvent event) throws IOException, ParseException{
		String str = tf_search.getText();
		errorLabel.setVisible(false);
		Boolean fields=true,flag=true; 
		File[] files = new File(LuceneConstants.INDEX_DIR).listFiles();
		System.out.println(files.length);
		if(files.length==1||files.length==0) {
			errorLabel.setText("No documents in indexer. Go to settings and import.");
			errorLabel.setVisible(true);
		}else {
			if((!str.isBlank()&&tf_search.isVisible())||(tf_title.isVisible())) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("SearchDocumentsUI.fxml"));
				Parent searchPage = loader.load();
				Scene searchScene = new Scene(searchPage);
				SearchUIController controller = loader.getController();	
				String[] query = new String[4];
				switch(b_advance.getText()) {
					case "Phrases": 
						// Making phrases search editable 
						controller.optionSelected(1);
						// Transfer the query in the other scene to display it
						query[0]=tf_search.getText().toLowerCase();
						controller.transferQuery(query,3);
						break;
					case "Vector":
						// "Vector search" editable
						controller.optionSelected(1);
						controller.tb_vector.setSelected(true);
						query[0]=tf_search.getText().toLowerCase();
						controller.transferQuery(query,1);	
						break;
					case "Boolean":
						// "Boolean search" editable
						controller.optionSelected(3);
						controller.tb_boolean.setSelected(true);
						query[0]=tf_search.getText().toLowerCase();
						controller.transferQuery(query,2);
						break;
					case "Top-K":
						// "Boolean search" editable
						controller.optionSelected(4);
						controller.tb_TopK.setSelected(true);
						query[0]=tf_search.getText();
						query[1]=tf_k.getText();
						
						try {
							int i = Integer.valueOf(query[1]);
							controller.transferQuery(query,5);
						}
						catch(NumberFormatException e) {
							flag=false;
							errorLabel.setVisible(true);
							errorLabel.setText("You must write a number.");
						}
						break;
					case "By Fields":
							// "Field search" editable
							controller.optionSelected(2);	
							// Setting selection for the current toggle button
							controller.tb_field.setSelected(true);
							query[0]=tf_title.getText().toLowerCase();
							query[1]=tf_person.getText().toLowerCase();
							query[2]=tf_place.getText().toLowerCase();
							query[3]=tf_content.getText().toLowerCase();
							fields=tf_title.getText().isBlank()&&tf_person.getText().isBlank()&&tf_place.getText().isBlank()&&tf_content.getText().isBlank();
							if(!fields)
								controller.transferQuery(query,4);
							else {
								errorLabel.setVisible(true);
								errorLabel.setText("You must search at least in one field.");
								flag=false;
							}
						break;
				}
				if(flag) {
					Stage searchStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					searchStage.setScene(searchScene);
					searchStage.show();
				}
				
			}else{
				// Error message shows up 
				errorLabel.setVisible(true);
				errorLabel.setText("You must search something.");
			}
		}
	}
	
	@FXML private void settingsHyperlink(MouseEvent event) throws IOException {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Settings.fxml"));
			Parent settingPage;
			settingPage = loader.load();
			Scene settingScene = new Scene(settingPage);	
		    SettingsController settingsController = loader.getController();
		    settingsController.showDocsInIndexer();
			Stage settingStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			settingStage.setScene(settingScene);
			settingStage.show();
	}
	
	@FXML private void advanceButton(ActionEvent event) {
		switch(b_advance.getText()) {
			case "Phrases": 
				b_advance.setText("Vector");
				tf_search.setPromptText("Search with Vector Model");
				tf_search.setText("");
				break;
			case "Vector":
				b_advance.setText("By Fields");
				tf_search.setVisible(false);
				tf_title.setVisible(true);
				tf_person.setVisible(true);
				tf_place.setVisible(true);
				tf_content.setVisible(true);
				tf_search.setText("");
				break;
			case "By Fields":
				tf_search.setVisible(true);
				tf_title.setVisible(false);
				tf_person.setVisible(false);
				tf_place.setVisible(false);
				tf_content.setVisible(false);
				tf_search.setText("");
				b_advance.setText("Boolean");
				tf_search.setPromptText("Search with Boolean Model");
				break;
			case "Boolean":
				tf_title.setText("");
				tf_person.setText("");
				tf_place.setText("");
				tf_content.setText("");
				tf_k.setVisible(true);
				b_advance.setText("Top-K");
				tf_search.setPromptText("Find the Top-K articles");
				break;
			case "Top-K":
				tf_k.setVisible(false);
				tf_k.setText("");
				tf_search.setText("");
				b_advance.setText("Phrases");
				tf_search.setPromptText("Search something");
				break;
		}	
	}
	
}
