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
import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.Capability;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class LeoAEDescriptorTest extends LeoAEDescriptor {
    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

    /**
     * Default constructor.  Just gets a blank AnalysisEngineDescriptor object from the factory.
     *
     * @throws java.io.IOException
     */
    public LeoAEDescriptorTest() throws IOException {
        super();
    }

    @Test
	public void testGetLeoAEDescriptorByName() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
		validateGetLeoAEDecriptor(fd);
	}

	
	@Test
	public void testGetLeoAEDescriptorByNameWithDelegates() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.TokenizerAggregateDescriptor", true);
		
		assertTrue(fd.isAggregate());
		assertTrue(fd.getDelegates().length == 2);
		LeoDelegate[] delegates = fd.getDelegates();
		assertTrue(delegates[0].getName().startsWith("aeWhitespaceTokenizer"));
		assertTrue(delegates[1].getName().startsWith("aeWordTokenizer"));
		assertTrue(fd.getDelegate("WhitespaceTokenizerDescriptor") != null);
	}

    @Test
    public void testToXmlWithName() throws Exception {
        LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.TokenizerAggregateDescriptor", true);
        String name = fd.getName();
        File xmlPath = new File(rootDirectory + "src/test/resources/xml-generated-test");
        xmlPath.mkdir();
        fd.setDescriptorLocator(xmlPath.toURI());
        fd.toXML(name);
        assertTrue(xmlPath.listFiles().length == 3);
        FileUtils.forceDelete(xmlPath);
    }

	@Test
	public void testGetLeoAEDescriptorByPath() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml", false);
		validateGetLeoAEDecriptor(fd);
	}
	
	private void validateGetLeoAEDecriptor(LeoAEDescriptor fd) {
		assertTrue(fd != null);
		assertFalse(fd.isAggregate());
		assertTrue(fd.getAnalysisEngineDescription().getImplementationName().equals("gov.va.vinci.marian.whitespace.ae.WhitespaceTokenizer"));
		assertTrue(fd.getDelegates() == null);
	}

	@Test
	public void testGetLeoAEDescriptorEmpty() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor();
		assertTrue(fd != null);
		fd.getAnalysisEngineDescription().setPrimitive(true);
		fd.getAnalysisEngineDescription().setAnnotatorImplementationName("desc.gov.va.vinci.leo.ae.WhitespaceTokenizer");
		fd.getAnalysisEngineDescription().getAnalysisEngineMetaData().setName("aeTestName");
		
	}
	
	public void testAEInspectionForServiceRegistryPrototype() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.TokenizerAggregateDescriptor", true);
		
		Map<String,ResourceSpecifier> a = fd.getAEDescriptor().getAllComponentSpecifiers(null);
		for (String s: a.keySet()) {
			System.out.println("s = " + s);
			ResourceSpecifier rs = a.get(s);
			System.out.println("rs=" + rs);
			if (rs instanceof AnalysisEngineDescription ) {
				AnalysisEngineDescription aed = (AnalysisEngineDescription)rs;
				System.out.println("Cap:" + aed.getAnalysisEngineMetaData().getCapabilities());
				for (Capability c: aed.getAnalysisEngineMetaData().getCapabilities()) {
					System.out.println("c" + c);
				}
			}
		}
		
	}
	
	@Test
	public void testAddParameterSetting() {
		LeoAEDescriptor fd = null;
		try {
			fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
		} catch (Exception e) {
			fail("testAddParameterSetting failed creating LeoAEDescriptor" + e.getMessage());
		}
		
		if(fd != null) {
			try {
				String name = "myOptTestparam";
				fd.addParameterSetting(name, false, false, ConfigurationParameter.TYPE_INTEGER, 2);
				Integer i  = (Integer)fd.getParameterValue(name);
				assertTrue((i != null) && (i.equals(new Integer(2))));
				fd.addParameterSetting(name, true, true, ConfigurationParameter.TYPE_STRING, 3);
				i = (Integer)fd.getParameterValue(name);
				assertTrue((i != null) && (i.equals(new Integer(3))));
			} catch (Exception e) {
				fail("Adding a new parameter failed: " + e.getMessage());
			}
		}//if
	}//testAddParameterSetting method
	
	@Test(expected=Exception.class)
	public void testAddParameterSettingBadType() throws Exception{
		new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true)
					.addParameterSetting("dud", true, false, "badType", null);
	}//testAddParameterSettingBadType method
	
	@Test
	public void testSetParameterStringArgs() throws Exception {
		String name = "myOptTestparam";
		LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
		fd.addParameterSetting(name, false, false, ConfigurationParameter.TYPE_INTEGER, 2);
		fd.setParameterSetting(name, 16);
		Integer i = (Integer)fd.getParameterValue(name);
		assertTrue((i != null) && (i.equals(new Integer(16))));
	}//testSetParameterStringArgs method
	
	@Test
	public void testSetParameterLeoPVArgs() throws Exception {
		String name = "testParam";
		LeoAEDescriptor fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
		fd.addParameterSetting(name, false, false, ConfigurationParameter.TYPE_BOOLEAN, true);
		NameValue fpv = new NameValue(name, false);
		fd.setParameterSetting(fpv);
		Boolean b = (Boolean)fd.getParameterValue(name);
		assertFalse(b);
	}
	
	@Test
	public void testSetParameterLeoPVConstructor() throws Exception {
		String name = "filterStopWords";
		
		LeoAEDescriptor fd
			= new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WordTokenizerDescriptor",
									true,
									new NameValue(name, false));
		Boolean b = (Boolean)fd.getParameterValue(name);
		assertFalse(b);
		fd = new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WordTokenizerDescriptor",
									true,
									new NameValue("badname", false));
		b = (Boolean)fd.getParameterValue(name);
		assertTrue(b);
    }
	
	@Test
	public void testAddDelegateLeoAEArgs() throws Exception {
		LeoAEDescriptor aggregate = new LeoAEDescriptor();
		aggregate.addDelegate(new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor",
													true))
				 .addDelegate("desc.gov.va.vinci.leo.ae.WordTokenizerDescriptor",
													true,
													new NameValue("filterStopWords", false));
		LeoDelegate[] delegates = aggregate.getDelegates();
		assertNotNull(delegates);
		assertTrue(delegates.length == 2);
		assertNotNull(delegates[0]);
		assertTrue(delegates[0].getName().contains("aeWhitespaceTokenizerDescriptor"));
		assertNotNull(delegates[1]);
		assertTrue(delegates[1].getName().contains("aeWordTokenizerDescriptor"));
	}
	
}//LeoAEDescriptorTest class
