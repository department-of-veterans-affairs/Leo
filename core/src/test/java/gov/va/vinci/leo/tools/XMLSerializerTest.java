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

import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.TransformerHandler;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * Created by thomasginter on 3/30/16.
 */
public class XMLSerializerTest {

    String rootDirectory = "";


    @Before
    public void setup() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("core")) {
            rootDirectory = "core/";
        }
    }

    @Test
    public void testFileConstructor() throws Exception {
        XMLSerializer xmlSerializer = new XMLSerializer(new File(rootDirectory + "src/test/resources/out.xml"));
        assertNotNull(xmlSerializer);
    }

    @Test
    public void testWriterConstructor() throws Exception {
        XMLSerializer xmlSerializer = new XMLSerializer(new StringWriter());
        assertNotNull(xmlSerializer);
    }

    @Test
    public void testSetOutputProperty() throws Exception {
        StringWriter writer = new StringWriter();
        XMLSerializer xmlSerializer = new XMLSerializer(writer);
        assertNotNull(xmlSerializer);
        xmlSerializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        TransformerHandler thd = xmlSerializer.getTHandler();
        thd.startDocument();
        thd.endDocument();
        String result = writer.toString();
        assertTrue(result.contains(OutputKeys.STANDALONE));

        XMLSerializer xmlSerializer1 = new XMLSerializer();
        assertNotNull(xmlSerializer1);
        xmlSerializer1.setOutputProperty(OutputKeys.STANDALONE, "no");
        thd = xmlSerializer1.getTHandler();
        result = thd.getTransformer().getOutputProperty(OutputKeys.STANDALONE);
        assertEquals("no", result);
    }
}
