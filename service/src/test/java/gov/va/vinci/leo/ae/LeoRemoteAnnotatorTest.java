package gov.va.vinci.leo.ae;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 14:30
 */
public class LeoRemoteAnnotatorTest {
    @Test
    public void testCreateDescriptor() throws Exception {
        LeoRemoteAnnotator remoteAnnotator = new LeoRemoteAnnotator("tcp://localhost:61616", "myInputQueue", "BOB");
        assertNotNull(remoteAnnotator);
        assertNotNull(remoteAnnotator.getDescriptor());
        assertTrue(remoteAnnotator.getName().startsWith("BOB"));
    }
}
