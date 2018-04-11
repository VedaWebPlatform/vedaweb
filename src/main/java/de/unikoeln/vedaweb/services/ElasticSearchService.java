package de.unikoeln.vedaweb.services;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.unikoeln.vedaweb.data.Verse;
import de.unikoeln.vedaweb.data.VerseRepository;
import de.unikoeln.vedaweb.search.SearchFormData;
import de.unikoeln.vedaweb.search.SearchRequestBuilder;
import de.unikoeln.vedaweb.search.SearchResult;
import de.unikoeln.vedaweb.search.SearchResults;

@Service
public class ElasticSearchService {

	@Autowired
	private VerseRepository verseRepo;
	
	@Autowired
	private ElasticService elastic;
	
	
	public SearchResults search(SearchFormData formData){
		formData.cleanAndFormatFields();
		System.out.println(formData);
		
		SearchRequest searchRequest = SearchRequestBuilder.buildAdvanced(formData);
		SearchResponse searchResponse = search(searchRequest);
		System.out.println(searchResponse);
		SearchResults searchResults = buildSearchResults(searchResponse);
		
		return searchResults;
	}
	
	
	public String aggregateGrammarField(String field){
		SearchRequest searchRequest = SearchRequestBuilder.buildAggregationFor(field);
		SearchResponse searchResponse = search(searchRequest);
		return searchResponse.toString();
	}
	
	
	private SearchResults buildSearchResults(SearchResponse response){
		SearchResults results = new SearchResults();
		for (SearchHit hit : response.getHits()){
			Optional<Verse> verse = verseRepo.findById(hit.getId());
			if (verse.isPresent())
				results.add(new SearchResult(hit.getScore(), hit.getId(), verse.get()));
		}
		return results;
	}
	
	
	private SearchResponse search(SearchRequest searchRequest){
		try {
			return elastic.client().search(searchRequest);
		} catch (IOException e) {
			System.err.println("[ERROR] Search request error");
			e.printStackTrace();
		}
		return null;
	}
	
	
}
