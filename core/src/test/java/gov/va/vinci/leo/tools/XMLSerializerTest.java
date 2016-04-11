package gov.va.vinci.leo.tools;

import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.TransformerHandler;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
