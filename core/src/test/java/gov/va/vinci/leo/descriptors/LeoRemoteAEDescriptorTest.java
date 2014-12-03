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


import gov.va.vinci.leo.model.NameValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.aae.jms_adapter.JmsAnalysisEngineServiceAdapter;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Thomas Ginter
 * Date: 10/29/13
 * Time: 3:35 PM
 */
public class LeoRemoteAEDescriptorTest {
    String descriptor = "desc.gov.va.vinci.leo.ae.RemoteExampleWhitespaceTokenizerDescriptor";

    @Test
    public void testEmptyConstructor() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor();
        assertEquals("Required ResourceClassName", JmsAnalysisEngineServiceAdapter.class.getCanonicalName(), lrae.getRemoteDescriptor().getResourceClassName());
    }

    @Test
    public void testXmlImportConstructor() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor(descriptor, true);
        assertNull(lrae.getName());
    }

    @Test
    public void testParameterConstructor() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor("tcp://localhost:61616", "myRemoteInputQueue");
        assertTrue(lrae
                .getParameterValue(LeoRemoteAEDescriptor.Param.BROKER_URL.getName())
                .equals("tcp://localhost:61616"));
        assertTrue(lrae
                .getParameterValue(LeoRemoteAEDescriptor.Param.ENDPOINT.getName())
                .equals("myRemoteInputQueue"));
    }

    @Test
    public void testSetGetName() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor();
        assertNull(lrae.getName());
        lrae.setName("myFunnyName");
        System.out.println("remote name: " + lrae.getName());
        assertTrue(lrae.getName().startsWith("myFunnyName"));
    }

    @Test
    public void testGetParameter() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor(descriptor, true);
        Parameter p = lrae.getParameterSetting(LeoRemoteAEDescriptor.Param.ENDPOINT.getName());
        assertTrue(p.getValue().equals("WhitespaceRemoteService"));
        String value = lrae.getParameterValue(LeoRemoteAEDescriptor.Param.ENDPOINT.getName());
        assertTrue(value.equals("WhitespaceRemoteService"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSetParameter() throws Exception {
        Parameter p = new Parameter_impl("bob", "dummy");
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor();
        lrae.addParameterSetting(p.getName(), p.getValue());
        assertEquals(lrae.getParameterValue(p.getName()), p.getValue());
        assertEquals(lrae.getParameterSetting(p.getName()).getValue(), p.getValue());
        p.setValue("some other dummy");
        lrae.setParameterSetting(p);
        assertEquals(lrae.getParameterValue(p.getName()), p.getValue());
        p.setValue("yomamma");
        lrae.setParameterSetting(new NameValue(p.getName(), p.getValue()));
        assertEquals(lrae.getParameterValue(p.getName()), p.getValue());
        lrae.setParameterSetting("Bad", "yomamma");
    }

    @Test
    public void testToXML() throws Exception {
        LeoRemoteAEDescriptor lrae = new LeoRemoteAEDescriptor();
        lrae.setName("myRemoteTestDescriptor");
        lrae.addParameterSetting(LeoRemoteAEDescriptor.Param.ENDPOINT.getName(), "TestInputQueueName");
        lrae.addParameterSetting(LeoRemoteAEDescriptor.Param.BROKER_URL.getName(), "tcp://localhost:61616");
        lrae.toXML("myRemoteTestFile");
        String lraeXmlPath = lrae.getDescriptorLocator();
        assertFalse(StringUtils.isBlank(lraeXmlPath));
        LeoRemoteAEDescriptor importLRAE = new LeoRemoteAEDescriptor(lraeXmlPath, false);
        assertNotNull(importLRAE);
        assertTrue(importLRAE.getName().startsWith("myRemoteTestDescriptor"));
        assertTrue(importLRAE
                .getParameterValue(LeoRemoteAEDescriptor.Param.ENDPOINT.getName())
                .equals("TestInputQueueName"));
        assertTrue(importLRAE
                .getParameterValue(LeoRemoteAEDescriptor.Param.BROKER_URL.getName())
                .equals("tcp://localhost:61616"));
    }
}
