package de.unikoeln.vedaweb.search;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.unikoeln.vedaweb.services.IndexService;
import de.unikoeln.vedaweb.services.SearchService;


public class VWIndexTest {
	
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private IndexService index;
	private SearchService search;
	
	
	public static void main(String[] args) {
		VWIndexTest vwit = new VWIndexTest();
		vwit.init();
		
		Scanner scan = new Scanner(System.in);
		String input;
		while (!(input = scan.nextLine()).equals("exit")){
			vwit.search(input);
		}
		scan.close();
		
		vwit.shutdown();
	}
	
	
	private void init(){
		System.out.println("[INFO] Initializing...");
		
		search = new SearchService();
		
		index = new IndexService();
		index.init();
		
		if (!index.indexExists()){
			mongoClient = new MongoClient("localhost");
			database = mongoClient.getDatabase("temp");
			collection = database.getCollection("merged");
			index.createIndex(collection.find().iterator());
		} else {
			System.out.println("[INFO] Index already exists.");
		}
		
		System.out.println("[INFO] Initialization done.");
	}
	
	
	private void search(String queryString){
		SearchRequest sr = new SearchRequest(-1, -1);
		
		for (String query : queryString.split("\\s*\\&\\s*")){
			TargetToken tt = new TargetToken();
			for (String queryPart : query.split(" ")){
				String[] queryPartElements = queryPart.split(":");
				tt.addAttribute(queryPartElements[0], queryPartElements[1].matches("\\d+") ? Integer.parseInt(queryPartElements[1]) : queryPartElements[1]);
			}
			sr.addTargetToken(tt);
		}
		
		SearchResults results = search.search(sr);
		System.out.println("[INFO] Final # of results after joining: " + results.size());
		
		for (SearchResult result : results.getResultsList()){
			System.out.println(result);
		}
	}
	
	
	private void shutdown(){
		if (mongoClient != null)
			mongoClient.close();
	}
	

}