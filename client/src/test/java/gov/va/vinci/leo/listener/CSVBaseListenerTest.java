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
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CSVBaseListenerTest {
    protected static CAS cas = null;

    @Before
    public void setup() throws Exception {
        if(cas != null)
            return;
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition().getAnalysisEngineDescription()
        );
        cas = ae.newCAS();
        cas.setDocumentText("a b c");
        CSI csi = new CSI(cas.getJCas());
        csi.setID("1");
        csi.setBegin(0);
        csi.setEnd(29);
        csi.addToIndexes();
        ae.process(cas);
    }

    @Test
    public void testSimple() throws IOException {
        File f = File.createTempFile("testSimple", "txt");
        TestCsvListener listener = new TestCsvListener(f);
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\"a\",\"b\",\"\"\"c\"", b.toString());
    }

    @Test
    public void testCharSeprarator() throws IOException {
        File f = File.createTempFile("testCharSeprarator", "txt");
        TestCsvListener listener = new TestCsvListener(f)
                .setSeparator('-');
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\"a\"-\"b\"-\"\"\"c\"", b.toString());
    }

    @Test
    public void testCharSepraratorQuoteChar() throws IOException {
        File f = File.createTempFile("testCharSepraratorQuoteCharEscapeChar", "txt");
        TestCsvListener listener = new TestCsvListener(f)
                .setSeparator('-')
                .setQuotechar('\'');
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\'a\'-\'b\'-\'\"\"c\'", b.toString());
    }


    @Test
    /**
     * Test NOT escaping quotes.
     */
    public void testCharSepraratorQuoteCharEscapeChar() throws IOException {
        File f = File.createTempFile("testCharSepraratorQuoteCharEscapeChar", "txt");
        TestCsvListener listener = new TestCsvListener(f)
                .setSeparator('-')
                .setQuotechar('\'')
                .setEscapechar('~');
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\'a\'-\'b\'-\'\"c\'", b.toString());
    }

    @Test
    /**
     * Test NOT escaping quotes.
     */
    public void testCharSepraratorQuoteCharEscapeCharNewLine() throws IOException {
        File f = File.createTempFile("testCharSepraratorQuoteCharEscapeCharNewLine", "txt");
        TestCsvListener listener = new TestCsvListener(f)
                .setSeparator('-')
				.setQuotechar('\'')
                .setLineEnd("<CRLF>")
                .setEscapechar('~');
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\'a\'-\'b\'-\'\"c\'<CRLF>", b);
    }

    @Test
    /**
     * Test NOT escaping quotes.
     */
    public void testCharSepraratorQuoteCharNewLine() throws IOException {
        File f = File.createTempFile("testCharSepraratorQuoteCharNewLine", "txt");
        TestCsvListener listener = new TestCsvListener(f)
                .setSeparator('-')
				.setQuotechar('\'')
				.setLineEnd("<CRLF>");
        listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
        assertEquals("\'a\'-\'b\'-\'\"\"c\'<CRLF>", b.toString());
    }

    public class TestCsvListener extends BaseCsvListener {

        public TestCsvListener(File pw) throws FileNotFoundException {
            super(pw);
        }

        @Override
        protected List<String[]> getRows(CAS aCas) {
            List<String[]> results = new ArrayList<String[]>();
            results.add(new String[]{"a", "b", "\"c"});
            return results;
        }

        @Override
        protected String[] getHeaders() {
            return new String[]{"co1", "col2", "col3"};
        }

    }
}
