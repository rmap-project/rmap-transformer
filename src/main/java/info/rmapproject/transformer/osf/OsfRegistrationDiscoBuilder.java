package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.Utils;
import info.rmapproject.transformer.vocabulary.Terms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dataconservancy.cos.osf.client.model.Registration;
import org.json.JSONObject;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;


/** 
 * Performs mapping from OSF Registration Java model to RDF DiSCO model.  
 * (Java Model -> RDF).
 * @author khanson
 *
 */
public class OsfRegistrationDiscoBuilder extends OsfNodeDiscoBuilder {

	private Registration record;
		
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription
	 * @param record
	 */
	public OsfRegistrationDiscoBuilder(){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
	}

	/**
	 * Initiates converter - uses values provided for discoCreator and discoDescription
	 * @param record
	 */
	public OsfRegistrationDiscoBuilder(String discoDescription){
		super(DEFAULT_CREATOR, discoDescription);
	}
	
	@Override
	public void setRecord(Object record){
		this.record = (Registration) record;
	}
	
	@Override
	public Model getModel() {		
								
		//disco header
		addDiscoHeader();
		addRegistration(record, null);
		
		//fill in
		
		return model;		
	}

	private void addRegistration(Registration registration, IRI parentId){
				
		IRI regId = factory.createIRI(OSF_PATH_PREFIX + registration.getId());
		addStmt(discoId, Terms.ORE_AGGREGATES, regId);
		

		if (parentId!=null){
			addStmt(parentId, DCTERMS.HAS_PART, regId);
		}
		
		//*** REGISTRATION TOP LEVEL DESCRIPTION ***
		addIriStmt(regId, RDF.TYPE, OSF_REGISTRATION);
		
		addLiteralStmt(regId, DCTERMS.ISSUED, registration.getDate_registered());
		
//		//TODO: add these - not yet part of API, but soon...
//		addIriStmt(regId, DCTERMS.IDENTIFIER, registration.getArkId());
//		addIriStmt(regId, DCTERMS.IDENTIFIER, registration.getDoi());
		//temporary measure that goes to api v1:
		addIdentifiers(regId, registration.getId());
		
		
		IRI category = mapCategoryToIri(registration.getCategory());
		addStmt(regId, DCTERMS.TYPE, category);

		addLiteralStmt(regId, DCTERMS.TITLE, registration.getTitle());
		addLiteralStmt(regId, DCTERMS.DESCRIPTION, registration.getDescription());

		
		//TODO:Not mapped yet
//		if (reg.getRegistered_meta()!=null){
//			addLiteralStmt(discoNode, DCTERMS.DESCRIPTION, registration.getRegistered_meta().getSummary());
//		}
		
		addContributors(registration.getContributors(), regId);				
		addChildRegistrations(registration.getChildren(), regId);
		
		String regFrom = registration.getRegistered_from();
		String sOrigNodeId = Utils.extractLastSubFolder(regFrom);							
		Resource origNodeId = factory.createIRI(OSF_PATH_PREFIX + sOrigNodeId);
		addStmt(regId, DCTERMS.IS_VERSION_OF, origNodeId);
		addIriStmt(origNodeId, RDF.TYPE, OSF_PROJECT);
		
		addFiles(registration, regId);
	}
	
	
	/**
	 * Add child registration metadata to model
	 * @param child
	 * @param discoNode
	 * @param regId
	 */
	private void addChildRegistrations(List<Registration> children, IRI parentId){
		if (children!=null){
			for (Registration child : children) {
				addRegistration(child, parentId);
			}
		}
		
	}
	
	/**
	 * 
	 */
	private void addIdentifiers(IRI regIdIri, String regId){
		List<String> identifiers = getIdentifiers(regId);
		if (identifiers!=null){
			for (String identifier : identifiers) {
				addIriStmt(regIdIri, DCTERMS.IDENTIFIER, identifier);
			}
		}
		
	}
	
	/**
	 * temporary measure until identifiers (doi, ark) are included in version 2 api.
	 * @param identifier
	 * @return
	 */
	public static List<String> getIdentifiers(String identifier){
		if (identifier==null || identifier.length()==0){
			throw new IllegalArgumentException("identifier cannot be null or empty");
		}
		
		List<String> identifiers = new ArrayList<String>();
		String surl = "https://osf.io/api/v1/project/" + identifier + "/";
		URL url;
		try {
			StringBuffer text = new StringBuffer();
			url = new URL(surl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
			connection.setRequestMethod("GET"); 
			connection.connect(); 
		    InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
		    BufferedReader buff = new BufferedReader(in);
		    String line;
		    do {
		      line = buff.readLine();
		      text.append(line + "\n");
		    } while (line != null);
						
			String jsonString = text.toString();
			JSONObject root = new JSONObject(jsonString);
			JSONObject node = root.getJSONObject("node");
			JSONObject jsonIds = node.getJSONObject("identifiers");
			if (jsonIds!=null){
				if (jsonIds.has("doi")){
					identifiers.add(jsonIds.getString("doi"));
				}
				if (jsonIds.has("ark")){
					identifiers.add(jsonIds.getString("ark"));
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Could not retrieve other identifiers for Registration");
		} 
				
		return identifiers;
	}
	
	
		
}
