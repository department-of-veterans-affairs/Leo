package gov.va.vinci.leo.listener;

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

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.types.CSI;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SimpleCsvListener_quoteTest {
    protected static CAS cas = null;
    protected static File inDir = new File("src/test/resources/inputDirectory");
    protected static String rootDirectory = "";
    AnalysisEngine ae;

    @Before
    public void setup() throws Exception {
        if (cas != null)
            return;
        String path = new File(".").getCanonicalPath();
        if(!path.endsWith("client")) {
            rootDirectory = "client/";
            inDir = new File(rootDirectory + "src/test/resources/inputDirectory");
        }


        ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition().getAnalysisEngineDescription()
        );
        cas = ae.newCAS();
        cas.setDocumentText("012345678901234567\"890123456789");
        CSI csi = new CSI(cas.getJCas());
        csi.setID("1");
        csi.setBegin(0);
        csi.setEnd(29);
        csi.addToIndexes();
        ae.process(cas);
    }

    @Test
    public void testWithAnnotationFeatureThatHasDoubleQuoteInIt() throws IOException {
        File f = File.createTempFile("testSimple", "txt");
        SimpleCsvListener listener = new SimpleCsvListener(f)//, false, '|', true, Token.class.getCanonicalName(), WordToken.class.getCanonicalName())
                .setIncludeFeatures(true)
                .setSeparator('|')
                .setExitOnError(false)
                .setAnnotationTypeFilter(Token.class.getCanonicalName(), WordToken.class.getCanonicalName());
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\"1\"|\"0\"|\"29\"|\"gov.va.vinci.leo.types.CSI\"|\"012345678901234567\"\"8901234567\"|\"[ID = 1]\"|\"[Locator = null]\"|\"[RowData = null]\"|\"[PropertiesKeys = null]\"|\"[PropertiesValues = null]\"\n" +
                "\"1\"|\"0\"|\"18\"|\"gov.va.vinci.leo.whitespace.types.Token\"|\"012345678901234567\"|\"[TokenType = 1]\"|\"[AnnotationFeature = 012345678901234567\\\"\"8901234567]\"\n" +
                "\"1\"|\"18\"|\"19\"|\"gov.va.vinci.leo.whitespace.types.Token\"|\"\"\"\"|\"[TokenType = 5]\"|\"[AnnotationFeature = 012345678901234567\\\"\"8901234567]\"\n" +
                "\"1\"|\"19\"|\"31\"|\"gov.va.vinci.leo.whitespace.types.Token\"|\"890123456789\"|\"[TokenType = 1]\"|\"[AnnotationFeature = 012345678901234567\\\"\"8901234567]\"", b.toString());
    }

}
