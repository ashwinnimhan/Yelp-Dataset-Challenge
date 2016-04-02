package task2;

import java.util.Iterator;
import java.util.Set;

import task1.EvaluateReview;

public class Task2RecommendItems {
	private static int truePos;
	private static float precision;
	private static float recall;
	private static int predictedFeatures;
	private static int actualFeatures;
	
	public static int getTruePos(){
		return truePos;
	}
	
	public static void setPredictedFeatures(int pred_feat){
		predictedFeatures += pred_feat;
	}
	
	public static void setActualFeatures(int actual_feat){
		actualFeatures += actual_feat;
	}
	
	public static void setUpTestData() throws Exception{
		TestData.buildSetup();
		TestData.parse("business_training.json");
		TestData.parse("review_training.json");
		TestData.buildHashTrainTest();
		XMLParser.init();
		EvaluateReview.initSetUp();
		RecommendItems.init();
	}
	
	public static void calcTruePos(Set<String> setTest, Set<String> setTrain){
		Iterator<String> it = setTrain.iterator();
		
		while(it.hasNext()){
			if(setTest.contains(it.next()))
				truePos++;
		}
		
	}
	
	public static void getEvalMetrics(){
		precision = (float) truePos/predictedFeatures;
		recall = (float) truePos/actualFeatures;
		
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall);
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println("Initializing the Recommendation System...");
		setUpTestData();
		
		System.out.println("Commencing Recommendation...");
		SentimentAnalyzer.performRecommendation();
		
		getEvalMetrics();
		
	}
}
