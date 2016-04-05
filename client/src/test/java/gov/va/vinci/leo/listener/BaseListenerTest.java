package gov.va.vinci.leo.listener;

import gov.va.vinci.leo.descriptors.AnalysisEngineFactory;
import org.apache.log4j.*;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.ProcessTrace;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by ryancornia on 1/20/15.
 */
public class BaseListenerTest {
    CAS cas = null;
    String rootDirectory = "";

    /**
     * Setup an in-memory db to test against with a simple schema.
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }

        AnalysisEngineDescription aed = AnalysisEngineFactory.generateAED(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml", false);

        cas = CasCreationUtils.createCas(aed);
    }



    @Test
    public void testError() {

        // Setup WriterAppender
        Writer w = new StringWriter();
        Layout l = new PatternLayout("%m%n");

        WriterAppender wa = new WriterAppender(l, w);
        wa.setEncoding("UTF-8");
        wa.setThreshold(Level.ALL);
        wa.activateOptions();// WriterAppender does nothing here, but I like defensive code...

        // Add it to logger
        Logger log = Logger.getLogger(String.valueOf(BaseListener.class));// ExceptionHandler is the class that contains this code : `log.warn("An error has occured:", e);'
        LogManager.getRootLogger().addAppender(wa);


        MyTestListener listener = new MyTestListener();
        listener.setLogErrors(true);
        assertTrue(listener.isLogErrors());

        listener.entityProcessComplete(cas, new EntityProcessStatus() {
            @Override
            public boolean isException() {
                return true;
            }

            @Override
            public String getStatusMessage() {
                return "return error";
            }

            @Override
            public List<Exception> getExceptions() {
                Exception e = new RuntimeException("test1");
                Exception e2 = new RuntimeException("test2");
                List<Exception> exceptions = new ArrayList<Exception>();
                exceptions.add(e);
                exceptions.add(e2);
                return exceptions;
            }

            @Override
            public List<String> getFailedComponentNames() {
                return null;
            }

            @Override
            public ProcessTrace getProcessTrace() {
                return null;
            }

            @Override
            public boolean isEntitySkipped() {
                return false;
            }
        });


        String log4jOutput = w.toString();
        assertTrue(log4jOutput.contains("java.lang.RuntimeException: test2"));
        assertTrue(log4jOutput.contains("java.lang.RuntimeException: test1"));
        assertTrue(log4jOutput.contains("ERROR processing CAS:"));
        wa.close();
        LogManager.getRootLogger().removeAppender(wa);
    }

    public class MyTestListener extends BaseListener {

    }
}
