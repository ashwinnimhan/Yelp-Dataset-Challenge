package com.yelpdataset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * This is comparator class which for Term Stats
 * @author Anwar Shaikh
 *
 */
class TermStatComparator implements Comparator<TermStats> 
{

	/**
	 * This overidden method from Comparator interface. Which compares the TermStat according to the
	 * term frequency.
	 * 
	 * @param o1 first TermStat object to be compared
	 * @param o2 second TermStat object to be compared
	 * 
	 * @return If o1 is has frequency (i.e word count) greater than, equal to, less than o2 then 
	 * returns +ve, 0 and -ve value respectively.
	 */
	public int compare(TermStats o1, TermStats o2) {
		// TODO Auto-generated method stub
		return (int) (o1.totalTermFreq - o2.totalTermFreq);
	}
	
}
/**
 * This class is responsible for extracting top 1000 highest frequency words from Lucene index for each city.
 * @author Anwar Shaikh
 *
 */
public class TopFrequencyWordsRetiever {

	public static void run()
	{
		try {
			MongoClient mongoClient = new MongoClient();

			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> businessLocation = restaurantDB.getCollection("BusinessLocation");

			MongoCursor<String> cursorCities = businessLocation.distinct("city", String.class).iterator();
			int count = 1;
			while(cursorCities.hasNext())
			{
				String city = cursorCities.next();
				String dBName = city;
				dBName = dBName.replace(" ", "_");

				MongoDatabase cityDB = mongoClient.getDatabase(dBName);
				
				final MongoCollection<Document> reviewWordCount = cityDB.getCollection("ReviewWordListCount");
				
				FSDirectory directory = FSDirectory.open(Paths.get("D:/Data/LuceneIndex/Yelp_Stop3/"+ city));

				IndexReader indexReader = DirectoryReader.open(directory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				List<LeafReaderContext> leafReaderContexts = indexReader.getContext().reader().leaves();
				
				TermStats[] termStatList = HighFreqTerms.getHighFreqTerms(indexReader, 1000, "TEXT", new TermStatComparator());
				
				FileWriter writer = new FileWriter(new File("D:/Top/" + city +".csv"));
				
				
				for(int i = 0; i< termStatList.length; i++)
				{
					TermStats termStat = termStatList[i];
					String termText = termStat.termtext.utf8ToString();
					long termCount = termStat.totalTermFreq;
					long docFreq = termStat.docFreq;
					
					
					writer.write(termText + "," + termCount + "\r\n");
					
					
				}
				
				System.out.println("Done-" + city);
				
				count++;
			}
			mongoClient.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
