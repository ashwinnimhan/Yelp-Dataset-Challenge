package com.yelpdataset;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.google.maps.model.StopDetails;

/**
 * This class is responsible for writing a review with id and text in a particular lucene index. 
 * @author Anwar Shaikh
 *
 */
public class LuceneWriter {
	
	public static Analyzer analyzer;
	public static IndexWriterConfig config;
	public static FSDirectory directory;
	public static IndexWriter indexWriter;
	
	public static void intialize(String city) throws IOException
	{
		List<String> stopWords = new ArrayList<String>();
		Scanner fReader = new Scanner(new File("D:/Study/Search/Final Project/StopWords.csv"));
		while(fReader.hasNextLine())
		{
			stopWords.add(fReader.nextLine());
		}
		fReader.close();
		
		Scanner fReader1 = new Scanner(new File("D:/Adjectives.csv"));
		while(fReader1.hasNextLine())
		{
			stopWords.add(fReader1.nextLine());
		}
		fReader1.close();
		
		CharArraySet charASet = new CharArraySet(stopWords, true);
		analyzer = new EnglishAnalyzer(charASet);
		config = new IndexWriterConfig(analyzer);
		directory = FSDirectory.open(Paths.get("D:/Data/LuceneIndex/Yelp_Stop3/"+ city));
		indexWriter = new IndexWriter(directory, config);
		
	}
	
	public static void writeToLuceneIndex(String review_id, String review_text) throws IOException
	{
		Document document = new Document();
		document.add(new StringField("ID", review_id, Field.Store.YES));
		document.add(new TextField("TEXT", review_text, Field.Store.YES));
		indexWriter.addDocument(document);
	}
	
	public static void end() throws IOException
	{
		indexWriter.forceMerge(1);
		indexWriter.close();
	}

}
