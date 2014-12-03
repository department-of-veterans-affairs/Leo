package gov.va.vinci.leo.descriptors;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
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
 * #L%
 */

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeployDescriptorFactoryTest {
	public static String XML_RESULT="<?xml version=\"1.0\" encoding=\"UTF-8\"?><analysisEngineDeploymentDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">    <name>Test Desc</name>    <description>Optional Desc.</description>    <version/>    <vendor/>    <deployment protocol=\"jms\" provider=\"activemq\">        <casPool numberOfCASes=\"10\" initialFsHeapSize=\"10\"/>        <service>            <inputQueue endpoint=\"myqueue\" brokerURL=\"tcp://localhost:6161\" prefetch=\"0\"/>            <topDescriptor>                <import location=\"path\"/>            </topDescriptor>            <analysisEngine async=\"false\">                <scaleout numberOfInstances=\"2\"/>                <asyncPrimitiveErrorConfiguration>                    <processCasErrors thresholdCount=\"0\" thresholdWindow=\"0\" thresholdAction=\"terminate\"/>                    <collectionProcessCompleteErrors timeout=\"0\" additionalErrorAction=\"terminate\"/>                </asyncPrimitiveErrorConfiguration>            </analysisEngine>        </service>    </deployment></analysisEngineDeploymentDescription>";

	private static Logger logger = Logger.getLogger(DeployDescriptorFactory.class);

	@Before
	public void setup() {
		try {
			new File("_test_.xml").delete();
		} catch (Exception e) {
			logger.warn("Error deleting the test xml file, may not have existed.", e);
		}
	}
	
	@After
	public void cleanup() {
		try {
			new File("_test_.xml").delete();
		} catch (Exception e) {
			logger.warn("Error deleting the test xml file, may not have existed.", e);
		}
	}
	
	@SuppressWarnings("unused")
	@Test(expected=Exception.class)
	public void testBadConstructor() throws Exception {
		Map<String, String> descMap = new HashMap<String,String>();		
		DeployDescriptorFactory factory = new DeployDescriptorFactory("_test_.xml", descMap);
	}
	
	@Test
	public void testConstructorAndSerializeFull() throws Exception {
		Map<String, String> descMap = new HashMap<String,String>();
		descMap.put("name", "Test Desc");
		descMap.put("endpoint", "myqueue");
		descMap.put("brokerURL", "tcp://localhost:6161");
		descMap.put("topDescriptor", "path");
		
		// Optional Entries. 
		descMap.put("description", "Optional Desc.");
		descMap.put("numberOfCASes", "10");
		descMap.put("initialFsHeapSize", "10");
		descMap.put("numberOfInstances", "2");
		
		DeployDescriptorFactory factory = new DeployDescriptorFactory("_test_.xml", descMap);
		assertNotNull(factory);
	
		factory.serialize();
		assertTrue(new File("_test_.xml").exists());
		
		String result = DeployDescriptorFactoryTest.readFile("_test_.xml");
		
		// Yank out carriages returns and see if they match. 
		assertTrue(result.replaceAll("\r\n", "").replaceAll("\n", "").equals(XML_RESULT));
		
	}
	
	
	private static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
		}
	
	
}
