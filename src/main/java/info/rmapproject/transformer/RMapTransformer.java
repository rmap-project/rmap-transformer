package info.rmapproject.transformer;

import info.rmapproject.transformer.osf.OsfNodeApiTransformIterator;
import info.rmapproject.transformer.osf.OsfNodeDiscoModel;
import info.rmapproject.transformer.osf.OsfRegApiTransformIterator;
import info.rmapproject.transformer.osf.OsfRegistrationDiscoModel;
import info.rmapproject.transformer.share.ShareApiTransformIterator;
import info.rmapproject.transformer.share.ShareDiscoModel;
import info.rmapproject.transformer.share.ShareLocalTransformIterator;

import java.io.File;

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
public class RMapTransformer {

	private static final String TYPE_SHARE="SHARE";
	private static final String TYPE_OSF_REGISTRATIONS="OSF_REGISTRATIONS";
	private static final String TYPE_OSF_NODES="OSF_NODES";
	private static final String TYPE_OSF_USERS="OSF_USERS";
	
	private static final String SOURCE_API = "api";
	private static final String SOURCE_LOCAL = "local";
	
	private static final String DEFAULT_TYPE = TYPE_SHARE;
	private static final String DEFAULT_SOURCE = SOURCE_LOCAL;
	private static final String DEFAULT_INPUT_FILEEXT = "json";
	private static final Integer DEFAULT_NUM_RECORDS = 100;
	
    /** Type of import e.g. SHARE, OSF_REGISTRATION, OSF_USER */
    @Argument(index = 0, metaVar = "Transform type", usage = "Type of transform. Options available: "
    														+ "SHARE, "
    														+ "OSF_REGISTRATIONS, "
    														+ "OSF_NODES, "
    														+ "OSF_USERS "
    														+ "(default: SHARE)")
    private String transformType = DEFAULT_TYPE;
        
    /** Source of data - local or api **/
    @Option(name="-src", aliases = {"-source"}, usage = "Source of the data - either api or local")
    private String source = DEFAULT_SOURCE;
    
    /** API params **/
    @Option(name="-f", aliases = {"-queryfilters"}, usage = "API request filters formatted in the style of a querystring e.g. q=osf&size=30&sort=providerUpdatedDateTime")
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

    /** DiSCO description */
    @Option(name = "-desc", aliases = {"-discodesc"}, usage = "Custom Description for DiSCO")
    public String discoDescription = "";
    
    /** File extension for DiSCO metadata file(s) */
    @Option(name = "-n", aliases = {"-numrecords"}, usage = "Maximum number of records to be converted. (default: 100)")
    public Integer numrecords = DEFAULT_NUM_RECORDS;

    /** Request for help/usage documentation */
    @Option(name = "-h", aliases = {"-help", "--help"}, usage = "Print help message")
    public boolean help = false;
        
    private static final Logger log = LoggerFactory.getLogger(RMapTransformer.class);
	    
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
        	e.printStackTrace();
        	System.exit(1);
        }
    }	

	public void run() throws Exception{
		//if output folder isn't there, create it
		File outputFolder = new File(outputpath);
		if (!outputFolder.exists() || !outputFolder.isDirectory()) {
			outputFolder.mkdir();
		}
		Integer totalTransformed = 0;
		TransformIterator iterator = null;
		DiscoModel discoModel = null;
		if (transformType.equals(TYPE_SHARE)){
			discoModel = new ShareDiscoModel(discoDescription);
			if (source.equals(SOURCE_API)){
				iterator = new ShareApiTransformIterator(filters);
			} else {
				iterator = new ShareLocalTransformIterator(inputpath, inputFileExtension);
			}
		} else if (transformType.equals(TYPE_OSF_REGISTRATIONS)){
			discoModel = new OsfRegistrationDiscoModel(discoDescription);
			iterator = new OsfRegApiTransformIterator(filters);
		} else if (transformType.equals(TYPE_OSF_NODES)){
			discoModel = new OsfNodeDiscoModel(discoDescription);
			iterator = new OsfNodeApiTransformIterator(filters);			
		} else if (transformType.equals(TYPE_OSF_USERS)) {
//			iterator = new OsfUserApiTransformIterator(filters);
		}
		TransformMgr datatransform = new TransformMgr(iterator, discoModel, outputpath);
		totalTransformed = datatransform.transform(numrecords);
		
		log.info("Transform complete! " + totalTransformed.toString() + " records processed.");
		
	}
	

}
