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

import java.util.List;

import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.cos.osf.client.model.Identifier;
import info.rmapproject.cos.osf.client.model.Registration;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.vocabulary.Terms;

/** 
 * Performs mapping from OSF Registration Java model to RDF DiSCO model.  
 * (Java Model -> RDF).
 * @author khanson
 *
 */
public class OsfRegistrationDiscoBuilder extends OsfNodeDiscoBuilder {

	/** The registration record. */
	private Registration record;
	
	/** DOI identifier category String **/
	private static final String DOI_ID_CATEGORY = "doi";
	
	/** ARK identifier category String **/
	private static final String ARK_ID_CATEGORY = "ark";
	
	/** ARK prefix. */
	private static final String ARK_PREFIX = "ark:/";
			
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
		
		addIdentifiers(registration.getIdentifiers(), regId);
		
		IRI category = mapCategoryToIri(registration.getCategory());
		addStmt(regId, RDF.TYPE, category);

		addLiteralStmt(regId, DCTERMS.TITLE, registration.getTitle());
		addLiteralStmt(regId, DCTERMS.DESCRIPTION, registration.getDescription());

		addContributors(registration.getContributors(), regId);				
		addChildRegistrations(registration.getChildren(), regId);
		
		String regFrom = registration.getRegistered_from();
		String sOrigNodeId = TransformUtils.extractLastSubFolder(regFrom);							
		Resource origNodeId = factory.createIRI(OSF_PATH_PREFIX + sOrigNodeId + "/");
		addStmt(regId, DCTERMS.IS_VERSION_OF, origNodeId);
		addIriStmt(origNodeId, RDF.TYPE, OSF_PROJECT);
		
		//addFiles(registration, regId);
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
	 * Adds the identifiers (ark, doi) to the model.
	 * @param identifiers list of identifiers
	 * @param regIdIri registration IRI
	 */
	private void addIdentifiers(List<Identifier> identifiers, IRI regIdIri){
		if (identifiers!=null){
			for (Identifier identifier : identifiers) {
				String category = identifier.getCategory();
				String value = identifier.getValue();
				if (category.equals(DOI_ID_CATEGORY) && TransformUtils.isDoi(value)){ 
					
					String doi = TransformUtils.normalizeDoi(value);
					addIriStmt(regIdIri, DCTERMS.IDENTIFIER, doi); // https://doi.org format
					
					//also add non-http format of ID - replace 
					doi = doi.replace(TransformUtils.getHttpDoiPrefix(), TransformUtils.getNonHttpDoiPrefix());
					addIriStmt(regIdIri, DCTERMS.IDENTIFIER, doi); //doi: format
					
				} else if (category.equals(ARK_ID_CATEGORY)) {
					addIriStmt(regIdIri, DCTERMS.IDENTIFIER, ARK_PREFIX + value);
				}
			}
		}	
	}		
}
