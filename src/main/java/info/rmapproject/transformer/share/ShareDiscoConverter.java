package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Agent;
import info.rmapproject.cos.share.client.model.AgentType;
import info.rmapproject.cos.share.client.model.OtherProperty;
import info.rmapproject.cos.share.client.model.OtherPropertyType;
import info.rmapproject.cos.share.client.model.OtherPropertyValue;
import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.model.Sponsorship;
import info.rmapproject.cos.share.client.utils.Utils;
import info.rmapproject.transformer.DiscoConverter;
import info.rmapproject.transformer.vocabulary.RdfType;
import info.rmapproject.transformer.vocabulary.Terms;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
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
public class ShareDiscoConverter extends DiscoConverter  {
	
	private Record record;
	private String discoCreator = Terms.RMAPAGENT_NAMESPACE + "RMap-OSF-Harvester-0.1";
	private String discoDescription = "Record harvested from SHARE API";
	
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription
	 * @param record
	 */
	public ShareDiscoConverter(Record record){
		if (record==null){
			throw new IllegalArgumentException("Record cannot be null");
		}
		this.record = record;
	}

	/**
	 * Initiates converter - will assign new values to discoCreator and discoDescription
	 * even if they are null
	 * @param record
	 * @param discoCreator
	 * @param discoDescription
	 */
	public ShareDiscoConverter(Record record, URI discoCreator, String discoDescription){
		if (record==null){
			throw new IllegalArgumentException("Record cannot be null");
		}
		this.record = record;
		
		if (discoCreator!=null) {
			this.discoCreator = discoCreator.toString();
		}
		else {
			this.discoCreator = null;
		}
		
		this.discoDescription = Utils.setEmptyToNull(discoDescription);
		
	}
	
	/**
	 * Convert a single SHARE JSON Record object to an DiSCO.
	 * @param record - in this case a JSON record using the SHARE data model.
	 */
	@Override
	public OutputStream generateDiscoRdf() throws Exception {		
		if (record==null){
			throw new IllegalArgumentException("Null or empty model");
		}
				
		Model disco = new LinkedHashModel();
		
		Statement stmt = null;
		BNode discoNode = factory.createBNode();
		
		//disco header
		stmt = factory.createStatement(discoNode, RDF.TYPE, Terms.RMAP_DISCO);
		disco.add(stmt);
		if (discoCreator!=null){
			stmt = factory.createStatement(discoNode, DCTERMS.CREATOR, factory.createIRI(discoCreator));
			disco.add(stmt);
		}
		if (discoDescription!=null){
			stmt = factory.createStatement(discoNode, DCTERMS.DESCRIPTION, factory.createLiteral(discoDescription));
			disco.add(stmt);
		}
		List<URI> canonicalUris = record.getUris().getCanonicalUris();
		IRI canonicalUri = factory.createIRI(canonicalUris.get(0).toString());		
		stmt = factory.createStatement(discoNode, Terms.ORE_AGGREGATES, canonicalUri);
		disco.add(stmt);

		if (canonicalUris.size()>1){
			canonicalUris.remove(0);
			for (URI uri:canonicalUris){
				if (!uri.toString().equals(canonicalUri.toString())){
					stmt = factory.createStatement(canonicalUri, OWL.SAMEAS, factory.createIRI(uri.toString()));	
					disco.add(stmt);
				}
			}			
		}
		
		//other uris
		List<URI> providerUris = record.getUris().getProviderUris();
		if (providerUris!=null && providerUris.size()>0){
			for (URI providerUri:providerUris){
				if (!providerUri.toString().equals(canonicalUri.toString())){
					stmt = factory.createStatement(canonicalUri, OWL.SAMEAS, factory.createIRI(providerUri.toString()));	
					disco.add(stmt);
				}
			}				
		}
		
		//first get otherProperties, because we need to know if there is a type list there before we proceed
		Model otherProperties = generateOtherProperties(record.getOtherProperties(), canonicalUri);
		disco.addAll(otherProperties);

		//was a proper rdf:type defined? if not let's make it a generic fabio:Expression.
		if (!disco.contains(canonicalUri, RDF.TYPE, null)){
			//generic type
			stmt = factory.createStatement(canonicalUri, RDF.TYPE, Terms.FABIO_EXPRESSION);
			disco.add(stmt);
		}
		
		//title
		stmt = factory.createStatement(canonicalUri, DCTERMS.TITLE, factory.createLiteral(record.getTitle()));
		disco.add(stmt);
		
		//description
		if (record.getDescription()!=null && record.getDescription().length()>0){
			stmt = factory.createStatement(canonicalUri, DCTERMS.DESCRIPTION, factory.createLiteral(record.getDescription()));
			disco.add(stmt);
		}
		
		//contributors		
		List<Agent> contributors = record.getContributors();
		for (Agent contributor:contributors){
			Model contributorLD = generateAgent(contributor, canonicalUri, DCTERMS.CREATOR);
			if (contributorLD!=null){
				disco.addAll(contributorLD);
			}
		}
		
		//publisher
		Agent publisher = record.getPublisher();
		Model publisherLD = generateAgent(publisher, canonicalUri, DCTERMS.PUBLISHER);
		if(publisherLD!=null){
			disco.addAll(publisherLD);
		}
		
		//language
		if (record.getLanguages()!=null && record.getLanguages().size()>0){
			for (URI lang : record.getLanguages()){
				stmt = factory.createStatement(canonicalUri, DCTERMS.LANGUAGE, factory.createLiteral(lang.toString()));
				disco.add(stmt);
			}
		}

		//sponsorship
		if (record.getSponsorships()!=null && record.getSponsorships().size()>0){
			for (Sponsorship sponsorship : record.getSponsorships()){
				Model sponsorshipLD = generateSponsorship(sponsorship, canonicalUri);
				if (sponsorshipLD!=null){
					disco.addAll(sponsorshipLD);
				}
			}
		}
		
		//version
		if (record.getVersion()!=null){
			if (record.getVersion().getVersionId()!=null){
				stmt = factory.createStatement(canonicalUri, DCTERMS.IDENTIFIER, factory.createLiteral(record.getVersion().getVersionId()));
				disco.add(stmt);
			}
			if (record.getVersion().getVersionOf()!=null){
				stmt = factory.createStatement(canonicalUri, DCTERMS.IDENTIFIER, factory.createIRI(record.getVersion().getVersionOf().toString()));
				disco.add(stmt);
			}
		}
		
		OutputStream rdf = generateRdf(disco);
		
		return rdf;		
	}

