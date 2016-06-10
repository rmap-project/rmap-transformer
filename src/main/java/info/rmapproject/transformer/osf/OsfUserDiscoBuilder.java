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
	
	protected static final String ORCID_PREFIX = "http://orcid.org/";
	protected static final String RESEARCHERID_PREFIX = "http://researcherid.com/rid/";
	protected static final String TWITTER_PREFIX = "https://twitter.com/";
	protected static final String GITHUB_PREFIX = "https://github.com/";
	protected static final String GOOGLESCHOLAR_PREFIX = "http://scholar.google.com/citations?user=";
	protected static final String LINKEDIN_PREFIX = "https://www.linkedin.com/";
	protected static final String IMPACTSTORY_PREFIX = "https://impactstory.org/";
	protected static final String RESEARCHGATE_PREFIX = "https://researchgate.net/profile/";
	protected static final String BAIDUSCHOLAR_PREFIX = "http://xueshu.baidu.com/scholarID/";
	protected static final String ACADEMIA_PREFIX = ".academia.edu/";
	
	
	
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
		//TODO: this not working at teh moment so commenting out.
//		List<String> websites = record.getPersonal_websites();
//		if (websites!=null){
//			for (String website : websites){
//				addIriStmt(userId, RDFS.SEEALSO, website);				
//			}
//		}

		if (record.getAcademiaProfileID()!=null && record.getAcademiaInstitution()!=null){
			String academiaUrl = "https://" + record.getAcademiaInstitution() 
								+ ACADEMIA_PREFIX + record.getAcademiaProfileID();
			addIriStmt(userId, RDFS.SEEALSO, academiaUrl);
		}
		
		if (record.getBaiduScholar()!=null){
			addIriStmt(userId, RDFS.SEEALSO, BAIDUSCHOLAR_PREFIX + record.getBaiduScholar());
		}
		if (record.getGitHub()!=null){
			addIriStmt(userId, RDFS.SEEALSO, GITHUB_PREFIX + record.getGitHub());
		}
		if (record.getImpactStory()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, IMPACTSTORY_PREFIX + record.getImpactStory());
		}
		if (record.getLinkedIn()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, LINKEDIN_PREFIX + record.getLinkedIn());
		}
		if (record.getOrcid()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, ORCID_PREFIX + record.getOrcid());
		}
		if (record.getResearcherId()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, RESEARCHERID_PREFIX + record.getResearcherId());
		}
		if (record.getResearchGate()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, RESEARCHGATE_PREFIX + record.getResearchGate());
		}
		if (record.getScholar()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, GOOGLESCHOLAR_PREFIX + record.getScholar());
		}
		if (record.getTwitter()!=null){		
			addIriStmt(userId, RDFS.SEEALSO, TWITTER_PREFIX + record.getTwitter());
		}

		addInstitutions();
	}
	
	
	private void addInstitutions(){
		List<Institution> institutions = record.getInstitutions();
		if (institutions!=null){
			for(Institution inst : institutions){
				Resource roleNode = factory.createBNode();
				IRI instIri = factory.createIRI(OSF_PATH_PREFIX + inst.getId());
				addStmt(userId, Terms.PRO_HOLDSROLEINTIME, roleNode);
				addStmt(roleNode, Terms.PRO_WITHROLE, Terms.SCORO_AFFILIATE);
				addStmt(roleNode, Terms.PRO_RELATESTOORGANIZATION, instIri);
				addStmt(instIri, RDF.TYPE, FOAF.ORGANIZATION);
				addLiteralStmt(instIri, FOAF.NAME, inst.getName());
			}
		}
	}
	
}
