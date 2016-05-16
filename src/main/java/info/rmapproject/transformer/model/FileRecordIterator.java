package info.rmapproject.transformer.model;



/**
 * Supports iteration through files on a particular path to retrieve records one at a time
 * @author khanson
 */
public interface FileRecordIterator {
	
    /**
     * Method for retrieving next record from file
     * different kinds of records will have different requirements for retrieval
     * @return
     */
	public String next();

    /**
     * Checks whether the current record is the last record in the last file of the import
     */
    public boolean hasNext();
	
}