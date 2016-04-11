package gov.va.vinci.leo.types;

import gov.va.vinci.leo.descriptors.AnalysisEngineFactory;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 4/8/16.
 */
public class CSITypeTest {

    @Test
    public void testCSIProperties() throws Exception {
        /**
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.RemoteExampleWhitespaceTokenizerDescriptor", true)
                    .addType(TypeLibrarian.getCSITypeSystemDescription())
                    .getAnalysisEngineDescription()
        );
        assertNotNull(ae);
         **/

        //Setup the CAS
//        JCas jcas = Mockito.mock(JCas.class);
//        assertNotNull(jcas);
        String docText = "hello world";

        //Create the CSI annotation
        CSI csi = Mockito.mock(CSI.class);

    }
}
