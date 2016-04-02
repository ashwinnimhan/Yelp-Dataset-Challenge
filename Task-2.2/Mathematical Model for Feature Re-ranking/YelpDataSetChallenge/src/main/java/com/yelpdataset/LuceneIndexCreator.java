package com.yelpdataset;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.DBCollection;
import com.mongodb.MapReduceCommand;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * This class creates Lucene index of reviews for every city. 
 * @author Anwar Shaikh
 *
 */
public class LuceneIndexCreator {

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

				LuceneWriter.intialize(city);
				MongoDatabase cityDB = mongoClient.getDatabase(dBName);
				MongoCollection<Document> cityReviews = cityDB.getCollection("Reviews");
				MongoCursor<Document> cursorReviews = cityReviews.find().iterator();
				while(cursorReviews.hasNext())
				{
					Document reviewDoc = cursorReviews.next();
					String review_id = reviewDoc.getString("review_id");
					String review_text = reviewDoc.getString("text");

					LuceneWriter.writeToLuceneIndex(review_id, review_text);
				}

				LuceneWriter.end();
				System.out.println("Done-" + city);


			}

			mongoClient.close();

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}		

}

