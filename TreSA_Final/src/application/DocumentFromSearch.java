package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DocumentFromSearch {
	private String title;
	private String person;
	private String place;
	private String content;
	private ArrayList<String> appearance;
	private ArrayList<String> appearanceType;
	
	public DocumentFromSearch(String path, String query) throws IOException{
		appearance = new ArrayList<>();
		appearanceType = new ArrayList<>();
		title=person=place=content="";
		File file = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if(line.contains("<TITLE>")) {
				line=line.replace("<TITLE>","");
				line=line.replace("</TITLE>","");
				if(line.toLowerCase().contains(query.toLowerCase())) {
					appearance.add(line);
					appearanceType.add("title");
				}		
				title = title.concat(line);
			}else if(line.contains("<PLACES>")) {
				line=line.replace("<PLACES>","");
				line=line.replace("</PLACES>","");
				if(line.isBlank())
					place = place.concat("Unknown");
				else {
					place = place.concat(line);
					if(line.toLowerCase().contains(query.toLowerCase())) {
						appearance.add(line);
						appearanceType.add("place");
					}	
				}
			}else if(line.contains("<PEOPLE>")) {
				line=line.replace("<PEOPLE>","");
				line=line.replace("</PEOPLE>","");
				if(line.isBlank())
					person = person.concat("Unknown");
				else {
					person = person.concat(line);
					if(line.toLowerCase().contains(query.toLowerCase())) {
						appearance.add(line);
						appearanceType.add("person");
					}
						
				}
			}else if(line.contains("<BODY>")) {
				line=line.replace("<BODY>","");
				content = content.concat(line+"\n");
				if(line.toLowerCase().contains(query.toLowerCase())) {
					appearance.add(line);
					appearanceType.add("body");
				}	
				while((line = reader.readLine()) != null) {
					if(line.contains("</BODY>")) {
						line=line.replace("</BODY>","");
					}
					content = content.concat(line+"\n");
					if(line.toLowerCase().contains(query.toLowerCase())) {
						appearance.add(line);
						appearanceType.add("body");
					}		
				}
				break;
			}
		}
	}
	public String getQueryAppereanceLine(String type){
		int num = appearanceType.indexOf(type);
		return appearance.get(num);
	}
	public String getTitle() {
		return title;
	}
	public String getPerson() {
		return person;
	}
	public String getPlace() {
		return place;
	}
	public String getContent() {
		return content;
	}
}
