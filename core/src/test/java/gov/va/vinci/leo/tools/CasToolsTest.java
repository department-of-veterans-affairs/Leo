package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Core
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

/**
 * Test CasTools methods.
 *
 * User: Thomas Ginter
 * Date: 12/5/13
 * Time: 10:43 AM
 */
public class CasToolsTest {

    /**    FUTURE CHANGE - fix this!
    LeoAEDescriptor aggregate = null;

    @Before
    public void setup() throws Exception {
        aggregate = IntegrationWhitespaceService.createExampleWhitespacePipeline(false);
    }

    @Test
    public void testGetReferenceID() throws Exception {
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

        //Create a new JCas then process it
        JCas jCas = ae.newJCas();
        jCas.setDocumentText("This is a test only!");
        ae.process(jCas);

        //Test the return value for a CAS with no known identifier annotation
        NameValue nv = CasTools.getReferenceID(jCas);
        assertNull(nv);

        //Add a CSI annotation
        CSI csi = new CSI(jCas);
        csi.setBegin(0);
        csi.setEnd(jCas.getDocumentText().length());
        csi.setID("1234");
        csi.setLocator("/path/to/doc");
        csi.addToIndexes();
        NameValue nameValue = CasTools.getReferenceID(jCas);
        assertTrue("1234".equals(nameValue.getName()));
        assertTrue(nameValue.getValue() instanceof CSI);
    }

    **/

}
