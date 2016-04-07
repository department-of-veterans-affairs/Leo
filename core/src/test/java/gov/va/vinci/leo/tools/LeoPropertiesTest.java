package gov.va.vinci.leo.tools;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
        assertEquals("tcp://myhost/jam", props.getJamServerBaseUrl());
        assertEquals(50, props.getJamQueryIntervalInSeconds());
        assertEquals(true, props.isJamResetStatisticsAfterQuery());
        assertEquals(false, props.isDeleteOnExit());
        assertEquals("descriptorDirectory", props.getDescriptorDirectory());
    }

    public class Properties extends LeoProperties {

    }
}
