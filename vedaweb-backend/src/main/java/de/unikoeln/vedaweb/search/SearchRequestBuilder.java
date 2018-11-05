package de.unikoeln.vedaweb.search;

import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import de.unikoeln.vedaweb.util.StringUtils;


public class SearchRequestBuilder {
	
	public static final String AGGREGATE_GRAMMAR_FIELDS = "grammar_fields";
	
	public static final FetchSourceContext FETCH_SOURCE_CONTEXT =
			new FetchSourceContext(true, new String[]{"book", "hymn", "verse", "form", "hymnAddressee", "hymnGroup", "strata"}, Strings.EMPTY_ARRAY);
	
	
	
	public static SearchRequest buildSmartQuery(SearchData searchData){
		SearchRequest searchRequest = new SearchRequest("vedaweb"); 
		searchRequest.types("doc");
		
		SearchSourceBuilder searchSourceBuilder = getCommonSearchSource(searchData);
		
		String searchTerm = StringUtils.normalizeNFC(searchData.getInput());
		String field = searchData.getField();
		
		if (StringUtils.containsAccents(searchTerm) && field.equals("form"))
			field = "form_raw";
		
		//add wildcard query
		searchSourceBuilder.query(new WildcardQueryBuilder(field, searchTerm));

		//Highlighting
		addHighlighting(searchSourceBuilder, "form", "form_raw", "translation");
		
		searchSourceBuilder.fetchSource(FETCH_SOURCE_CONTEXT);
		searchRequest.source(searchSourceBuilder);
		return searchRequest;
	}
	
	
	public static SearchRequest buildGrammarQuery(SearchData searchData){
		SearchRequest searchRequest = new SearchRequest("vedaweb"); 
		searchRequest.types("doc");
		SearchSourceBuilder searchSourceBuilder = getCommonSearchSource(searchData);
		
		//root bool query
		BoolQueryBuilder bool = QueryBuilders.boolQuery();
		
		//add search block queries
		addGrammarBlockQueries(bool, searchData);
		
		//add search scope queries
		if (searchData.getScopes().size() > 0)
			bool.must(getSearchScopesQuery(searchData));
		
		//add search meta queries
		if (searchData.getMeta().size() > 0)
			bool.must(getSearchMetaQuery(searchData));
		
		searchSourceBuilder.query(bool);

		searchSourceBuilder.fetchSource(FETCH_SOURCE_CONTEXT);

		//System.out.println("\n\n" + searchSourceBuilder.toString() + "\n\n");
		searchRequest.source(searchSourceBuilder);
			
		return searchRequest;
	}
	
	
	private static BoolQueryBuilder getSearchMetaQuery(SearchData searchData) {
		BoolQueryBuilder metasQuery = QueryBuilders.boolQuery();
		for (String meta : searchData.getMeta().keySet()) {
			if (searchData.getMeta().get(meta).length == 0) continue;
			BoolQueryBuilder metaQuery = QueryBuilders.boolQuery();
			for (String value : searchData.getMeta().get(meta)) {
				metaQuery.should(QueryBuilders.termQuery(meta, value));
			}
			//add to root meta query
			metasQuery.should(metaQuery);
		}
		return metasQuery;
	}


//	private static void addScopeQueries(BoolQueryBuilder rootQuery, List<SearchScope> scopes){
//		if (scopes == null || scopes.size() == 0) return;
//		
//		//TODO temp: only one range!
//		SearchScope scope = scopes.get(0);
//		
//		//construct book scope query
//		RangeQueryBuilder bookRange = QueryBuilders.rangeQuery("book");
//		if (scope.fromBook > 0) bookRange.gte(scope.fromBook);
//		if (scope.toBook > 0) bookRange.lte(scope.toBook);
//		rootQuery.must(bookRange);
//		
//		//construct book scope query
//		RangeQueryBuilder hymnRange = QueryBuilders.rangeQuery("hymn");
//		if (scope.fromHymn > 0) hymnRange.gte(scope.fromHymn);
//		if (scope.toHymn > 0) hymnRange.lte(scope.toHymn);
//		rootQuery.must(hymnRange);
//	}
	
	
	private static void addGrammarBlockQueries(BoolQueryBuilder rootQuery, SearchData searchData){
		//iterate search blocks
		for (Map<String, Object> block : searchData.getBlocks()){
			//construct bool query for each block
			BoolQueryBuilder bool = QueryBuilders.boolQuery();
			
			//remove empty fields
			block.values().stream().filter(v -> v != null && v.toString().length() > 0);
			
			//trim values
			block.values().forEach(v -> v = v.toString().trim());
			
			//add queries for block data
			for (String key : block.keySet()){
				
				//unimplemented
				if (key.equalsIgnoreCase("distance"))
					continue; //TEMP DEV
				
				//if fields="form", also search in "lemma"-field
				if (key.equals("form")) {
					bool.must(getMultiFieldBoolQuery(
							StringUtils.removeUnicodeAccents((String)block.get(key), true),
							false,
							"tokens.form",
							"tokens.lemma"
					));
				} else {
					//...otherwise, add a simple term query
					bool.must(QueryBuilders.termQuery("tokens.grammar." + key, block.get(key)));
				}
			}
			
			//wrap in nested query, add to root query
			rootQuery.must(QueryBuilders.nestedQuery("tokens", bool, ScoreMode.Avg));
		}
	}
	
	
	private static BoolQueryBuilder getMultiFieldBoolQuery(String query, boolean must, String ... fields) {
		BoolQueryBuilder bool = QueryBuilders.boolQuery();
		
		//TODO: fuzzy or nah?
		for (String field : fields) {
			if (must) {
				bool.must(QueryBuilders.matchQuery(field, query));
			} else {
				bool.should(QueryBuilders.matchQuery(field, query));
			}
		}
		
		return bool;
	}
	
	
	private static void addHighlighting(SearchSourceBuilder searchSourceBuilder, String... fields){
		HighlightBuilder highlightBuilder = new HighlightBuilder(); 
		
		for (String field : fields){
			HighlightBuilder.Field highlightField =
			        new HighlightBuilder.Field(field); 
			highlightField.highlighterType("unified");  
			highlightBuilder.field(highlightField);
		}
		  
		searchSourceBuilder.highlighter(highlightBuilder);
	}
	
	
	private static BoolQueryBuilder getSearchScopesQuery(SearchData searchData) {
		BoolQueryBuilder scopesQuery = QueryBuilders.boolQuery();
		for (SearchScope scope : searchData.getScopes()) {
			BoolQueryBuilder scopeQuery = QueryBuilders.boolQuery();
			//construct and add query for book range
			if (scope.getFromBook() > 0 || scope.getToBook() > 0) {
				RangeQueryBuilder bookRange = QueryBuilders.rangeQuery("book");
				if (scope.getFromBook() > 0)
					bookRange.gte(scope.getFromBook());
				if (scope.getToBook() > 0)
					bookRange.lte(scope.getToBook());
				scopeQuery.must(bookRange);
			}
			//construct and add query for hymn range
			if (scope.getFromHymn() > 0 || scope.getToHymn() > 0) {
				RangeQueryBuilder hymnRange = QueryBuilders.rangeQuery("hymn");
				if (scope.getFromHymn() > 0)
					hymnRange.gte(scope.getFromHymn());
				if (scope.getToHymn() > 0)
					hymnRange.lte(scope.getToHymn());
				scopeQuery.must(hymnRange);
			}
			//add to root scopes query
			scopesQuery.should(scopeQuery);
		}
		return scopesQuery;
	}
	
	
	public static SearchRequest buildAggregationFor(String grammarField){
		SearchRequest searchRequest = new SearchRequest("vedaweb"); 
		searchRequest.types("doc");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		
		//match all
		MatchAllQueryBuilder match = QueryBuilders.matchAllQuery();
		
		//aggregation request
		TermsAggregationBuilder terms
			= AggregationBuilders.terms("agg")
				.field("tokens." + grammarField)
				.size(100);
		NestedAggregationBuilder nestedAgg
			= AggregationBuilders.nested("tokens", "tokens")
				.subAggregation(terms);
		
		searchSourceBuilder.query(match);
		searchSourceBuilder.aggregation(nestedAgg);
		searchSourceBuilder.size(0);
		//System.out.println("\n\n" + searchSourceBuilder.toString() + "\n\n");
		searchRequest.source(searchSourceBuilder);
		return searchRequest;
	}
	
	
	private static SearchSourceBuilder getCommonSearchSource(SearchData searchData) {
		SearchSourceBuilder source = new SearchSourceBuilder();
		source.from(searchData.getFrom() >= 0 ? searchData.getFrom() : 0);
		source.size(searchData.getSize() >= 0 ? searchData.getSize() : 0);
		return source;
	}
	
	
}
