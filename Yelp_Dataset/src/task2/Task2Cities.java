package task2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

// Implementation class
public class Task2Cities {
	static List<String> listCity;
	
	public static void init() throws Exception{
		listCity = new ArrayList<String>();
		
		CitiesTrainModel.buildSetup();
		RecommendItems.init();
		CitiesTestModel.init();
		RecommendItems.setupNegAdjectives();
		modifyNlpConfigFile();
		
		listCity.add("Madison");
		listCity.add("Phoenix");
		listCity.add("LasVegas");
	}
	
	public static void resetHashMaps(){
		CitiesTrainModel.hashBusiness.clear();
		CitiesTrainModel.hashBusinessReview.clear();
		XMLParser.hashOpinionList.clear();
		XMLParser.hashFeatureFreq.clear();
		RecommendItems.getHashRecBusList().clear();
		RecommendItems.getHashNonRecBusList().clear();
	}
	
	public static void clearLists(){
		CitiesTestModel.listFeatureFreq.clear();
		CitiesTestModel.listNonRecBus.clear();
		CitiesTestModel.listRecBus.clear();
		CitiesTestModel.listBusinessRating.clear();
	}
	
	// Below method modifies the config file of the Stanford Core NLP. Only the required annotators are used.
	// This speeds up the generation of the output XML file by the parser. 
	public static void modifyNlpConfigFile() throws IOException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+CitiesTrainModel.getNlpDir()+"\\"+CitiesTrainModel.getNlpConfigFile());
		fw.write("annotators = tokenize, ssplit, pos");
		fw.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void saveCityInfo(){
		CitiesTestModel.listFeatureFreq.add((HashMap<String, Integer>) XMLParser.hashFeatureFreq.clone());
		CitiesTestModel.listNonRecBus.add((HashMap<String, Set<String>>) RecommendItems.getHashNonRecBusList().clone());
		CitiesTestModel.listRecBus.add((HashMap<String, Set<String>>) RecommendItems.getHashRecBusList().clone());
		CitiesTestModel.listBusinessRating.add((HashMap<String, Float>) CitiesTrainModel.hashBusiness.clone());
	}
	
	public static void displayRecFeatures(FileWriter fw) throws IOException{
		List<FeatureObj> set = CitiesTestModel.listTopFeatureObj;
		
		for(FeatureObj f : set){
			fw.write(f.feature+"\n");
			System.out.println(f.feature);
		}
	}
	
	public static void main(String args[]) throws Exception{
		
		System.out.println("Initializing Training Model...");
		init();
		
		for(String city : listCity){
		  FileWriter fw = new FileWriter(CitiesTrainModel.curDir+"\\"+city+"_TopFeatures.txt");
	      CitiesTrainModel.extractRecords(city+".json", 4, null);
		  CitiesTrainModel.buildFeatureList();
		  
		  saveCityInfo();
		
		  CitiesTestModel.getTopFeatures();
		  displayRecFeatures(fw);
		
		  clearLists();
		  fw.close();
		}
		
		// After getting the top features, the features were re-ranked using the formula discussed in the slides
		// The re-ranking and the evaluation is done offline
		
	}
}

