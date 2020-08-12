package de.unikoeln.vedaweb.search.metricalposition;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.index.query.SimpleQueryStringFlag;

import de.unikoeln.vedaweb.search.CommonSearchRequest;
import de.unikoeln.vedaweb.util.StringUtils;

public class MetricalPositionSearchRequestBuilder {
	

	public static SearchRequest buildSearchRequest(MetricalPositionSearchData searchData) {
		//root bool query
		BoolQueryBuilder bool = QueryBuilders.boolQuery();
		
		//prepare query data
		String field = "metricalPositions.form" + (searchData.isAccents() ? "_raw" : "");
		String input = searchData.getInput().trim();
		input = (searchData.isAccents() ? input : StringUtils.removeVowelAccents(input));
		input = StringUtils.normalizeNFC(input); // normalize NFC
		
		//System.out.println(field + ": " + input);
		
		//query
		SimpleQueryStringBuilder q = QueryBuilders.simpleQueryStringQuery(input)
			.field(field)
			.defaultOperator(Operator.AND)
			.flags(SimpleQueryStringFlag.PREFIX)
			.analyzeWildcard(true);
		
		//add query
		bool.must(QueryBuilders.nestedQuery("metricalPositions", q, ScoreMode.Avg));
		
		//add search scope filters
		if (searchData.getScopes().size() > 0)
			bool.filter(CommonSearchRequest.getSearchScopesQuery(searchData));
		
		//add search meta filters
		if (searchData.getMeta().size() > 0)
			bool.filter(CommonSearchRequest.getSearchMetaQuery(searchData));
		
		return CommonSearchRequest.getSearchRequest(searchData.getIndexName()).source(
				CommonSearchRequest.getSearchSourceBuilder(searchData)
				.query(bool)
				.fetchSource(CommonSearchRequest.getFetchSourceContext())
		);
	}

}