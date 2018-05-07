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

import gov.va.vinci.leo.ae.Example2Annotator;
import gov.va.vinci.leo.ae.ExampleAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoDeployDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class ServiceTest {

    String rootDirectory = "";

    @Before
    public void onBefore() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("service")) {
            rootDirectory = "service/";
        }
    }

    @Test
    public void emptyConstructor() throws Exception {
        Service s = new Service();
        assertNotNull(s);
        assertTrue("tcp://localhost:61616".equals(s.getBrokerURL()));
        assertTrue("mySimpleQueueName".equals(s.getEndpoint()));
        assertNull(s.getServiceName());
    }

    @Test
    public void badPropertiesFile() throws Exception {
        Service s = new Service("junk-file");
        assertNotNull(s);
        assertTrue("tcp://localhost:61616".equals(s.getBrokerURL()));
        assertTrue("mySimpleQueueName".equals(s.getEndpoint()));
        assertNull(s.getServiceName());
    }

    @Test
    public void testPropertiesFileValues() throws Exception {
        //Test loading properties initially in constructor
        Service s = new Service(rootDirectory + "src/test/resources/conf/test.properties");
        assertNotNull(s);
        assertTrue("tcp://testhost:61616".equals(s.getBrokerURL()));
        assertTrue("myTestQueueName".equals(s.getEndpoint()));
        assertTrue("myAwesomeTestServiceDude".equals(s.getServiceName()));
        assertTrue("http://testhost:8080/jam".equals(s.getJamServerBaseUrl()));
        assertTrue(s.getJamQueryIntervalInSeconds() == 30);

        //Test loading properties after initially creating service
        s = new Service();
        assertNotNull(s);
        s.loadprops(rootDirectory + "src/test/resources/conf/test.properties");
        assertTrue("tcp://testhost:61616".equals(s.getBrokerURL()));
        assertTrue("myTestQueueName".equals(s.getEndpoint()));
        assertTrue("myAwesomeTestServiceDude".equals(s.getServiceName()));
        assertTrue("http://testhost:8080/jam".equals(s.getJamServerBaseUrl()));
        assertTrue(s.getJamQueryIntervalInSeconds() == 30);
    }//testPropertiesFileValues method

    @Test
    public void testNoPropertiesFile() throws Exception {
        Service s = new Service();
        assertNotNull(s);
        s.setBrokerURL("tcp://testhost:61616");
        s.setEndpoint("myTestQueueName");
        s.setServiceName("myAwesomeTestServiceDude");
        s.setJamServerBaseUrl("http://testhost:8080/jam");
        s.setJamQueryIntervalInSeconds(30);

        //Confirm that the settings actually got set
        assertTrue("tcp://testhost:61616".equals(s.getBrokerURL()));
        assertTrue("myTestQueueName".equals(s.getEndpoint()));
        assertTrue("myAwesomeTestServiceDude".equals(s.getServiceName()));
        assertTrue("http://testhost:8080/jam".equals(s.getJamServerBaseUrl()));
        assertTrue(s.getJamQueryIntervalInSeconds() == 30);
    }//testNoPropertiesFile method

    @Test
    public void testDeployStringPrimitivesList() throws Exception {
        //import by name
        Service s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        ArrayList<String> list = new ArrayList<String>();
        list.add("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor");
        list.add("desc.gov.va.vinci.leo.ae.WordTokenizerDescriptor");
        s.deploy(list, true);
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);

        //import by location
        s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        list = new ArrayList<String>();
        list.add(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml");
        list.add(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WordTokenizerDescriptor.xml");
        s.deploy(list, false);
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);
    }//testDeployStringPrimitives method

    @Test
    public void testDeployLeoAnnotators() throws Exception {
        //Test default values with Example annotator
        Service s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        s.deploy(new Example2Annotator("gov.va.vinci.leo.types.myOutputType").setName("Example").getLeoAEDescriptor());
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);

        //Test LeoAnnotator deployment
        s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        ExampleAnnotator exampleAnnotator = new ExampleAnnotator("myParamValue", "myReqParamValue");
        s.deploy(exampleAnnotator.getLeoTypeSystemDescription(), exampleAnnotator);
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);

        //Test AnalysisEngineDescription deployment
        s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        s.deploy(new ExampleAnnotator("myParamValue", "myReqParamValue").getLeoAEDescriptor().getAnalysisEngineDescription());
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);

        //Test different number of instances with Example Annotator
        s = new Service();
        s.mUAEngine = EasyMock.createMock(UimaAsynchronousEngine.class);
        s.deploy(new LeoTypeSystemDescription("gov.va.vinci.leo.types.myOutputType", "description", "uima.tcas.Annotation"),
                10,
                new Example2Annotator("gov.va.vinci.leo.types.myOutputType"));
        assertTrue(StringUtils.isNotBlank(s.getAggregateDescriptorFile()));
        assertNotNull(s.mAppCtx);
        //check that the number of instances was set to 10 in the deployment descriptor
        String deployXml = FileUtils.readFileToString(new File(s.getDeploymentDescriptorFile()));
        assertTrue(deployXml.contains("numberOfInstances=\"10\""));

        //Make sure the type system was propagated to the delegate descriptor
        LeoAEDescriptor gen_agg_desc = new LeoAEDescriptor(s.getAggregateDescriptorFile(), false);
        LeoAEDescriptor example = (LeoAEDescriptor) gen_agg_desc.getDelegates()[0];
        assertNotNull(example.getTypeSystemDescription().getType("gov.va.vinci.leo.types.myOutputType"));
    }

    @Test(expected = Exception.class)
    public void testNullDeploy() throws Exception {
        Service s = new Service();
        s.deploy((LeoAEDescriptor) null);
    }//testNullDeploy method

    @Test
    public void testCasPoolSettings() throws Exception {
        //Init the pipeline
        LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.TokenizerAggregateDescriptor", true);
        assertNotNull(fd);
        assertTrue(fd.getNumberOfInstances() == 1);
        fd.setNumberOfInstances(6);

        //Init the service
        int casPoolSize = 0;
        Service s = new Service();
        casPoolSize = s.getCasPoolSize();
        assertTrue(casPoolSize == 4);

        LeoDeployDescriptor fdd = new LeoDeployDescriptor(fd);
        casPoolSize = fdd.getCasPoolSize();
        assertTrue(casPoolSize == 6);

        fd.setNumberOfInstances(1);
        fdd = new LeoDeployDescriptor(fd);
        casPoolSize = fdd.getCasPoolSize();
        assertTrue(casPoolSize == 1);
        assertTrue(s.getCasPoolSize() > casPoolSize);
    }

    @Test
    public void testUsingMapForConstructorProperties() throws FileNotFoundException {
        Map<String, Object> o = new HashedMap();

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

        Service testProperties = new Service(o);
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

    public void testUndeploy() throws Exception {
        Service service = new Service();

        // Call undeploy without a running service.
        service.undeploy();

        // Call undeploy with a running service. 
        service.uiamAsContainerId = "testId";
        UimaAsynchronousEngine mock = EasyMock.createMock(UimaAsynchronousEngine.class);

        mock.undeploy("testId");
        EasyMock.expectLastCall();
        EasyMock.replay(mock);
        service.mUAEngine = mock;
        service.undeploy();

    }


}//ServiceTest class
