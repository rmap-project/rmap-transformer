package info.rmapproject.transformer.share;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

/**
 * Maps SHARE Record model to RMap DiSCO RDF
 * @author khanson
 *
 */
public class SHAREModelToDiscoMapper {

	private static OutputStream generateRdf(Model model) throws Exception {
		RDFFormat rdfFormat = null;
		OutputStream bOut = new ByteArrayOutputStream();
		try {
			rdfFormat = RDFFormat.TURTLE;
			Rio.write(model, bOut, rdfFormat);
		} catch (Exception e) {
			throw new Exception("Exception thrown creating RDF from statement list", e);
		}
		return bOut;	
		
	}
	
	public OutputStream toRDF(Object model) throws Exception	{		
		if (model==null){
			throw new Exception("Null or empty model");
		}
		
		Model disco = new LinkedHashModel();
		
		ValueFactory factory = SimpleValueFactory.getInstance();
		Statement stmt = factory.createStatement(null, null, null);
		disco.add(stmt);
		
		OutputStream rdf = generateRdf(disco);
		
		return rdf;		
	}
	
}
