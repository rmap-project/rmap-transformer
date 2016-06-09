package info.rmapproject.transformer.osf;

import info.rmapproject.transformer.DiscoBuilder;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.vocabulary.Terms;

import java.util.List;

import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
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

public class OsfNodeDiscoBuilder extends DiscoBuilder {

	private Node record;

	protected static final String DEFAULT_CREATOR = Terms.RMAPAGENT_NAMESPACE + "RMap-OSF-Harvester-0.1";
	protected static final String DEFAULT_DESCRIPTION = "Record harvested from OSF API";
	protected static final String OSF_PATH_PREFIX = "http://osf.io/";
	//TODO: replace these with proper ontology term!
	protected static final String OSF_TERMS_PREFIX = OSF_PATH_PREFIX + "terms/";
	protected static final String OSF_REGISTRATION = OSF_TERMS_PREFIX + "Registration";
	protected static final String OSF_PROJECT = OSF_TERMS_PREFIX + "Project";
	
	
	/**
	 * Constructor for Node to pass default params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	public OsfNodeDiscoBuilder(){
		super(DEFAULT_CREATOR, DEFAULT_DESCRIPTION);
	}
	
	
	/**
	 * Constructor for Node to pass params up to super()
	 * @param discoDescription
	 */
	public OsfNodeDiscoBuilder(String discoDescription){
		super(DEFAULT_CREATOR, discoDescription);
	}
		
	
	/**
	 * Constructor for Node to pass params up to super()
	 * @param discoCreator
	 * @param discoDescription
	 */
	public OsfNodeDiscoBuilder(String discoCreator, String discoDescription){
		super(discoCreator, discoDescription);
	}


	
	@Override
	public void setRecord(Object record) {
		this.record = (Node) record;
		discoId = null;
		model = null;
	}
	
	@Override
	public Model getModel()	{
		model = new LinkedHashModel();		
		discoId = factory.createBNode(); 					
		//disco header
		addDiscoHeader();
		addNode(record, null);
		
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

		addForkedFrom(node.getForked_from(), nodeId);
		
		addLiteralStmt(nodeId, DCTERMS.CREATED, node.getDate_created());

		addLiteralStmt(nodeId, DCTERMS.TITLE, node.getTitle());
		addLiteralStmt(nodeId, DCTERMS.DESCRIPTION, node.getDescription());
		
		addContributors(node.getContributors(), nodeId);				
		addChildNodes(node.getChildren(), nodeId);
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
	 * Add contributor metadata to Model
	 * @param contributors
	 * @param nodeId
	 */
	protected void addContributors(List<Contributor> contributors, IRI nodeId){
		if (contributors!=null){
			for (Contributor contributor : contributors) {
				//TODO... does this have /user/ in the path?
				IRI userId = factory.createIRI(OSF_PATH_PREFIX + contributor.getId());
				addStmt(nodeId, DCTERMS.CONTRIBUTOR, userId);
				addStmt(userId, RDF.TYPE, FOAF.PERSON);
			}
		}	
	}

	protected void addForkedFrom(String forkRef, IRI nodeId){
		if (forkRef!=null && forkRef.length()>0){
			String forkedFromNodeId = TransformUtils.extractLastSubFolder(forkRef);
			IRI forkId = factory.createIRI(OSF_PATH_PREFIX + forkedFromNodeId);
			addStmt(nodeId, Terms.PROV_WASDERIVEDFROM, forkId);			
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
			NodeBase node = (NodeBase) root;
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
			addStmt(fileId, RDF.TYPE, Terms.FABIO_COMPUTERFILE);		
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
			String cat = category.value();
			cat = cat.replace(" ", "-");
			String firstletter = cat.substring(0,1);
			firstletter = firstletter.toUpperCase();
			cat = firstletter + cat.substring(1);
			return factory.createIRI(OSF_TERMS_PREFIX + cat);			
		} else {
			return null;
		}
	}

	
}
