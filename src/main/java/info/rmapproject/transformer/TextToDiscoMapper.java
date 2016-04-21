package info.rmapproject.transformer;

import java.io.OutputStream;

/**
 * Supports mapping a single text-based record such as XML, JSON, CSV to a Java object model
 * @author khanson
 *
 */
public interface TextToDiscoMapper {
	/**
	 * Pass a single record as text to be converted to RDF. 
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public OutputStream toDiscoRdf(String record) throws Exception;
}
