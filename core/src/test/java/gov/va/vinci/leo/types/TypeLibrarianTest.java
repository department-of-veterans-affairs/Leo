package gov.va.vinci.leo.types;

/*
 * #%L
 * Leo Core
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import gov.va.vinci.leo.model.FeatureNameType;
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
        assertEquals("gov.va.vinci.knowtator.types.RelationshipAnnotation", typeDescription.getName());
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
