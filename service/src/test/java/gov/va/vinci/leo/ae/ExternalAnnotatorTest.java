package gov.va.vinci.leo.ae;

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testNameClassConstructor() throws Exception {
        ExternalAnnotator externalAnnotator = new ExternalAnnotator("ExampleAnnotator", ExampleAnnotator.class.getName())
                .setNumInstances(5);
        assertNotNull(externalAnnotator);
        assertTrue(externalAnnotator.getName().startsWith("ExampleAnnotator"));
        assertEquals(5, externalAnnotator.getNumInstances());
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        ExternalAnnotator externalAnnotator = new ExternalAnnotator("ExampleAnnotator", ExampleAnnotator.class.getName());
        assertNotNull(externalAnnotator);
        assertTrue(externalAnnotator.getName().startsWith("ExampleAnnotator"));

        externalAnnotator.setName("BobExample");
        assertTrue(externalAnnotator.getName().startsWith("BobExample"));

        LeoTypeSystemDescription typeSystemDescription = new ExampleAnnotator().getLeoTypeSystemDescription();
        externalAnnotator.addTypeSystemDescription(typeSystemDescription);
        LeoTypeSystemDescription externalTypeSystem = externalAnnotator.getLeoTypeSystemDescription();
        assertNotNull(externalTypeSystem);
        for(TypeDescription typeDescription : externalTypeSystem.getTypes()) {
            TypeDescription exampleType = typeSystemDescription.getType(typeDescription.getName());
            if(exampleType == null)
                System.out.println(typeDescription.getName() + " was not found in ExampleAnnotator type system.");
            else
                System.out.println(typeDescription.getName() + " was found!");
            assertNotNull(exampleType);
        }
    }
}
