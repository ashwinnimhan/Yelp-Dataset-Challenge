package task2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CitiesTestModel {
	static String curDir;
	static List<HashMap<String, Integer>> listFeatureFreq;
	static List<HashMap<String, Set<String>>> listNonRecBus;
	static List<HashMap<String, Set<String>>> listRecBus;
	static List<HashMap<String, Float>> listBusinessRating;
	static List<FeatureObj> listTopFeatureObj;
	
	public static void init() throws IOException{		
		listFeatureFreq = new ArrayList<HashMap<String, Integer>>();
		listNonRecBus = new ArrayList<HashMap<String, Set<String>>>();
		listRecBus = new ArrayList<HashMap<String, Set<String>>>();
		listBusinessRating = new ArrayList<HashMap<String, Float>>();
		listTopFeatureObj = new ArrayList<FeatureObj>();
	}
	
	// Below method sorts the features according to its frequency(number of times it is mentioned in the review)
	static void getTopFeatures(){
		Set<String> set = listFeatureFreq.get(0).keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			String feature = it.next();
			int freq = listFeatureFreq.get(0).get(feature);
			FeatureObj fo = new FeatureObj(feature, freq);
			listTopFeatureObj.add(fo);
		 }
		
		Collections.sort(listTopFeatureObj, new Comparator<FeatureObj>(){
			@Override
			public int compare(FeatureObj f1, FeatureObj f2){
				if(f1.freq == f2.freq)
					return 0;
				return f2.freq > f1.freq ? 1 : -1; 
			}
		});
			
	}
	
}


class FeatureObj{
	String feature;
	int freq;
	
	FeatureObj(String feature, int freq){
		this.feature = feature;
		this.freq = freq;
	}
}


