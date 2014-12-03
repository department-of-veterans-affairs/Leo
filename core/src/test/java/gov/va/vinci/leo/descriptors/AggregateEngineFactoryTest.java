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

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class AggregateEngineFactoryTest {

    String rootDirectory = "";

    @Before
    public void onBefore() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }
	
	
	@Test
	public void testCreateAggregateByName() throws Exception {
		ArrayList<String> primitives = new ArrayList<String>();
		
		primitives.add("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor");
		primitives.add("desc.gov.va.vinci.leo.ae.WordTokenizerDescriptor");
		
		
		AnalysisEngineDescription aed = AggregateEngineFactory.createAggregateDescription("My Aggregate", primitives, true);
		validateCreatedAggregate(aed);
	}
	
	@Test
	public void testCreateAggregateByNotName() throws Exception {
		ArrayList<String> primitives = new ArrayList<String>();
		
		primitives.add(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml");
		primitives.add(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WordTokenizerDescriptor.xml");
		
		
		AnalysisEngineDescription aed = AggregateEngineFactory.createAggregateDescription("My Aggregate", primitives, false);
		validateCreatedAggregate(aed);
	}
	
	protected void validateCreatedAggregate(AnalysisEngineDescription aed) throws Exception {
		Map<String,ResourceSpecifier> map = aed.getDelegateAnalysisEngineSpecifiers();
		assertTrue(map.size() == 2);
		
		Set<String> keys = map.keySet();
		Object[] keyArray = keys.toArray();
        List keyList = new ArrayList(Arrays.asList(keyArray));

		assertTrue(keyList.contains("aeWordTokenizerDescriptor"));
		assertTrue(keyList.contains("aeWhitespaceTokenizerDescriptor"));
	}
}
