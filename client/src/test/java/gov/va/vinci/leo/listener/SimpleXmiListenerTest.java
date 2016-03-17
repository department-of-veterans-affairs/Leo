package gov.va.vinci.leo.listener;

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.cr.FileCollectionReader;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.impl.ProcessTrace_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomasginter on 3/17/16.
 */
public class SimpleXmiListenerTest {
    protected static AnalysisEngine ae = null;
    protected static File aggregateDescriptor = new File("client/src/test/resources/aggSimpleXmiListenerTest.xml");
    protected static File outDir = new File("client/src/test/resources/xmi-listener-test-out");
    protected static File inDir = new File("client/src/test/resources/inputDirectory");

    @Before
    public void setup() throws Exception {
        if (ae != null)
            return;
        LeoAEDescriptor aggDesc = SampleService.simpleServiceDefinition();
        ae = UIMAFramework.produceAnalysisEngine(
                aggDesc.getAnalysisEngineDescription()
        );
        aggDesc.setDescriptorLocator(aggregateDescriptor.toURI()).toXML();
        //aggDesc.getAnalysisEngineDescription().toXML(FileUtils.openOutputStream(aggregateDescriptor));
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
                .setLaunchAnnotationViewer(false)
                .setTypeSystemDescriptor(aggregateDescriptor);
        FileCollectionReader reader = (FileCollectionReader) new FileCollectionReader(inDir, false).produceCollectionReader();
        int counter = 0;
        while(reader.hasNext()) {
            CAS cas = ae.newCAS();
            reader.getNext(cas);
            listener.onBeforeMessageSend(new UimaASProcessStatusImpl(new ProcessTrace_impl(), cas, "" + counter++));
            ae.process(cas);
            listener.entityProcessComplete(cas, null);
        }
        listener.collectionProcessComplete(null);
        File[] files = outDir.listFiles();
        assertEquals(counter, files.length);
        if(files.length > 0) {
            String xmiText = FileUtils.readFileToString(files[0]);
            assertTrue(xmiText.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmi:XMI xmlns:tcas=\"http:///uima/tcas.ecore\""));
        }
    }

    @After
    public void cleanup() throws Exception {
        if(outDir.exists())
            FileUtils.deleteDirectory(outDir);
        if(aggregateDescriptor.exists())
            FileUtils.forceDelete(aggregateDescriptor);
    }
}
