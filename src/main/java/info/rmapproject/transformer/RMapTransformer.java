package info.rmapproject.transformer;

import info.rmapproject.transformer.share.JsonToDiscoMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */

/**
 * Imports SHARE JSON into a SHARE object and then transforms it to and RMap DiSCO
 * @author khanson
 *
 */
public class RMapTransformer {
    
    /** Path of file containing API data as JSON */
    @Option(name = "-t", aliases = {"-type"}, usage = "Type of import. Options available: SHARE (default: SHARE)")
    public String type = "SHARE";
	
    /** Path of file containing API data as JSON */
    @Option(name = "-i", aliases = {"-inputpath"}, usage = "Path that holds input data files (default: current folder")
    public String inputpath = ".";
    
    /** File extension for metadata file(s) */
    @Option(name = "-iex", aliases = {"-inputfileext"}, usage = "File extension for input data files (default: json)")
    public String inputFileExtension = "json";
    
    /** Path of file containing API data as JSON */
    @Option(name = "-o", aliases = {"-outputpath"}, usage = "Path of output files(s) for DiSCOs")
    public String outputpath = ".";
    
    /** File extension for DiSCO metadata file(s) */
    @Option(name = "-oex", aliases = {"-outputfileext"}, usage = "File extension for output data files (default: rdf)")
    public String outputFileExtension = "rdf";

    /** Request for help/usage documentation */
    @Option(name = "-h", aliases = {"-help", "--help"}, usage = "Print help message")
    public boolean help = false;
    
    private static final Integer COUNTER_START= 10000000;
    
    private static final String RECORD_ROOT_ELEMENT = "results";
    
    private static final Logger log = LoggerFactory
	            .getLogger(RMapTransformer.class);
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        final RMapTransformer application = new RMapTransformer();

        CmdLineParser parser =
                new CmdLineParser(application, ParserProperties.defaults()
                        .withUsageWidth(100));
        parser.getProperties().withUsageWidth(80);

        try {
            parser.parseArgument(args);

            /* Handle general options such as help */
            if (application.help) {
                parser.printUsage(System.err);
                System.err.println();
                System.exit(0);
            }

            /* Run the package generation application proper */
           	application.run();
             	
           	
        } catch (CmdLineException e) {
            /*
             * This is an error in command line args, just print out usage data
             * and description of the error.
             */
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        } catch (Exception e) {
        	//general error message
        	System.err.println(e.getMessage());
        	System.exit(1);
        }
    }	

	public void run() throws Exception{
		//if output folder isn't there, create it
		File outputFolder = new File(outputpath);
		if (!outputFolder.exists() || !outputFolder.isDirectory()) {
			outputFolder.mkdir();
		}
		
		//initiate importer for iteration through files
		FileBasedRecordImporter importer = null;
		TextToDiscoMapper mapper = null;
		
		//TODO: make this an enum, or handle this better
		if (type=="SHARE"){
			importer = new JsonFileBasedRecordImporter(inputpath, inputFileExtension, RECORD_ROOT_ELEMENT);			
			mapper = new JsonToDiscoMapper();
		}
		else { //defaults to SHARE
			importer = new JsonFileBasedRecordImporter(inputpath, inputFileExtension, RECORD_ROOT_ELEMENT);			
			mapper = new JsonToDiscoMapper();			
		}
		
		//Format for output file name
		String newfileName = outputFolder + "/disco_####." + outputFileExtension;
		
		//Reset counter
		Integer counter = COUNTER_START;
		
		do {
			String record = importer.getNextRecord();
			counter = counter + 1;
			
			//pass a JSON record to mapper class and get back RDF
			OutputStream rdf = mapper.toDiscoRdf(record);
			
			File outputFile = new File(newfileName.replace("####",counter.toString()));
			if  (outputFile.createNewFile()) {
				FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(rdf.toString());
				bw.close();
			}
			else {
				throw new RuntimeException("Could not create new DiSCO output file + " + outputFile.getName());
			}
			log.info("DiSCO RDF saved to file " + outputFile.getName());
			
		} while (!importer.isLastRecord());

		Integer totalTransformed = counter - COUNTER_START ;
		log.info("Transform complete! " + totalTransformed.toString() + " records processed.");
				
	}
	

}
