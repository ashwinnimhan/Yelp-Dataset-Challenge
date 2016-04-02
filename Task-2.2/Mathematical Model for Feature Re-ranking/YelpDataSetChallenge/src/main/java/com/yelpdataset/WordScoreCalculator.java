package com.yelpdataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStore.Entry.Attribute;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;


import com.google.maps.model.QueryAutocompletePrediction.Term;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * This class is responsible for calculating the score for each word. It takes word list from NLP as input and 
 * returns the score for each word as output. It uses review weight and relevance score from lucene index for 
 * calculating score for each word.
 *  
 * @author Anwar Shaikh
 *
 */
public class WordScoreCalculator {

	public static void run()
	{
		try 
		{
			MongoClient mongoClient = new MongoClient();

			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> businessLocation = restaurantDB.getCollection("BusinessLocation");


			MongoCursor<String> cursorCities = businessLocation.distinct("city", String.class).iterator();


			while(cursorCities.hasNext())
			{
				String city = cursorCities.next();
				String dBName = city;
				dBName = dBName.replace(" ", "_");
				
				System.out.println("Started City: " + city);

				MongoDatabase cityDB = mongoClient.getDatabase(dBName);
				ArrayList<Integer> ilist = new ArrayList<Integer>();
				ilist.add(5);ilist.add(6);ilist.add(8);
				for(int index : ilist)
				{
					final MongoCollection<Document> reviewWordCount = cityDB.getCollection("ReviewWord_S3Count");
					final MongoCollection<Document> reviewWordScore = cityDB.getCollection("Review_Freq_Word_Score_Point" + index);
					final MongoCollection<Document> reviewWeight = cityDB.getCollection("Review_WT_Point" + index);

					FSDirectory directory = FSDirectory.open(Paths.get("D:/Data/LuceneIndex/Yelp_S_S/"+ city));

					IndexReader indexReader = DirectoryReader.open(directory);
					EnglishAnalyzer analyzer = new EnglishAnalyzer();
					IndexSearcher indexSearcher = new IndexSearcher(indexReader);
					QueryParser queryParser = new QueryParser("TEXT",analyzer); 


					MongoCursor<String> wordCusor = reviewWordCount.distinct("word", String.class).iterator();

					//				Scanner scanner = new Scanner(new File("D:/Study/Search/Final Project/NLP_TOP_WORDS/" + city +".txt"));
					//				while(scanner.hasNext())
					//				{
					while(wordCusor.hasNext())
					{

						String queryString = wordCusor.next();
						Query query = queryParser.parse(QueryParserUtil.escape(queryString));
						ScoreDoc[] scoreDocs = indexSearcher.search(query, 1000).scoreDocs;

						double word_score = 0;
						for(int i = 0; i< scoreDocs.length; i++)
						{
							ScoreDoc doc = scoreDocs[i];
							String review_id = indexSearcher.doc(doc.doc).get("ID");
							double relevance_score = doc.score;
							FindIterable<Document> documents = reviewWeight.find(eq("_id", review_id));
							double review_wt = 0;
							if(documents.first() != null)
							{
								review_wt = documents.first().getDouble("review_weight");
							}

							word_score += review_wt*relevance_score;				
						}

						Document document = new Document();
						document.append("word", queryString);
						document.append("score", word_score);

						reviewWordScore.insertOne(document);
					}

					System.out.println("Done-" + dBName + index);
				}
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
