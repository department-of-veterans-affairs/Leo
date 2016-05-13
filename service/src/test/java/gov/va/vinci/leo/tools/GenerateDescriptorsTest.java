package gov.va.vinci.leo.tools;

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
        assertEquals(3, files.length);
        String descriptorText = FileUtils.readFileToString(new File(destDir, "ExampleWhitespaceTokenizerDescriptor.xml"));
        assertTrue(StringUtils.isNotBlank(descriptorText));
        assertTrue(descriptorText.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<analysisEngineDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">"));
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
        assertEquals(3, files.length);
        String descriptorText = FileUtils.readFileToString(new File(destDir, "ExampleWhitespaceTokenizerDescriptor.xml"));
        assertTrue(StringUtils.isNotBlank(descriptorText));
        assertTrue(descriptorText.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<analysisEngineDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">"));
    }

    @After
    public void cleanup() throws Exception {
        if (outDir != null && outDir.exists())
            FileUtils.forceDelete(outDir);
    }
}
