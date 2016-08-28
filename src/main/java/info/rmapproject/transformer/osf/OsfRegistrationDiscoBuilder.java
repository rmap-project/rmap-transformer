/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.TransformUtils;
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

	/** The registration record. */
	private Registration record;
	
	/** DOI prefix (non-http). */
	private static final String DOI_COLON_PREFIX = "doi:";
	
	/** DOI prefix (http). */
	private static final String DOI_HTTP_PREFIX = "http://dx.doi.org/";
	
	/** ARK prefix. */
	private static final String ARK_PREFIX = "ark:/";
	
	/** Prop key for OSF API V1 Base URL. */
	private static final String OSF_APIV1_BASEURL_PROPNAME = "rmaptransformer.osfApiBaseUrlV1";
	
	/** OSF API V1 Base URL. */
	private String apiBaseUrlV1 = null;	
		
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription.
	 */
	public OsfRegistrationDiscoBuilder(){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
	}

	/**
	 * Initiates converter - uses values provided for discoCreator and discoDescription.
	 *
	 * @param discoDescription the disco description
	 */
	public OsfRegistrationDiscoBuilder(String discoDescription){
		super(DEFAULT_CREATOR, discoDescription);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.transformer.osf.OsfNodeDiscoBuilder#setRecord(java.lang.Object)
	 */
	@Override
	public void setRecord(Object record){
		this.record = (Registration) record;
	}
	
	/**
	 * Sets the v1 API base url.
	 *
	 * @param apiBaseUrlV1 the new V1 API base url
	 */
	public void setV1ApiBaseUrl(String apiBaseUrlV1) {
		this.apiBaseUrlV1=apiBaseUrlV1;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.transformer.osf.OsfNodeDiscoBuilder#getModel()
	 */
	@Override
	public Model getModel() {		
								
		//disco header
		addDiscoHeader();
		addRegistration(record, null);
		
		//fill in
		
		return model;		
	}

	/**
	 * Adds the registration.
	 *
	 * @param registration the registration
	 * @param parentId the parent id
	 */
	private void addRegistration(Registration registration, IRI parentId){
				
		IRI regId = factory.createIRI(OSF_PATH_PREFIX + registration.getId() + "/");
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
		addStmt(regId, RDF.TYPE, category);

		addLiteralStmt(regId, DCTERMS.TITLE, registration.getTitle());
		addLiteralStmt(regId, DCTERMS.DESCRIPTION, registration.getDescription());

		
		//TODO:Not mapped yet
//		if (reg.getRegistered_meta()!=null){
//			addLiteralStmt(discoNode, DCTERMS.DESCRIPTION, registration.getRegistered_meta().getSummary());
//		}
		
		addContributors(registration.getContributors(), regId);				
		addChildRegistrations(registration.getChildren(), regId);
		
		String regFrom = registration.getRegistered_from();
		String sOrigNodeId = TransformUtils.extractLastSubFolder(regFrom);							
		Resource origNodeId = factory.createIRI(OSF_PATH_PREFIX + sOrigNodeId + "/");
		addStmt(regId, DCTERMS.IS_VERSION_OF, origNodeId);
		addIriStmt(origNodeId, RDF.TYPE, OSF_PROJECT);
		
		addFiles(registration, regId);
	}
	
	
	/**
	 * Add child registration metadata to model.
	 *
	 * @param children the children
	 * @param parentId the parent id
	 */
	private void addChildRegistrations(List<Registration> children, IRI parentId){
		if (children!=null){
			for (Registration child : children) {
				addRegistration(child, parentId);
			}
		}
		
	}
	
	/**
	 * Adds the identifiers to the model.
	 *
	 * @param regIdIri the reg id iri
	 * @param regId the reg id
	 */
	private void addIdentifiers(IRI regIdIri, String regId){
		List<String> identifiers = getIdentifiers(regId, this.apiBaseUrlV1);
		if (identifiers!=null){
			for (String identifier : identifiers) {
				addIriStmt(regIdIri, DCTERMS.IDENTIFIER, identifier);
			}
		}
		
	}
	
	/**
	 * temporary measure until identifiers (doi, ark) are included in version 2 api.
	 *
	 * @param identifier the identifier
	 * @param baseUrl the base url
	 * @return the identifiers
	 */
	public static List<String> getIdentifiers(String identifier, String baseUrl){
		if (identifier==null || identifier.length()==0){
			throw new IllegalArgumentException("identifier cannot be null or empty");
		}

		if (baseUrl==null||baseUrl.length()==0){
			baseUrl = TransformUtils.getPropertyValue(OSF_APIV1_BASEURL_PROPNAME);
		}
		
		List<String> identifiers = new ArrayList<String>();
		String surl = baseUrl + "project/" + identifier + "/";
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
				String doi = jsonIds.get("doi").toString();

				if (jsonIds.has("doi") && !doi.equals("null") && TransformUtils.isDoi(doi)){
					doi=TransformUtils.normalizeDoi(doi);
					if (!doi.startsWith(DOI_HTTP_PREFIX)){
						doi = DOI_HTTP_PREFIX + doi;	
					} 
					if (doi.startsWith(DOI_HTTP_PREFIX + "10.")){
						//going to include the doi: and http://dx.doi.org/ prefixes to improve interconnectedness
						identifiers.add(doi);
						identifiers.add(doi.replace(DOI_HTTP_PREFIX, DOI_COLON_PREFIX));						
					}
					
				}
				String ark = jsonIds.get("ark").toString();
				if (jsonIds.has("ark") && !ark.equals("null")){
					if (!ark.startsWith(ARK_PREFIX) && !ark.startsWith("http")){
						ark = ARK_PREFIX + ark;
					}
					identifiers.add(ark);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Could not retrieve other identifiers for Registration");
		} 
				
		return identifiers;
	}
	
	
		
}
