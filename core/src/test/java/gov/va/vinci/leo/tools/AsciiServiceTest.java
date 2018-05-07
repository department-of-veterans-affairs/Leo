package gov.va.vinci.leo.tools;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 3/28/16.
 */
public class AsciiServiceTest {

    @Test
    public void testAscii7() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append((char)135);
        sb.append((char)225);
        sb.append((char)134);
        String nonAscii7 = sb.toString();
        assertEquals("\u0087á\u0086", nonAscii7);
        String ascii7 = AsciiService.toASCII7(nonAscii7);
        assertEquals("cat", ascii7);
    }

    @Test
    public void testBadAscii7() throws Exception {
        StringBuilder sb = new StringBuilder(1);
        sb.append((char) 355);
        String res = AsciiService.toASCII7(sb.toString());
        assertEquals("_", res);
    }
}
