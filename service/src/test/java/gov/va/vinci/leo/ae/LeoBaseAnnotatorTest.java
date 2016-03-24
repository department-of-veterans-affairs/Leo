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

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.types.ExampleType;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LeoBaseAnnotatorTest {
    String rootDirectory = "";


    @Before
    public void setTestString() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("service")) {
            rootDirectory = "service/";
        }
    }

    @Test(expected = AnalysisEngineProcessException.class)
    public void testTypeNotInFilter() throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        ExampleAnnotator exampleAnnotator = new ExampleAnnotator("pvalue", "reqpvalue")
                .setIncludeTypesFilter("gov.va.vinci.leo.types.INVALID_TYPE");
        aggregate.addDelegate(exampleAnnotator.getLeoAEDescriptor()
                .addTypeSystemDescription(new ExampleAnnotator().getLeoTypeSystemDescription()));

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());
        JCas jCas = ae.newJCas();
        jCas.setDocumentText("a b c");
        ae.process(jCas);
    }

    @Test
    public void testIncludeFilter() throws Exception {
        LeoTypeSystemDescription typeSystemDescription = new ExampleWhitespaceTokenizer().getLeoTypeSystemDescription()
                .addTypeSystemDescription(new ExampleAnnotator().getLeoTypeSystemDescription());
        //Setup the aggregate pipeline
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(
            new ExampleWhitespaceTokenizer(Token.class.getCanonicalName(), "TokenType",
                    WordToken.class.getCanonicalName(), null)
                .getLeoAEDescriptor()
                .setTypeSystemDescription(typeSystemDescription)
        ).addDelegate(
            new ExampleAnnotator("pvalue", "preqvalue")
                .setIncludeTypesFilter(WordToken.class.getCanonicalName())
                .getLeoAEDescriptor()
                .setTypeSystemDescription(typeSystemDescription)
        );
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

        //Process the first cas with word tokens
        JCas jCas = ae.newJCas();
        jCas.setDocumentText("aa bb cc");
        ae.process(jCas);

        //Make sure that the number of CASes processed and filtered CASes processed are the same.
        ExampleType exampleType = getExampleType(jCas);
        assertNotNull(exampleType);
        assertEquals(exampleType.getNumberOfCASesProcessed(), exampleType.getNumberOfFilteredCASesProcessed());
//        System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());

        //Get a new CAS with only number tokens and process it
        jCas = ae.newJCas();
        jCas.setDocumentText("1 2 3");
        ae.process(jCas);

        //Check the results of processing
        exampleType = getExampleType(jCas);
        assertNull(exampleType);
//        if(exampleType == null)
//            System.out.println("CAS2 example type is null.");
//        else
//            System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                    + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());
    }

    @Test
    public void testExcludeFilter() throws Exception {
        LeoTypeSystemDescription typeSystemDescription = new ExampleWhitespaceTokenizer().getLeoTypeSystemDescription()
                .addTypeSystemDescription(new ExampleAnnotator().getLeoTypeSystemDescription());
        //Setup the aggregate pipeline
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(
                new ExampleWhitespaceTokenizer(Token.class.getCanonicalName(), "TokenType",
                        WordToken.class.getCanonicalName(), null)
                        .getLeoAEDescriptor()
                        .setTypeSystemDescription(typeSystemDescription)
        ).addDelegate(
                new ExampleAnnotator("pvalue", "preqvalue")
                        .setExcludeTypesFilter(WordToken.class.getCanonicalName())
                        .getLeoAEDescriptor()
                        .setTypeSystemDescription(typeSystemDescription)
        );
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

        //Process the first cas with word tokens
        JCas jCas = ae.newJCas();
        jCas.setDocumentText("aa bb cc");
        ae.process(jCas);

        //Make sure that there is no example type.
        ExampleType exampleType = getExampleType(jCas);
        assertNull(exampleType);
//        if(exampleType == null)
//            System.out.println("CAS2 example type is null.");
//        else
//            System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                    + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());

        //Get a new CAS with only number tokens and process it
        jCas = ae.newJCas();
        jCas.setDocumentText("1 2 3");
        ae.process(jCas);

        //Check the results of processing
        exampleType = getExampleType(jCas);
        assertNotNull(exampleType);
        assertEquals(2, exampleType.getNumberOfCASesProcessed());
        assertEquals(1, exampleType.getNumberOfFilteredCASesProcessed());
//        if(exampleType == null)
//            System.out.println("CAS2 example type is null.");
//        else
//            System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                    + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());
    }

    @Test
    public void testInputTypes() throws Exception {
        LeoTypeSystemDescription typeSystemDescription = new ExampleWhitespaceTokenizer().getLeoTypeSystemDescription()
                .addTypeSystemDescription(new ExampleAnnotator().getLeoTypeSystemDescription());
        //Setup the aggregate pipeline
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(
                new ExampleWhitespaceTokenizer(Token.class.getCanonicalName(), "TokenType",
                        WordToken.class.getCanonicalName(), null)
                        .getLeoAEDescriptor()
                        .setTypeSystemDescription(typeSystemDescription)
        ).addDelegate(
                new ExampleAnnotator("pvalue", "preqvalue")
                        .setInputTypes(WordToken.class.getCanonicalName())
                        .getLeoAEDescriptor()
                        .setTypeSystemDescription(typeSystemDescription)
        );
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aggregate.getAnalysisEngineDescription());

        //Process the first cas with word tokens
        JCas jCas = ae.newJCas();
        jCas.setDocumentText("aa bb cc");
        ae.process(jCas);

        ExampleType exampleType = getExampleType(jCas);
        assertNotNull(exampleType);
        assertEquals(1, exampleType.getNumberOfCASesProcessed());
        assertEquals(1, exampleType.getNumberOfFilteredCASesProcessed());
