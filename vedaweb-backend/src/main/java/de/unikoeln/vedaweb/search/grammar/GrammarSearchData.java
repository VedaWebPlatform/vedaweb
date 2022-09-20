package de.unikoeln.vedaweb.search.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.unikoeln.vedaweb.search.CommonSearchData;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * POJO that represents the data of a grammar search request
 * 
 * @author bkis
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrammarSearchData extends CommonSearchData {
	
	@JsonProperty("blocks")
	@Schema(description = "List of search blocks")
	private List<Map<String, Object>> blocks;
	
	
	public GrammarSearchData(){
		blocks = new ArrayList<Map<String, Object>>();
	}
	
	
	public List<Map<String, Object>> getBlocks() {
		return blocks;
	}


	public void setBlocks(List<Map<String, Object>> blocks) {
		this.blocks = blocks;
	}


	public void addBlock(Map<String, Object> block) {
		this.blocks.add(block);
	}
	

	@Override
	public String toString() {
		return "GrammarSearch:" + blocks;
	}
	
}
