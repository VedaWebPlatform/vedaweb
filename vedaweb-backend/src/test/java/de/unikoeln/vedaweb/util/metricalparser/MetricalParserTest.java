package de.unikoeln.vedaweb.util.metricalparser;

import static org.junit.Assert.*;
import org.junit.Test;


public class MetricalParserTest {

	@Test
	public void testParsing() {
		String input = "yahvā́ iva prá vayā́m ujjíhānāḥ";
		String expected = "—— ◡— ◡ ◡— —◡——";
		String actual = MetricalAnalysis.parse(input, "—", "◡");
//		System.out.println("[" + input + "] -> [" + actual + 
//				"] (should be [" + expected + "])");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParsing2() {
		String input = "kr̥tam";
		String expected = "◡—";
		String actual = MetricalAnalysis.parse(input, "—", "◡");
//		System.out.println("[" + input + "] -> [" + actual + 
//				"] (should be [" + expected + "])");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParsing3() {
		String input = "górabhasam ádribhir vaatā́pyam";
		String expected = "—◡◡◡ —◡— ◡◡——";
		String actual = MetricalAnalysis.parse(input, "—", "◡");
//		System.out.println("[" + input + "] -> [" + actual + 
//				"] (should be [" + expected + "])");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParsing4() {
		String input = "ví ā́śāḥ párvatānaam";
		String expected = "◡ —— —◡—◡—";
		String actual = MetricalAnalysis.parse(input, "—", "◡");
//		System.out.println("[" + input + "] -> [" + actual + 
//				"] (should be [" + expected + "])");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParsing5() {
		assertEquals(
			5,
			MetricalAnalysis.analyze("agním īḷe puróhitaṁ")
				.getMetricalPosition("puróhitaṁ")
		);
	}
	
	@Test
	public void testParsing6() {
		assertEquals(
			3,
			MetricalAnalysis.analyze("agním īḷe puróhitaṁ")
				.getMetricalPosition(1)
		);
	}
	

}
