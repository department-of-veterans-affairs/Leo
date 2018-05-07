package gov.va.vinci.leo.descriptors;

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
