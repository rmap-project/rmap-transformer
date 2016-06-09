package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SHARETransformerTest {

	String inputPath;
	
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sampledata.json").getFile());

		inputPath = file.getParentFile().getAbsolutePath();

		try {
			inputPath = URLDecoder.decode(inputPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}
	
	@After
	public void removefiles() throws Exception {
		File file = new File("testshare");
		if (file.exists()){
			FileUtils.deleteDirectory(new File("testshare"));
		}
	}

	@Test
	public void testShareLocalTransform() {
		String[] args = {"-src","local","-i", inputPath, "-iex", "json", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(30));
		//delete output files

	}

	@Test
	public void testShareCharEncodeTransform() {
		String[] args = {"-src","local","-i", inputPath+"/char-encode", "-iex", "json", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(1));
		//delete output files

	}
	@Test 
	public void testShareApiTransform() throws Exception{
		String[] args = {"-src", "api","-f", "?q=boutot", "-n","32", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(4));
	}
	
	@Test 
	public void testShareWithExactPaginationNum() throws Exception{
		String[] args = {"-src", "api","-f", "?q=heart", "-n","30", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(30));
	}
	
	@Test 
	public void testShareQueryWithNoMatches() throws Exception{
		String[] args = {"-src", "api","-f", "?q=asdfda", "-n","5", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(0));
	}
	
	@Test 
	public void testShareQueryWithOneMatch() throws Exception{
		String[] args = {"-src", "api","-f", "?q=asdfd", "-n","5", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(1));
	}
	
}
