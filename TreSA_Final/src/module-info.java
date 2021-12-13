module TreSA_Final {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires lucene.core;
	requires lucene.queryparser;
	
	opens application to javafx.graphics, javafx.fxml;
}
