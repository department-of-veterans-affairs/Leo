package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Core
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

import com.google.gson.JsonSyntaxException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test methods in the LeoJsonHandler class
 *
 * User: Thomas Ginter
 * Date: 12/5/13
 * Time: 2:22 PM
 */
public class LeoJsonHandlerTest {

    String jsonString = "{ \"Key1\":\"Value1\", \"Key2\":\"Value2\", \"Key3\":\"Value3\" }";
    String jsonIDString = "{\n" +
            "    \"id1\": {\n" +
            "        \"bob\": \"value1\"\n" +
            "    },\n" +
            "    \"id2\": {\n" +
            "        \"bob\": \"value2\"\n" +
            "    }\n" +
            "}";

    @Test(expected= JsonSyntaxException.class)
    public void testParseValidationStringInvalidInput() throws Exception {
        LeoUtils.parseMappingString("A non JSON String");
    }

    @Test
    public void testParseValidationString() throws Exception {
        //test empty string
        Map<String, String> map = LeoUtils.parseMappingString("  ");
        assertEquals(0, map.size());

        //test jsonString
        map = LeoUtils.parseMappingString(jsonString);
        assertEquals(3, map.size());
        assertTrue(map.containsKey("Key1"));
        assertTrue(map.containsKey("Key3"));
        assertTrue(map.get("Key1").equals("Value1"));
        assertTrue(map.get("Key3").equals("Value3"));
    }
}
