/**
 * ClientTest.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo;

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

import gov.va.vinci.leo.cr.XmiFileCollectionReader;
import gov.va.vinci.leo.listener.SimpleXmiListener;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test the Client object methods and features
 * 
 * @author thomasginter
 */
public class ClientTest {
	
	public static String testInputDirectory = "src/test/resources/inputDirectory";

    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }
    }


    @Test
	public void emptyConstructor() throws Exception {
		Client c = new Client();
		assertNotNull(c);
		assertTrue("tcp://localhost:61616".equals(c.getBrokerURL()));
		assertTrue("mySimpleQueueName".equals(c.getEndpoint()));
		assertNull(c.getServiceName());
	}
	
	@Test
	public void badPropertiesFile() throws Exception {
		Client c = new Client("junk-file");
		assertNotNull(c);
		assertTrue("tcp://localhost:61616".equals(c.getBrokerURL()));
		assertTrue("mySimpleQueueName".equals(c.getEndpoint()));
		assertNull(c.getServiceName());
	}
	
	@Test
	public void testPropertiesFileValues() throws Exception {
		//Test loading properties initially in constructor
		Client c = new Client(rootDirectory + "src/test/resources/conf/test.properties");
		assertNotNull(c);
		assertTrue("tcp://testhost:61616".equals(c.getBrokerURL()));
		assertTrue("myTestQueueName".equals(c.getEndpoint()));
		assertTrue("myAwesomeTestServiceDude".equals(c.getServiceName()));
		
		//Test loading properties after initially creating service
		c = new Client();
		assertNotNull(c);
		c.loadprops(rootDirectory + "src/test/resources/conf/test.properties");
		assertTrue("tcp://testhost:61616".equals(c.getBrokerURL()));
		assertTrue("myTestQueueName".equals(c.getEndpoint()));
		assertTrue("myAwesomeTestServiceDude".equals(c.getServiceName()));
	}//testPropertiesFileValues method
	
	@Test
	public void testNoPropertiesFile() throws Exception {
		Client c = new Client();
		assertNotNull(c);
		c.setBrokerURL("tcp://testhost:61616");
		c.setEndpoint("myTestQueueName");
		c.setServiceName("myAwesomeTestServiceDude");
		
		//Confirm that the settings actually got set
		assertTrue("tcp://testhost:61616".equals(c.getBrokerURL()));
		assertTrue("myTestQueueName".equals(c.getEndpoint()));
		assertTrue("myAwesomeTestServiceDude".equals(c.getServiceName()));
	}//testNoPropertiesFile method
	
	@Test(expected=Exception.class)
	public void testNoListenerAdded() throws Exception {
		Client c = new Client();
		assertNotNull(c);
		c.init();
	}//tesetNoListenerAdded method
	
	@Test
	public void testAddMultipleListeners() throws Exception {
		Client c = new Client();
		assertNotNull(c);
		c.addUABListener(new SimpleXmiListener(new File(".")));
        c.addUABListener(new SimpleXmiListener(new File(".")));
		c.init();
	}//testAddMultipleListeners method
	
	@Test(expected=Exception.class)
	public void testInitNullListener() throws Exception {
		Client c = new Client();
		assertNotNull(c);
		c.init(null);
	}//testInitNullListener method
	
	@Test
	public void testAddListenerMethods() throws Exception {
		Client c = new Client(new SimpleXmiListener(new File(".")));
		assertNotNull(c);
		assertTrue(c.isUAListenerAdded);
		c = new Client(rootDirectory + "src/test/resources/conf/test.properties");
		assertNotNull(c);
		assertFalse(c.isUAListenerAdded);
		c = new Client(rootDirectory + "src/test/resources/conf/test.properties", new XmiFileCollectionReader( new File(rootDirectory + "src/test/resources/results"), false), new SimpleXmiListener(new File(".")));
		assertNotNull(c);
		assertTrue(c.isUAListenerAdded);
	}//testAddListenerMethods method
	
}//ClientTest class
