package task1;

/* Name: Shrijit Pillai
 * Username: pillaish
 */

import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document; 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;

public class EvaluateReview {
	public static IndexReader reader;
	public static IndexSearcher searcher;
	public static Analyzer analyzer;
	public static QueryParser parser;
	public static String curDir;
	public static HashMap<String, String> hashEval;
	static int truePos;
	static int relevantDocs;
	static int predictedDocs;
	
	final static int businessReviewCountLimit = 5;
	static HashMap<String, Integer> hashBusinessReviewCount;
	
	public static void initSetUp() throws IOException{
	    curDir = System.getProperty("user.dir");  
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(curDir+"\\search_training.json")));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer();
		parser = new QueryParser("REVIEW", analyzer);	
		hashEval = new HashMap<String, String>();
		hashBusinessReviewCount = new HashMap<String, Integer>();
	}
	
	
	// Below method concatenates the reviews for each business id...
	// For each business id, maximum of 5 reviews are concatenated to obtain a good query.. 
	static void buildTestReview(Object obj){
		JSONObject jObject = (JSONObject)obj;
		
		String business_id = jObject.get("business_id") + "";
		String review_id = jObject.get("text") + "";
		
		if(TrainReview.hashReview.containsKey(business_id)){
			if(hashBusinessReviewCount.get(business_id) <= businessReviewCountLimit){
				TrainReview.hashReview.put(business_id, TrainReview.hashReview.get(business_id)+" "+review_id);
			}
			hashBusinessReviewCount.put(business_id, hashBusinessReviewCount.get(business_id)+1);
		   
		}
		else{
			hashBusinessReviewCount.put(business_id, 1);
			TrainReview.hashReview.put(business_id, review_id);
		}
	}
    
    // Below method retrieves the top predicted categories based on the similarity algorithm used...
    public static void evalQuery(String queryStr, FileWriter fw, int n) throws Exception{
    	try{
	    	BooleanQuery.setMaxClauseCount(queryStr.length());
	    	Query query = parser.parse(QueryParser.escape(queryStr));
	    	TopDocs results = searcher.search(query, n);
	    	String category = "";
	    	
			ScoreDoc[] hits = results.scoreDocs;
			
			if(fw!=null)
			  fw.write("---Predicted---\n");
			
			for(int i=0;i<hits.length;i++){
			   Document doc=searcher.doc(hits[i].doc);
			   
			   category = doc.get("CATEGORY")+"";
			   
			   if(fw!=null)
			     fw.write(category+" - "+hits[i].score+",   ");
			   
			   hashEval.put(category, category);
				   
			}
			
			if(fw!=null)
			  fw.write("\n");
	    }
    	catch(Exception e){}   
		
    }
    
    // Below method computes the list of predicted and relevant categories by calling the above evalQuery method
    static void evalAlgos(String algo, int noTopDocs) throws Exception{
    	String query = "";
    	int count = 0;
    	List<String> list_category = new ArrayList<String>();
    	FileWriter fw = new FileWriter(curDir+"\\output.txt");
    	
    	String fileName = curDir+"\\corpus1\\review_testing.json";
		File file = new File(fileName);
		
		Task1IR.parseJsonFile(file, 1, null);
		
		if(algo.equals("D"))
    	    searcher.setSimilarity(new DefaultSimilarity());
		else if(algo.equals("B"))
			searcher.setSimilarity(new BM25Similarity());
		else if(algo.equals("L"))
			searcher.setSimilarity(new LMDirichletSimilarity());
    	
    	Set<String> set = TrainReview.hashReview.keySet();
    	Iterator<String> it = set.iterator();
    	
    	while(it.hasNext()){
    		String bus_id = it.next();
    		query = TrainReview.hashReview.get(bus_id);
    		evalQuery(query, fw, noTopDocs);
    		
    		fw.write("-----Actual----\n");
    		list_category = TrainReview.hashCategory.get(bus_id);
    		for(String cat : list_category){
    			if(hashEval.containsKey(cat)){
    				count++;
    				break;
    			}
    		}
    		
    		
    		for(String cat : list_category){
    			if(hashEval.containsKey(cat))
    				truePos++;
    			
    			fw.write(cat+",   ");
    		}
    		
    		predictedDocs += hashEval.size();
    		relevantDocs += list_category.size();
    		hashEval.clear();
    		
    		fw.write("\n\n");
    	}
    	
    	float accuracy = (float)count/TrainReview.hashReview.size();
    	System.out.println("At least 1 True Positive: "+accuracy);
    	fw.write("At least 1 True Positive: "+accuracy);
    	
    	fw.close();
    	
     }
	
}