	/**
	 * Generate statements for otherProperties
	 * @param otherProperties
	 * @param subjectIri
	 * @return
	 */
	private Model generateOtherProperties(List<OtherProperty> otherProperties, IRI subjectIri) {
		
		Statement stmt = null;
		Model model = new LinkedHashModel();
		
		if (otherProperties!=null){
			
			for (OtherProperty otherProperty : otherProperties){
				OtherPropertyValue propVal = otherProperty.getProperties();
				OtherPropertyType propType = propVal.getOtherPropertyType();
	
				if (propType!=null){
			        switch (propType) {
			        case TYPES:
						Model typesModel = extractTypes(propVal.getTypes(), subjectIri);
						model.addAll(typesModel);
						break;
			        case DOI:
						stmt = factory.createStatement(subjectIri, DCTERMS.IDENTIFIER, factory.createIRI(propVal.getDoi().toString()));
						model.add(stmt);			            	
			        	break;
			        case FORMATS:
						for (String format:propVal.getFormats()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.TYPE, format);
							if (stmt!=null) {
								model.add(stmt);
							}
						}	
						break;
			        case IDENTIFIERS:
						for (String identifier:propVal.getIdentifiers()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IDENTIFIER, identifier);
							if (stmt!=null) {
								model.add(stmt);
							}
						}	
						break;
			        case ISSN:
						for (String issn:propVal.getIssns()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IS_PART_OF, issn);
							if (stmt!=null) {
								model.add(stmt);
							}
						}
						break;
			        case EISSN:
						for (String eissn:propVal.getEissns()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IS_PART_OF, eissn);
							if (stmt!=null) {
								model.add(stmt);
							}
						}
						break;
			        case ISBN:
						for (String isbn:propVal.getIsbns()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IS_PART_OF, isbn);
							if (stmt!=null) {
								model.add(stmt);
							}
						}
						break;
			        case EISBN:
						for (String eisbn:propVal.getEisbn()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IS_PART_OF, eisbn);
							if (stmt!=null) {
								model.add(stmt);
							}
						}
						break;
			        case LINKS:
						for (String link : propVal.getLinks()){
							stmt = createStmtWithIRIorLiteralObject(subjectIri, DCTERMS.IDENTIFIER, link);
							if (stmt!=null) {
								model.add(stmt);
							}
						}		
						break;
			        case RELATIONS:
						for (String relation : propVal.getRelations()){
							if (!relation.toString().equals(subjectIri.toString())){
								try {
									URI uriRelation = new URI (relation);
									stmt = factory.createStatement(subjectIri, DCTERMS.IDENTIFIER, factory.createIRI(uriRelation.toString()));			
								}catch (Exception e){
									stmt = factory.createStatement(subjectIri, DCTERMS.RELATION, factory.createLiteral(relation));			
								}
								model.add(stmt);
							}
						}		
						break;
					default:
						break;	            	
					}
				}
			}
		}
	    return model;
	        
	}
	
	/**
	 * Creates a statement using an object that is either a URI or a Literal - tries to create as URI first, if it fails
	 * it create it as a Literal. Returns null if subject and object have same value.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	private Statement createStmtWithIRIorLiteralObject(IRI subject, IRI predicate, String object) {
		Statement stmt = null;
		if (!subject.toString().equals(object.toString())){
			try {
				URI uri = new URI (object);
				stmt = factory.createStatement(subject, predicate, factory.createIRI(uri.toString()));			
			}catch (Exception e){
				stmt = factory.createStatement(subject, predicate, factory.createLiteral(object));			
			}
		}
		return stmt;
	}
	
	
	/**
	 * Pass in a SHARE Agent, the canonicalUri and the predicate that will connect the agent to the canonicalUri, and this will
	 * build the RDF statements for an Agent.
	 * @param agent
	 * @param subjectIri
	 * @param predicateIri
	 * @return
	 */
	private Model generateAgent(Agent agent, Resource subjectIri, IRI predicateIri){
		
		Statement stmt = null;
		Model model = new LinkedHashModel();
		Resource primaryiri = null;
		
		if (agent!=null){
			
			List<URI> agentUris = agent.getSameAs();
			primaryiri = getFirstIriOrBNode(agentUris);
			if (agentUris!=null && agentUris.size()>0){
				stmt = factory.createStatement(subjectIri, predicateIri, primaryiri);
				model.add(stmt);
				for (URI agentUri : agentUris){
					if (!agentUri.toString().equals(primaryiri.toString())){
						stmt = factory.createStatement(primaryiri, RDFS.SEEALSO, factory.createIRI(agentUri.toString()));
						model.add(stmt);						
					}					
				}
			}
			else{
				stmt = factory.createStatement(subjectIri, predicateIri, primaryiri);
				model.add(stmt);
			}

			//agent type
			AgentType agentType = agent.getType();
			if (agentType!=null){
				switch (agentType) {
	            case ORGANIZATION:  
	            	stmt = factory.createStatement(primaryiri, RDF.TYPE, FOAF.ORGANIZATION);
	                break;
	            case PERSON: 
	            	stmt = factory.createStatement(primaryiri, RDF.TYPE, FOAF.PERSON);
	                break;
	            default:
	            	stmt = factory.createStatement(primaryiri, RDF.TYPE, FOAF.AGENT);
	                break;
				}    
			} else {
            	stmt = factory.createStatement(primaryiri, RDF.TYPE, FOAF.AGENT);				
			}
			model.add(stmt);
			
			
			if (agent.getName()!=null && agent.getName().length()>0){
				stmt = factory.createStatement(primaryiri, FOAF.NAME, factory.createLiteral(agent.getName()));
				model.add(stmt);
			}

			if (agent.getEmail()!=null && agent.getEmail().length()>0){
				String email = agent.getEmail();
				if (!email.startsWith(Terms.MAILTO)){
					email = Terms.MAILTO + email;
				}
				stmt = factory.createStatement(primaryiri, FOAF.MBOX, factory.createLiteral(agent.getEmail()));
				model.add(stmt);
			}
			
			if (agent.getGivenName()!=null){
				stmt = factory.createStatement(primaryiri, FOAF.GIVEN_NAME, factory.createLiteral(agent.getGivenName()));
				model.add(stmt);				
			}
			
			if (agent.getFamilyName()!=null){
				stmt = factory.createStatement(primaryiri, FOAF.FAMILY_NAME, factory.createLiteral(agent.getFamilyName()));
				model.add(stmt);				
			}
			
			if (agent.getAdditionalName()!=null){
				stmt = factory.createStatement(primaryiri, Terms.VCARD_ADDITIONALNAME , factory.createLiteral(agent.getAdditionalName()));
				model.add(stmt);				
			}
			
			if (agent.getAffiliations()!=null && agent.getAffiliations().size()>0){
				List<Agent> affiliations = agent.getAffiliations();
				for (Agent affiliation:affiliations) {
					BNode orgRoleNode = factory.createBNode();
					stmt = factory.createStatement(primaryiri, Terms.PRO_HOLDSROLEINTIME, orgRoleNode);
					model.add(stmt);
					stmt = factory.createStatement(orgRoleNode, RDF.TYPE, Terms.PRO_ROLEINTIME);
					model.add(stmt);
					stmt = factory.createStatement(orgRoleNode, Terms.PRO_WITHROLE, Terms.SCORO_AFFILIATE);
					model.add(stmt);		
									
					Model stmts = generateAgent(affiliation, orgRoleNode, Terms.PRO_RELATESTOORGANIZATION);
					model.addAll(stmts);
				}
			}
			
		}
		return model;
	}

	/**
	 * Generates the RDF Model object for SHARE sponsorship.
	 * @param sponsorship
	 * @param canonicalIri
	 * @return
	 */
	private Model generateSponsorship(Sponsorship sponsorship, IRI canonicalIri){
		Model model = new LinkedHashModel();
		Statement stmt = null;
		
		if (sponsorship!=null && canonicalIri!=null){
			BNode projectNode = factory.createBNode();
			stmt = factory.createStatement(canonicalIri, Terms.FRAPO_ISOUTPUTOF, projectNode);
			model.add(stmt);
			
			Resource awardId = null;
			Resource sponsorId = null;
			
			if (sponsorship.hasAwardInfo()){
				awardId = getIriOrBNode(sponsorship.getAward().getAwardIdentifier());
				stmt = factory.createStatement(projectNode, Terms.FRAPO_ISFUNDEDBY, awardId);
				model.add(stmt);
				
				stmt = factory.createStatement(awardId, RDF.TYPE, Terms.FRAPO_FUNDING);
				model.add(stmt);
				
				String awardName = sponsorship.getAward().getAwardName();
				if (awardName!=null && awardName.length()>0){
					stmt = factory.createStatement(awardId, DCTERMS.TITLE, factory.createLiteral(awardName));
					model.add(stmt);
				}
				
			}
			
			if (sponsorship.hasSponsorInfo()){
				sponsorId = getIriOrBNode(sponsorship.getSponsor().getSponsorIdentifier());
				if (sponsorship.hasAwardInfo()){
					stmt = factory.createStatement(awardId, Terms.FRAPO_ISAWARDEDBY, sponsorId);
				} else {					
					stmt = factory.createStatement(projectNode, Terms.FRAPO_ISFUNDEDBY, sponsorId);
				}
				model.add(stmt);
				
				stmt = factory.createStatement(sponsorId, RDF.TYPE, Terms.FRAPO_FUNDINGAGENCY);
				model.add(stmt);
				
				String sponsorName = sponsorship.getSponsor().getSponsorName();
				if (sponsorName!=null && sponsorName.length()>0){
					stmt = factory.createStatement(sponsorId, DCTERMS.TITLE, factory.createLiteral(sponsorName));
					model.add(stmt);
				}				
			}			
		}
		
		
		return model;		
	}
	
	private Model extractTypes(Object types, IRI canonicalUri){
		Model typesModel = new LinkedHashModel();
		Statement stmt = null;
		if (types instanceof String){
			String type = (String) types;
			String typePath = RdfType.get(type);
			if (typePath!=null){
				stmt = factory.createStatement(canonicalUri, RDF.TYPE, factory.createIRI(typePath));
				typesModel.add(stmt);					
			}
			else {
				stmt = factory.createStatement(canonicalUri, DCTERMS.TYPE, factory.createLiteral(type));
				typesModel.add(stmt);						
			}			
		} else {
			@SuppressWarnings("unchecked")
			List<String> sTypes = (List<String>)types;
			
			for (String type:sTypes){
				if (type!=null && type.length()>0){
					String typePath = RdfType.get(type);
					if (typePath!=null){
						stmt = factory.createStatement(canonicalUri, RDF.TYPE, factory.createIRI(typePath));
						typesModel.add(stmt);					
					}
					else {
						stmt = factory.createStatement(canonicalUri, DCTERMS.TYPE, factory.createLiteral(type));
						typesModel.add(stmt);						
					}			
				}
			}	
		}
		return typesModel;
	}
	
}
