package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RecommendItems {
	private static String curDir;
    private static final String NOT = "not";
    private static final int WORD_DISTANCE = 4;
    private static Set<String> negativeAdj;
    private static HashMap<String, Set<String>> hashRecBusList; 
    private static HashMap<String, Set<String>> hashNonRecBusList;
    private static Set<String> setRec;
    
    static void init() throws FileNotFoundException {
    	curDir = System.getProperty("user.dir");
        negativeAdj = new HashSet<String>();
        hashRecBusList = new HashMap<String, Set<String>>();
        hashNonRecBusList = new HashMap<String, Set<String>>();
        setRec = new HashSet<String>();
        setupNegAdjectives();
    }
    
    public static Set<String> getNegativeAdj(){
    	return negativeAdj;
    }
    
    public static HashMap<String, Set<String>> getHashRecBusList(){
    	return hashRecBusList;
    }
    
    public static HashMap<String, Set<String>> getHashNonRecBusList(){
    	return hashNonRecBusList;
    }
    
    public static Set<String> getSetRec(){
    	return setRec;
    }
    
    // Below method builds a list of negative adjectives by reading from file containing a list of possible negative adjectives 
    public static void setupNegAdjectives() throws FileNotFoundException {

        File f = new File(curDir+"\\corpus1\\NegativeAdjectives.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String s = "";
        
        try {
            while((s=br.readLine())!=null){
            	negativeAdj.add(s.toLowerCase());    	
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

     }

    // ** Core Method ** 
    // Below method builds a list of Recommended and Non-Recommended features by taking into account ...
    // the sentiment of the input sentence.
    public static void identifySentence(List<String> sentence, HashMap<String, Set<String>> nounToAdjectiveMapping, String business_id) {
        int posCount = 0;
        int negCount = 0;
        
        for(Entry<String, Set<String>> entry : nounToAdjectiveMapping.entrySet()) {
            boolean flag = false;
            boolean negative = false;
            Set<String> setOpinion = entry.getValue();
            
            for(String s : setOpinion){
               if(negativeAdj.contains(s))
            	  negative = true;
            	
               int i = 0;
               for(; i < sentence.size(); i++) {                   
                  if(sentence.get(i).equalsIgnoreCase(s))
                     break;
                } 
                
               int start = (i - WORD_DISTANCE) < 0 ? 0 : i - WORD_DISTANCE;
               int end = (i + WORD_DISTANCE) < sentence.size()? (i + WORD_DISTANCE) : sentence.size() - 1;
               while(start <= end) {
                   if(sentence.get(start).equalsIgnoreCase(NOT)) {
                       flag = true;
                       break;
                   }
                   start++;
               }
               
               if((negative && flag) || (!negative && !flag)) {
                   posCount++;
               } else {
                  negCount++;
               }
               
               flag = false;
               negative = false;
                
            }
            	
            if(posCount > negCount){
               if(business_id!=null){
	            	String feature = entry.getKey();
	            	
	            	if(!hashRecBusList.containsKey(feature)){
	  				  Set<String> temp = new HashSet<String>();
	  				  temp.add(business_id);
	  				  hashRecBusList.put(feature, temp);
	  				}
	  				else{
	  				  Set<String> temp = hashRecBusList.get(feature);
	  				  temp.add(business_id);
	  				  hashRecBusList.put(feature, temp);
	  				}
            	}
               else{
            	  //System.out.println("Recommended: " + entry.getKey());
                  setRec.add(entry.getKey());
               }
            }
            else if(business_id!=null){
            	String feature = entry.getKey();
            	
            	if(!hashNonRecBusList.containsKey(feature)){
  				  Set<String> temp = new HashSet<String>();
  				  temp.add(business_id);
  				  hashNonRecBusList.put(feature, temp);
  				}
  				else{
  				  Set<String> temp = hashNonRecBusList.get(feature);
  				  temp.add(business_id);
  				  hashNonRecBusList.put(feature, temp);
  				}
            }
            
            posCount = 0;
            negCount = 0;
        }
    }
    
}