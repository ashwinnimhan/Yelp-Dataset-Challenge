package com.yelpdataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.nin;

/**
 * This class is responsible for fetching the city of business as per the Geo-code (i.e. Latitude and Longitude)
 * for businesses using Google Maps API.
 * 
 * NOTE: Even using the method we were getting around unique 150+ cities as there is no distinction between actual
 * cities and suburbs of city (i.e locale attribute) in the response from Google Map API. Hence we have used clustering
 * algorithm to  group the businesses according to city which provided with correct and desired result.
 *  
 * @author Anwar Shaikh
 *
 */
public class BusinessCityRetriever {

	public static void run()
	{
		try 
		{
			Scanner fileReader = new Scanner(new File("D:/Study/Search/Final Project/RestaurantCategories.csv"));
			List<String> lstRestaurantCategories = new ArrayList<String>();
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				lstRestaurantCategories.add(line);
			}
			fileReader.close();

			// TODO Auto-generated method stub
			MongoClient mongoClient = new MongoClient();
			MongoDatabase yelpDB = mongoClient.getDatabase("YelpData");
			MongoDatabase restaurantDB = mongoClient.getDatabase("YelpRestaurantData");

			final MongoCollection<Document> reviews = yelpDB.getCollection("Reviews");
			final MongoCollection<Document> restaurantBusinessCollection = restaurantDB.getCollection("Business");
			final MongoCollection<Document> r_BusinessCities = restaurantDB.getCollection("BusinessCities");
	
			GeoApiContext apiContext = new GeoApiContext();
			apiContext.setApiKey("AIzaSyAIlwDsNfcb65d163_QIOxszX_xcOF6d-g");
			GeocodingApiRequest req = new GeocodingApiRequest(apiContext);
			
			MongoCursor<String> proceesedRestBIDs = r_BusinessCities.distinct("business_id", String.class).iterator();
			List<String> lstProcessRestBIDs = new ArrayList<String>();
			while(proceesedRestBIDs.hasNext())
			{
				lstProcessRestBIDs.add(proceesedRestBIDs.next());
			}

			MongoCursor<Document> restuarants = restaurantBusinessCollection.find(nin("business_id", lstProcessRestBIDs)).iterator();
			Gson gson = new Gson();
			System.out.println("Count R-PR: " +  restaurantBusinessCollection.count(nin("business_id", lstProcessRestBIDs)));
			while(restuarants.hasNext())
			{
				Document restaurant = restuarants.next();
				double latitude = restaurant.getDouble("latitude");
				double longitude = restaurant.getDouble("longitude");

				LatLng latLng = new LatLng(latitude, longitude);
				req.latlng(latLng);
				GeocodingResult[] results = GeocodingApi.reverseGeocode(apiContext, latLng).await();
				String city = getCity(results);
				System.out.println("City: " + city);
				List<GeocodingResult> resultList = new ArrayList<GeocodingResult>(Arrays.asList(results));
				Document r_doc = new Document();
				r_doc.append("business_id", restaurant.get("business_id"));
				r_doc.append("city", city);
				r_doc.append("gmap_result", gson.toJson(resultList));
				
				r_BusinessCities.insertOne(r_doc);
			}

			mongoClient.close();

		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String getCity(GeocodingResult[] results)
	{
		String city = "";
		
		AddressComponent[] addressComponents = results[0].addressComponents;
		for(AddressComponent addressComponent : addressComponents)
		{
			AddressComponentType[] adTypes = addressComponent.types;
			for(AddressComponentType adType: adTypes)
			{
				if(adType == AddressComponentType.LOCALITY)
				{
					city = addressComponent.longName;
					break;
				}
			}
		}
		
		return city;
	}

}
