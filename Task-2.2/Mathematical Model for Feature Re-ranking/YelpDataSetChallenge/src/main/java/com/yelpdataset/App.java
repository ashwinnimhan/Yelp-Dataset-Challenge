package com.yelpdataset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;




/**
 * This is the main entry point of the second program. This class is responsible for necessary instantiation and
 * execution of the other working classes in the program.
 *
 */
public class App 
{
	/**
	 * This is main method which executes other classes in sequential manner of as per different results required.
	 * @param args
	 */
    public static void main( String[] args )
    {
    	//BusinessCityRetriever.run();
    	//CityWiseDataSeparator.run();
    	//LuceneIndexCreator.run();
    	//TopFrequencyWordsRetiever.run();
    	//UserStatsExctractor.run();
    	//ReviewStatsExtractor.run();
    	
        WordScoreCalculator.run();
    }
}
