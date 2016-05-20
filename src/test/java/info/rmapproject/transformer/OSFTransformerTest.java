package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OSFTransformerTest {

	String inputPath;
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void removefiles() throws Exception {
		File file1 = new File("testregosf");
		if (file1.exists()){
			FileUtils.deleteDirectory(new File("testregosf"));
		}
		File file2 = new File("testnodesosf");
		if (file2.exists()){
			FileUtils.deleteDirectory(new File("testnodesosf"));
		}
	}

	@Test 
	public void testOSFRegApiTransform() throws Exception{
		String[] args = {"OSF_REGISTRATIONS","-src", "api","-n","5", "-o", "testregosf/"};
		RMapTransformer.main(args);
		//check output files
		Integer numfiles = new File("testregosf").list().length;
		assertTrue(numfiles.equals(2)); //only 2 are roots of Registrations
	}
	
	@Test 
	public void testOSFNodeApiTransform() throws Exception{
		String[] args = {"OSF_NODES","-src", "api","-n","5", "-o", "testnodeosf/"};
		RMapTransformer.main(args);
		//check output files
		Integer numfiles = new File("testnodeosf").list().length;
		assertTrue(numfiles.equals(2)); //one root Public Project, one partial
	}
}
