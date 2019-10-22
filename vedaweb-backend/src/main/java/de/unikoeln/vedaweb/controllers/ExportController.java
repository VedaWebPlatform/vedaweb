package de.unikoeln.vedaweb.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.unikoeln.vedaweb.document.Stanza;
import de.unikoeln.vedaweb.document.StanzaRepository;
import de.unikoeln.vedaweb.document.StanzaXml;
import de.unikoeln.vedaweb.document.StanzaXmlRepository;
import de.unikoeln.vedaweb.export.ExportLayers;
import de.unikoeln.vedaweb.export.GlossingsHtmlExport;
import de.unikoeln.vedaweb.export.GlossingsTxtExport;
import de.unikoeln.vedaweb.export.SearchResultsCsvExport;
import de.unikoeln.vedaweb.export.StanzaTxtExport;
import de.unikoeln.vedaweb.search.AbstractSearchData;
import de.unikoeln.vedaweb.search.ElasticSearchService;
import de.unikoeln.vedaweb.search.SearchHitsConverter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("api/export")
public class ExportController {
	
	private static final String STANZA_TEI_TEMPLATE = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
			"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
			"<teiHeader>\n" + 
			"    <fileDesc>\n" + 
			"        <titleStmt>\n" + 
			"            <title>Rigveda - ##DocId##</title>\n" + 
			"        </titleStmt>\n" + 
			"        <publicationStmt>\n" + 
			"            <p>Rigveda - VedaWeb Edition</p>\n" + 
			"        </publicationStmt>\n" + 
			"        <sourceDesc>\n" + 
			"            <p></p>\n" + 
			"        </sourceDesc>\n" + 
			"    </fileDesc>\n" + 
			"</teiHeader>\n" + 
			"<text>\n" + 
			"	<body>\n" + 
			"		##DocData##\n" + 
			"	</body>\n" + 
			"</text>\n" + 
			"</TEI>";
	
	@Autowired
	private ElasticSearchService search;
	
	@Autowired
	private StanzaXmlRepository stanzaXmlRepo;
	
	@Autowired
	private StanzaRepository stanzaRepo;
	
	
	@ApiOperation(
			value = "Export results of the given search as CSV")
	@PostMapping(
			value = "/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = "text/plain;charset=UTF-8")
    public String exportSearchCSV(@RequestBody AbstractSearchData searchData) {
		
		searchData.setSize((int)stanzaRepo.count());	//get ALL results
		searchData.setFrom(0);	//export from result 0
		return SearchResultsCsvExport.searchHitsAsCsv(
				SearchHitsConverter.processSearchResponse(
						search.search(searchData)));
    }
	
	
	@ApiOperation(
			httpMethod = "POST",
			value = "Export a specific stanza as TEI XML (this is a POST endpoint, "
					+ "because layer selection via request body will be implemented in the future)")
	@PostMapping(
			value = "/doc/{docId}/xml",
			//consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = "text/xml;charset=UTF-8")
    public String exportDocXml(
    		@ApiParam(example = "0300201")
    		@PathVariable("docId") String docId) {

		Optional<StanzaXml> xml = stanzaXmlRepo.findById(docId);
		String idPattern = "(\\d{2})(\\d{3})(\\d{2})";
		return xml.isPresent()
			? STANZA_TEI_TEMPLATE
				.replace("##DocId##", docId.replaceFirst(idPattern, "$1.$2.$3"))
				.replace("##DocData##", xml.get().getXml()) 
			: "";
    }
	

	@ApiOperation(
			value = "Export a specific stanza's data as plain text")
	@PostMapping(
			value = "/doc/{docId}/txt",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = "text/plain;charset=UTF-8")
    public String exportDocTxt(
    		@ApiParam(example = "0300201")
    		@PathVariable("docId") String docId,
    		@RequestBody ExportLayers exportLayers) {
		
		Optional<Stanza> stanza = stanzaRepo.findById(docId);
		return stanza.isPresent()
				? StanzaTxtExport.stanzaTxt(stanza.get(), exportLayers)
				: "";
    }
	
	
	

	@ApiOperation(
			value = "Export a specific stanza's morphological glossing as plain text")
	@GetMapping(
			value = "/glossings/{docId}/txt",
			produces = "text/plain;charset=UTF-8")
    public String exportGlossingsTxt(
    		@ApiParam(example = "0300201")
    		@PathVariable("docId") String docId) {
		
		Optional<Stanza> stanza = stanzaRepo.findById(docId);
		return stanza.isPresent() ? GlossingsTxtExport.glossingsTxt(stanza.get()) : "";
    }
	

	@ApiOperation(
			value = "Export a specific stanza's morphological glossing as HTML table")
	@GetMapping(
			value = "/glossings/{docId}/html",
			produces = "text/plain;charset=UTF-8")
    public String exportGlossingsHtml(
    		@ApiParam(example = "0300201")
    		@PathVariable("docId") String docId) {
		
		Optional<Stanza> stanza = stanzaRepo.findById(docId);
		return stanza.isPresent() ? GlossingsHtmlExport.glossingsHtml(stanza.get()) : "";
    }
	
	
}
