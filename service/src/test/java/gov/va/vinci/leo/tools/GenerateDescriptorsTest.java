package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.ae.ExampleWhitespaceTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by thomasginter on 3/28/16.
 */
public class GenerateDescriptorsTest {

    String rootDirectory = "";
    File outDir = null;

    @Before
    public void before() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("service")) {
            rootDirectory = "service/";
        }
    }

    @Test
    public void testGenerate() throws Exception {
        outDir = new File(rootDirectory + "src/test/resources/test-generate");
        outDir.mkdirs();
        GenerateDescriptors.generate(ExampleWhitespaceTokenizer.class.getCanonicalName(), outDir.getAbsolutePath());
        File destDir = new File(outDir, "gov/va/vinci/leo/ae");
        File[] files = destDir.listFiles();
        assertNotNull(files);
        assertEquals(2, files.length);
        String descriptorText = FileUtils.readFileToString(new File(destDir, "ExampleWhitespaceTokenizerDescriptor.xml"));
        assertTrue(StringUtils.isNotBlank(descriptorText));
        assertTrue(descriptorText.contains("analysisEngineDescription"));
    }

    @Test
    public void testMainGenerate() throws Exception {
        File outputDir = new File(rootDirectory + "src/test/resources/test-main-generate");
        outputDir.mkdirs();
        GenerateDescriptors.main(
                new String[]{ExampleWhitespaceTokenizer.class.getCanonicalName(), outputDir.getAbsolutePath()}
        );
        File destDir = new File(outputDir, "gov/va/vinci/leo/ae");
        File[] files = destDir.listFiles();
        assertNotNull(files);
        assertEquals(2, files.length);
        String descriptorText = FileUtils.readFileToString(new File(destDir, "ExampleWhitespaceTokenizerDescriptor.xml"));
        assertTrue(StringUtils.isNotBlank(descriptorText));
        assertTrue(descriptorText.contains("analysisEngineDescription"));
    }

    @After
    public void cleanup() throws Exception {
        if (outDir != null && outDir.exists())
            FileUtils.forceDelete(outDir);
    }
}
