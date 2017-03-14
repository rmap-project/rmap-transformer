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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for OSF transformer
 */
public class OSFTransformerTest {

	/** The input path. */
	String inputPath;
	
	/**
	 * Set up pre test
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void removefiles() throws Exception {
		File file1 = new File("testnodepagedosf");
		if (file1.exists()){
			FileUtils.deleteDirectory(file1);
		}
		File file2 = new File("testOneUser");
		if (file2.exists()){
			FileUtils.deleteDirectory(file2);
		}
		File file3 = new File("testuserosf");
		if (file3.exists()){
			FileUtils.deleteDirectory(file3);
		}
		File file4 = new File("testOneReg");
		if (file4.exists()){
			FileUtils.deleteDirectory(file4);
		}
		File file5 = new File("testnodeosf");
		if (file5.exists()){
			FileUtils.deleteDirectory(file5);
		}
		File file6 = new File("testOneNode");
		if (file6.exists()){
			FileUtils.deleteDirectory(file6);
		}
		File file7 = new File("testregosf");
		if (file7.exists()){
			FileUtils.deleteDirectory(file7);
		}
		
		
	}

	/**
	 * Test OSF Registration API transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testOSFRegApiTransform() throws Exception{
		String[] args = {"osf_registration","-src", "api","-n","12", "-o", "testregosf/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testregosf").list().length;
		assertTrue(numfiles.equals(12)); //only 2 are roots of Registrations
		assertTrue(numfiles>0);
	}

	/**
	 * Test OSF Node API transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testOSFNodeApiTransform() throws Exception{
		String[] args = {"osf_node","-src", "api","-n","10", "-o", "testnodeosf/", "-f", "?page=4"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testnodeosf").list().length;
		//assertTrue(numfiles.equals(2)); //one root Public Project, one partial
		assertTrue(numfiles>0);
	}

	/**
	 * Test OSF Node API transform with pagination.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testOSFNodeApiTransformWithPagination() throws Exception{
		String[] args = {"osf_node","-src", "api","-n","12", "-o", "testnodepagedosf/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testnodepagedosf").list().length;
		//assertTrue(numfiles.equals(2)); //one root Public Project, one partial
		assertTrue(numfiles>0);
	}
	
	/**
	 * Test single OSF Registration transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testSingleRegTransform() throws Exception {
		//String id = "pt2d7"; //live api example
		String id = "h48cy";
		String[] args = {"osf_registration","-id",id, "-o", "testOneReg/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testOneReg").list().length;
		assertTrue(numfiles.equals(1)); //one root Public Project, one partial
	}	
	
	/**
	 * Test single OSF Node transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testSingleNodeTransform() throws Exception {
		//String id = "7gwtr"; //live api example
		String id = "zr4bx";
		String[] args = {"osf_node","-id",id, "-o", "testOneNode/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testOneNode").list().length;
		assertTrue(numfiles.equals(1)); //one root Public Project, one partial
	}	
	
	/**
	 * Test single OSF User transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testSingleUserTransform() throws Exception {
		//String id = "6suwb"; //live api example
		String id = "sguxh";
		String[] args = {"osf_user","-id",id, "-o", "testOneUser/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testOneUser").list().length;
		assertTrue(numfiles.equals(1)); //one root Public Project, one partial
	}	

	/**
	 * Test multiple OSF Users transform.
	 *
	 * @throws Exception the exception
	 */
	@Test 
	public void testUsersTransform() throws Exception {
		String[] args = {"osf_user","-src", "api","-n","20", "-o", "testuserosf/"};
		RMapTransformerCLI.main(args);
		//check output files
		Integer numfiles = new File("testuserosf").list().length;
		assertTrue(numfiles>0);
	}	
	
}
