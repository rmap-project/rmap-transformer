package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Agent;
import info.rmapproject.cos.share.client.model.AgentType;
import info.rmapproject.cos.share.client.model.OtherProperty;
import info.rmapproject.cos.share.client.model.OtherPropertyType;
import info.rmapproject.cos.share.client.model.OtherPropertyValue;
import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.model.Sponsorship;
import info.rmapproject.transformer.DiscoBuilder;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.vocabulary.RdfType;
import info.rmapproject.transformer.vocabulary.Terms;

import java.net.URI;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

/** 
 * Performs mapping from SHARE JSON to RDF.  
 * This mapping goes via a Java object model, so SHARE JSON -> Java Model -> RDF.
 * @author khanson
 *
 */
public class ShareDiscoBuilder extends DiscoBuilder  {
	
	private Record record;
	private static final String DEFAULT_CREATOR = Terms.RMAPAGENT_NAMESPACE + "RMap-SHARE-Harvester-0.1";
	private static final String DEFAULT_DESCRIPTION = "Record harvested from SHARE API";
	
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription
	 * @param record
	 */
	public ShareDiscoBuilder(){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
	}

	/**
	 * Initiates converter - will assign new values to discoCreator and discoDescription
	 * even if they are null
	 * @param record
	 * @param discoDescription
	 */
	public ShareDiscoBuilder(String discoDescription){
		super(DEFAULT_CREATOR, discoDescription);
	}	

	@Override
	public void setRecord(Object record){
		this.record = (Record) record;
	}
	
	
	/**
	 * Convert a single SHARE JSON Record object to an DiSCO.
	 * @param record - in this case a JSON record using the SHARE data model.
	 */
	@Override
	public Model getModel() {		
		if (record==null){
			throw new IllegalArgumentException("Must setRecord before retrieving model");
		}		
		
		addDiscoHeader();
		
		//Handle canonical URIs
		List<URI> canonicalUris = record.getUris().getCanonicalUris();
		IRI canonicalUri = factory.createIRI(canonicalUris.get(0).toString());		
		addStmt(discoId, Terms.ORE_AGGREGATES, canonicalUri);		
		if (canonicalUris.size()>1){
			canonicalUris.remove(0);
			for (URI uri:canonicalUris){
				if (!uri.toString().equals(canonicalUri.toString())){
					addIriStmt(canonicalUri, OWL.SAMEAS, uri.toString());
				}
			}			
		}
		
		//provider URIs
		List<URI> providerUris = record.getUris().getProviderUris();
		if (providerUris!=null && providerUris.size()>0){
			for (URI providerUri:providerUris){
				if (!providerUri.toString().equals(canonicalUri.toString())){
					addIriStmt(canonicalUri, OWL.SAMEAS, providerUri.toString());	
				}
			}				
		}
		
		//object URIs
		List<URI> objectUris = record.getUris().getObjectUris();
		if (objectUris!=null && objectUris.size()>0){
			for (URI objectUri:objectUris){
				if (!objectUri.toString().equals(canonicalUri.toString())){
					addIriStmt(canonicalUri, OWL.SAMEAS, objectUri.toString());	
				}
			}				
		}
		
		//first generate otherProperties, because we need to know if there is a type list there before we proceed
		addOtherProperties(record.getOtherProperties(), canonicalUri);

		//was a proper rdf:type defined? if not let's make it a generic fabio:Expression.
		if (!model.contains(canonicalUri, RDF.TYPE, null)){
			//generic type
			addStmt(canonicalUri, RDF.TYPE, Terms.FABIO_EXPRESSION);
		}
		
		//title
		addLiteralStmt(canonicalUri, DCTERMS.TITLE, record.getTitle());
		
		//description
		addLiteralStmt(canonicalUri, DCTERMS.DESCRIPTION, record.getDescription());
		
		//contributors		
		List<Agent> contributors = record.getContributors();
		for (Agent contributor:contributors){
			addAgent(contributor, canonicalUri, DCTERMS.CREATOR);
		}
		
		//publisher
		Agent publisher = record.getPublisher();
		addAgent(publisher, canonicalUri, DCTERMS.PUBLISHER);
		
		//language
		if (record.getLanguages()!=null && record.getLanguages().size()>0){
			for (URI lang : record.getLanguages()){
				addIRIorLiteralStmt(canonicalUri, DCTERMS.LANGUAGE, lang.toString());
			}
		}

		//sponsorship
		if (record.getSponsorships()!=null && record.getSponsorships().size()>0){
			for (Sponsorship sponsorship : record.getSponsorships()){
				addSponsorship(sponsorship, canonicalUri);
			}
		}
		
		//version
		if (record.getVersion()!=null){
			addLiteralStmt(canonicalUri, DCTERMS.IDENTIFIER, record.getVersion().getVersionId());
			addIriStmt(canonicalUri, DCTERMS.IDENTIFIER, record.getVersion().getVersionOf().toString());
		}
		
		return model;
	}

