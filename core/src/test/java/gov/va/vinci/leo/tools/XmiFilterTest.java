/**
 * XmiFilterTest.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo.tools;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * 
 * @author thomasginter
 */
public class XmiFilterTest {


	public String TEST_FILE = "";
    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
        TEST_FILE = rootDirectory + "src/test/resources/filterTest/xml.txt";
    }

    @Test
	public void testXmlStringInput() throws Exception {
		String text = "this is a test." + String.valueOf(Character.toChars(26));
		assertTrue(StringUtils.isNotBlank(text));
		XmlFilter xf = new XmlFilter();
		String filtered = xf.filter(text);
		assertTrue("this is a test. ".equals(filtered));
	}//testXmlStringInput method
	
	@Test
	public void testXmlReaderInput() throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(TEST_FILE);
			assertNotNull(fis);
			XmlFilter xf = new XmlFilter();
			String filtered = xf.filter(fis);
			assertTrue("this is a test. ".equals(filtered));
		} catch (Exception e) {
			fail("Exception thrown testing unicode filter, reader input:\n" 
					+ ExceptionUtils.getStackTrace(e));
		} finally {
			if(fis != null)
				fis.close();
		}//finally
	}//testXmlReaderInput
	
	@Test
	public void testToXml10() throws Exception {
		String text = "this is a test." + String.valueOf(Character.toChars(26));
		assertTrue(StringUtils.isNotBlank(text));
		String filtered = XmlFilter.toXml10(text);
		assertTrue("this is a test. ".equals(filtered));
	}//testToXml10 method
	
}//XmiFilterTest class
