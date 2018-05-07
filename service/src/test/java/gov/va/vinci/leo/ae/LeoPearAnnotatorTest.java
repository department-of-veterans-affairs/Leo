package gov.va.vinci.leo.ae;

import gov.va.vinci.leo.descriptors.LeoPearDescriptor;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LeoPearAnnotatorTest {

    protected static final String SENT_PEAR_PATH = "src/test/resources/pear/sent_full-test-7.20.2017.pear";
    protected static final File OUT_DIR = new File("src/test/resources/pear/out");
    protected static File sentDescPath;

    @Before
    public void setup() throws Exception {
        if(!OUT_DIR.exists())
            OUT_DIR.mkdir();
        File extDir = new File(OUT_DIR, "extracted");
        if(!extDir.exists())  {
            extDir.mkdir();
        }

        if(sentDescPath == null || !sentDescPath.exists()) {
            StopWatch clock = new StopWatch();
            clock.start();
            sentDescPath = new File(LeoUtils.extractPearFile(extDir, new File(SENT_PEAR_PATH), false, false));
            clock.stop();
            System.out.println("Sent pear extraction took " + clock);
        }
    }

    @Test
    public void testConstructors() throws Exception {
        URI pearDescriptorURI = sentDescPath.toURI();
        LeoPearAnnotator annotator = new LeoPearAnnotator(pearDescriptorURI);
        assertNotNull(annotator);
        assertNotNull(annotator.getDescriptor());
        annotator = new LeoPearAnnotator(new LeoPearDescriptor(pearDescriptorURI));
        assertNotNull(annotator);
    }

    @Test
    public void testPearProcessing() throws Exception {
        LeoPearAnnotator pearAnnotator = new LeoPearAnnotator(sentDescPath.toURI());
        assertNotNull(pearAnnotator);
        assertNotNull(pearAnnotator.getDescriptor());
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        AnalysisEngine analysisengine = UIMAFramework.produceAnalysisEngine(pearAnnotator.getDescriptor().getResourceSpecifier());
        stopWatch.stop();
        System.out.println("Engine initialization took " + stopWatch);
        JCas jCas = analysisengine.newJCas();
        jCas.setDocumentText("I love hamburgers and fries!  JIB is the bomb!!");

        stopWatch.reset();
        stopWatch.start();
        analysisengine.process(jCas);
        stopWatch.stop();
        System.out.println("Processing took: " + stopWatch);

        assertTrue(jCas.getAnnotationIndex().size() > 0);
    }

    protected void printAnnotations(JCas jCas) {
        if(jCas == null) return;
        FSIterator<Annotation> iterator = jCas.getAnnotationIndex().iterator();
        while(iterator.hasNext()) {
            Annotation annotation = iterator.next();
            if(annotation.getType().getName().startsWith("uima"))
                continue;
            System.out.println("Annotation: " + annotation.getType().getName()
                    + " begin: " + annotation.getBegin() + " end: " + annotation.getEnd()
                    + " Text: " + annotation.getCoveredText());
            for(Feature feature : annotation.getType().getFeatures()) {
                if(feature.getName().startsWith("uima"))
                    continue;
                System.out.println("Feature: " + feature.getShortName() + ", value: " + annotation.getFeatureValueAsString(feature));
            }
        }
    }

    protected void copyAnnotations(JCas sourceCas, JCas destCas) {
        TypeSystem destSystem = destCas.getTypeSystem();
        for(Annotation annotation : sourceCas.getAnnotationIndex()) {
            if(!annotation.getType().getName().startsWith("uima")
                    && destSystem.getType(annotation.getType().getName()) != null) {
                try {
                    addOutputAnnotation(annotation.getType().getName(), destCas, annotation.getBegin(), annotation.getEnd(), null);
                } catch (AnalysisEngineProcessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create an output annotation, and add it to the cas.
     *
     * @param outputType The classname of the type. ie- gov.va.vinci.Token
     * @param cas        The cas to add the output annotation to.
     * @param begin      the start index of the annotation
     * @param end        the end index of the annotation
     * @param  featureNameValues a map of feature names and associated values to set on the annotation.
     * @return  the created annotation.
     * @throws AnalysisEngineProcessException If any exception occurs when getting and instantiating the
     *                                        output annotation type, this exception is thrown with the real exception inside of it. (analysisEngineProcessException.getCause());
     */
    protected static Annotation addOutputAnnotation(String outputType, JCas cas,
                                                    int begin, int end, Map<String, Object> featureNameValues) throws AnalysisEngineProcessException {
        try {
            Class<?> outputTypeClass = Class.forName(outputType);
            Constructor<?> con1 = outputTypeClass.getConstructor(JCas.class);
            Annotation outputAnnotation = (Annotation) con1.newInstance(cas);
            outputAnnotation.setBegin(begin);
            outputAnnotation.setEnd(end);
            outputAnnotation.addToIndexes();

            /**
             * Put in feature values if a map is passed in.
             */
            if (featureNameValues != null) {
                for (String featureName : featureNameValues.keySet()) {
                    Feature feature = outputAnnotation.getType().getFeatureByBaseName(featureName);
                    if (featureNameValues.get(featureName) instanceof Boolean) {
                        outputAnnotation.setBooleanValue(feature, (Boolean) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Byte) {
                        outputAnnotation.setByteValue(feature, (Byte) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Double) {
                        outputAnnotation.setDoubleValue(feature, (Double) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Float) {
                        outputAnnotation.setFloatValue(feature, (Float) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Integer) {
                        outputAnnotation.setIntValue(feature, (Integer) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Long) {
                        outputAnnotation.setLongValue(feature, (Long) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof Short) {
                        outputAnnotation.setShortValue(feature, (Short) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof String) {
                        outputAnnotation.setStringValue(feature, (String) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) instanceof FeatureStructure) {
                        outputAnnotation.setFeatureValue(feature, (FeatureStructure) featureNameValues.get(featureName));
                    } else if (featureNameValues.get(featureName) == null) {
                        outputAnnotation.setFeatureValue(feature, (FeatureStructure)null);
                    } else {
                        throw new AnalysisEngineProcessException(
                                new RuntimeException("Unknown feature type in map for feature name: " + featureName + " value: " + featureNameValues.get(featureName)));
                    }
                }
            }
            return outputAnnotation;
        } catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    @AfterClass
    public static void cleanup() throws Exception {
        if(OUT_DIR.exists())
            FileUtils.deleteDirectory(OUT_DIR);
    }
}
