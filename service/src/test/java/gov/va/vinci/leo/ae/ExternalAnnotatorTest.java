package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Test;

import static org.junit.Assert.*;

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
            assertNotNull(exampleType);
        }
    }
}
