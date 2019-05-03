package de.unikoeln.vedaweb.document;

public class StanzaLocation {
	
	private int book;
	private int hymn;
	private int stanza;
	
	
	public StanzaLocation(int book, int hymn, int stanza) {
		super();
		this.book = book;
		this.hymn = hymn;
		this.stanza = stanza;
	}
	
	public StanzaLocation(String locationString) {
		locationString = normalizeId(locationString);
		if (locationString == null) {
			this.book = 1;
			this.hymn = 1;
			this.stanza = 1;
		} else {
			this.book = Integer.parseInt(locationString.substring(0, 2));
			this.hymn = Integer.parseInt(locationString.substring(2, 5));
			this.stanza = Integer.parseInt(locationString.substring(5));
		}
	}
	
	public int getBook() {
		return book;
	}
	
	public int getHymn() {
		return hymn;
	}
	
	public int getStanza() {
		return stanza;
	}
	
	public void setNextFallbackLocation() {
		if (stanza > 1)
			stanza = 1;
		else if (hymn > 1)
			hymn = 1;
		else
			book = 1;
	}
	
	public static String normalizeId(String id){
		if (id.matches("\\d{7}"))
			return id;
		else if (id.matches("\\D*\\d{2}\\D\\d{3}\\D\\d{2}\\D*"))
			id = id.replaceAll("\\D", "");
		else
			id = constructId(id);
		return id;
	}
	
	public static String constructId(String input){
		String[] digits = input.split("\\D+");
		if (digits.length != 3) return null;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.format("%02d", Integer.parseInt(digits[0])));
		sb.append(String.format("%03d", Integer.parseInt(digits[1])));
		sb.append(String.format("%02d", Integer.parseInt(digits[2])));
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return book + "." + hymn + "." + stanza;
	}
	
}