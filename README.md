# rmap-transformer

## Getting started
build via 
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
 -h (-help, --help)             : print help message (default: true)
 -t (-type)                     : Type of import. Options are: SHARE. (default: SHARE) 
 -i (-inputpath) VAL            : Path that holds input data files
            				              (default: current folder)
 -iex (-inputfileext)  VAL      : File extension for input data files (default: json)
 -o (-outputpath) VAL           : Path of output files(s) for DiSCOs
 -oex (-outputfileext) VAL      : File extension for output data files (default: rdf)
```
The app reads in textfile records from a folder, and generates RMap DiSCO RDF for each file found.  
An example of running the program from the commandline example:
```
java -jar target/rmap-transformer-0.1.jar -t SHARE -i /jsonfiles/ -iex txt -o /discofiles/ -oex disco 
```
This example reads in any files in the folder /jsonfiles/ and loops through retrieving SHARE records. 
It converts these records to DiSCO RDF.


