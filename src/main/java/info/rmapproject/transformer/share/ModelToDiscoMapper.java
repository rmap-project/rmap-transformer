package info.rmapproject.transformer.share;

import info.rmapproject.cos.share.client.model.Agent;
import info.rmapproject.cos.share.client.model.AgentType;
import info.rmapproject.cos.share.client.model.OtherProperty;
import info.rmapproject.cos.share.client.model.OtherPropertyType;
import info.rmapproject.cos.share.client.model.OtherPropertyValue;
import info.rmapproject.cos.share.client.model.Record;
import info.rmapproject.cos.share.client.model.Sponsorship;
import info.rmapproject.transformer.vocabulary.RdfType;
import info.rmapproject.transformer.vocabulary.Terms;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

/**
 * Maps SHARE Record model to RMap DiSCO RDF
 * @author khanson
 *
 */
public class ModelToDiscoMapper {

	private static ValueFactory factory = SimpleValueFactory.getInstance();
	
	private static OutputStream generateRdf(Model model) throws Exception {
		OutputStream bOut = new ByteArrayOutputStream();
		try {
			Rio.write(model, bOut, RDFFormat.TURTLE);
		} catch (Exception e) {
			throw new Exception("Exception thrown creating RDF from statement list", e);
		}
		return bOut;	
		
	}
	
