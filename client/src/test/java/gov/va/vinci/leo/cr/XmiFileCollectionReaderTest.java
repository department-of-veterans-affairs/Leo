package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
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

import gov.va.vinci.leo.MockClient;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.listener.DoNothingListener;
import gov.va.vinci.leo.listener.SimpleXmiListener;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static gov.va.vinci.leo.SampleService.simpleServiceDefinition;
import static org.junit.Assert.*;


/**
 * <Description>
 * <p/>
 * User: thomasginter
 * Date: 7/9/13
 * Time: 10:52 AM
 */
public class XmiFileCollectionReaderTest {

    public static File xmiTestCorpus = null;
    public static File xmiTestBadCorpus = null;
    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }
        xmiTestBadCorpus = new File(rootDirectory + "src/test/resources/xmi-corpus-bad");
        xmiTestCorpus = new File(rootDirectory + "src/test/resources/xmi-corpus-good");
    }


    @Test
    public void testEmptyConstructor() throws Exception {
        XmiFileCollectionReader xfsr = new XmiFileCollectionReader();
        assertNotNull(xfsr);
    }

    public void testConstructor() throws Exception {
        XmiFileCollectionReader xfsr = new XmiFileCollectionReader(xmiTestCorpus, false);
        checkCollection(xfsr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPathConstructor() throws Exception {
        XmiFileCollectionReader xfsr = new XmiFileCollectionReader(null, true);
        assertNotNull(xfsr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadPathConsructor() throws Exception {
        XmiFileCollectionReader xmiFileSubReader = new XmiFileCollectionReader(new File("client/src/test/no-path-that-exists"), false);
        assertNotNull(xmiFileSubReader);
    }

    @Test(expected = ResourceInitializationException.class)
    public void testBadInitialize() throws NullPointerException, IllegalAccessException, ResourceInitializationException {
        HashMap<String, String> params = new HashMap<String, String>();
        XmiFileCollectionReader xmiFileSubReader = new XmiFileCollectionReader();
        assertNotNull(xmiFileSubReader);
        xmiFileSubReader.initialize();
    }

    @Test(expected = ResourceProcessException.class)
    public void testRunReaderBadTypeSystem() throws Exception {
        XmiFileCollectionReader reader = new XmiFileCollectionReader(xmiTestBadCorpus, false);
        assertNotNull(reader);
        DoNothingListener listener = new DoNothingListener();
        MockClient client = new MockClient();
        assertNotNull(client);
        assertNotNull(listener);

        //Add the type system
        client.getEngine().setAnalysisEngineFromDescription(
                simpleServiceDefinition(
                        new LeoTypeSystemDescription(new File(xmiTestBadCorpus, "type-system.xml").getAbsolutePath(), false)
                )
        );

        //Run the client
        client.run(reader, listener);
    }

    @Test
    public void testRunReader() throws Exception {
        XmiFileCollectionReader reader = new XmiFileCollectionReader(xmiTestCorpus, false);
        assertNotNull(reader);
        DoNothingListener listener = new DoNothingListener();
        MockClient client = new MockClient();
        assertNotNull(client);
        assertNotNull(listener);

        //Add the type system
        client.getEngine().setAnalysisEngineFromDescription(
                simpleServiceDefinition(
                        new LeoTypeSystemDescription(new File(xmiTestCorpus, SimpleXmiListener.TYPE_DESCRIPTION_NAME).getAbsolutePath(), false)
                )
        );

        //Run the client
        client.run(reader, listener);
        assertEquals(xmiTestCorpus.listFiles().length - 1, listener.getNumReceived());
    }

    @Test
    public void testInitialize() throws Exception {
        XmiFileCollectionReader xmiFileCollectionReader =
                (XmiFileCollectionReader) new XmiFileCollectionReader(xmiTestCorpus, true).produceCollectionReader();
        assertNotNull(xmiFileCollectionReader);
        checkCollection(xmiFileCollectionReader);
    }

    public void checkCollection(XmiFileCollectionReader xfsr) throws Exception {
        assertNotNull(xfsr);
        assertTrue(xfsr.hasNext());
        assertEquals(2, xfsr.getCollectionSize());
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(simpleServiceDefinition().getAnalysisEngineDescription());
        TypeDescription[] ar = ae.getAnalysisEngineMetaData().getTypeSystem().getTypes();
        JCas mockCas = ae.newJCas();
        xfsr.getNext(mockCas.getCas());
        String docText = mockCas.getDocumentText();
        assertTrue(docText.startsWith("This"));
    }
}