//        if(exampleType == null)
//            System.out.println("CAS2 example type is null.");
//        else
//            System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                    + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());

        jCas = ae.newJCas();
        jCas.setDocumentText("1 2 3");
        ae.process(jCas);

        exampleType = getExampleType(jCas);
        assertNull(exampleType);
//        if(exampleType == null)
//            System.out.println("CAS2 example type is null.");
//        else
//            System.out.println("Number of CASes: " + exampleType.getNumberOfCASesProcessed()
//                    + ", Number of Filtered CASes: " + exampleType.getNumberOfFilteredCASesProcessed());
    }

    protected ExampleType getExampleType(JCas jCas) {
        FSIterator iterator = jCas.getAnnotationIndex(ExampleType.type).iterator();
        if(iterator.hasNext()) {
            return (ExampleType) iterator.next();
        }
        return null;
    }

    @Test
    public void testClassPath() throws IOException {
        ExampleAnnotator annotator = new ExampleAnnotator();
        InputStream s = annotator.getResourceAsInputStream("classpath:a/b/c/1.txt");

        assertNotNull(s);
        assertEquals(116, s.read());
        assertEquals(annotator.getResourceFileAsString("classpath:a/b/c/1.txt"), "this is a test.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCall() throws IOException {
        new ExampleAnnotator().getResourceAsInputStream(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadClassPath() throws IOException {
        ExampleAnnotator annotator = new ExampleAnnotator();
        annotator.getResourceAsInputStream("classpath:a/b/c/JUNK-DOESNT-EXIST.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadAbsoluteFilePath() throws IOException {
        ExampleAnnotator annotator = new ExampleAnnotator();
        annotator.getResourceAsInputStream("file:/____BAD___/b/c/JUNK-DOESNT-EXIST.txt");
    }

    @Test
    public void testAbsoluteFilePath() throws IOException {
        String cwd = new File(".").getCanonicalPath() + "/";
        ExampleAnnotator annotator = new ExampleAnnotator();
        assertNotNull(annotator.getResourceAsInputStream("file:" + cwd + rootDirectory + "src/test/resources/a/b/c/1.txt"));
        assertEquals(annotator.getResourceFileAsString("file:" + cwd + rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");
        assertNotNull(annotator.getResourceAsInputStream(cwd + rootDirectory + "src/test/resources/a/b/c/1.txt"));
        assertEquals(annotator.getResourceFileAsString(cwd + rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");

    }

    @Test
    public void testRelativeFilePath() throws IOException {
        ExampleAnnotator annotator = new ExampleAnnotator();
        assertNotNull(annotator.getResourceAsInputStream("file:" + rootDirectory + "src/test/resources/a/b/c/1.txt"));
        assertEquals(annotator.getResourceFileAsString("file:" + rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");
        assertNotNull(annotator.getResourceAsInputStream(rootDirectory + "src/test/resources/a/b/c/1.txt"));
        assertEquals(annotator.getResourceFileAsString(rootDirectory + "src/test/resources/a/b/c/1.txt"), "this is a test.");

    }


    @Test
    public void testInitializeWithTypesSet() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[]{"gov.va.vinci.Token"});
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
        assertNotNull(a);
        assertEquals(c.getAnnotationIndex().iterator().get(), a);
    }

    @Test
    public void testInitializeWithMyParamSet() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[]{"gov.va.vinci.Token"});
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        Mockito.when(aContext.getConfigParameterValue("myParamRequired")).thenReturn("req");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
        assert ("Got my param!".equals(ta.myParam));
        assert ("req".equals(ta.myParamRequired));
    }

    @Test(expected = ResourceInitializationException.class)
    public void testInitializeMissingRequired() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[]{"gov.va.vinci.Token"});
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
    }

    @Test(expected = ResourceInitializationException.class)
    public void testInitializeEmptyRequired() throws ResourceInitializationException {
        UimaContext aContext = Mockito.mock(UimaContext.class);
        Mockito.when(aContext.getConfigParameterValue("inputType")).thenReturn(new String[]{"gov.va.vinci.Token"});
        Mockito.when(aContext.getConfigParameterValue("outputType")).thenReturn("gov.va.vinci.OutputToken");
        Mockito.when(aContext.getConfigParameterValue("myParam")).thenReturn("Got my param!");
        Mockito.when(aContext.getConfigParameterValue("myParamRequired")).thenReturn("  ");
        ExampleAnnotator ta = new ExampleAnnotator();
        ta.initialize(aContext);
    }
}
