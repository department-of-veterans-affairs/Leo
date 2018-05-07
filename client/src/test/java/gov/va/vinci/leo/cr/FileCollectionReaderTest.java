package gov.va.vinci.leo.cr;

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
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileCollectionReaderTest {

    /**
     * Array of File objects being processed.
     */
    protected ArrayList<File> mFileCollection = null;
    private static String RESULTS = "this is a test";
    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }
    }

    @Test
    public void testEmptyConstructor() {
        FileCollectionReader fsr = new FileCollectionReader();
        assertNotNull(fsr);
    }

    @Test
    public void testConstructor() {
        FileCollectionReader fsr = new FileCollectionReader(new File(rootDirectory + "src/test/resources/inputDirectory"), true);
        assertNotNull(fsr);
    }

    @Test
    public void testRecurseFlag() throws Exception {
        FileCollectionReader fsr =
                (FileCollectionReader) new FileCollectionReader(new File(rootDirectory + "src/test/resources/inputDirectory"), false)
                        .produceCollectionReader();
        assertNotNull(fsr);
        assertTrue(fsr.getCollectionSize() == 1);
        FileCollectionReader fsr2 =
                (FileCollectionReader) new FileCollectionReader(new File(rootDirectory + "src/test/resources/inputDirectory"), true)
                        .produceCollectionReader();
        assertNotNull(fsr);
        assertTrue(fsr2.getCollectionSize() == 2);
    }

    @Test
    public void testInitialize() throws IllegalAccessException, ResourceInitializationException {
        FileCollectionReader fileCollectionReader =
                (FileCollectionReader) new FileCollectionReader(new File(rootDirectory + "src/test/resources/inputDirectory"), true)
                        .produceCollectionReader();
        assertEquals(1, fileCollectionReader.getCollectionSize(), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFilePathConstructor() throws Exception {
        FileCollectionReader fsr = new FileCollectionReader(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstructor() throws Exception {
        FileCollectionReader fsr = new FileCollectionReader(new File(rootDirectory + "src/test/resources/badDirectory"), true);
    }

    @Test
    public void testGetNext() throws Exception {
        FileCollectionReader fsr =
                (FileCollectionReader) new FileCollectionReader(new File(rootDirectory + "src/test/resources/inputDirectory"), true)
                        .setEncoding(Charset.defaultCharset().displayName())
                        .setRecurseFlag(false)
                        .produceCollectionReader();
        assertNotNull(fsr);
        assertEquals(Charset.defaultCharset().displayName(), fsr.getEncoding());
        assertEquals(new File(rootDirectory + "src/test/resources/inputDirectory").getAbsolutePath(), fsr.getInputDirectory().getAbsolutePath());
        FilenameFilter filenameFilter = fsr.getFilenameFilter();
        assertTrue(filenameFilter instanceof SuffixFileFilter);
        assertFalse(fsr.mRecurse);

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(SampleService
                .simpleServiceDefinition()
                .getAnalysisEngineDescription());
        CAS mockCas = ae.newCAS();
        assertTrue(fsr.hasNext());
        fsr.getNext(mockCas);
        assertEquals(1, fsr.getCurrentIndex());
        FSIterator<AnnotationFS> csiIterator = mockCas.getAnnotationIndex(mockCas.getTypeSystem().getType(CSI.class.getCanonicalName())).iterator();
        assertTrue(csiIterator.hasNext());
        CSI csi = (CSI) csiIterator.next();
        assertTrue("text1.txt".equals(csi.getID()));
        assertTrue(csi.getLocator().endsWith("client/src/test/resources/inputDirectory/text1.txt"));
        assertNull(csi.getPropertiesKeys());
        assertNull(csi.getRowData());
        assertTrue(RESULTS.equals(mockCas.getDocumentText()));
    }

}