package gov.va.vinci.leo.descriptors;

import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by tginter on 7/7/17.
 */
public class LeoPearDescriptorTest {

    protected static final String SENT_PEAR_PATH = "src/test/resources/tools/sent_full-test-7.20.2017.pear";
    protected static final File OUT_DIR = new File("src/test/resources/results/pear");
    protected static String sentDescPath;

    @Before
    public void setup() throws Exception {
        if(!OUT_DIR.exists())
            OUT_DIR.mkdir();
        File extDir = new File(OUT_DIR, "extracted");
        if(!extDir.exists())  {
            extDir.mkdir();
        }
        if(StringUtils.isBlank(sentDescPath)) {
            StopWatch clock = new StopWatch();
            clock.start();
            sentDescPath = LeoUtils.extractPearFile(extDir, new File(SENT_PEAR_PATH), false, false);
            clock.stop();
            System.out.println("Sent pear extraction took " + clock);
        }
    }

    @Test
    public void testConstructors()  throws Exception {
        LeoPearDescriptor pearDescriptor = new LeoPearDescriptor(sentDescPath);
        assertNotNull(pearDescriptor);
        assertNotNull(pearDescriptor.getResourceSpecifier());
        assertTrue(pearDescriptor.getResourceSpecifier().getSourceUrl().toString().endsWith("_pear.xml"));
    }

    @Test
    public void testPearTextProcessing() throws Exception {
        LeoPearDescriptor pearDescriptor = new LeoPearDescriptor(sentDescPath);
        assertNotNull(pearDescriptor);
        assertNotNull(pearDescriptor.getResourceSpecifier());
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        AnalysisEngine analysisengine = UIMAFramework.produceAnalysisEngine(pearDescriptor.getResourceSpecifier());
        stopWatch.stop();
        System.out.println("Engine initialization took " + stopWatch);
        JCas jCas = analysisengine.newJCas();
        jCas.setDocumentText("I love hamburgers and fries!  JIB is the bomb!!");

        stopWatch.reset();
        stopWatch.start();
        analysisengine.process(jCas);
        stopWatch.stop();
        System.out.println("Processing took: " + stopWatch);

        assertTrue(jCas.getAnnotationIndex().size() > 0);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        if(OUT_DIR.exists())
            FileUtils.deleteDirectory(OUT_DIR);
    }
}
