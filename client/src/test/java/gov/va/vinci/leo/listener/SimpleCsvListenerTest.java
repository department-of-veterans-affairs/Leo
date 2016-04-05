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


import au.com.bytecode.opencsv.CSVWriter;
import clover.org.apache.commons.lang3.StringUtils;
import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.cr.RandomStringCollectionReader;
import gov.va.vinci.leo.types.CSI;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.impl.EntityProcessStatusImpl;
import org.apache.uima.util.ProcessTrace;
import org.apache.uima.util.impl.ProcessTrace_impl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class SimpleCsvListenerTest {
    protected static CAS cas = null;
    protected static File inDir = new File("src/test/resources/inputDirectory");
    protected static String rootDirectory = "";

    @Before
    public void setup() throws Exception {
        if (cas != null)
            return;
        String path = new File(".").getCanonicalPath();
        if(!path.endsWith("client")) {
            rootDirectory = "client/";
            inDir = new File(rootDirectory + "src/test/resources/inputDirectory");
        }
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
    public void testOutputStreamConstructor() throws Exception {
        OutputStream out = FileUtils.openOutputStream(File.createTempFile("testOutput", "csv"));
        assertNotNull(out);
        SimpleCsvListener listener = new SimpleCsvListener(out);
        assertNotNull(listener);
        listener.setIncludeFeatures(true);
        assertTrue(listener.isIncludeFeatures());
    }

    @Test
    public void testWriterConstructor() throws Exception {
        StringWriter writer = new StringWriter();
        SimpleCsvListener listener = new SimpleCsvListener(writer);
        assertNotNull(listener);
        assertEquals(CSVWriter.DEFAULT_ESCAPE_CHARACTER, listener.getEscapechar());
        assertEquals(CSVWriter.DEFAULT_SEPARATOR, listener.getSeparator());
        assertEquals(CSVWriter.DEFAULT_QUOTE_CHARACTER, listener.getQuotechar());
        assertEquals(CSVWriter.DEFAULT_LINE_END, listener.getLineEnd());
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

    @Test
    public void testRunSimpleCsvListenerTest() throws Exception {
        StringWriter writer = new StringWriter();
        SimpleCsvListener listener = new SimpleCsvListener(writer);
        assertNotNull(listener);
        listener.setInputType(Token.class.getCanonicalName());
        assertTrue(listener.getInputType()[0].equals(Token.class.getCanonicalName()));
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition().getAnalysisEngineDescription()
        );
        RandomStringCollectionReader reader = new RandomStringCollectionReader(10).setMaxStringLength(16);
        int counter = 0;
        ProcessTrace pTrace = null;
        while(reader.hasNext()) {
            CAS cas = ae.newCAS();
            reader.getNext(cas);
            listener.onBeforeMessageSend(new UimaASProcessStatusImpl(new ProcessTrace_impl(), cas, "" + counter++));
            pTrace = ae.process(cas);
            listener.entityProcessComplete(cas, new EntityProcessStatusImpl(pTrace));
        }
        listener.collectionProcessComplete(new EntityProcessStatusImpl(pTrace));
        assertEquals(counter, listener.getNumReceived());
        assertTrue(StringUtils.isNotBlank(writer.toString()));
    }

}