	/**
	 * Generate statements for otherProperties
	 * @param otherProperties
	 * @param subjectIri
	 * @return
	 */
	private void addOtherProperties(List<OtherProperty> otherProperties, IRI subjectIri) {

		if (otherProperties!=null && subjectIri!=null){
			
			for (OtherProperty otherProperty : otherProperties){
				OtherPropertyValue propVal = otherProperty.getProperties();
				OtherPropertyType propType = propVal.getOtherPropertyType();
	
				if (propType!=null){
			        switch (propType) {
			        case TYPES:
						addTypes(propVal.getTypes(), subjectIri);
						break;
			        case DOI:
			        	addIRIorLiteralStmt(subjectIri, DCTERMS.IDENTIFIER, propVal.getDoi().toString());		            	
			        	break;
			        case FORMATS:
						for (String format:propVal.getFormats()){
							addIRIorLiteralStmt(subjectIri, DCTERMS.FORMAT, format);
						}	
						break;
			        case IDENTIFIERS:
						for (String identifier:propVal.getIdentifiers()){
							addIRIorLiteralStmt(subjectIri, DCTERMS.IDENTIFIER, identifier);
						}	
						break;
			        case ISSN:
						for (String issn:propVal.getIssns()){
							issn = TransformUtils.issnFormatter(issn);
							addIRIorLiteralStmt(subjectIri, DCTERMS.IS_PART_OF, issn);
						}
						break;
			        case EISSN:
						for (String eissn:propVal.getEissns()){
							eissn = TransformUtils.issnFormatter(eissn);
							addIRIorLiteralStmt(subjectIri, DCTERMS.IS_PART_OF, eissn);
						}
						break;
			        case ISBN:
						for (String isbn:propVal.getIsbns()){
							isbn = TransformUtils.isbnFormatter(isbn);
							addIRIorLiteralStmt(subjectIri, DCTERMS.IS_PART_OF, isbn);
						}
						break;
			        case EISBN:
						for (String eisbn:propVal.getEisbn()){
							eisbn = TransformUtils.isbnFormatter(eisbn);
							addIRIorLiteralStmt(subjectIri, DCTERMS.IS_PART_OF, eisbn);
						}
						break;
			        case LINKS:
						for (String link : propVal.getLinks()){
							addIRIorLiteralStmt(subjectIri, RDFS.SEEALSO, link);
						}		
						break;
			        case RELATIONS:
						for (String relation : propVal.getRelations()){
							addIRIorLiteralStmt(subjectIri, DCTERMS.RELATION, relation);
						}		
						break;
					default:
						break;	            	
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Pass in a SHARE Agent, the canonicalUri and the predicate that will connect the agent to the canonicalUri, and this will
	 * build the RDF statements for an Agent.
	 * @param agent
	 * @param subjectIri
	 * @param predicateIri
	 * @return
	 */
	private void addAgent(Agent agent, Resource subjectIri, IRI predicateIri){
		Resource primaryiri = null;
		Integer stmtsInModelBeforeAgent = model.size();
		if (agent!=null){
			
			List<URI> agentUris = agent.getSameAs();
			primaryiri = getFirstIriOrBNode(agentUris);
			if (agentUris!=null && agentUris.size()>0){
				for (URI agentUri : agentUris){
					if (!agentUri.toString().equals(primaryiri.toString())){
						addIriStmt(primaryiri, RDFS.SEEALSO, agentUri.toString());
					}					
				}
			}
			
			addLiteralStmt(primaryiri, FOAF.NAME, agent.getName());

			String email = agent.getEmail();
			if (email!=null && email.length()>0){
				if (!email.startsWith(Terms.MAILTO)){
					email = Terms.MAILTO + email;
				}
				addLiteralStmt(primaryiri, FOAF.MBOX, email);
			}
			
			addLiteralStmt(primaryiri, FOAF.GIVEN_NAME, agent.getGivenName());
			addLiteralStmt(primaryiri, FOAF.FAMILY_NAME, agent.getFamilyName());
			addLiteralStmt(primaryiri, Terms.VCARD_ADDITIONALNAME , agent.getAdditionalName());
		
			if (agent.getAffiliations()!=null && agent.getAffiliations().size()>0){
				List<Agent> affiliations = agent.getAffiliations();
				for (Agent affiliation:affiliations) {
					BNode orgRoleNode = factory.createBNode();
					addStmt(primaryiri, Terms.PRO_HOLDSROLEINTIME, orgRoleNode);
					addStmt(orgRoleNode, RDF.TYPE, Terms.PRO_ROLEINTIME);
					addStmt(orgRoleNode, Terms.PRO_WITHROLE, Terms.SCORO_AFFILIATE);
					
					addAgent(affiliation, orgRoleNode, Terms.PRO_RELATESTOORGANIZATION);
				}
			}
						
			//if the model has grown, that means there is new data! Link the Agent to the rest of the graph
			//if it has stayed the same but an actual IRI was provided, rather than just a blank node
			//this should be in the graph too
			if(model.size()>stmtsInModelBeforeAgent || primaryiri instanceof IRI){ 
				//add stmt to connect agent back to graph
				addStmt(subjectIri, predicateIri, primaryiri);
			}
			
			//Type stmt is always added regardless... but before this is done we want to make sure new data was generated
			//otherwise this will be a typed blank node and nothing else.
			if (model.size()>stmtsInModelBeforeAgent) {
				//add agent type
				AgentType agentType = agent.getType();
				if (agentType!=null){
					switch (agentType) {
		            case ORGANIZATION:  
		            	addStmt(primaryiri, RDF.TYPE, FOAF.ORGANIZATION);
		                break;
		            case PERSON: 
		            	addStmt(primaryiri, RDF.TYPE, FOAF.PERSON);
		                break;
		            default:
		            	addStmt(primaryiri, RDF.TYPE, FOAF.AGENT);
		                break;
					}    
				} else {
					addStmt(primaryiri, RDF.TYPE, FOAF.AGENT);				
				}	
			}	
		}
	}

	/**
	 * Adds Statements for SHARE sponsorship.
	 * @param sponsorship
	 * @param canonicalIri
	 * @return
	 */
	private void addSponsorship(Sponsorship sponsorship, IRI canonicalIri){

		if (sponsorship!=null && canonicalIri!=null){
			BNode projectNode = factory.createBNode();
			addStmt(canonicalIri, Terms.FRAPO_ISOUTPUTOF, projectNode);
			
			Resource awardId = null;
			Resource sponsorId = null;
			
			if (sponsorship.hasAwardInfo()){
				awardId = getIriOrBNode(sponsorship.getAward().getAwardIdentifier());
				addStmt(projectNode, Terms.FRAPO_ISFUNDEDBY, awardId);
				
				addStmt(awardId, RDF.TYPE, Terms.FRAPO_FUNDING);
				
				String awardName = sponsorship.getAward().getAwardName();
				if (awardName!=null && awardName.length()>0){
					addLiteralStmt(awardId, DCTERMS.TITLE, awardName);
				}				
			}
			
			if (sponsorship.hasSponsorInfo()){
				sponsorId = getIriOrBNode(sponsorship.getSponsor().getSponsorIdentifier());
				if (sponsorship.hasAwardInfo()){
					addStmt(awardId, Terms.FRAPO_ISAWARDEDBY, sponsorId);
				} else {					
					addStmt(projectNode, Terms.FRAPO_ISFUNDEDBY, sponsorId);
				}				
				addStmt(sponsorId, RDF.TYPE, Terms.FRAPO_FUNDINGAGENCY);
				addLiteralStmt(sponsorId, DCTERMS.TITLE, sponsorship.getSponsor().getSponsorName());
			}			
		}
	}
	
	private void addTypes(Object types, IRI canonicalUri){
		if (types instanceof String){
			String type = (String) types;
			String typePath = RdfType.get(type);
			if (typePath!=null){
				addIriStmt(canonicalUri, RDF.TYPE, typePath);
			}
			else {
				addLiteralStmt(canonicalUri, DCTERMS.TYPE, typePath);
			}			
		} else {
			@SuppressWarnings("unchecked")
			List<String> sTypes = (List<String>)types;
			
			for (String type:sTypes){
				if (type!=null && type.length()>0){
					String typePath = RdfType.get(type);
					if (typePath!=null){
						addIriStmt(canonicalUri, RDF.TYPE, typePath);
					}
					else {
						addLiteralStmt(canonicalUri, DCTERMS.TYPE, type);
					}			
				}
			}	
		}
	}

	
}
