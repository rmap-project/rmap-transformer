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
package info.rmapproject.transformer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for SHARE Transformer
 */
public class SHARETransformerTest {

	/** The input path. */
	String inputPath;
	
	/**
	 * Pre-test setup
	 *
	 * @throws Exception the exception
	 */
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
	
	/**
	 * Remove test files when test complete
	 *
	 * @throws Exception the exception
	 */
	@After
	public void removefiles() throws Exception {
		File file = new File("testshare");
		if (file.exists()){
			FileUtils.deleteDirectory(new File("testshare"));
		}
	}

	/**
	 * Test SHARE local file transform.
	 */
	@Test
	public void testShareLocalTransform() {
		String[] args = {"-src","local","-i", inputPath, "-iex", "json", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(30));
		//delete output files

	}

	/**
	 * Test SHARE character encoding transform.
	 */
	@Test
	public void testShareCharEncodeTransform() {
		String[] args = {"-src","local","-i", inputPath+"/char-encode", "-iex", "json", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(1));
		//delete output files

	}
	
	/**
	 * Test SHARE API transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testShareApiTransform() throws Exception{
		//String[] args = {"-src", "api","-f", "?q=boutot", "-n","32", "-o", "testshare/"};
		String[] args = {"-src", "api","-f", "?q=pt2d7&sort=providerUpdatedDateTime", "-n","2", "-o", "prezz/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(4));
	}
	
	/**
	 * Test SHARE with exact pagination number.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testShareWithExactPaginationNum() throws Exception{
		String[] args = {"-src", "api","-f", "?q=heart", "-n","30", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(30));
	}
	
	/**
	 * Test SHARE query with no matches.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testShareQueryWithNoMatches() throws Exception{
		String[] args = {"-src", "api","-f", "?q=asdfda", "-n","5", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(0));
	}
	
	/**
	 * Test SHARE query with one match.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testShareQueryWithOneMatch() throws Exception{
		String[] args = {"-src", "api","-f", "?q=asdfd", "-n","5", "-o", "testshare/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testshare").list().length;
		assertTrue(numfiles.equals(1));
	}
	
}
