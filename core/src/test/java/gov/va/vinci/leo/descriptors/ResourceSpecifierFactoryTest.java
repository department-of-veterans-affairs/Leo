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

import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.uima.resource.ResourceSpecifier;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResourceSpecifierFactoryTest {

    String rootDirectory = "";

    @Before
    public void onBefore() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

	@Test
	public void testCreateResourceSpecifierWithURL() {
		try {
			ResourceSpecifier r = ResourceSpecifierFactory
					.createResourceSpecifier(LeoUtils.createURL("desc.gov.va.vinci.leo.ae.WhitespaceTokenizerDescriptor"));

			assertTrue(r!=null);
			assertTrue("gov.va.vinci.marian.whitespace.ae.WhitespaceTokenizer".equals(r.getAttributeValue("implementationName")));
		} catch (Exception e) {
			fail("Threw exception:" + e);
		}
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testCreateResourceSpecifierWithURL_BAD() throws Exception {
			ResourceSpecifier r = ResourceSpecifierFactory.createResourceSpecifier(new File(rootDirectory + "src/test/resources/BAD_FILENAME").toURI().toURL());
			fail("Should have had an exception.");
	}
	
	
	@Test
	public void testCreateResourceSpecifierWithFile() {
		try {
			ResourceSpecifier r = ResourceSpecifierFactory.createResourceSpecifier(new File(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml"));

			assertTrue(r!=null);
			assertTrue("gov.va.vinci.marian.whitespace.ae.WhitespaceTokenizer".equals(r.getAttributeValue("implementationName")));
		} catch (Exception e) {
			fail("Threw exception:" + e);
		}
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testCreateResourceSpecifierWithFile_BAD() throws Exception {
			ResourceSpecifier r = ResourceSpecifierFactory.createResourceSpecifier(new File("src/test/resources/BAD_FILENAME"));
			fail("Should have had an exception.");
	}
	
	@Test
	public void testCreateResourceSpecifierWithPath() {
		try {
			ResourceSpecifier r = ResourceSpecifierFactory.createResourceSpecifier(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml");

			assertTrue(r!=null);
			assertTrue("gov.va.vinci.marian.whitespace.ae.WhitespaceTokenizer".equals(r.getAttributeValue("implementationName")));
		} catch (Exception e) {
			fail("Threw exception:" + e);
		}
	}

	@Test(expected=FileNotFoundException.class)
	public void testCreateResourceSpecifierWithPath_BAD() throws Exception {
			ResourceSpecifier r = ResourceSpecifierFactory.createResourceSpecifier("src/test/resources/BAD_FILENAME");
			fail("Should have had an exception.");
	}
}
