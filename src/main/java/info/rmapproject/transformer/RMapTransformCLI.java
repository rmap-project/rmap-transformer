package info.rmapproject.transformer;

import info.rmapproject.transformer.model.RecordDTO;
import info.rmapproject.transformer.model.RecordType;
import info.rmapproject.transformer.model.TransformType;
import info.rmapproject.transformer.osf.OsfClientService;
import info.rmapproject.transformer.osf.OsfNodeApiIterator;
import info.rmapproject.transformer.osf.OsfRegistrationApiIterator;
import info.rmapproject.transformer.osf.OsfUserApiIterator;
import info.rmapproject.transformer.share.ShareApiTransformIterator;
import info.rmapproject.transformer.share.ShareLocalTransformIterator;

import java.util.Iterator;

import org.kohsuke.args4j.Argument;
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
public class RMapTransformCLI {

	private static final String DEFAULT_TYPE = "SHARE";
	private static final String DEFAULT_SOURCE = "api";
	private static final String DEFAULT_INPUT_FILEEXT = "json";
	private static final Integer DEFAULT_NUM_RECORDS = 50;
	
    /** Type of import e.g. SHARE, OSF_REGISTRATION, OSF_USER */
    @Argument(index = 0, metaVar = "Transform type", usage = "Type of transform. Options available: "
    														+ "SHARE, "
    														+ "OSF_REGISTRATION, "
    														+ "OSF_NODE, "
    														+ "OSF_USER "
    														+ "(default: SHARE)")
    private String transformType = DEFAULT_TYPE;
        
    /** Source of data - local or api **/
    @Option(name="-src", aliases = {"-source"}, usage = "Source of the data - either api or local (default: api)")
    private String source = DEFAULT_SOURCE;
    
    /** API params **/
    @Option(name="-f", aliases = {"-queryfilters"}, usage = "API request filters formatted in the style of a querystring e.g. q=osf&size=30&sort=providerUpdatedDateTime (default: no filters)")
    public String filters = "";
	
    /** Path of file containing API data as JSON */
    @Option(name = "-i", aliases = {"-inputpath"}, usage = "Path that holds input data files (default: current folder")
    public String inputpath = ".";
    
    /** File extension for metadata file(s) */
    @Option(name = "-iex", aliases = {"-inputfileext"}, usage = "File extension for input data files (default: json)")
    public String inputFileExtension = DEFAULT_INPUT_FILEEXT;
    
    /** Path of file containing API data as JSON */
    @Option(name = "-o", aliases = {"-outputpath"}, usage = "Path of output files(s) for DiSCOs")
    public String outputpath = ".";
    
    /** Identifier of record to import */
    @Option(name = "-id", aliases = {"-identifier"}, usage = "ID to import - supports import of a single record (valid for OSF requests only)")
    public String identifier = "";

    /** DiSCO description */
    @Option(name = "-desc", aliases = {"-discodesc"}, usage = "Custom Description for DiSCO")
    public String discoDescription = "";
    
    /** File extension for DiSCO metadata file(s) */
    @Option(name = "-n", aliases = {"-numrecords"}, usage = "Maximum number of records to be converted. (default: 50)")
    public Integer numrecords = DEFAULT_NUM_RECORDS;

    /** Request for help/usage documentation */
    @Option(name = "-h", aliases = {"-help", "--help"}, usage = "Print help message")
    public boolean help = false;
        
    private static final Logger log = LoggerFactory.getLogger(RMapTransformCLI.class);
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        final RMapTransformCLI application = new RMapTransformCLI();

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
        	e.printStackTrace();
        	System.exit(1);
        }
    }	

	public void run() throws Exception{

		Integer totalTransformed = 0;
		TransformType type = TransformType.getVal(transformType, source);
		if (type==null){
			log.error("The transform type " + transformType + " is not available from source " + source);
			throw new RuntimeException("The transform type " + transformType + " is not available.");			
		}
		RecordType recordType = type.recordType();
		
		if (identifier.length()>0){ //single identifier transform
			OsfClientService osf = new OsfClientService();
			Object record = null;
			
			switch (recordType) {
			case OSF_NODE:
				record = osf.getNode(identifier);
				break;
			case OSF_REGISTRATION:
				record = osf.getRegistration(identifier);
				break;
			case OSF_USER:
				record = osf.getUser(identifier);
				break;
			default:
				break;
			}
			
			if (record!=null){
				RecordDTO dto = new RecordDTO(record, identifier, recordType);
				Transformer transformer = new Transformer(outputpath, discoDescription);
				transformer.transform(dto);	
				totalTransformed = 1;
			} else {
				throw new RuntimeException("Single record transform is not available for this type of data");
			}
		} else {
			Iterator<RecordDTO> iterator = null;
				
			switch (type) {
			case SHARE_API:
				iterator = new ShareApiTransformIterator(filters);
				break;
			case SHARE_LOCAL:
				iterator = new ShareLocalTransformIterator(inputpath, inputFileExtension);
				break;
			case OSF_NODES_API:
				iterator = new OsfNodeApiIterator(filters);			
				break;            
			case OSF_USERS_API:
				iterator = new OsfUserApiIterator(filters);
				break;
			case OSF_REGISTRATIONS_API:
				iterator = new OsfRegistrationApiIterator(filters);
				break;
			}

			Transformer transformer = new Transformer(outputpath, discoDescription);
			totalTransformed = transformer.transform(iterator, numrecords);	
		}

		log.info("Transform complete! " + totalTransformed.toString() + " records processed.");
		
	}
	

}
