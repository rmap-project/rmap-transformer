package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.DiscoConverter;
import info.rmapproject.transformer.vocabulary.Terms;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;


/** 
 * Performs mapping from OSF Registration Java model to RDF DiSCO model.  
 * (Java Model -> RDF).
 * @author khanson
 *
 */
public class OsfRegistrationDiscoConverter extends DiscoConverter {

	private Registration reg;
	private static final String DEFAULT_CREATOR = Terms.RMAPAGENT_NAMESPACE + "RMap-OSF-Harvester-0.1";
	private static final String DEFAULT_DESCRIPTION = "Record harvested from OSF API";
	private static final String OSF_PATH_PREFIX = "http://osf.io/";
	//TODO: replace these with proper ontology term!
	private static final String OSF_REGISTRATION = "http://osf.io/terms/Registration";
	private static final String OSF_CATEGORY = "http://osf.io/terms/category/";
	
	/**
	 * Initiates converter - uses values provided for discoCreator and discoDescription
	 * @param record
	 */
	public OsfRegistrationDiscoConverter(Registration reg, URI discoCreator, String discoDescription){
		super(discoCreator, discoDescription);
		if (reg==null){
			throw new IllegalArgumentException("Record cannot be null");
		}
		this.reg = reg;
	}
		
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription
	 * @param record
	 */
	public OsfRegistrationDiscoConverter(Registration reg){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
		if (reg==null){
			throw new IllegalArgumentException("Record cannot be null");
		}
		this.reg = reg;
	}
	
	@Override
	public OutputStream generateDiscoRdf() throws Exception 	{		
		
		Model disco = new LinkedHashModel();
		
		Statement stmt = null;
		BNode discoNode = factory.createBNode();
		
		IRI regId = factory.createIRI(OSF_PATH_PREFIX + reg.getId());
		
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
	
		stmt = factory.createStatement(discoNode, Terms.ORE_AGGREGATES, regId);
		disco.add(stmt);
		
		//*** REGISTRATION TOP LEVEL DESCRIPTION ***
		stmt = factory.createStatement(regId, RDF.TYPE, factory.createIRI(OSF_REGISTRATION));
		disco.add(stmt);
		
		stmt = factory.createStatement(regId, DCTERMS.ISSUED, factory.createLiteral(reg.getDate_registered()));
		disco.add(stmt);
		
//		//TODO: add these - not yet part of API, but soon...
//		if (reg.getArkId()!=null){
//			stmt = factory.createStatement(regId, DCTERMS.IDENTIFIER, factory.createIRI(reg.getArkId()));
//			disco.add(stmt);			
//		}
//		if (reg.getDoi()!=null){
//			stmt = factory.createStatement(regId, DCTERMS.IDENTIFIER, factory.createIRI(reg.getDoi()));
//			disco.add(stmt);			
//		}
		
		IRI category = mapCategoryToIri(reg.getCategory());
		stmt = factory.createStatement(regId, DCTERMS.TYPE, category);
		disco.add(stmt);

		stmt = factory.createStatement(regId, DCTERMS.TITLE, factory.createLiteral(reg.getTitle()));
		disco.add(stmt);
		
		if (reg.getDescription()!=null){
			stmt = factory.createStatement(regId, DCTERMS.DESCRIPTION, factory.createLiteral(reg.getDescription()));
			disco.add(stmt);
		}
		
		//TODO:Not mapped yet
//		if (reg.getRegistered_meta().getSummary()!=null){
//			stmt = factory.createStatement(discoNode, DCTERMS.DESCRIPTION, factory.createLiteral(reg.getDescription()));
//			disco.add(stmt);
//		}
		
		List<Contributor> contributors = reg.getContributors();
		for (Contributor contributor : contributors) {
			disco.addAll(processContributorNode(contributor, regId));
		}
		
				
		
		List<Node> children = reg.getChildren();
		for (Node child : children) {
			disco.addAll(processChildNode(child, discoNode, regId));			
		}
		

		//fill in
		
		
		OutputStream rdf = generateRdf(disco);
		
		return rdf;		
	}
	
	private IRI mapCategoryToIri(Category category) throws Exception {
		String cat = URLEncoder.encode(category.value(), "UTF-8");
		return factory.createIRI(OSF_CATEGORY + cat);
	}
	

	private Model processContributorNode(Contributor contributor, IRI regId){
		Model model = new LinkedHashModel();
		Statement stmt = null;
		//TODO... does this have /user/ in the path?
		IRI userId = factory.createIRI(OSF_PATH_PREFIX + contributor.getId());
		
		stmt = factory.createStatement(regId, DCTERMS.CONTRIBUTOR, userId);
		model.add(stmt);
		
		stmt = factory.createStatement(userId, RDF.TYPE, FOAF.PERSON);
		model.add(stmt);
		
		return model;
	}
	
	private Model processChildNode(Node child, BNode discoNode, IRI regId){
		Model model = new LinkedHashModel();
		Statement stmt = null;
		
		IRI childId = factory.createIRI(OSF_PATH_PREFIX + child.getId());
		
		stmt = factory.createStatement(discoNode, Terms.ORE_AGGREGATES, childId);
		model.add(stmt);

		stmt = factory.createStatement(regId, DCTERMS.HAS_PART, childId);
		model.add(stmt);
		
		stmt = factory.createStatement(regId, RDF.TYPE, factory.createIRI(OSF_REGISTRATION));
		model.add(stmt);
		
		return model;
	}
	

	
}
