package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class OSFTransformerTest {

	String inputPath;
	
	@Before
	public void setUp() throws Exception {
	}
	
//	@After
//	public void removefiles() throws Exception {
//		File file = new File("testregosf");
//		if (file.exists()){
//			FileUtils.deleteDirectory(new File("testregosf"));
//		}
//		File file = new File("testnodesosf");
//		if (file.exists()){
//			FileUtils.deleteDirectory(new File("testnodesosf"));
//		}
//	}

	@Test 
	public void testOSFRegApiTransform() throws Exception{
		String[] args = {"OSF_REGISTRATIONS","-src", "api","-n","5", "-o", "testregosf/", "-dc", "http://test.org"};
		RMapTransformer.main(args);
		//check output files
		Integer numfiles = new File("testregosf").list().length;
		assertTrue(numfiles.equals(5));
	}
	

	@Test 
	public void testOSFNodeApiTransform() throws Exception{
		String[] args = {"OSF_NODES","-src", "api","-n","5", "-o", "testnodeosf/"};
		RMapTransformer.main(args);
		//check output files
		Integer numfiles = new File("testnodeosf").list().length;
		assertTrue(numfiles.equals(5));
	}
}
