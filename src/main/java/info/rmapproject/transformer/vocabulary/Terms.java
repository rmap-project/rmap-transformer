package info.rmapproject.transformer.vocabulary;

import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;

/**
 * Vocabulary constants for the RMapProject Metadata Element Set, version 1.0
 * 
 * @see http://rmap-project.org/elements/
 * @author Karen Hanson
 */
public class Terms {
	
	 /*RMap vocabulary constants*/
	public static final String RMAP_NAMESPACE = "http://rmap-project.org/rmap/terms/";
	public static final String RMAP_PREFIX = "rmap";
	public static final String DISCO = "DiSCO";

	/*ORE vocabulary constants*/
	public static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";
	public static final String ORE_PREFIX = "ore";
	public static final String AGGREGATES = "aggregates";

	/*VCARD vocabulary constants*/
	public static final String VCARD_NAMESPACE = "https://www.w3.org/2006/vcard/ns#";
	public static final String VCARD_PREFIX = "vcard";
	public static final String ADDITIONALNAME = "additional-name";
	

	/*PRO vocabulary constants*/
	public static final String PRO_NAMESPACE = "http://purl.org/spar/pro/";
	public static final String PRO_PREFIX = "pro";
	public static final String HOLDSROLEINTIME = "holdsRoleInTime";
	public static final String ROLEINTIME = "RoleInTime";
	public static final String WITHROLE = "withRole";
	public static final String RELATESTOORGANIZATION = "relatesToOrganization";


	/*FRAPO vocabulary constants*/
	public static final String FRAPO_NAMESPACE = "http://purl.org/cerif/frapo/";
	public static final String FRAPO_PREFIX = "frapo";
	public static final String ISOUTPUTOF = "isOutputOf";
	public static final String ISFUNDEDBY = "isFundedBy";
	public static final String ISAWARDEDBY = "isAwardedBy";
	public static final String FUNDINGAGENCY = "FundingAgency";
	public static final String FUNDING = "Funding";

	/*FABIO vocabulary constants*/
	public static final String FABIO_NAMESPACE = "http://purl.org/spar/fabio/";
	public static final String FABIO_PREFIX = "fabio";
	public static final String EXPRESSION = "Expression";

	/*SCORO vocabulary constants*/
	public static final String SCORO_NAMESPACE = "http://purl.org/spar/scoro/";
	public static final String SCORO_PREFIX = "scoro";
	public static final String AFFILIATE = "affiliate";
		

	/*Premis vocabulary constants*/
	public static final String PREMIS_NAMESPACE = "http://www.loc.gov/premis/rdf/v1#";
	public static final String PREMIS_PREFIX = "premis";
	public static final String FILE = "File";
	
	/*Harvester names*/
	public static final String RMAPAGENT_NAMESPACE = "http://rmap-project.org/rmap/agents/";
		
	/*other terms*/
	public static final String MAILTO = "mailto:";
	
	/** rmap:DiSCO */
	public static final IRI RMAP_DISCO;
	/** ore:aggregates */
	public static final IRI ORE_AGGREGATES;
	/** vcard:additional-name */
	public static final IRI VCARD_ADDITIONALNAME;
	/** pro:holdsRoleInTime */
	public static final IRI PRO_HOLDSROLEINTIME;
	/** pro:RoleInTime */
	public static final IRI PRO_ROLEINTIME;
	/** pro:withRole */
	public static final IRI PRO_WITHROLE;
	/** pro:relatesToOrganization */
	public static final IRI PRO_RELATESTOORGANIZATION;
	/** scoro:Affiliate */
	public static final IRI SCORO_AFFILIATE;
	/** frapo:isOutputOf */
	public static final IRI FRAPO_ISOUTPUTOF;
	/** frapo:isFundedBy */
	public static final IRI FRAPO_ISFUNDEDBY;
	/** frapo:isAwardedBy */
	public static final IRI FRAPO_ISAWARDEDBY;
	/** frapo:FundingAgency */
	public static final IRI FRAPO_FUNDINGAGENCY;
	/** frapo:Funding */
	public static final IRI FRAPO_FUNDING;
	/** fabio:Expression */
	public static final IRI FABIO_EXPRESSION;
	/** premis:File */
	public static final IRI PREMIS_FILE;
	
	static {
		final ValueFactory f = SimpleValueFactory.getInstance();
		//rmap object types 
		RMAP_DISCO = f.createIRI(RMAP_NAMESPACE, DISCO);
		ORE_AGGREGATES = f.createIRI(ORE_NAMESPACE, AGGREGATES);
		VCARD_ADDITIONALNAME = f.createIRI(VCARD_NAMESPACE, ADDITIONALNAME);
		PRO_HOLDSROLEINTIME = f.createIRI(PRO_NAMESPACE, HOLDSROLEINTIME);
		PRO_ROLEINTIME = f.createIRI(PRO_NAMESPACE, ROLEINTIME);
		PRO_WITHROLE = f.createIRI(PRO_NAMESPACE, WITHROLE);
		PRO_RELATESTOORGANIZATION = f.createIRI(PRO_NAMESPACE, RELATESTOORGANIZATION);
		SCORO_AFFILIATE = f.createIRI(SCORO_NAMESPACE, AFFILIATE);		
		FRAPO_ISFUNDEDBY = f.createIRI(FRAPO_NAMESPACE, ISFUNDEDBY);		
		FRAPO_ISOUTPUTOF = f.createIRI(FRAPO_NAMESPACE, ISOUTPUTOF);		
		FRAPO_ISAWARDEDBY = f.createIRI(FRAPO_NAMESPACE, ISAWARDEDBY);		
		FRAPO_FUNDINGAGENCY = f.createIRI(FRAPO_NAMESPACE, FUNDINGAGENCY);		
		FRAPO_FUNDING = f.createIRI(FRAPO_NAMESPACE, FUNDING);		
		FABIO_EXPRESSION = f.createIRI(FABIO_NAMESPACE, EXPRESSION);	
		PREMIS_FILE = f.createIRI(PREMIS_NAMESPACE, FILE);		
	}
}