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

public class StringExceptionTest {

	@Test
	public void testEmptyConstructor() {
		StringException x = new StringException();
		assertTrue("Error: The pattern cannot be empty".equals(x.toString()));

		 x = new StringException(null);
 		 assertTrue("Error: The pattern cannot be empty".equals(x.toString())); 		 
	}

	@Test
	public void testConstructor() {
		String pattern = "test";
		StringException x = new StringException(pattern);
		assertTrue(pattern.equals(x.toString()));
	
	}

}
