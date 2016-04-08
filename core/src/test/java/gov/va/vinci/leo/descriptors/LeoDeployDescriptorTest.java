package gov.va.vinci.leo.descriptors;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/8/16.
 */
public class LeoDeployDescriptorTest {

    @Test
    public void testGettersAndSetters() throws Exception {
        LeoDeployDescriptor deployDescriptor = new LeoDeployDescriptor("desc.gov.va.vinci.leo.ae.TokenizerAggregateDescriptor", true)
                .setName("myDeployDescriptor")
                .setDescription("Describe this descriptor")
                .setCasPoolSize(25)
                .setInitialFsHeapSize(10)
                .setBrokerURL("tcp://somhost:2112")
                .setEndpoint("MyQueue");
        assertEquals("myDeployDescriptor", deployDescriptor.getName());
        assertEquals("Describe this descriptor", deployDescriptor.getDescription());
        assertEquals(25, deployDescriptor.getCasPoolSize());
        assertEquals(10, deployDescriptor.getInitialFsHeapSize());
        assertEquals("tcp://somhost:2112", deployDescriptor.getBrokerURL());
        assertEquals("MyQueue", deployDescriptor.getEndpoint());
    }
}
