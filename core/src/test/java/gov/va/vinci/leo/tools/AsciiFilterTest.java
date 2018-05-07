/**
 * AsciiFilterTest.java
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * 
 * @author Thomas Ginter
 */
public class AsciiFilterTest {
	
	public String TEST_FILE = "";
	public static String TEST_STRING = "bad";
	
	private static final Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    String rootDirectory = "";


	@Before
	public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
        TEST_FILE = rootDirectory + "src/test/resources/filterTest/utf8.txt";

        try {
			TEST_STRING = FileUtils.readFileToString(new File(rootDirectory + "src/test/resources/results/utf8toascii.txt"));
		} catch (Exception e) {
			TEST_STRING = "bad";
			LOG.warn("Unable to slurp up the utf8toascii.txt file for Conversion results:\n"
							+ ExceptionUtils.getStackTrace(e));
		}
		LOG.info("TEST_STRING: " + TEST_STRING);



	}//setTestString
	
	@Test
	public void testUnicodeStringInput() throws Exception {
		String text = FileUtils.readFileToString(new File(TEST_FILE), "UTF-8");
		assertTrue(StringUtils.isNotBlank(text));
		AsciiFilter af = new AsciiFilter();
		String filtered = af.filter(text);
		LOG.info("filtered: " + filtered);
		assertTrue(TEST_STRING.equals(filtered));
	}//testUnicodeStringInput method
	
	@Test
	public void testUnicodeReaderInput() throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(TEST_FILE);
			assertNotNull(fis);
			AsciiFilter af = new AsciiFilter();
			String filtered = af.filter(fis);
			assertTrue(TEST_STRING.equals(filtered));
		} catch (Exception e) {
			fail("Exception thrown testing unicode filter, reader input:\n" 
					+ ExceptionUtils.getStackTrace(e));
		} finally {
			if(fis != null)
				fis.close();
		}//finally
	}//testUnicodeReaderInput method
	
}//AsciiFilterTest class
