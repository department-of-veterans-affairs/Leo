package gov.va.vinci.leo.descriptors;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
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

import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypeSystemFactoryTest {

    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

	@Test
	public void testEmptyGenerateTypeSystemDescriptor() throws Exception {
		TypeSystemDescription desc = TypeSystemFactory.generateTypeSystemDescription();
		assertTrue(desc != null);	
		assertTrue(desc.getTypes().length == 0);
	}

	@Test
	public void testGenerateTypeSystemDescriptorByName() throws Exception {
		TypeSystemDescription desc = TypeSystemFactory.generateTypeSystemDescription("gov.va.vinci.leo.types.CSI", true);
		validateConstructorResult(desc);
	}

	@Test
	public void testGenerateTypeSystemDescriptorByPath() throws Exception {
		TypeSystemDescription desc = TypeSystemFactory.generateTypeSystemDescription(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/types/CSI.xml", false);
		validateConstructorResult(desc);
	}
	
	private void validateConstructorResult(TypeSystemDescription desc) {
		assertNotNull(desc);	
		assertTrue(desc.getName().equals("CSITypeDescriptor"));
		assertTrue(desc.getTypes().length == 1);
		assertNotNull(desc.getType("gov.va.vinci.leo.types.CSI"));
	}

	@Test(expected=InvalidXMLException.class)
	public void testNonTypeDescriptor() throws Exception {
		TypeSystemDescription desc = TypeSystemFactory.generateTypeSystemDescription(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml", false);
	}
}
