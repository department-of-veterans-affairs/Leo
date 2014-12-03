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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XMLExceptionTest {

	@Test
	public void testEmptyConstructor() {
		XMLException x = new XMLException();
		assertTrue("The compile pattern does not follow the required XML syntax standard".equals(x.toString()));

		 x = new XMLException(null);
 		 assertTrue("The compile pattern does not follow the required XML syntax standard".equals(x.toString())); 		 
	}

	@Test
	public void testConstructor() {
		String pattern = "test";
		XMLException x = new XMLException(pattern);
		assertTrue(("ERROR : The Pattern \"" + pattern + "\" passed in the compile pattern does not " +
				"follow the required XML syntax standard").equals(x.toString()));
	
	}
}
