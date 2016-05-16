package info.rmapproject.transformer.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File list class to create and hold a list of files, along with functions 
 * to support iteration through the list.
 * @author khanson
 *
 */
public class FileList {

	/**
	 * List of files
	 */
	private List<File> files;
	
	/**
	 * Size of file list
	 */
	private Integer size;
		
	/**
	 * Stores current position in file list
	 */
	protected Integer currFileIndex = -1;
	
	
    private static final Logger log = LoggerFactory
            .getLogger(FileList.class);
	
    /**
     * Initiates file path based on path defined and filtered by file extension.
     * Note: does not currently iterate through sub-folders.
     * @param inputfilepath
     * @param inputfileext
     */
	public FileList(String inputfilepath, String inputfileext){
		if (inputfilepath==null){
			throw new IllegalArgumentException("inputfilepath cannot be null");
		}
		if (inputfileext==null){
			throw new IllegalArgumentException("inputfileext cannot be null");
		}

		loadInputFileList(inputfilepath, inputfileext);
	}	

	public Integer getSize() {
		return this.size;
	}

	public Integer getCurrFileIndex() {
		return this.currFileIndex;
	}

	/**
	 * Loads list of files to be processed
	 * @param inputPath
	 * @param inputFileExt
	 */
	protected void loadInputFileList(String inputPath, String inputFileExt) {
		log.info("Generating file list using input path '" + inputPath + "' and filtering by file extension '" + inputFileExt + "'");
		
		//if input folder isn't there, throw error.
		File inputFolder = new File(inputPath);
				
		if (!inputFolder.exists() 
				|| !inputFolder.isDirectory()
				|| !inputFolder.canRead()) {
			throw new RuntimeException("Input folder does not exist, or is inaccessible.");
		}		
		
		File[] files = inputFolder.listFiles(
							(File file) -> 
								file.isFile() && file.getName().endsWith("."+inputFileExt)
							);
					
		List <File> filelist = Arrays.asList(files);
		this.files = filelist;		
    	this.size=filelist.size();    	
      }
	
	/**
	 * retrieve the next file in the list and update the current fileIndex.
	 * @return
	 */
	public File next(){
		if (!this.hasNext()){
			throw new RuntimeException("You have reached the end of the file list.");
		}
		this.currFileIndex=this.currFileIndex+1;
		return this.files.get(this.currFileIndex);		
	}
		
	/**
	 * Determines whether current file is last file
	 * @return
	 */
    public boolean hasNext() {
    	//are we on last record of last file?
    	return (this.size!=(currFileIndex+1));
    }
	
	
}
