package task1;

/* Name: Shrijit Pillai
 * Username: pillaish
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.json.simple.parser.JSONParser;

import task2.CitiesTrainModel;


public class Task1IR {    
	
	// Below method parses the input json file and also calls the required method based on the value of the option argument
	public static void parseJsonFile(File file, int option, FileWriter fw) throws Exception{
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		String s = "";
		
		JSONParser parser=new JSONParser();
		
		while((s=br.readLine())!=null){			
			try{	
			  Object obj= parser.parse(s);
			  
			  switch(option){
			     case 1: EvaluateReview.buildTestReview(obj);
			             break;
			             
			     case 2: TrainReview.buildHashCategory(obj); 
			             break;
			             
			     case 3: if(CitiesTrainModel.isRestaurant(obj))
					        fw.write(s+"\n");
			             break;
			             
			     case 4: CitiesTrainModel.buildHashBusiness(obj);        
			             break;
			             
			     default: System.out.println("Valid option not provided");        
			   }
		    }
			catch(Exception e){}
			
		}
		
		br.close();

	}
	
	// Below method calculates the Evaluation Metrics
	public static void getEvalMetrics(String algo, int noTopDocs) throws IOException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Desktop\\"+algo+"_"+noTopDocs+".txt");
		
		float precision = (float)EvaluateReview.truePos/EvaluateReview.predictedDocs;
		float recall = (float)EvaluateReview.truePos/EvaluateReview.relevantDocs;
		
		fw.write("Precision: "+precision+"\n");
		fw.write("Recall: "+recall+"\n");
		
		System.out.println("Algorithm: "+algo+", Top Categories retrieved: "+noTopDocs);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		
		int beta = 2;
		float f2_measure = (1 + (float)Math.pow(beta,2)) * precision * recall / ((float)Math.pow(beta,2) * precision + recall);
		fw.write("F2 Measure: "+f2_measure);
		fw.close();
		System.out.println("F2 Measure: " + f2_measure);
		
	}
    
    public static void main(String args[]) throws Exception{
    	GenerateIndex.buildSetUp();
    	GenerateIndex.setDirPath("corpus1");
    	GenerateIndex.setFileExt("review_training.json");
    	GenerateIndex.genIndex(GenerateIndex.getDirPath(), "json", new StandardAnalyzer());
    	
    	System.out.println("Review Training Index created...");
    	
		TrainReview.initSetUp();
		TrainReview.buildDataModel();
		
		System.out.println("Data Model created...");
		GenerateIndex.genIndex(GenerateIndex.getDirPath(), "text", new StandardAnalyzer());
		
		System.out.println("Search Index created...");
		TrainReview.hashReview.clear();
		EvaluateReview.initSetUp();
		
		for(int i=3;i<=7;i+=2){
		  EvaluateReview.evalAlgos("D",i);  // Default Similarity with Top 3, 5 and 7 categories
		  getEvalMetrics("D",i);
		  
		  EvaluateReview.evalAlgos("B",i);  // BM25 Similarity with Top 3, 5 and 7 categories
		  getEvalMetrics("B",i);
			
		  EvaluateReview.evalAlgos("L",i);  // LMDirichlet Similarity with Top 3, 5 and 7 categories
		  getEvalMetrics("L",i);
		}
		
		
	}
	
}

