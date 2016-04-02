package task2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SentimentAnalyzer {
	
	// Below method calls the CoreNLP parser to generate the output .xml file
	public static void analyzeSentence(boolean msgYN) throws IOException, InterruptedException{
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", "*", "-Xmx2g", "edu.stanford.nlp.pipeline.StanfordCoreNLP", 
                                               "-props", "config.properties", "-file", "input.txt");
		
		pb.directory(new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20"));
		Process process = pb.start();

		int errCode = process.waitFor();
		if(errCode!=0)
		   System.out.println("Error Analyzing Sentence...");
		else if(msgYN)
		       System.out.println("Review parsed successfully");
	}
	
	
	// Below method creates the input file to be given to the CoreNLP parser
	public static void createNLPInputFile(String business_id, String type) throws IOException, InterruptedException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt");
		
		if(type.equals("train"))
		   fw.write(TestData.hashTrainReview.get(business_id));
		else
		   fw.write(TestData.hashTestReview.get(business_id));
		fw.close();
	}
	
	
	// Below method displays the Featues and the Opinions
	public static void displayOpinionList(String business_id){
		Set<String> set = XMLParser.hashOpinionList.keySet();
		Iterator<String> it = set.iterator();
		System.out.println("\nBusiness Id: "+business_id);
		
		while(it.hasNext()){
			String feature = it.next();
			
			System.out.print("Feature: "+feature+", Opinion: ");
			
			Set<String> setOpinion = XMLParser.hashOpinionList.get(feature);
			for(String s : setOpinion)
				System.out.print(s+", ");
			System.out.println();
		}
	}
	
	public static void displayRecommendedItems(Set<String> setRecList){
		Iterator<String> it = setRecList.iterator();
		while(it.hasNext()){
			System.out.println("Recommended: "+it.next());
		}
	}
	
	// ** Core Method **
	// Below method creates input file for CoreNLP, sends it to the parser for parsing, parses the output .xml file...
	// and then performs the recommendation by calling RecommendItems.identifySentence method
	public static void performRecommendation() throws Exception{
		Set<String> set = TestData.hashTestReview.keySet();
		Iterator<String> it = set.iterator();
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\config.properties");
		fw.write("annotators = tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		fw.close();
		
		while(it.hasNext()){
			String business_id = it.next();
			createNLPInputFile(business_id, "train");  // creates the input file for NLP parser to get recommended features from training set...
			                                           // This corresponds to 60% of reviews for the given business id
			
			System.out.println("Parsing the review for business id: "+business_id);
			analyzeSentence(false);  // calls the NLP parser to generate the output(input.txt.xml) file
			File file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt.xml");
			XMLParser.parseXML(file, true); // parses the xml file and generates the features and sentiments
			
			displayOpinionList(business_id);
			RecommendItems.identifySentence(XMLParser.words, XMLParser.hashOpinionList, null); // builds list of recommended and non-recommend features
			
			Set<String> setRec = RecommendItems.getSetRec();
			displayRecommendedItems(setRec);
			
			Set<String> tempSetRec = new HashSet<String>(setRec);
			setRec.clear();
			
			System.out.println("------------------------");
			XMLParser.hashOpinionList.clear();
			
            createNLPInputFile(business_id, "test");  // creates the input file for NLP parser to get recommended features from training set...
            										  // This corresponds to 40% of reviews for the given business id
		
			analyzeSentence(false);  // calls the NLP parser to generate the output(input.txt.xml) file
			file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt.xml");
			XMLParser.parseXML(file, true); // parses the xml file and generates the features and sentiments
			
			RecommendItems.identifySentence(XMLParser.words, XMLParser.hashOpinionList, null); // builds list of recommended and non-recommend features
			
			setRec = RecommendItems.getSetRec();
			
			Task2RecommendItems.calcTruePos(setRec, tempSetRec);
			Task2RecommendItems.setPredictedFeatures(setRec.size());
			Task2RecommendItems.setActualFeatures(tempSetRec.size());
			
			setRec.clear();
			tempSetRec.clear();
			
			XMLParser.hashOpinionList.clear();
		}
	}
}
