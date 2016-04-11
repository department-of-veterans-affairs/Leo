package gov.va.vinci.leo.types;

import gov.va.vinci.leo.model.FeatureNameType;
import jdk.nashorn.internal.runtime.ECMAException;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 3/25/16.
 */
public class TypeLibrarianTest {

    @Test
    public void testValidationAnnotationTypeSystem() throws Exception {
        TypeDescription typeDescription = TypeLibrarian.getValidationAnnotationTypeSystemDescription();
        assertEquals("gov.va.vinci.leo.types.ValidationAnnotation", typeDescription.getName());
        FeatureDescription[] featureDescriptions = typeDescription.getFeatures();
        assertNotNull(featureDescriptions);
        assertEquals(4, featureDescriptions.length);
    }

    @Test
    public void testRelationshipAnnotationTypeSystemDescription() throws Exception {
        TypeDescription typeDescription = TypeLibrarian.getRelationshipAnnotationTypeSystemDescription();
        assertEquals("gov.va.vinci.leo.types.RelationshipAnnotation", typeDescription.getName());
        FeatureDescription[] featureDescriptions = typeDescription.getFeatures();
        assertNotNull(featureDescriptions);
        assertEquals(2, featureDescriptions.length);
    }

    @Test
    public void testCSITypeSystemDescription() throws Exception {
        TypeDescription typeDescription = TypeLibrarian.getCSITypeSystemDescription();
        assertEquals("gov.va.vinci.leo.types.CSI", typeDescription.getName());
        FeatureDescription[] featureDescriptions = typeDescription.getFeatures();
        assertNotNull(featureDescriptions);
        assertEquals(5, featureDescriptions.length);
    }

    @Test
    public void testGetTypeSystemDescription() throws Exception {
        TypeDescription typeDescription = TypeLibrarian.getTypeSystemDescription("gov.va.vinci.leo.TestType",
                new FeatureNameType("Bob", "uima.cas.String"));
        assertEquals("gov.va.vinci.leo.TestType", typeDescription.getName());
        FeatureDescription[] featureDescriptions = typeDescription.getFeatures();
        assertNotNull(featureDescriptions);
        assertEquals("Bob", featureDescriptions[0].getName());
        assertEquals("uima.cas.String", featureDescriptions[0].getRangeTypeName());
    }
}
