/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformer.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to support conversion of a DiSCO to a file.
 */
public class DiscoFile {
	
    /** The log. */
    protected static final Logger log = LoggerFactory.getLogger(DiscoFile.class);
    
	/** The filename of the DiSCO file. */
	private String filename;
	
	/** The filepath for the DiSCO file. */
	private String filepath;
	
	/** The DiSCO RDF. */
	private OutputStream rdf;
		
	/**
	 * Instantiates a new DiSCO file.
	 *
	 * @param rdf the DiSCO RDF
	 * @param filepath the file path
	 * @param filename the file name
	 */
	public DiscoFile(OutputStream rdf, String filepath, String filename){
		if (rdf==null){
			throw new IllegalArgumentException("rdf cannot be null");
		}
		if (filepath==null){
			throw new IllegalArgumentException("filepath cannot be null");
		}
		if (filename==null){
			throw new IllegalArgumentException("filename cannot be null");
		}
		this.rdf = rdf;
		if (filepath=="."){
			filepath="";
		}
		//add forward slash to end of filepath to make sure folder name parsed correctly
		//unless the filepath is blank, in which case it will write to classpath folder.
		if (filepath.length()>0 && (!filepath.endsWith("\\") && !filepath.endsWith("/"))){
			filepath = filepath + "/";
		}
		this.filepath = filepath;
		this.filename = filename;
	}
	
	/**
	 * Uses the DiSCO RDF to write a file to the filepath
	 */
	public void writeFile() {
		
		//if output folder isn't there, create it now
		File outputFolder = new File(this.filepath);
		if (!outputFolder.exists() || !outputFolder.isDirectory()) {
			outputFolder.mkdir();
		}		
		
		String writePath = null;
		writePath = this.filepath + this.filename;
		
		File outputFile = new File(writePath);
		try {
			if (outputFile.exists()) { // we will accept one duplicate
				log.error("File already exists. Creating file with '_duplicate' appended.  Please review to confirm whether to include this");
				writePath = this.filepath + this.filename + "_duplicate";
				outputFile = new File(writePath);
			}
			
			if  (outputFile.createNewFile()) {
				FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(rdf.toString());
				bw.close();
				//log.info("DiSCO RDF saved to file " + this.filename);
			}
			else {
				throw new Exception("Create new file failed");
			}
		} catch (Exception e){
			log.error("Stopping process: Could not create new DiSCO output file " + this.filename 
					+ ". Please verify the output folder is accessible and that this file does not already exist.", e);
			System.exit(0);		
		}
	}

	
	
	
	
}
