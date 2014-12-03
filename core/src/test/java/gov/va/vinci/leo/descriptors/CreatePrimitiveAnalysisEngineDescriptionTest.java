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
import org.apache.uima.resource.metadata.Capability;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.Capability_impl;
import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;
import org.apache.uima.resource.metadata.impl.FeatureDescription_impl;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;
import org.apache.uima.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class CreatePrimitiveAnalysisEngineDescriptionTest {
	/**
	 * Logging object of output
	 */
	public static final Logger log = Logger.getLogger(CreatePrimitiveAnalysisEngineDescriptionTest.class.getName());

    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

	/**
	 * Create the whitespacetokenizer AnalysisEngineDescription programatically,
	 * and run it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProgramaticPrimitive() throws Exception {
		LeoAEDescriptor fd = new LeoAEDescriptor();

		fd.setDescription("Ryan's Test");
		fd.setName("Primitive-Whitespace-Tokenizer");
		fd.setVersion("1");
		fd.setModifiesCas(true);
		fd.setMultipleDeploymentAllowed(true);
		fd.setOutputsNewCASes(false);
		
		// Create the token type and feature.
		TypeDescription td = new TypeDescription_impl();
		td.setName("gov.va.vinci.leo.types.Token");
		td.setDescription("My Desc");
		td.setSupertypeName("uima.tcas.Annotation");
		
		FeatureDescription f = new FeatureDescription_impl();
		f.setName("tokenType");
		f.setRangeTypeName("uima.cas.Integer");
		td.setFeatures(new FeatureDescription[] { f });
		
		// Add the type. 
		fd.addType(td);
		fd.setImplementationName("gov.va.vinci.leo.ae.WhitespaceTokenizer");
		
		// Add any capabilities
		Capability c = new Capability_impl();
		c.addOutputType("gov.va.vinci.leo.types.Token", true);
		fd.addCapability(c);

		
		// Add a bogus parameter
		ConfigurationParameter p = new ConfigurationParameter_impl();
		p.setName("fakeTokenParam");
		p.setDescription("This is a fake parameter.");
		p.setMandatory(true);
		p.setType("String");
		fd.addConfigurationParameter(p);
		
		// Add a bogus parameter
		fd.addParameterSetting("fakeTokenParam2", false, false, "Integer", 1);

		// Set value, or it won't run because it is required.
		fd.setParameterSetting("fakeTokenParam", "THIS IS A TEST VALUE");
		fd.setParameterSetting("fakeTokenParam2", 2);

		StringWriter writer = new StringWriter();
		fd.getAnalysisEngineDescription().toXML(writer);
		
		// The queue name has a uuid appended, remove it:
		String result = writer.toString();
		
		String toCheck = result.substring(0, result.indexOf("<name>Primitive-Whitespace-Tokenizer"));
		toCheck += "<name>Primitive-Whitespace-Tokenizer" + 
					result.substring(result.indexOf("<name>Primitive-Whitespace-Tokenizer") + 76,
							result.indexOf("<name>leoTypeDescription"));
		toCheck += "<name>leoTypeDescription" +
					result.substring(result.indexOf("<name>leoTypeDescription") + 61);

		String fileString = FileUtils.file2String(new File(rootDirectory + "src/test/resources/results/CreatePrimitiveAnalysisEngineDescriptionTest-testProgramaticPrimitive.xml"));

        /** Ends with to avoid license header issues and whitespace **/
		assertTrue(fileString.replaceAll("\\s+","").endsWith(toCheck.replaceAll("\\s+","").substring(toCheck.indexOf("\n"))));
	}//testProgrammaticPrimitive method


}
