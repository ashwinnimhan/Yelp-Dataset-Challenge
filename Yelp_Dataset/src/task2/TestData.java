package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class TestData {
	static String curDir;
	static HashMap<String, String> hashCat;
	static HashMap<String, String> hashBusiness;
	static HashMap<String, String> hashTestReview;
	static HashMap<String, String> hashTrainReview;
	static HashMap<String, List<String>> hashListReview;
	final static String restaurantCategoryFile = "categories.txt";
	
	static void init(){
		curDir = System.getProperty("user.dir");
		hashCat = new HashMap<String, String>();
		hashBusiness = new HashMap<String, String>();
		hashTestReview = new HashMap<String, String>();
		hashTrainReview = new HashMap<String, String>();
		hashListReview = new HashMap<String, List<String>>();
	}
	
	static void buildSetup() throws Exception{
		init();
		buildHashCat();
	}
	
	static void parse(String fileName) throws Exception{
		fileName = curDir+"\\corpus1\\"+fileName; 
		File file = new File(fileName);
		parseJsonFile(file);
	}
	
	//Below method reads from categories.txt and puts the categories in a HashMap
	static void buildHashCat() throws Exception{
		FileReader fr = new FileReader(curDir+"\\corpus1\\"+restaurantCategoryFile);
		BufferedReader br = new BufferedReader(fr);
		String s = "";
		
		while((s=br.readLine())!=null)
			hashCat.put(s, s);
		
		br.close();
		
	}
	
	// Below method reads business json records with review count less than or equal to 5...
	// The algorithm is tested on businesses with less reviews in order to check its prediction quality  
	static void buildHashBusiness(Object obj) throws Exception{
		JSONObject jObject = (JSONObject)obj;
		Integer review_count = Integer.parseInt(jObject.get("review_count")+"");
		String business_id = jObject.get("business_id") + ""; 
		
		if(review_count <= 5){
			JSONArray catObj = (JSONArray)jObject.get("categories");
			for(int i=0;i<catObj.size();i++){
				if(hashCat.containsKey(catObj.get(i))){
					hashBusiness.put(business_id,business_id);
					break;
				}
			}
		
		}
		
	}
	
	static void buildHashTrainTest(){
		Set<String> set = hashListReview.keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			String business_id = it.next();
			List<String> list = hashListReview.get(business_id);
			
			int train_size = list.size()*2/3;
			int count = 0;
			
			for(String s : list){
				if(count<train_size){
			      if(!hashTrainReview.containsKey(business_id))
					 hashTrainReview.put(business_id, s);
			      else
			    	 hashTrainReview.put(business_id, hashTrainReview.get(business_id) + " " + s);
				 }
				else{
				  if(!hashTestReview.containsKey(business_id))
					  hashTestReview.put(business_id, s);
				   else
				      hashTestReview.put(business_id, hashTestReview.get(business_id) + " " + s);
				}
				
				count++;
			}
			
		}
	}
	
	// Below method builds a HashMap with the business as the key and the review as the value for testing purpose
	static void buildTestReview(Object obj) throws Exception{
		JSONObject jObject = (JSONObject)obj;
		String business_id = jObject.get("business_id") + "";
		String review = jObject.get("text") + "";
		
		if(hashBusiness.containsKey(business_id)){
			if(!hashListReview.containsKey(business_id)){
				List<String> list = new ArrayList<String>();
				list.add(review);
				hashListReview.put(business_id, list);
			}
			else{
				List<String> list = hashListReview.get(business_id);
				list.add(review);
				hashListReview.put(business_id, list);
			}
			/*if(!hashTestReview.containsKey(business_id))
			   hashTestReview.put(business_id, review);
			else
			   hashTestReview.put(business_id, hashTestReview.get(business_id)+" "+review);*/
		}
		
	}
	
	static void parseJsonFile(File file) throws Exception{
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		JSONParser parser=new JSONParser();
		String s = "";
		String fileName = file.getName();
		
		while((s=br.readLine())!=null){			
		  try{	
			   Object obj= parser.parse(s);
			   
			   if(fileName.equals("business_training.json"))
			      buildHashBusiness(obj);
			   else if(fileName.equals("review_training.json"))
				       buildTestReview(obj);
			 }
		  
		  catch(Exception e){}
		
		}
		
		br.close();
		
	}
	
}