	public OutputStream toRDF(Record model) throws Exception	{		
		if (model==null){
			throw new Exception("Null or empty model");
		}
		
		Model disco = new LinkedHashModel();
		
		Statement stmt = null;
		BNode discoNode = factory.createBNode();
		
		//disco header
		stmt = factory.createStatement(discoNode, RDF.TYPE, Terms.RMAP_DISCO);
		disco.add(stmt);
		stmt = factory.createStatement(discoNode, DCTERMS.CREATOR, Terms.SHARE_HARVEST_AGENT);
		disco.add(stmt);
		stmt = factory.createStatement(discoNode, DCTERMS.DESCRIPTION, factory.createLiteral("Record harvested from SHARE API"));
		disco.add(stmt);
		IRI canonicalUri = factory.createIRI(model.getUris().getCanonicalUri().toString());		
		stmt = factory.createStatement(discoNode, Terms.ORE_AGGREGATES, canonicalUri);
		disco.add(stmt);

		//other uris
		List<URI> providerUris = model.getUris().getProviderUris();
		if (providerUris!=null && providerUris.size()>0){
			for (URI providerUri:providerUris){
				if (!providerUri.toString().equals(canonicalUri.toString())){
					stmt = factory.createStatement(canonicalUri, OWL.SAMEAS, factory.createIRI(providerUri.toString()));	
					disco.add(stmt);
				}
			}				
		}
		
		//first get otherProperties, because we need to know if there is a type list there before we proceed
		Model otherProperties = generateOtherProperties(model.getOtherProperties(), canonicalUri);
		disco.addAll(otherProperties);

		//was a proper rdf:type defined? if not let's make it a generic fabio:Expression.
		if (!disco.contains(canonicalUri, RDF.TYPE, null)){
			//generic type
			stmt = factory.createStatement(canonicalUri, RDF.TYPE, Terms.FABIO_EXPRESSION);
			disco.add(stmt);
		}
		
		//title
		stmt = factory.createStatement(canonicalUri, DCTERMS.TITLE, factory.createLiteral(model.getTitle()));
		disco.add(stmt);
		
		//description
		if (model.getDescription()!=null && model.getDescription().length()>0){
			stmt = factory.createStatement(canonicalUri, DCTERMS.DESCRIPTION, factory.createLiteral(model.getDescription()));
			disco.add(stmt);
		}
		
		//contributors		
		List<Agent> contributors = model.getContributors();
		for (Agent contributor:contributors){
			Model contributorLD = generateAgent(contributor, canonicalUri, DCTERMS.CREATOR);
			if (contributorLD!=null){
				disco.addAll(contributorLD);
			}
		}
		
		//publisher
		Agent publisher = model.getPublisher();
		Model publisherLD = generateAgent(publisher, canonicalUri, DCTERMS.PUBLISHER);
		if(publisherLD!=null){
			disco.addAll(publisherLD);
		}
		
		//language
		if (model.getLanguages()!=null && model.getLanguages().size()>0){
			for (URI lang : model.getLanguages()){
				stmt = factory.createStatement(canonicalUri, DCTERMS.LANGUAGE, factory.createLiteral(lang.toString()));
				disco.add(stmt);
			}
		}

		//sponsorship
		if (model.getSponsorships()!=null && model.getSponsorships().size()>0){
			for (Sponsorship sponsorship : model.getSponsorships()){
				Model sponsorshipLD = generateSponsorship(sponsorship, canonicalUri);
				if (sponsorshipLD!=null){
					disco.addAll(sponsorshipLD);
				}
			}
		}
		
		//version
		if (model.getVersion()!=null){
			if (model.getVersion().getVersionId()!=null){
				stmt = factory.createStatement(canonicalUri, DCTERMS.IDENTIFIER, factory.createLiteral(model.getVersion().getVersionId()));
				disco.add(stmt);
			}
			if (model.getVersion().getVersionOf()!=null){
				stmt = factory.createStatement(canonicalUri, DCTERMS.IDENTIFIER, factory.createIRI(model.getVersion().getVersionOf().toString()));
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
			        	if (propVal.getFormats() instanceof String){
			    			String format = (String) propVal.getFormats();
		    				stmt = factory.createStatement(subjectIri, DCTERMS.TYPE, factory.createLiteral(format));
		    				model.add(stmt);	
			    		} else {
			    			@SuppressWarnings("unchecked")
			    			List<String> formats = (List<String>)propVal.getFormats();
			    			for (String format:formats){
			    				if (format!=null && format.length()>0){
		    						stmt = factory.createStatement(subjectIri, RDF.TYPE, factory.createLiteral(format));
		    						model.add(stmt);	
			    				}
			    			}	
			    		}
			        	break;
			        case IDENTIFIERS:
						for (String identifier:propVal.getIdentifier()){
							if (!identifier.equals(subjectIri.toString())){
								try {
									URI id = new URI (identifier);
									stmt = factory.createStatement(subjectIri, DCTERMS.IDENTIFIER, factory.createIRI(id.toString()));					
								}catch (URISyntaxException e){
									stmt = factory.createStatement(subjectIri, DCTERMS.IDENTIFIER, factory.createLiteral(identifier));					
								}
								model.add(stmt);
							}
						}	
						break;
			        case ISSN:
			            try {
							URI id = new URI (propVal.getIssn());
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createIRI(id.toString()));					
						}catch (URISyntaxException e){
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createLiteral(propVal.getIssn()));			
						}
			            model.add(stmt);
						break;
			        case EISSN:
						try {
							URI id = new URI (propVal.getEissn());
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createIRI(id.toString()));					
						}catch (URISyntaxException e){
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createLiteral(propVal.getEissn()));			
						}
						model.add(stmt);	
						break;	
			        case ISBN:
						try {
							URI id = new URI (propVal.getIsbn());
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createIRI(id.toString()));					
						}catch (URISyntaxException e){
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createLiteral(propVal.getIsbn()));			
						}
						model.add(stmt);	
						break;
			        case EISBN:
						try {
							URI id = new URI (propVal.getEisbn());
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createIRI(id.toString()));					
						}catch (URISyntaxException e){
							stmt = factory.createStatement(subjectIri, DCTERMS.IS_PART_OF, factory.createLiteral(propVal.getEisbn()));			
						}
						model.add(stmt);				
						break;	 
			        case LINKS:
						for (URI link : propVal.getLinks()){
							if (!link.toString().equals(subjectIri.toString())){
								stmt = factory.createStatement(subjectIri, DCTERMS.IDENTIFIER, factory.createIRI(link.toString()));
								model.add(stmt);
							}
						}		
						break;
			        case RELATIONS:
						for (URI relation : propVal.getRelations()){
							if (!relation.toString().equals(subjectIri.toString())){
								stmt = factory.createStatement(subjectIri, DCTERMS.RELATION, factory.createIRI(relation.toString()));
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
	
	
	
	/**
	 * Looks at list of identifiers. If there are values in the list, it takes the first one and
	 * generates an IRI to use as the primary identifier in the RDF. Otherwise returns a blank node.
	 * @param sameAsList
	 * @return
	 */
	private Resource getFirstIriOrBNode(List<URI> uriList){
		Resource val = null;
		if (uriList==null){
			val = factory.createBNode();
		}
		else {
			URI firstUri = uriList.get(0);
			val = getIriOrBNode(firstUri);
		}		
		return val;
	}
	
	/** 
	 * Looks at URI value provided. If it's null it passes back a blank node, 
	 * if it has a value it passes it back as IRI
	 * @param iriVal
	 * @return
	 */

	private Resource getIriOrBNode(URI uriVal){
		Resource val = null;
		if (uriVal==null){
			val = factory.createBNode();
		}
		else {
			val = factory.createIRI(uriVal.toString());				
		}		
		return val;
	}
	
	
	
}
