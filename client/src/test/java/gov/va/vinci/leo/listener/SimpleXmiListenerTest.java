package gov.va.vinci.leo.listener;

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.cr.FileCollectionReader;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.impl.EntityProcessStatusImpl;
import org.apache.uima.util.ProcessTrace;
import org.apache.uima.util.impl.ProcessTrace_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomasginter on 3/17/16.
 */
public class SimpleXmiListenerTest {
    protected static AnalysisEngine ae = null;
    protected static File aggregateDescriptor = new File("src/test/resources/aggSimpleXmiListenerTest.xml");
    protected static File outDir = new File("src/test/resources/xmi-listener-test");
    protected static File inDir = new File("src/test/resources/inputDirectory");
    protected static String rootDirectory = "";
    protected static LeoAEDescriptor aggDesc = null;

    @Before
    public void setup() throws Exception {
        String path = new File(".").getCanonicalPath();
        System.out.println(path);
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
            aggregateDescriptor = new File(rootDirectory + "src/test/resources/aggSimpleXmiListenerTest.xml");
            outDir = new File(rootDirectory + "src/test/resources/xmi-listener-test");
            inDir = new File(rootDirectory + "src/test/resources/inputDirectory");
        }

        if (ae != null)
            return;
        aggDesc = SampleService.simpleServiceDefinition();
        ae = UIMAFramework.produceAnalysisEngine(
                aggDesc.getAnalysisEngineDescription()
        );
        aggDesc.setDescriptorLocator(aggregateDescriptor.toURI()).toXML();
        if (!outDir.exists())
            outDir.mkdir();
        /**
         cas = ae.newCAS();
         cas.setDocumentText("012345678901234567890123456789");
         CSI csi = new CSI(cas.getJCas());
         csi.setID("1");
         csi.setBegin(0);
         csi.setEnd(29);
         csi.addToIndexes();
         ae.process(cas);
         **/
    }

    @Test
    public void testSimpleXmiListener() throws Exception {
        SimpleXmiListener listener = new SimpleXmiListener(outDir)
                .setTypeSystemDescriptor(aggDesc.getTypeSystemDescription())
                .setLaunchAnnotationViewer(false);
        assertNotNull(listener);
        FileCollectionReader reader = (FileCollectionReader) new FileCollectionReader(inDir, true).produceCollectionReader();
        int counter = 0;
        while(reader.hasNext()) {
            CAS cas = ae.newCAS();
            reader.getNext(cas);
            listener.onBeforeMessageSend(new UimaASProcessStatusImpl(new ProcessTrace_impl(), cas, "" + counter++));
            ProcessTrace pTrace = ae.process(cas);
            listener.entityProcessComplete(cas, new EntityProcessStatusImpl(pTrace));
        }
        listener.collectionProcessComplete(null);
        File[] files = outDir.listFiles();
        assertEquals(counter+1, files.length);
        if(files.length > 0) {
            String xmiText = FileUtils.readFileToString(files[0]);
            assertTrue(xmiText.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmi:XMI xmlns:tcas=\"http:///uima/tcas.ecore\""));
        }
    }

    @Test
    public void testSimpleXmiListenerWithTypeSystem() throws Exception {
        SimpleXmiListener listener = new SimpleXmiListener(outDir)
                .setTypeSystemDescriptor(new File(rootDirectory + "src/test/resources/xmi-corpus-good/type_system_desc.xml"))
                .setLaunchAnnotationViewer(true);
        LeoTypeSystemDescription typeSystemDescription = listener.getTypeSystemDescriptor();
        assertNotNull(typeSystemDescription);
        assertEquals(3, typeSystemDescription.getTypes().length);
        assertTrue(listener.isLaunchAnnotationViewer());
    }

    @After
    public void cleanup() throws Exception {
        if(outDir.exists())
            FileUtils.deleteDirectory(outDir);
        if(aggregateDescriptor.exists())
            FileUtils.forceDelete(aggregateDescriptor);
    }
}
