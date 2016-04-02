package com.yelpdataset;

import static com.mongodb.client.model.Filters.nin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.nin;

/**
 * This class separates the Yelp data (only restaurant) according to different cities. That is it creates MongoDB 
 * collections of Users, Tips, Businesses for each city separately.
 * 
 * @author Anwar Shaikh
 *
 */
public class CityWiseDataSeparator {

	public static void run()
	{


		try 
		{

			// TODO Auto-generated method stub
			MongoClient mongoClient = new MongoClient();
			MongoDatabase yelpDB = mongoClient.getDatabase("YelpData");
			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> reviews = yelpDB.getCollection("Reviews");
			final MongoCollection<Document> tips = yelpDB.getCollection("Tips");
			final MongoCollection<Document> business = restaurantDB.getCollection("Business");
			final MongoCollection<Document> users = yelpDB.getCollection("Users");
			final MongoCollection<Document> businessLocation = restaurantDB.getCollection("BusinessLocation");

			MongoCursor<String> cursorCities = businessLocation.distinct("city", String.class).iterator();
			while(cursorCities.hasNext())
			{
				String city = cursorCities.next();
				String dBName = city;
				dBName = dBName.replace(" ", "_");

				MongoDatabase cityDB = mongoClient.getDatabase(dBName);
				
				final MongoCollection<Document> cityReviews = cityDB.getCollection("Reviews");
				final MongoCollection<Document> cityTips = cityDB.getCollection("Tips");
				final MongoCollection<Document> cityUsers = cityDB.getCollection("Users");
				
				MongoCursor<String> cityUsersList = cityReviews.distinct("user_id", String.class).iterator();
				
				List<String> lstCityUserIds = new ArrayList<String>();
				while(cityUsersList.hasNext())
				{
					String business_id = cityUsersList.next();
					lstCityUserIds.add(business_id);
				}
				
				MongoCursor<Document> cursorUsers = users.find(in("user_id", lstCityUserIds)).iterator();
				while(cursorUsers.hasNext())
				{
					Document userDoc = cursorUsers.next();
					cityUsers.insertOne(userDoc);	
				}

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
