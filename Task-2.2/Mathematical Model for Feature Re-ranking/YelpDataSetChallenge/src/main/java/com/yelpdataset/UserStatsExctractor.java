package com.yelpdataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * This class is responsible for extracting out the stats that are required for calculation of User Weight
 * like Useful Votes, No. of Elite Years, Review Count, Fans etc. As extracting and calculating review weight in a single program would 
 * be very computational intensive given unstructured data in mongodb. 
 * 
 * @author Anwar Shaikh
 *
 */
public class UserStatsExctractor {
	
	public static void run()
	{


		try 
		{

			// TODO Auto-generated method stub
			MongoClient mongoClient = new MongoClient();
			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> businessLocation = restaurantDB.getCollection("BusinessLocation");

//			MongoCursor<String> cursorCities = businessLocation.distinct("city", String.class).iterator();
//			while(cursorCities.hasNext())
//			{
				String city = "Charlotte";
				String dBName = city;
				dBName = dBName.replace(" ", "_");

				MongoDatabase cityDB = mongoClient.getDatabase(dBName);
				
				final MongoCollection<Document> cityUsers = cityDB.getCollection("Users");
				final MongoCollection<Document> cityUserWeight = cityDB.getCollection("UserWeight");
	
				MongoCursor<Document> cursorCityUsers = cityUsers.find().iterator();
				
				while(cursorCityUsers.hasNext())
				{					
					Document userDoc = cursorCityUsers.next();
					String user_id = userDoc.getString("user_id");
					int fans = userDoc.getInteger("fans");
					int reviewCount = userDoc.getInteger("review_count");
					int eliteYears = userDoc.get("elite", ArrayList.class).size();
					Map<String, Integer> votes = new HashMap<String, Integer>();
					votes = userDoc.get("votes", Map.class);
					int usefulVotes = votes.get("useful");
					int funnyVotes = votes.get("useful");
					int coolVotes = votes.get("useful");
					
					Document userWeightDoc = new Document();
					userWeightDoc.append("user_id", user_id);
					userWeightDoc.append("fans", fans);
					userWeightDoc.append("review_count", reviewCount);
					userWeightDoc.append("elite_years", eliteYears);
					userWeightDoc.append("votes_useful", usefulVotes);
					userWeightDoc.append("votes_funny", funnyVotes);
					userWeightDoc.append("votes_cool", coolVotes);

					cityUserWeight.insertOne(userWeightDoc);

				}
				

				System.out.println("Done-" + city);

			//}


			mongoClient.close();

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
