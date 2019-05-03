package de.unikoeln.vedaweb.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.unikoeln.vedaweb.logging.ClientError;
import de.unikoeln.vedaweb.logging.ClientErrorRepository;
import de.unikoeln.vedaweb.search.ElasticIndexService;
import de.unikoeln.vedaweb.uidata.UiDataService;
import de.unikoeln.vedaweb.xmlimport.DataImportService;



@RestController
@RequestMapping("system")
public class SystemController {
	
	@Value("${vw.system.auth}")
	private String auth;
	
	@Autowired
	private DataImportService dataImportService;
	
	@Autowired
	private ElasticIndexService indexService;
	
	@Autowired
	private UiDataService uiDataService;
	
	@Autowired
	private JsonUtilService json;
	
	@Autowired
	private ClientErrorRepository clientErrorRepo;
	
	
	@GetMapping(value = {"/index/{action}"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String stanzaById(
    		@PathVariable(name = "action") String action,
    		@RequestParam(name = "auth", required = false) String auth) {
		
		ObjectNode response = json.newNode();
		
		if (!auth(auth)) {
			response.put("error", "authentication failed");
			return response.toString();
		}
					
		switch (action){
		case "delete":
			response.set("indexDelete", indexService.deleteIndex());
			break;
		case "create":
			response.set("indexCreate", indexService.createIndex());
			break;
		case "fill":
			response.set("indexFill", indexService.indexDbDocuments());
			break;
		case "rebuild":
			response.set("indexRebuild", indexService.rebuildIndex());
			break;
		default:
			response.put("error", "unknown command");
			break;
		}
		return response.toString();
    }
	
	
	@GetMapping(value = {"/import/{dryRun}", "/import"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String importData(
    		@PathVariable(name = "dryRun", required = false) String dryRun,
    		@RequestParam(name = "auth", required = false) String auth) {
		
		if (!auth(auth))
			return "{error:'authentication failed'}";
		
		boolean dry = dryRun != null;
		int docCount = dataImportService.importXMLData(DataImportService.LOCAL_XML_DIR, dry);
		ObjectNode response = json.newNode();
		response.put("dryRun", dry);
		response.put("importedDocsCount", docCount);
		//if (!dry) response.put("indexActions", indexService.rebuildIndex());
		return response.toString();
    }
	
	
	@GetMapping(value = {"/uidata/refresh"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String importData(
    		@RequestParam(name = "auth", required = false) String auth) {
		
		if (!auth(auth))
			return "{error:'authentication failed'}";
		
		return uiDataService.init().toString();
    }
	
	
	@PostMapping(value = {"/error"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ClientError reportClientError(@RequestBody ClientError errorData) {
		return clientErrorRepo.insert(errorData);
    }
	
	
	private boolean auth(String auth) {
		return auth != null && auth.equals(this.auth);
	}
	
}