# RMap Transformer
This tool was developed to transform [SHARE](https://share.osf.io/) API (V1) and [OSF](https://osf.io) API (v2) data to RMap DiSCOs.  To do the transforms, it uses separate [SHARE client](https://github.com/rmap-project/share-client) and [OSF client](https://github.com/rmap-project/rmap-osf-client-extension) to convert JSON API data to a Java model. It is a work in progress. Some refactoring in the CLI and model would be required to support plug in and play transforms, but for now it may provide a few useful tools for other kinds of transforms.

## Getting started
Build via 
```
mvn clean install
```
This will create an executable jar file in 
```
rmap-transformer/rmap-transformer/target/rmap-transformer-{$version}.jar
```
To run it, just do 
```
java -jar <path to jar>
```
Use -h to show the help screen:
```
$ java -jar rmap-transformer-0.1.jar -h
 Transform type           : Type of transform. Options available: SHARE,
                            OSF_REGISTRATIONS, OSF_NODES, OSF_USERS (default: SHARE)
                            (default: SHARE)
 -desc (-discodesc) VAL   : Custom Description for DiSCO (default: [varies by type])
 -f (-queryfilters) VAL   : API request filters formatted in the style of a
                            querystring e.g. q=osf&size=30&sort=providerUpdatedD
                            ateTime (default: [blank])
 -h (-help, --help)       : Print help message (default: true)
 -i (-inputpath) VAL      : Path that holds input data files (default: current
                            folder (default: .)
 -iex (-inputfileext) VAL : File extension for input data files (default: json)
                            (default: json)
 -n (-numrecords) N       : Maximum number of records to be converted.
                            (default: 100)
 -o (-outputpath) VAL     : Path of output files(s) for DiSCOs (default: .)
 -src (-source) VAL       : Source of the data - either api or local (default:
                            local)
```
The app supports import of data (currently JSON) from either a folder or API client app and uses the data to generate RMap DiSCO RDF for each file found.  Each new import requires custom development to map to the DiSCO, but this transformer provider some tools to support this. 

So far the transformer supports import from SHARE and Registrations from the OSF API.
An example of running the program from the commandline example:
```
java -jar rmap-transformer-0.1.jar SHARE -src local -i jsonfiles/ -iex txt -o discofiles/ -oex disco 
```
This example reads in any files in the folder /jsonfiles/ and loops through retrieving SHARE records. 
It converts these records to DiSCO RDF.

Here is another example for running against an API:
```
java -jar rmap-transformer-0.1.jar SHARE -src api -o discofiles/ -oex disco  -f ?q=heart
```



