package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Core
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomasginter on 4/7/16.
 */
public class LeoPropertiesTest {

    String rootDirectory = "";

    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

    @Test
    public void testPropertiesFileLoading() throws Exception {
        File propsFile = new File(rootDirectory + "src/test/resources/conf/test.properties");
        Properties props = new Properties()
                .setDeploymentDescriptorFile("deploymentDescriptor")
                .setDescriptorDirectory("descriptorDirectory");
        props.loadprops(propsFile.getAbsolutePath());
        assertEquals("http://testhost:8080/jam", props.getJamServerBaseUrl());
        assertEquals(30, props.getJamQueryIntervalInSeconds());
        assertEquals(true, props.isJamResetStatisticsAfterQuery());
        assertEquals("tcp://testhost:61616", props.getBrokerURL());
        assertEquals("myTestQueueName", props.getEndpoint());
        assertEquals("myAwesomeTestServiceDude", props.getServiceName());
        assertEquals("deploymentDescriptor", props.getDeploymentDescriptorFile());
        assertEquals("descriptorDirectory", props.getDescriptorDirectory());
        assertEquals(2, props.getCasPoolSize());
        assertEquals(2000000, props.getFSHeapSize());
        assertEquals(0, props.getTimeout());
        assertEquals(60, props.getInitTimeout());
        assertEquals(0, props.getCCTimeout());
        assertEquals(false, props.isDeleteOnExit());
    }

    @Test
    public void testPropertySettings() throws Exception {
        Properties props = new Properties()
                .setDeploymentDescriptorFile("deploymentDescriptor")
                .setServiceName("serviceName")
                .setBrokerURL(LeoProperties.BROKERURL_DEFAULT)
                .setEndpoint(LeoProperties.ENDPOINT_DEFAULT)
                .setInputQueueName("inputQueue")
                .setCasPoolSize(25)
                .setFSHeapSize(125)
                .setTimeout(100)
                .setInitTimeout(30)
                .setCCTimeout(1000)
                .setJamServerBaseUrl("tcp://myhost/jam")
                .setJamQueryIntervalInSeconds(50)
                .setJamResetStatisticsAfterQuery(true)
                .setJamJmxPort(1234)
                .setDeleteOnExit(false)
                .setDescriptorDirectory("descriptorDirectory");
        assertEquals("deploymentDescriptor", props.getDeploymentDescriptorFile());
        assertEquals("serviceName", props.getServiceName());
        assertEquals(LeoProperties.BROKERURL_DEFAULT, props.getBrokerURL());
        assertEquals("inputQueue", props.getEndpoint());
        assertEquals("inputQueue", props.getInputQueueName());
        assertEquals(25, props.getCasPoolSize());
        assertEquals(125, props.getFSHeapSize());
        assertEquals(100, props.getTimeout());
        assertEquals(30, props.getInitTimeout());
        assertEquals(1000, props.getCCTimeout());
        assertEquals(1234, props.getJamJmxPort());
        assertEquals("tcp://myhost/jam", props.getJamServerBaseUrl());
        assertEquals(50, props.getJamQueryIntervalInSeconds());
        assertEquals(true, props.isJamResetStatisticsAfterQuery());
        assertEquals(false, props.isDeleteOnExit());
        assertEquals("descriptorDirectory", props.getDescriptorDirectory());
    }



    @Test
    public void testNullProperties() throws Exception {

        java.util.Properties o = new java.util.Properties();


        Properties testProperties = new Properties();
        testProperties.loadProperties(o);

        assertNull(testProperties.getBrokerURL());

        assertNull(testProperties.getEndpoint());
        assertEquals(testProperties.getJamQueryIntervalInSeconds(), 3600);
        assertTrue(testProperties.isJamResetStatisticsAfterQuery());

    }

    @Test
    public void testUsingProperties() throws Exception {

        java.util.Properties o = new java.util.Properties();

        // o.put("propertiesFile", "propertiesFile");
        o.put("deploymentDescriptor", "pom.xml");
        o.put("serviceName", "def");
        o.put("brokerURL", "myUrl");
        o.put("endpoint", "myEndPoint");
        o.put("descriptorPath", "myDirectory");
        o.put("casPoolSize", 1999);
        o.put("fsHeapSize", 1234);
        o.put("timeout", 1234567);
        o.put("initTimeout", 98);
        o.put("ccTimeout", -3);
        o.put("jamServerBaseUrl", "myBaseUrl");
        o.put("jamQueryIntervalInSeconds", 9 * 60 * 60);
        o.put("jamResetStatisticsAfterQuery", false);
        o.put("jamJmxPort", -100);
        o.put("deleteOnExit", false);

        Properties testProperties = new Properties();
        testProperties.loadProperties(o);
        assertEquals(testProperties.getDeploymentDescriptorFile(), "pom.xml");
        assertEquals(testProperties.getServiceName(), "def");
        assertEquals(testProperties.getBrokerURL(), "myUrl");
        assertEquals(testProperties.getEndpoint(), "myEndPoint");
        assertEquals(testProperties.getDescriptorDirectory(), "myDirectory");
        assertEquals(testProperties.getCasPoolSize(), 1999);
        assertEquals(testProperties.getFSHeapSize(), 1234);
        assertEquals(testProperties.getTimeout(), 1234567);
        assertEquals(testProperties.getInitTimeout(), 98);
        assertEquals(testProperties.getCCTimeout(), -3);
        assertEquals(testProperties.getJamServerBaseUrl(), "myBaseUrl");
        assertEquals(testProperties.getJamQueryIntervalInSeconds(), 9 * 60 * 60);
        assertEquals(testProperties.isJamResetStatisticsAfterQuery(), false);
        assertEquals(testProperties.getJamJmxPort(), -100);
        assertEquals(testProperties.isDeleteOnExit(), false);
    }

    public class Properties extends LeoProperties {

    }
}
