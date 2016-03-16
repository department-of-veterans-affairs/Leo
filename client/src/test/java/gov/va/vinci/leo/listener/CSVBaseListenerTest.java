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


import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CSVBaseListenerTest {
    CAS cas = null;


    /**
     * Setup an in-memory db to test against with a simple schema.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(new WhitespaceTokenizer().getLeoAEDescriptor()
                .setParameterSetting("tokenOutputType", Token.class.getCanonicalName())
                .setParameterSetting("wordOutputType", WordToken.class.getCanonicalName())
                .setParameterSetting("tokenOutputTypeFeature", new Integer(3))
                .setTypeSystemDescription(new WhitespaceTokenizer().getLeoTypeSystemDescription())
                .getAnalysisEngineDescription());
        cas = CasCreationUtils.createCas(ae.getAnalysisEngineMetaData());

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
                .setSeparator('-');
        listener.setQuotechar('\'');
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
