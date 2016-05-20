package info.rmapproject.transformer;

import info.rmapproject.cos.share.client.utils.Utils;
import info.rmapproject.transformer.vocabulary.Terms;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

public abstract class DiscoModel {

	protected Model model;	
	protected BNode discoId;
	
	protected String discoCreator=null;
	protected String discoDescription=null;
	
	protected static ValueFactory factory = SimpleValueFactory.getInstance();

	protected DiscoModel() {
		super();
		this.model = new LinkedHashModel();		
		discoId = factory.createBNode(); 
	}

	/**
	 * Initiates converter
	 * @param discoCreator
	 * @param discoDescription
	 */
	protected DiscoModel(String discoCreator, String discoDescription){
		this();
		this.discoCreator = Utils.setEmptyToNull(discoCreator);	
		this.discoDescription = Utils.setEmptyToNull(discoDescription);		
	}
		
	/**
	 * Initiates converter
	 * @param discoCreator
	 * @param discoDescription
	 */
	public DiscoModel(URI discoCreator, String discoDescription){
		this();
		if (discoCreator!=null) {
			this.discoCreator = discoCreator.toString();
			this.discoCreator = Utils.setEmptyToNull(this.discoCreator);	
		}
		else {
			this.discoCreator = null;
		}	
		this.discoDescription = Utils.setEmptyToNull(discoDescription);	
	}

	public abstract Model getModel();
	public abstract void setRecord(Object record);

	/**
	 * If all values are not null add a statement to the model 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	protected void addStmt(Resource subject, IRI predicate, Value object){
		if (subject!=null && predicate!=null && object!=null){
			Statement stmt = factory.createStatement(subject, predicate, object);
			model.add(stmt);
		}
	}	
	
	/**
	 * If all values are not null add a statement to the model with an IRI as the object
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	protected void addIriStmt(Resource subject, IRI predicate, String object){
		if (object!=null && object.length()>0){
			addStmt(subject, predicate, factory.createIRI(object));
		}
	}
	
	/**
	 * If all values are not null add a statement to the model with a Literal as the object
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	protected void addLiteralStmt(Resource subject, IRI predicate, String object){
		if (object!=null && object.length()>0){
			addStmt(subject, predicate, factory.createLiteral(object));
		}		
	}

	/**
	 * If all values are not null add a statement to the model with a Date Literal as the object
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	protected void addLiteralStmt(Resource subject, IRI predicate, Date object){
		if (object!=null){
			addStmt(subject, predicate, factory.createLiteral(object));
		}		
	}

	/**
	 * If all values are not null add a statement to the model with an Integer Literal as the object
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	protected void addLiteralStmt(Resource subject, IRI predicate, Integer object){
		if (object!=null){
			addStmt(subject, predicate, factory.createLiteral(object));
		}		
	}
	
	/**
	 * Creates a statement using an object that is either a URI or a Literal - tries to create as URI first, if it fails
	 * it create it as a Literal. Returns null if subject and object have same value.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	protected Statement addIRIorLiteralStmt(IRI subject, IRI predicate, String object) {
		Statement stmt = null;
		if (!subject.toString().equals(object.toString())){
			try {
				URI uri = new URI (object);
				addIriStmt(subject, predicate, uri.toString());			
			}catch (Exception e){
				addLiteralStmt(subject, predicate, object);			
			}
		}
		return stmt;
	}
	
	
	/**
	 * Looks at list of identifiers. If there are values in the list, it takes the first one and
	 * generates an IRI to use as the primary identifier in the RDF. Otherwise returns a blank node.
	 * @param sameAsList
	 * @return
	 */
	protected static Resource getFirstIriOrBNode(List<URI> uriList) {
		Resource val = null;
		if (uriList==null||uriList.size()==0){
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
	protected static Resource getIriOrBNode(URI uriVal) {
		Resource val = null;
		if (uriVal==null){
			val = factory.createBNode();
		}
		else {
			val = factory.createIRI(uriVal.toString());				
		}		
		return val;
	}

	/**
	 * Starts building DiSCO model by generating header statements.
	 */
	protected void addDiscoHeader(){
		//Generate DiSCO header
		addStmt(discoId, RDF.TYPE, Terms.RMAP_DISCO);
		addIriStmt(discoId, DCTERMS.CREATOR, discoCreator);
		addLiteralStmt(discoId, DCTERMS.DESCRIPTION, discoDescription);
	}

}