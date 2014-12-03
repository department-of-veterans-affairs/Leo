package gov.va.vinci.leo.ae;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 14:23
 */
public class ExternalAnnotatorTest {

    @Test
    public void testImportDescriptor() throws Exception {
        ExternalAnnotator externalAnnotator = new ExternalAnnotator("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor", true);
        assertNotNull(externalAnnotator);
        assertNotNull(externalAnnotator.getDescriptor());
    }
}
