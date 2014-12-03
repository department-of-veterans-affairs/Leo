package gov.va.vinci.leo.ae;

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

import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LeoBaseAnnotatorTest {
    String rootDirectory = "";


    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("service")) {
            rootDirectory = "service/";
        }
    }

	@Test
	public void testClassPath() throws IOException {
		ExampleAnnotator annotator = new ExampleAnnotator();
        InputStream s = annotator.getResourceAsInputStream("classpath:a/b/c/1.txt");

		Assert.assertNotNull(s);
        Assert.assertEquals(116, s.read());
		Assert.assertEquals(annotator.getResourceFileAsString("classpath:a/b/c/1.txt"), "this is a test.");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testNullCall() throws IOException {
		 new ExampleAnnotator().getResourceAsInputStream(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadClassPath() throws IOException {
		ExampleAnnotator annotator = new ExampleAnnotator();
		annotator.getResourceAsInputStream("classpath:a/b/c/JUNK-DOESNT-EXIST.txt");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testBadAbsoluteFilePath() throws IOException {
		ExampleAnnotator annotator = new ExampleAnnotator();
		annotator.getResourceAsInputStream("file:/____BAD___/b/c/JUNK-DOESNT-EXIST.txt");
	}
	
	@Test
	public void testAbsoluteFilePath() throws IOException {
		String cwd = new File (".").getCanonicalPath() + "/";
		ExampleAnnotator annotator = new ExampleAnnotator();
		Assert.assertNotNull(annotator.getResourceAsInputStream("file:" + cwd + rootDirectory +"src/test/resources/a/b/c/1.txt"));
		Assert.assertEquals(annotator.getResourceFileAsString("file:" + cwd + rootDirectory +"src/test/resources/a/b/c/1.txt"), "this is a test.");
		Assert.assertNotNull(annotator.getResourceAsInputStream(cwd + rootDirectory + "src/test/resources/a/b/c/1.txt"));
		Assert.assertEquals(annotator.getResourceFileAsString(cwd + rootDirectory +"src/test/resources/a/b/c/1.txt"), "this is a test.");
	
	}
	
	@Test
	public void testRelativeFilePath() throws IOException {
		ExampleAnnotator annotator = new ExampleAnnotator();
		Assert.assertNotNull(annotator.getResourceAsInputStream("file:" + rootDirectory + "src/test/resources/a/b/c/1.txt"));
                Assert.assertEquals(annotator.getResourceFileAsString("file:" + rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");
		Assert.assertNotNull(annotator.getResourceAsInputStream(rootDirectory + "src/test/resources/a/b/c/1.txt"));
		Assert.assertEquals(annotator.getResourceFileAsString(rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");
	
	}	
	


	@Test
	public void testInitializeWithTypesSet() throws ResourceInitializationException {
		UimaContext aContext = Mockito.mock(UimaContext.class);
		Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[] { "gov.va.vinci.Token" });
		Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParamRequired")).thenReturn("req");
		new ExampleAnnotator().initialize(aContext);
	}
	

	@Test 
	public void testAddAnnotation() throws Exception {
		ExampleAnnotator annotator = new ExampleAnnotator();
		LeoAEDescriptor ae = new LeoAEDescriptor();
		ae.addType(WordToken.class.getCanonicalName(), "Word Token", "uima.tcas.Annotation");
		CAS c = CasCreationUtils.createCas(ae.getAnalysisEngineDescription());
		
		Annotation a = annotator.addOutputAnnotation(WordToken.class.getCanonicalName(), c.getJCas(), 1, 2);
		Assert.assertNotNull(a);
		Assert.assertEquals(c.getAnnotationIndex().iterator().get(), a);
	}

    @Test
    public void testInitializeWithMyParamSet() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[] { "gov.va.vinci.Token" });
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        Mockito.when(aContext.getConfigParameterValue("myParamRequired")).thenReturn("req");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
        assert("Got my param!".equals(ta.myParam));
        assert("req".equals(ta.myParamRequired));
    }

    @Test(expected=ResourceInitializationException.class)
    public void testInitializeMissingRequired() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[] { "gov.va.vinci.Token" });
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
    }

    @Test(expected=ResourceInitializationException.class)
    public void testInitializeEmptyRequired() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[] { "gov.va.vinci.Token" });
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        Mockito.when(aContext.getConfigParameterValue("myParamRequired")).thenReturn("  ");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
    }
}
