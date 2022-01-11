package application;

import java.io.File;

public class LuceneConstants {
	 public static final String FILE_NAME = "filename";
	 public static final String FILE_PATH = "filepath";
	 public static final String PLACES = "fileplaces";
	 public static final String PEOPLE = "filepeople";
	 public static final String TITLE = "filetitle";
	 public static final String BODY = "filebody";
	 public static final String CONTENTS = "fieldcontents";
	 
	 private static String absolutePath = new File(".").getAbsolutePath(); 
	 public static final String INDEX_DIR = absolutePath.substring(0,absolutePath.length()-1) + "Index\\";
	 public static final String DATA_DIR = absolutePath.substring(0,absolutePath.length()-1) + "Data\\";
	 public static final int MAX_SEARCH = 2000;
}
