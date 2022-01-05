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
	private String[] splittedQuery;
	private Boolean isBooleanModel;
	
	public DocumentFromSearch(String path, String query,String fieldType) throws IOException{
		isBooleanModel=false;
		if(query.contains("||")||query.contains("&&")) {
			 splittedQuery = query.split(" ");
			 isBooleanModel=true;
		}
		
		Boolean flag = (fieldType.toLowerCase().contains("content"))?true:false;
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
//				if(isBooleanModel) {
//					for(int i=0;i<splittedQuery.length;i++) {
//						if(line.toLowerCase().contains(splittedQuery[i].toLowerCase())) {
//							System.out.println(line+"hhh"+splittedQuery[i]);
//							appearance.add(line);
//							appearanceType.add(LuceneConstants.TITLE);
//						}
//					}
//				}else {
					if(line.toLowerCase().contains(query.toLowerCase())) {
						System.out.println("appearanceTITLE:"+line+" "+query);
						appearance.add(line);
						appearanceType.add(LuceneConstants.TITLE);
						System.out.println(appearance.size()+"index "+appearance.get(0));
						System.out.println(appearanceType.size()+"index "+appearanceType.get(0));
					}		
		//		}
				title = title.concat(line);
			}else if(line.contains("<PLACES>")) {
				line=line.replace("<PLACES>","");
				line=line.replace("</PLACES>","");
				if(line.isBlank())
					place = place.concat("Unknown");
				else {
					place = place.concat(line);
//					if(isBooleanModel) {
//						for(int i=0;i<splittedQuery.length;i++) {
//							if(line.toLowerCase().contains(splittedQuery[i].toLowerCase())) {
//								System.out.println(line+"hhh"+splittedQuery[i]);
//								appearance.add(line);
//								appearanceType.add(LuceneConstants.PLACES);
//							}
//						}
//					}else {
						if(line.toLowerCase().contains(query.toLowerCase())) {
							System.out.println("appearancePLACES:"+line+" "+query);
							appearance.add(line);
							appearanceType.add(LuceneConstants.PLACES);
							
						}	
				//	}
				}
			}else if(line.contains("<PEOPLE>")) {
				line=line.replace("<PEOPLE>","");
				line=line.replace("</PEOPLE>","");
				if(line.isBlank())
					person = person.concat("Unknown");
				else {
					person = person.concat(line);
//					if(isBooleanModel) {
//						for(int i=0;i<splittedQuery.length;i++) {
//							
//							if(line.toLowerCase().contains(splittedQuery[i].toLowerCase())) {
//								System.out.println(line+"hhh"+splittedQuery[i]);
//								appearance.add(line);
//								appearanceType.add(LuceneConstants.PEOPLE);
//							}
//						}
//					}else{
						if(line.toLowerCase().contains(query.toLowerCase())) {
							System.out.println("appearancePEOPLE:"+line+" "+query);
							appearance.add(line);
							appearanceType.add(LuceneConstants.PEOPLE);
						}
				//	}	
				}
			}else if(line.contains("<BODY>")) {
				line=line.replace("<BODY>","");
				content = content.concat(line+"\n");
				if(isBooleanModel) {
					for(int i=0;i<splittedQuery.length;i++) {
						if(line.toLowerCase().contains(splittedQuery[i].toLowerCase())) {
							System.out.println("appearanceCONTENTS:"+line+" "+query);
							appearance.add(line);
							appearanceType.add((flag)?LuceneConstants.CONTENTS:LuceneConstants.BODY);
						}
					}
				}else {
					if(line.toLowerCase().contains(query.toLowerCase())) {
						System.out.println("appearanceCONETNTS:"+line+" "+query);
						appearance.add(line);
						appearanceType.add((flag)?LuceneConstants.CONTENTS:LuceneConstants.BODY);
					}
			}
					
				while((line = reader.readLine()) != null) {
					if(line.contains("</BODY>")) {
						line=line.replace("</BODY>","");
					}
					content = content.concat(line+"\n");
					if(isBooleanModel) {
						for(int i=0;i<splittedQuery.length;i++) {
							if(line.toLowerCase().contains(splittedQuery[i].toLowerCase())) {
								appearance.add(line);
								appearanceType.add((flag)?LuceneConstants.CONTENTS:LuceneConstants.BODY);
							}
						}
					}else {
						if(line.toLowerCase().contains(query.toLowerCase())) {
							appearance.add(line);
							appearanceType.add((flag)?LuceneConstants.CONTENTS:LuceneConstants.BODY);
						}
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
