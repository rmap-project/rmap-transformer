package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.DiscoModel;
import info.rmapproject.transformer.vocabulary.Terms;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;


/** 
 * Performs mapping from OSF Registration Java model to RDF DiSCO model.  
 * (Java Model -> RDF).
 * @author khanson
 *
 */
public class OsfNodeDiscoModel extends DiscoModel {

	private Node node;

	protected static final String DEFAULT_CREATOR = Terms.RMAPAGENT_NAMESPACE + "RMap-OSF-Harvester-0.1";
	protected static final String DEFAULT_DESCRIPTION = "Record harvested from OSF API";
	protected static final String OSF_PATH_PREFIX = "http://osf.io/";
	//TODO: replace these with proper ontology term!
	protected static final String OSF_REGISTRATION = "http://osf.io/terms/Registration";
	protected static final String OSF_PROJECT = "http://osf.io/terms/Project";
	protected static final String OSF_CATEGORY = "http://osf.io/terms/category/";
	
	/**
	 * Constructor for Registrations to pass params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	protected OsfNodeDiscoModel(URI discoCreator, String discoDescription){
		super(discoCreator, discoDescription);
	}
	/**
	 * Constructor for Registrations to pass params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	protected OsfNodeDiscoModel(String discoCreator, String discoDescription){
		super(discoCreator, discoDescription);
	}
	
	/**
	 * Initiates converter - uses values provided for discoCreator and discoDescription
	 * @param record
	 */
	public OsfNodeDiscoModel(Node node, URI discoCreator, String discoDescription){
		this(discoCreator, discoDescription);
		if (node==null){
			throw new IllegalArgumentException("Record cannot be null");
		}
		this.node = node;
	}
		
	/**
	 * Initiates converter - will assign default values to discoCreator and discoDescription
	 * @param record
	 */
	public OsfNodeDiscoModel(Node node){
		this(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
		if (node==null){
			throw new IllegalArgumentException("Node cannot be null");
		}
		this.node = node;
	}
	
	@Override
	public Model getModel() throws Exception 	{		
								
		//disco header
		addDiscoHeader();
		addNode(node, null);
		
		//fill in
		
		return model;		
	}

	private void addNode(Node node, IRI parentId){
				
		IRI nodeId = factory.createIRI(OSF_PATH_PREFIX + node.getId());
		addStmt(discoId, Terms.ORE_AGGREGATES, nodeId);

		if (parentId!=null){
			addStmt(parentId, DCTERMS.HAS_PART, nodeId);
		}

		IRI category = mapCategoryToIri(node.getCategory());
		addStmt(nodeId, RDF.TYPE, category);
		
		addLiteralStmt(nodeId, DCTERMS.CREATED, node.getDate_created());
		
//		//TODO: add these - not yet part of API, but soon...
//		addIriStmt(regId, DCTERMS.IDENTIFIER, registration.getArkId());
//		addIriStmt(regId, DCTERMS.IDENTIFIER, registration.getDoi());
		

		addLiteralStmt(nodeId, DCTERMS.TITLE, node.getTitle());
		addLiteralStmt(nodeId, DCTERMS.DESCRIPTION, node.getDescription());
		
		addContributors(node.getContributors(), nodeId);				
		addChildNodes(node.getChildNodes(), nodeId);
		addFiles(node, nodeId);
	}
	
	
	/**
	 * Add child node metadata to model
	 * @param child
	 * @param regId
	 */
	private void addChildNodes(List<Node> children, IRI parentId){
		if (children!=null){
			for (Node child : children) {
				addNode(child, parentId);
			}
		}
		
	}
	
	
	
	/**
	 * Convert an OSF API path to a OSF URL - will only work correctly if ID at end of path.
	 * e.g. for https://api.osf.io/v2/registrations/sdfkj/ sdfkj will be extracted
	 * @param linkUrl
	 * @return
	 */
	protected static Resource extractOsfNodeId(String linkUrl){
		if (linkUrl!=null && linkUrl.length()>0){
			if (linkUrl.endsWith("/")){
				linkUrl = linkUrl.substring(0,linkUrl.length()-1);
			}
			String id = linkUrl.substring(linkUrl.lastIndexOf('/') + 1);							
			Resource origNodeId = factory.createIRI(OSF_PATH_PREFIX + id);
			return origNodeId;
		} else {
			return null;
		}		
	}
	
	
	/**
	 * Add contributor metadata to Model
	 * @param contributors
	 * @param regId
	 */
	protected void addContributors(List<Contributor> contributors, IRI regId){
		if (contributors!=null){
			for (Contributor contributor : contributors) {
				//TODO... does this have /user/ in the path?
				IRI userId = factory.createIRI(OSF_PATH_PREFIX + contributor.getId());
				addStmt(regId, DCTERMS.CONTRIBUTOR, userId);
				addStmt(userId, RDF.TYPE, FOAF.PERSON);
			}
		}	
	}

	
	/**
	 * Add file metadata to Model
	 * @param file
	 * @param discoNode
	 * @param regId
	 */
	protected void addFiles(Object root, IRI regId) {
		List<File> files = null;
		if (root instanceof Registration) {
			Registration registration = (Registration) root;
			files = registration.getFiles();
		} else if (root instanceof File) {
			File file = (File) root;
			files = file.getFiles();
		} else if (root instanceof Node){
			Node node = (Node) root;
			files = node.getFiles();
		}
		
		if (files!=null)	{
			for (File file : files){
				addFile(file, regId);
			}
		}
	}

	/**
	 * Add OSF file metadata to map
	 * @param file
	 * @param parentRegId
	 */
	protected void addFile(File file, IRI parentRegId){
		if (file.getKind().equals("file")){
			IRI fileId = factory.createIRI(file.getLinks().get("download").toString());
			addStmt(parentRegId, DCTERMS.HAS_PART, fileId);
			addStmt(fileId, RDF.TYPE, Terms.PREMIS_FILE);		
			//TODO:giving these temp predicates... need to find terms for these
			addLiteralStmt(fileId, RDFS.LABEL, file.getName());
			addLiteralStmt(fileId, SKOS.ALT_LABEL, file.getMaterialized_path());
			
			addLiteralStmt(fileId, DCTERMS.CREATED, file.getDate_created());
			addLiteralStmt(fileId, DCTERMS.MODIFIED, file.getDate_modified());
		} else {
			addFiles(file, parentRegId);
		}
	}
	
	
	
	/**
	 * Map category to an IRI
	 * @param category
	 * @return
	 * @throws Exception
	 */
	protected IRI mapCategoryToIri(Category category) {
		if (category!=null){
			try {
				String cat = URLEncoder.encode(category.value(), "UTF-8");
				return factory.createIRI(OSF_CATEGORY + cat);			
			}
			catch (UnsupportedEncodingException e){
				throw new RuntimeException("could not convert category to IRI", e);
			}
		} else {
			return null;
		}
	}

	
}
