package gov.va.vinci.leo.cr;

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.types.CSI;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * User: Thomas Ginter
 * Date: 10/31/14
 * Time: 12:32
 */
public class ExternalFileCollectionReaderTest extends FileCollectionReader {

    File descriptor = null;
    ExternalFileCollectionReader externalReader = null;
    private static String RESULTS = "this is a test";
    String rootDirectory = "";

    @Before
    public void setup() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }
        descriptor = new File(rootDirectory + "src/test/resources/ExternalFileCollectionReaderDesc.xml");
        externalReader = new ExternalFileCollectionReader(descriptor, new File(rootDirectory + "src/test/resources/inputDirectory"), false);
        externalReader.produceCollectionReader();
    }

    @Test
    public void testConstructors() throws Exception {
        ExternalCollectionReader reader = new ExternalCollectionReader(descriptor.getAbsolutePath());
        assertNotNull(reader);
        CollectionReader generatedReader = reader.produceCollectionReader();
        assertNotNull(generatedReader);
        assertTrue(generatedReader instanceof ExternalFileCollectionReader);

        reader = new ExternalCollectionReader((CollectionReader_ImplBase) generatedReader);
        assertNotNull(reader);
        assertEquals(generatedReader, reader.produceCollectionReader());
    }

    @Test
    public void testDescriptorOutput() throws Exception {
        assertTrue(descriptor.exists());
    }

    @Test
    public void testGetNext() throws Exception{
        CollectionReader ecr = new ExternalCollectionReader(descriptor).produceCollectionReader();
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(SampleService
                .simpleServiceDefinition()
                .getAnalysisEngineDescription());
        CAS mockCas = ae.newCAS();
        ecr.getNext(mockCas);
        FSIterator<AnnotationFS> csiIterator = mockCas.getAnnotationIndex(mockCas.getTypeSystem().getType(CSI.class.getCanonicalName())).iterator();
        assertTrue(csiIterator.hasNext());
        CSI csi = (CSI) csiIterator.next();
        assertTrue("text1.txt".equals(csi.getID()));
        //System.out.println(csi.getLocator());
        assertTrue(csi.getLocator().endsWith(rootDirectory + "src/test/resources/inputDirectory/text1.txt"));
        assertNull(csi.getPropertiesKeys());
        assertNull(csi.getRowData());
        assertTrue(RESULTS.equals(mockCas.getDocumentText()));
    }

    @After
    public void cleanup() throws Exception {
        if(descriptor != null && descriptor.exists())
            FileUtils.forceDelete(descriptor);
    }

}
