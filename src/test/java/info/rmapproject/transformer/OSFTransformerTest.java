package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;
import info.rmapproject.transformer.osf.OsfRegistrationDiscoBuilder;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class OSFTransformerTest {

	String inputPath;
	
	@Before
	public void setUp() throws Exception {
	}
	
//	@After
//	public void removefiles() throws Exception {
//		File file1 = new File("testregosf");
//		if (file1.exists()){
//			FileUtils.deleteDirectory(new File("testregosf"));
//		}
//		File file2 = new File("testnodesosf");
//		if (file2.exists()){
//			FileUtils.deleteDirectory(new File("testnodesosf"));
//		}
	//	File file3 = new File("testOneNode");
	//	if (file3.exists()){
	//		FileUtils.deleteDirectory(new File("testOneNode"));
	//	}
	//	File file4 = new File("testusersosf");
	//	if (file4.exists()){
	//		FileUtils.deleteDirectory(new File("testusersosf"));
	//	}
//	}

	@Test 
	public void testOSFRegApiTransform() throws Exception{
		String[] args = {"OSF_REGISTRATION","-src", "api","-n","10", "-o", "testregosf/"};
		RMapTransformCLI.main(args);
		//check output files
		Integer numfiles = new File("testregosf").list().length;
		//assertTrue(numfiles.equals(2)); //only 2 are roots of Registrations
		assertTrue(numfiles>0);
	}

	@Test 
	public void testOSFNodeApiTransform() throws Exception{
		String[] args = {"OSF_NODE","-src", "api","-n","10", "-o", "testnodeosf/", "-f", "?page=4"};
		RMapTransformCLI.main(args);
		//check output files
		Integer numfiles = new File("testnodeosf").list().length;
		//assertTrue(numfiles.equals(2)); //one root Public Project, one partial
		assertTrue(numfiles>0);
	}
	
	@Test 
	public void testSingleNodeTransform() throws Exception {
		String id = "ndry9";
		String[] args = {"OSF_NODE","-id",id, "-o", "testOneNode/"};
		RMapTransformCLI.main(args);
		//check output files
		Integer numfiles = new File("testOneNode").list().length;
		assertTrue(numfiles.equals(1)); //one root Public Project, one partial
	}	
	
	
	@Test 
	public void testSingleUserTransform() throws Exception {
		//String id = "km4wh";
		String id = "cdi38";
		String[] args = {"OSF_USER","-id",id, "-o", "testOneUser/"};
		RMapTransformCLI.main(args);
		//check output files
		Integer numfiles = new File("testOneUser").list().length;
		assertTrue(numfiles.equals(1)); //one root Public Project, one partial
	}	

	@Test 
	public void testUsersTransform() throws Exception {
		String[] args = {"OSF_USER","-src", "api","-n","10", "-o", "testuserosf/", "-f", "?page=4"};
		RMapTransformCLI.main(args);
		//check output files
		Integer numfiles = new File("testuserosf").list().length;
		assertTrue(numfiles>0);
	}	
	
	@Test
	public void testTempIdentifierRetrieval() throws Exception{
		String regid= "rxgmb";
		List<String> identifiers = OsfRegistrationDiscoBuilder.getIdentifiers(regid);
		assertTrue(identifiers.size()==2);
		assertTrue(identifiers.get(0).equals("10.17605/OSF.IO/RXGMB"));
		assertTrue(identifiers.get(1).equals("c7605/osf.io/rxgmb"));
	}
	
}
