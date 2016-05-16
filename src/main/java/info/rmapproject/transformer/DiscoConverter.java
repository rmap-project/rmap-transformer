package info.rmapproject.transformer;

import info.rmapproject.cos.share.client.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

public abstract class DiscoConverter {

	protected String discoCreator;
	protected String discoDescription;
	
	protected static ValueFactory factory = SimpleValueFactory.getInstance();
	
	/**
	 * Initiates converter
	 * @param discoCreator
	 * @param discoDescription
	 */
	public DiscoConverter(String discoCreator, String discoDescription){
		this.discoCreator = Utils.setEmptyToNull(discoCreator);	
		this.discoDescription = Utils.setEmptyToNull(discoDescription);		
	}
	
	
	/**
	 * Initiates converter
	 * @param discoCreator
	 * @param discoDescription
	 */
	public DiscoConverter(URI discoCreator, String discoDescription){
		if (discoCreator!=null) {
			this.discoCreator = discoCreator.toString();
			this.discoCreator = Utils.setEmptyToNull(this.discoCreator);	
		}
		else {
			this.discoCreator = null;
		}	
		this.discoDescription = Utils.setEmptyToNull(discoDescription);	
	}

	public abstract OutputStream generateDiscoRdf() throws Exception;

	
	protected static OutputStream generateRdf(Model model) throws Exception {
		OutputStream bOut = new ByteArrayOutputStream();
		try {
			Rio.write(model, bOut, RDFFormat.TURTLE);
		} catch (Exception e) {
			throw new Exception("Exception thrown creating RDF from statement list", e);
		}
		return bOut;	
	}

	public DiscoConverter() {
		super();
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

}