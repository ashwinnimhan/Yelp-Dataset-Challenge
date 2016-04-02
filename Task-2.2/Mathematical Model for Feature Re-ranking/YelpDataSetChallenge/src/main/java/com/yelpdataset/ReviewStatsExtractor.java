package com.yelpdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

/**
 * This class is responsible for extracting out the stats that are required for calculation of Review Weight
 * like Useful Votes, Date, Text Length etc. As extracting and calculating review weight in a single program would 
 * be very computational intensive given unstructured data in mongodb/Lucene. 
 * 
 * @author Anwar Shaikh
 *
 */
public class ReviewStatsExtractor {

	public static void run()
	{


		try 
		{

			// TODO Auto-generated method stub
			MongoClient mongoClient = new MongoClient();
			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> businessLocation = restaurantDB.getCollection("BusinessLocation");

			MongoCursor<String> cursorCities = businessLocation.distinct("city", String.class).iterator();
//			while(cursorCities.hasNext())
//			{
				String city = cursorCities.next();
				String dBName = "Las Vegas";

				dBName = dBName.replace(" ", "_");

				MongoDatabase cityDB = mongoClient.getDatabase(dBName);

				final MongoCollection<Document> cityReview = cityDB.getCollection("Reviews");
				final MongoCollection<Document> cityReviewWeight = cityDB.getCollection("ReviewWeight");
				MongoCursor<Document> cursorCityReviews = cityReview.find().iterator();

				while(cursorCityReviews.hasNext())
				{					
					Document reviewDoc = cursorCityReviews.next();

					String user_id = reviewDoc.getString("user_id");
					String review_id =  reviewDoc.getString("review_id");
					int stars = reviewDoc.getInteger("stars");
					//int textLength = reviewDoc.getString("text").length();
					//int date = Integer.parseInt(reviewDoc.getString("date").replace("-", ""));
					Map<String, Integer> votes = new HashMap<String, Integer>();

					votes = reviewDoc.get("votes", Map.class);
					int usefulVotes = votes.get("useful");
					//int funnyVotes = votes.get("useful");
					//int coolVotes = votes.get("useful");					


					Document userWeightDoc = new Document();
					userWeightDoc.append("review_id", review_id);
					userWeightDoc.append("stars", stars);
					//userWeightDoc.append("text_length", textLength);
					//userWeightDoc.append("date", date);
					userWeightDoc.append("votes_useful", usefulVotes);
					//userWeightDoc.append("votes_funny", funnyVotes);
					//userWeightDoc.append("votes_cool", coolVotes);
					userWeightDoc.append("user_id", user_id);

					cityReviewWeight.insertOne(userWeightDoc);

				}

				System.out.println("Done-" + city);


//			}

			mongoClient.close();

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
