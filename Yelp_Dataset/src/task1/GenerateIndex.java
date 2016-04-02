package task1;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field; 
import org.apache.lucene.document.StringField; 
import org.apache.lucene.document.TextField; 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GenerateIndex {
	private static String curDir;
	private static String dirPath;
	private static String fileExt;
    
	public static void buildSetUp(){
		curDir = System.getProperty("user.dir");  
	}
	
	public static String getDirPath(){
		return dirPath;
	}
	
	public static void setDirPath(String path){
		dirPath = curDir+"\\"+path;
	}
	
	public static String getFileExt(){
		return fileExt;
	}
	
	public static void setFileExt(String ext){
		fileExt = ext;
	}
	
	//Below method build the Lucene Index
	public static void indexDoc(Object obj, IndexWriter writer) throws IOException{
		
		if(!obj.getClass().getName().equals("org.json.simple.JSONObject")){
			Set<String> set = TrainReview.hashReview.keySet();
			Iterator<String> it = set.iterator();
			
			while(it.hasNext()){
				Document luceneDoc = new Document();
				String category = it.next();
				luceneDoc.add(new StringField("CATEGORY", category, Field.Store.YES));
				luceneDoc.add(new TextField("REVIEW", TrainReview.hashReview.get(category)+"",Field.Store.YES));
				writer.addDocument(luceneDoc);
			}
		 }
		else{
		  Document luceneDoc = new Document();
		  JSONObject jObject = (JSONObject)obj;
		  String business_id = jObject.get("business_id")+"";
		  String review = jObject.get("text")+"";
		  String city = jObject.get("city")+"";
			
		  luceneDoc.add(new StringField("BUSID", business_id,Field.Store.YES));
		  luceneDoc.add(new TextField("REVIEW", review,Field.Store.YES));
		  luceneDoc.add(new TextField("CITY", city,Field.Store.YES));
		  writer.addDocument(luceneDoc);	
        }
		
	}
	
	
	// Below method parses the input json file 
	public static void parseJsonAndIndex(File file, IndexWriter writer) throws Exception{
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		String s = "";
		JSONParser parser=new JSONParser();
		
		while((s=br.readLine())!=null){
			try{	
			  Object obj= parser.parse(s);
		      indexDoc(obj, writer);
			}
			catch(Exception e){}
			
		}
		
		br.close();
		
	}
	
	
	//Below method calls the above parse method and also the method which generates the Lucene index
	public static void genIndex(String dirPath, String fileType, Analyzer analyzer)throws Exception{
		File dataDir = new File(dirPath);
		File[] files = dataDir.listFiles();
        String indexPath = "";
        
        if(fileType.equals("json"))
		  indexPath = curDir + "\\review_training.json";
        else if(fileType.equals("text"))
               indexPath = curDir + "\\search_training.json";
		
        Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc); 
		
		if(fileType.equals("json")){
			for(int i=0;i<files.length;i++){
				if(files[i].isFile() && files[i].getName().endsWith(fileExt)){
					//System.out.println("Before parseJsonAndIndex...");
					parseJsonAndIndex(files[i], writer);
				}
             }
		 }
		else if(fileType.equals("text")){
		       indexDoc(TrainReview.hashReview, writer);
		}
		
		writer.forceMerge(1);
		writer.commit();
		writer.close();
		
	 }
  
}
