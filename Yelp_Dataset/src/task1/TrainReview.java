package task1;

/* Name: Shrijit Pillai
 * Username: pillaish
 */

import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document; 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class TrainReview {
	static IndexReader reader;
	static IndexSearcher searcher;
	static Analyzer analyzer;
	static QueryParser parser;
	static String curDir;
	static HashMap<String, List<String>> hashCategory;
	static HashMap<String, String> hashReview;
	static int maxDocs;

	public static void initSetUp() throws IOException{
	    curDir = System.getProperty("user.dir");  
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(curDir+"\\review_training.json")));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer();
		parser = new QueryParser("REVIEW", analyzer);
		hashCategory = new HashMap<String, List<String>>();
		hashReview = new HashMap<String, String>();
		maxDocs = reader.maxDoc();
     }
	
	public static IndexSearcher getIndexSearcher(){
		return searcher;
	}
	
	public static IndexReader getIndexReader(){
		return reader;
	}
	
	public static int getMaxDocs(){
		return maxDocs;
	}
	
	// Below method builds a HashMap with key as the business id and the value as a list of its corresponding categories
	public static void buildHashCategory(Object obj) throws Exception{ 
	    String business_id = "";
	    List<String> list = new ArrayList<String>();
	    
		JSONObject jObject = (JSONObject)obj;
		
		business_id = jObject.get("business_id") + "";
		JSONArray catObj = (JSONArray)jObject.get("categories");
		
		for(int i=0;i<catObj.size();i++)
			list.add(catObj.get(i)+"");
		
		hashCategory.put(business_id, list);
	}
		   
	// Below method builds a HashMap with category as the key and its corresponding review as the value	
	public static void buildHashReview() throws Exception{	
		String business_id = "";
		String review = "";
		
		for(int docId=0;docId<maxDocs;docId++){
			Document doc = searcher.doc(docId);
			
			business_id = doc.get("BUSID");
			review = doc.get("REVIEW");  
			
			List<String> list_category = hashCategory.get(business_id);
			
			for(String category : list_category){	
				if(hashReview.containsKey(category)){
				    hashReview.put(category, hashReview.get(category)+". "+review);  // the reviews for the corresponding category are concatenated 
				    //System.out.println(hashReview.get(category));                   
				 }
				else
				   hashReview.put(category, review);	   
			}
			
		    list_category.clear();
		  }
		
	}
	
	// Below method builds the training model from the input json file
	public static void buildDataModel() throws Exception{
		String fileName = curDir+"\\corpus1\\business_training.json";
		File file = new File(fileName);
		Task1IR.parseJsonFile(file, 2, null);
	   
		buildHashReview();  // Hash of category to its review
		reader.close();
	}
	
	
}

