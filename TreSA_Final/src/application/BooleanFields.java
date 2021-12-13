package application;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class BooleanFields {
	private Button logicalButton;
	private TextField ta1;
	private TextField ta2;
	
	public BooleanFields() {
		logicalButton = new Button();
		logicalButton.setText("&&");
		logicalButton.setPrefWidth(40);
		ta1 = new TextField();
		ta1.setPrefWidth(92);
		ta2= new TextField();
		ta2.setPrefWidth(92);
		
		logicalButton.setOnAction((e)->{
			String buttonName = logicalButton.getText();
			if(buttonName.equals("&&"))
				setLogicalButton("||");
			else
				setLogicalButton("&&");
		});
	}
	public Button getLogicalButton() {
		return logicalButton;
	}
	public void setLogicalButton(String buttonName) {
		logicalButton.setText(buttonName);;
	}
	public TextField getTextField1() {
		return ta1;
	}
	public TextField getTextField2() {
		return ta2;
	}
	
}
