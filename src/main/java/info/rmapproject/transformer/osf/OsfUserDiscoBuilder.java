package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.DiscoBuilder;
import info.rmapproject.transformer.vocabulary.Terms;

import java.util.List;

import org.dataconservancy.cos.osf.client.model.Institution;
import org.dataconservancy.cos.osf.client.model.User;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;


/** 
 * Performs mapping from OSF Registration Java model to RDF DiSCO model.  
 * (Java Model -> RDF).
 * @author khanson
 *
 */

public class OsfUserDiscoBuilder extends DiscoBuilder {

	private User record;
	private IRI userId;

	protected static final String DEFAULT_CREATOR = Terms.RMAPAGENT_NAMESPACE + "RMap-OSF-Harvester-0.1";
	protected static final String DEFAULT_DESCRIPTION = "User record harvested from OSF API v2";
	protected static final String OSF_PATH_PREFIX = "http://osf.io/";
	
	
	/**
	 * Constructor for Node to pass default params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	public OsfUserDiscoBuilder(){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
	}
	
	
	/**
	 * Constructor for Node to pass params up to super()
	 * @param discoDescription
	 */
	public OsfUserDiscoBuilder(String discoDescription){
		super(DEFAULT_CREATOR, discoDescription);
	}
		
	
	/**
	 * Constructor for Node to pass params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	public OsfUserDiscoBuilder(String discoCreator, String discoDescription){
		super(discoCreator, discoDescription);
	}

	
	@Override
	public void setRecord(Object record) {
		this.record = (User) record;
		discoId = null;
		model = null;
	}
	
	@Override
	public Model getModel()	{
		model = new LinkedHashModel();		
		discoId = factory.createBNode(); 	
		userId = factory.createIRI(OSF_PATH_PREFIX + record.getId());				

		//disco header
		addDiscoHeader();
		addUser();
				
		return model;		
	}

	private void addUser(){
				
		addStmt(discoId, Terms.ORE_AGGREGATES, userId);
		
		addStmt(userId, RDF.TYPE, FOAF.PERSON);
		addLiteralStmt(userId, FOAF.GIVEN_NAME, record.getGiven_name());
		addLiteralStmt(userId, FOAF.FAMILY_NAME, record.getFamily_name());
		addLiteralStmt(userId, FOAF.NAME, record.getFull_name());
		//TODO: not sure if this is the appropriate use of this...
		addLiteralStmt(userId, Terms.VCARD_ADDITIONALNAME, record.getMiddle_names());
		//TODO: ditto with this mapping - not sure if it's exactly right... is "jr." honorific?!
		addLiteralStmt(userId, Terms.VCARD_HONORIFICSUFFIX, record.getSuffix());
		
		// now for all of the possible accounts...
		addIriStmt(userId, RDFS.SEEALSO, record.getAcademiaProfileId());
		addIriStmt(userId, RDFS.SEEALSO, record.getBaiduScholar());
		addIriStmt(userId, RDFS.SEEALSO, record.getGitHub());
		addIriStmt(userId, RDFS.SEEALSO, record.getImpactStory());
		addIriStmt(userId, RDFS.SEEALSO, record.getLinkedIn());
		addIriStmt(userId, RDFS.SEEALSO, record.getOrcid());
		addIriStmt(userId, RDFS.SEEALSO, record.getPersonal_website());
		addIriStmt(userId, RDFS.SEEALSO, record.getResearcherId());
		addIriStmt(userId, RDFS.SEEALSO, record.getResearchGate());
		addIriStmt(userId, RDFS.SEEALSO, record.getScholar());
		addIriStmt(userId, RDFS.SEEALSO, record.getTwitter());

		addInstitutions();
	}
	
	
	private void addInstitutions(){
		List<Institution> institutions = record.getInstitutions();
		if (institutions!=null){
			for(Institution inst : institutions){
				Resource roleNode = factory.createBNode();
				IRI instIri = factory.createIRI(OSF_PATH_PREFIX + inst.getId());
				addStmt(userId, Terms.PRO_HOLDSROLEINTIME, roleNode);
				addStmt(userId, Terms.PRO_WITHROLE, Terms.SCORO_AFFILIATE);
				addStmt(roleNode, Terms.PRO_RELATESTOORGANIZATION, instIri);
				addStmt(roleNode, RDF.TYPE, FOAF.ORGANIZATION);
				addLiteralStmt(instIri, FOAF.NAME, inst.getName());
			}
		}
	}
	
}
