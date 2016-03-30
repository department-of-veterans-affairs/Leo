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
import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimpleCsvListenerTest {
    protected static CAS cas = null;


    @Before
    public void setup() throws Exception {
        if(cas != null)
            return;
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition().getAnalysisEngineDescription()
        );
        cas = ae.newCAS();
        cas.setDocumentText("012345678901234567890123456789");
        CSI csi = new CSI(cas.getJCas());
        csi.setID("1");
        csi.setBegin(0);
        csi.setEnd(29);
        csi.addToIndexes();
        ae.process(cas);
    }

    @Test
    public void testSimple() throws Exception {
        File f = File.createTempFile("testSimple", "txt");
        SimpleCsvListener listener = new SimpleCsvListener(f).setIncludeHeader(true);
        listener.initializationComplete(null);
        listener.entityProcessComplete(cas, null);
        assertEquals("1", listener.getReferenceLocation(cas.getJCas()));
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\"DocumentId\",\"Start\",\"End\",\"Type\",\"CoveredText\"\n" +
                "\"1\",\"0\",\"29\",\"gov.va.vinci.leo.types.CSI\",\"01234567890123456789012345678\"\n" +
                "\"1\",\"0\",\"30\",\"gov.va.vinci.leo.whitespace.types.Token\",\"012345678901234567890123456789\"", b.toString());
    }

    @Test
    public void testSimple2() throws IOException {
        File f = File.createTempFile("testSimple", "txt");
        SimpleCsvListener listener = new SimpleCsvListener(f)//, false, '|', true, Token.class.getCanonicalName(), WordToken.class.getCanonicalName())
                .setIncludeFeatures(true)
                .setSeparator('|')
                .setExitOnError(false)
                .setAnnotationTypeFilter(Token.class.getCanonicalName(), WordToken.class.getCanonicalName());
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\"1\"|\"0\"|\"29\"|\"gov.va.vinci.leo.types.CSI\"|\"01234567890123456789012345678\"|\"[ID = 1]\"|\"[Locator = null]\"|\"[RowData = null]\"|\"[PropertiesKeys = null]\"|\"[PropertiesValues = null]\"\n" +
                "\"1\"|\"0\"|\"30\"|\"gov.va.vinci.leo.whitespace.types.Token\"|\"012345678901234567890123456789\"|\"[TokenType = 1]\"", b.toString());
    }

}
