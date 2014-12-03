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

import groovy.util.ConfigObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Thomas Ginter
 * Date: 3/31/14
 * Time: 3:50 PM
 */
public class LeoUtilsTest {

    @Test
    public void testLoadConfigFile() throws Exception {
        ConfigObject config = LeoUtils.loadConfigFile("simple", "conf/common.groovy", "conf/bob.groovy");

        String commonVariable = (String) config.get("myCommonVariable");
        String bobVariable = (String) config.get("myBobVariable");
        boolean isSimple = (Boolean) config.get("isSimple");
        boolean isHappy = (Boolean) config.get("isHappy");
        assertEquals(commonVariable, "common1234567");
        assertEquals(bobVariable, "bob123");
        assertTrue(isSimple);
        assertTrue(isHappy);

        config = LeoUtils.loadConfigFile("sad", "conf/common.groovy", "conf/bob.groovy");
        commonVariable = (String) config.get("myCommonVariable");
        bobVariable = (String) config.get("myBobVariable");
        isSimple = (Boolean) config.get("isSimple");
        isHappy = (Boolean) config.get("isHappy");
        assertEquals(commonVariable, "common1234567");
        assertEquals(bobVariable, "bob123");
        assertFalse(isSimple);
        assertFalse(isHappy);

    }
}
