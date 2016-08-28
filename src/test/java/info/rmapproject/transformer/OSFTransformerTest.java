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
import info.rmapproject.transformer.osf.OsfRegistrationDiscoBuilder;

import java.io.File;
import java.util.List;

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
		String id = "pt2d7";
		String[] args = {"osf_registration","-id",id, "-o", "prezz/"};
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
		String id = "pu6sd";
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
		//String id = "km4wh";
		//String id = "cdi38";
		String id = "6suwb";
		String[] args = {"osf_user","-id",id, "-o", "prezz/"};
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
	
	/**
	 * Test the temporary method for retrieving alternative identifiers from API v1 (doi, ark).
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testTempAltIdentifierRetrieval() throws Exception{
		String regid= "rxgmb";
		List<String> identifiers = OsfRegistrationDiscoBuilder.getIdentifiers(regid, null);
		assertTrue(identifiers.size()==2);
		assertTrue(identifiers.get(0).equals("doi:10.17605/OSF.IO/RXGMB"));
		assertTrue(identifiers.get(1).equals("ark:/c7605/osf.io/rxgmb"));
	}
	
}
