package de.unikoeln.vedaweb.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SearchData {
	
	@JsonProperty("mode")
	private String mode;
	
	@JsonProperty("accents")
	private boolean accents;
	
	@JsonProperty("regex")
	private boolean regex;
	
	@JsonProperty("input")
	private String input;
	
	@JsonProperty("field")
	private String field;
	
	@JsonProperty("scopes")
	private List<SearchScope> scopes;
	
	@JsonProperty("meta")
	private Map<String, String[]> meta;
	
	@JsonProperty("blocks")
	private List<Map<String, Object>> blocks;
	
	@JsonProperty("from")
	private int from;
	
	@JsonProperty("size")
	private int size;
	
	@JsonProperty("sortBy")
	private String sortBy;
	
	@JsonProperty("sortOrder")
	private String sortOrder;
	
	
	public SearchData(){
		blocks = new ArrayList<Map<String, Object>>();
		sortBy = "_score";
		sortOrder = "descend";
	}
	
	
	public String getMode() {
		return mode;
	}

	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	public boolean isAccents() {
		return accents;
	}


	public void setAccents(boolean accents) {
		this.accents = accents;
	}
	
	
	public boolean isRegex() {
		return regex;
	}


	public void setRegex(boolean regex) {
		this.regex = regex;
	}


	public String getInput() {
		return input;
	}


	public void setInput(String input) {
		this.input = input.trim();
	}
	
	
	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public List<SearchScope> getScopes() {
		return scopes;
	}


	public void setScopes(List<SearchScope> scopes) {
		this.scopes = scopes;
	}
	

	public Map<String, String[]> getMeta() {
		return meta;
	}


	public void setMeta(Map<String, String[]> meta) {
		this.meta = meta;
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
	
	
	public int getFrom() {
		return from;
	}


	public void setFrom(int from) {
		this.from = from;
	}
	

	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}
	
	
	public String getSortBy() {
		return sortBy;
	}


	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	
	public String getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}


	@Override
	public String toString() {
		return "mode:" + mode + (input != null ? " input:" + input : "");
	}
	
}
