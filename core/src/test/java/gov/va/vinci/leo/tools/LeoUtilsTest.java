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

import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import groovy.util.ConfigObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * User: Thomas Ginter
 * Date: 3/31/14
 * Time: 3:50 PM
 */
public class LeoUtilsTest {

    protected static final String PEAR_PATH = "src/test/resources/tools/sent_full-test-7.20.2017.pear";
    protected static final File OUT_DIR = new File("src/test/resources/results/pear");

    @Before
    public void setup() {
        if(!OUT_DIR.exists())
            OUT_DIR.mkdir();
    }

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

    @Test
    public void extractPearFileTest() throws Exception {
        File extDir = new File(OUT_DIR, "extracted");
        if(!extDir.exists()) extDir.mkdir();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String descPath = LeoUtils.extractPearFile(extDir, new File(PEAR_PATH), false, false);
        stopWatch.stop();
        System.out.println("Extraction with no validation took: " + stopWatch);
        assertTrue(StringUtils.isNotBlank(descPath));

        //Test extraction with validation
        stopWatch.reset();
        stopWatch.start();
        descPath = LeoUtils.extractPearFile(extDir, new File(PEAR_PATH), true, true);
        stopWatch.stop();
        System.out.println("Extraction with validation and cleanup took: " + stopWatch);
        assertTrue(StringUtils.isNotBlank(descPath));
    }

    @Test(expected = ClassCastException.class)
    public void testLeoAEDescriptorFromPear() throws Exception {
        File extDir = new File(OUT_DIR, "extracted");
        if(!extDir.exists()) extDir.mkdir();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String descPath = LeoUtils.extractPearFile(extDir, new File(PEAR_PATH), false, true);
        stopWatch.stop();
        System.out.println("Extraction with no validation took: " + stopWatch);
        assertTrue(StringUtils.isNotBlank(descPath));

        LeoAEDescriptor aeDescriptor = new LeoAEDescriptor(descPath, false);
    }

    @Test
    public void testGetParameter() throws Exception {
        Parameter[] parameters = new Parameter[] {
                new Parameter_impl("bob", "foo"),
                new Parameter_impl("joe", "bar"),
                new Parameter_impl("sue", "baz")
        };

        //Test finding a matching value - happy path
        Parameter p = LeoUtils.getParameter("bob", parameters);
        assertNotNull(p);
        assertEquals("foo", p.getValue());

        //Test finding no match
        p = LeoUtils.getParameter("mary", parameters);
        assertNull(p);

        //Test blank name
        p = LeoUtils.getParameter("", parameters);
        assertNull(p);

        //Test empty array
        p = LeoUtils.getParameter("bob", new Parameter[0]);
        assertNull(p);
    }

    @After
    public void cleanup() throws Exception {
        if(OUT_DIR.exists())
            FileUtils.deleteDirectory(OUT_DIR);
    }
}
