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
import org.apache.uima.util.InvalidXMLException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnalysisEngineFactoryTest {


    @Test
    public void testGenerateAEDByName() throws Exception {
        AnalysisEngineDescription d = AnalysisEngineFactory.generateAED("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
        assertTrue(d.getImplementationName().equals("gov.va.vinci.marian.whitespace.ae.WhitespaceTokenizer"));
    }

    @Test
    public void testGenerateEmptyDescriptor() throws Exception {
        AnalysisEngineDescription d = AnalysisEngineFactory.generateAED();
        assertNotNull(d);
    }

    @Test(expected = InvalidXMLException.class)
    public void testGenerateNonAEDescriptor() throws Exception {
        AnalysisEngineDescription d = AnalysisEngineFactory.generateAED("desc.gov.va.vinci.leo.types.CSI", true);
    }
}
