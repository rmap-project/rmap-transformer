package info.rmapproject.transformer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports iteration through files on a particular path to retrieve records one at a time
 * @author khanson
 */
public abstract class FileBasedRecordImporter {

	/**
	 * Stores file list so that we don't need to rebuild the file list for each record request
	 */
	protected List<File> inputFileList;
	
	/**
	 * Stores current position in file list
	 */
	protected Integer fileIndex = -1;
	
	/**
	 * Stores size of file list
	 */
	protected Integer fileListSize;
		
	/**
	 * Stores number of records in current file
	 */
	protected Integer recordArraySize;
	
	/**
	 * Stores current position in record array
	 */
	protected Integer recordArrayIndex = -1;
	
	protected FileBasedRecordImporter(String inputPath, String inputFileExt) {
		loadInputFileList(inputPath, inputFileExt);
	}
		
    private static final Logger log = LoggerFactory
	            .getLogger(FileBasedRecordImporter.class);
    
    /**
     * abstract method for retrieving next record in import - 
     * different kinds of records will have different requirements for retrieval
     * @return
     */
	public abstract String getNextRecord();
	
	/**
	 * Loads list of files to be processed
	 * @param inputPath
	 * @param inputFileExt
	 */
	protected void loadInputFileList(String inputPath, String inputFileExt) {
		log.info("Using input path '" + inputPath + "' and filtering by file extension '" + inputFileExt + "'");
		
		//if input folder isn't there, throw error.
		File inputFolder = new File(inputPath);
				
		if (!inputFolder.exists() 
				|| !inputFolder.isDirectory()
				|| !inputFolder.canRead()) {
			throw new RuntimeException("Input folder does not exist, or is inaccessible.");
		}
		
		//all is well... set file list
    	setInputFileList(inputFolder, inputFileExt);
    	this.fileListSize=inputFileList.size();
    	
      }
    
    /**
     * Set list of files that will be iterated through to get records.
     * @param inputFolder
     * @param inputFileExt
     */
	protected void setInputFileList(File inputFolder, String inputFileExt) {
		File[] files = 
				inputFolder.listFiles((File file) -> file.isFile() && file.getName().endsWith("."+inputFileExt));
				
		List <File> filelist = Arrays.asList(files);
		this.inputFileList = filelist;
    }


    /**
     * Checks whether the current record is the last record in the last file of the import
     */
    public boolean isLastRecord() {
    	//are we on last record of last file?
    	return (fileListSize==(fileIndex+1) && recordArraySize==(recordArrayIndex+1));
    }
	
}