package gov.va.vinci.leo.regex.ae;

/*
 * #%L
 * Regex Annotator
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


import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.regex.RegexConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.va.vinci.leo.regex.RegexUtils.readFileToStringList;


/**
 * Uses Regular Expressions obtained from a resource file to get associated
 * annotations of this configuration parameter type (regex). These annotat-
 * ions are used to create a new output annotation type object which are
 * then added to the CasIndex with updated feature structure.
 *
 * @author Supraja
 * @author Ryan Cornia
 * @author Thomas Ginter
 */
public class RegexAnnotator extends LeoBaseAnnotator {

    /**
     * Default is false - true/false to determine if the regex patterns are case sensitive. Default is false, it is not
     * case sensitive.
     */
    @LeoConfigurationParameter
    protected boolean caseSensitive = DEFAULT_CASE_SENSITIVITY;

    /**
     * If set, this is the feature name on the output annotation that will be populated with the value set for concept_feature_value.
     */
    @LeoConfigurationParameter
    protected String conceptFeatureName = null;

    /**
     * If concept_feature_name is set, this is value that is set on the concept_feature_value feature of the output annotation.
     */
    @LeoConfigurationParameter
    protected String conceptFeatureValue = null;

    /**
     * The path to the groovy configuration file. Either a resource or groovy config file must be used, but not both.
     */
    @LeoConfigurationParameter
    protected String groovyConfigFile = null;

    /**
     * If set and the regular expression has groups, this feature on the annotation will be a string array of group
     * values.
     */
    @LeoConfigurationParameter
    protected String groupFeatureName = null;

    /**
     * The name of the feature to put the matched pattern in. If this is set, and the annotation has a feature
     * of this name, the annotation will put the regular expression pattern that matched and created the annotation in that feature. For instance,
     * if the annotation covers test, and the regex that caught test was \bt.*t\b, then the annotation feature would be set to \bt.*t\b.
     */
    @LeoConfigurationParameter
    protected String matchedPatternFeatureName = null;

    /**
     * Pattern object integer flags to be compiled with each pattern.
     */
    @LeoConfigurationParameter
    protected int patternFlags = DEFAULT_PATTERN_FLAGS;

    /**
     * The path to the regular expressions file.
     */
    @LeoConfigurationParameter
    protected String resource = null;

    /**
     * Default is false - If true then word boundaries will be added to the beginning and end of each regex.
     */
    @LeoConfigurationParameter
    protected boolean wordBoundary = DEFAULT_WORD_FLAG;

    /**
     * Map of type names to UIMA Type objects.
     */
    protected Map<String, Type> typeMap = new HashMap<String, Type>();

    /**
     * Map of output annotation type names to associated Constructor objects.
     */
    Map<String, Constructor<?>> outputTypeMap = new HashMap<String, Constructor<?>>();

    /**
     * The default for case sensitivity if one is not specified.
     */
    public static boolean DEFAULT_CASE_SENSITIVITY = false;
    /**
     * Default regex pattern flags if none are specified.
     */
    public static Integer DEFAULT_PATTERN_FLAGS = 0;
    /**
     * Default regex word flag if one is not specified.
     */
    public static boolean DEFAULT_WORD_FLAG = false;


    /**
     * Logger.
     */
    protected final static Logger logger = Logger.getLogger(RegexAnnotator.class.getCanonicalName());

    /**
     * The runtime configuration after the annotator is initialized.
     */
    protected RegexConfiguration configuration;

    /** Performance monitoring variables. **/
    private static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    private EtmPoint point = null;


    /**
     * Default constructor.
     */
    public RegexAnnotator() {
        super();
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public RegexAnnotator setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    public String getConceptFeatureName() {
        return conceptFeatureName;
    }

    public RegexAnnotator setConceptFeatureName(String conceptFeatureName) {
        this.conceptFeatureName = conceptFeatureName;
        return this;
    }

    public String getConceptFeatureValue() {
        return conceptFeatureValue;
    }

    public RegexAnnotator setConceptFeatureValue(String conceptFeatureValue) {
        this.conceptFeatureValue = conceptFeatureValue;
        return this;
    }

    public String getGroovyConfigFile() {
        return groovyConfigFile;
    }

    public RegexAnnotator setGroovyConfigFile(String groovyConfigFile) {
        this.groovyConfigFile = groovyConfigFile;
        return this;
    }

    public String getGroupFeatureName() {
        return groupFeatureName;
    }

    public RegexAnnotator setGroupFeatureName(String groupFeatureName) {
        this.groupFeatureName = groupFeatureName;
        return this;
    }

    public String getMatchedPatternFeatureName() {
        return matchedPatternFeatureName;
    }

    public RegexAnnotator setMatchedPatternFeatureName(String matchedPatternFeatureName) {
        this.matchedPatternFeatureName = matchedPatternFeatureName;
        return this;
    }

    public int getPatternFlags() {
        return patternFlags;
    }

    public RegexAnnotator setPatternFlags(int patternFlags) {
        this.patternFlags = patternFlags;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public RegexAnnotator setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public boolean isWordBoundary() {
        return wordBoundary;
    }

    public RegexAnnotator setWordBoundary(boolean wordBoundary) {
        this.wordBoundary = wordBoundary;
        return this;
    }

    /**
     * Method to get the input Annotation type, Output Annotation type and the
     * resource file
     *
     * @param aContext instance
     */
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        if (inputTypes == null || inputTypes.length == 0) {
            inputTypes = new String[]{"uima.tcas.DocumentAnnotation"};
        }

        if (StringUtils.isBlank(resource) && StringUtils.isBlank(groovyConfigFile)) {
            throw new ResourceInitializationException(new IllegalArgumentException("Please provide resource file or groovy resource file parameter."));
        }
        if (StringUtils.isNotBlank(resource) && StringUtils.isNotBlank(groovyConfigFile)) {
            throw new ResourceInitializationException(new IllegalArgumentException("You may only specify a resource file or groovy resource file, not both. "));
        }

        try {
            /** Create the appropriate configuration object. **/
            if (StringUtils.isNotBlank(resource)) {
                simpleFileInitialization(aContext, resource);
            } else {
                configuration = new RegexConfiguration(new File(groovyConfigFile));
            }
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }//initialize method

    /**
     * Initialization called when not using a groovy config file. It grabs the parameters specified and creates
     * the appropriate RegexConfiguration.
     *
     * @param aContext     the uima context
     * @param resourceFile the resource file with regex patterns.
     * @throws IOException
     */
    private void simpleFileInitialization(UimaContext aContext, String resourceFile) throws IOException {
        // Read the resource file to get all the regexes
        List<String> expressions = new ArrayList<String>();
        expressions = readFileToStringList(new File(resourceFile));

        configuration = new RegexConfiguration("simpleFileConfiguration",
                expressions, caseSensitive, wordBoundary,
                matchedPatternFeatureName,
                groupFeatureName,
                conceptFeatureName,
                conceptFeatureValue,
                patternFlags,
                outputType,
                inputTypes);
    }

    @Override
    public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
        for (String configurationName : configuration.getConfigNames()) {
            Map parameters = configuration.getParameters(configurationName);
            Map<Pattern, Matcher> patterns = configuration.getPatterns(configurationName);
            for (Pattern patt1 : patterns.keySet()) {
                processPattern(aJCas, patt1, patterns.get(patt1), parameters);
            }
        } // End Loop through configuration names.
    }//process method

    /**
     * Processes an individual pattern on the cas.
     *
     * @param aJCas         the cas to run the pattern on.
     * @param patt1         the pattern to be executed.
     * @param m             the matcher to use on the pattern.
     * @param parameters    the parameters for this pattern.
     * @throws AnalysisEngineProcessException any exception that occurs while processing.
     */
    protected void processPattern(JCas aJCas, Pattern patt1, Matcher m, Map parameters) throws AnalysisEngineProcessException {
        if (configuration.isPerformanceMonitoring()){
            point = etmMonitor.createPoint(RegexAnnotator.class.getCanonicalName() + ":Pattern=" + patt1.pattern());
        }
        // For each input type, get all annotations belonging to this type
        for (String type : (String[]) parameters.get("inputTypes")) {
            // Get an iterator for all annotations of type
            Type inputTypeObj = typeMap.get(type);
            FSIterator<Annotation> annotationList = null;
            if (inputTypeObj == null) {
                inputTypeObj = aJCas.getTypeSystem().getType(type);
                //If there is no such type on the typeSystem it is an exception and likely configured wrong.
                if (inputTypeObj == null) {
                    throw new AnalysisEngineProcessException(new Exception("Could not find type (" + type + ") in the CAS."));
                }
                typeMap.put(type, inputTypeObj);
            }

            annotationList = aJCas.getAnnotationIndex(inputTypeObj).iterator();
            // Iterate over the input type annotations
            while (annotationList.hasNext()) {
                Annotation inputAnnotation = annotationList.next();
                // Get the text of the document covered by this annotation
                String coveredText = inputAnnotation.getCoveredText();
                // Get a matcher instance to find matches in the document of sentence

                // Reset the matcher with each annotation. This is faster and less memory intensive than
                // creating a new matcher evertime.
                m.reset(coveredText);
                int pos = 0;
                // While a match is found

                while (m.find(pos)) {
                    // Get an instance of the class whose name is outputType
                    Constructor<?> con1 = outputTypeMap.get(parameters.get("outputType"));
                    if (con1 == null) {
                        try {
                            con1 = Class.forName((String) parameters.get("outputType")).getConstructor(JCas.class);
                        } catch (Exception e) {
                            throw new AnalysisEngineProcessException(e);
                        }
                        outputTypeMap.put((String) parameters.get("outputType"), con1);
                    }

                    Annotation outputAnnotationType;
                    try {
                        outputAnnotationType = (Annotation) con1.newInstance(aJCas);
                    } catch (Exception e) {
                        throw new AnalysisEngineProcessException(e);
                    }
                    outputAnnotationType.setBegin(m.start() + inputAnnotation.getBegin());
                    outputAnnotationType.setEnd(m.end() + inputAnnotation.getBegin());

                    // Set matched pattern
                    doSetFeatureValue((String) parameters.get("matchedPatternFeatureName"), patt1.pattern(), outputAnnotationType);

                    // Set groups (if needed)
                    doSetGroups((String) parameters.get("groupFeatureName"), m, outputAnnotationType, aJCas);

                    // Set concept value (if needed)
                    doSetFeatureValue((String) parameters.get("concept_feature_name"), (String) parameters.get("concept_feature_value"), outputAnnotationType);

                    // Add the output annotation feature structure to the Cas index
                    outputAnnotationType.addToIndexes();

                    // Update position for next match
                    pos = m.end();

                    if (logger.isDebugEnabled()) {
                        logger.debug("*******************Input Annotation***************************");
                        logger.debug(coveredText);
                        logger.debug("*********************Get Begin Position************************");
                        logger.debug(outputAnnotationType.getBegin());
                        logger.debug("*********************Get End Position**************************");
                        logger.debug(outputAnnotationType.getEnd());
                        logger.debug("******************Position for next Match**********************");
                        logger.debug(pos);
                        logger.debug("*************get Output type*****************************************");
                        logger.debug(outputAnnotationType.getType());
                    }//if debug
                }//while m.find(pos)
            }//while annotationList.hasNext()
            if (configuration.isPerformanceMonitoring()) {
                point.collect();
            }
        }
    }

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        TypeDescription regex = new TypeDescription_impl("gov.va.vinci.leo.regex.types.RegularExpressionType", "", "uima.tcas.Annotation");
        regex.addFeature("pattern", "The regular expression pattern than matched. ", "uima.cas.String");
        regex.addFeature("concept", "A user settable concept label.", "uima.cas.String");
        regex.addFeature("group", "The group array of values from a regex that contains group identifiers.", "uima.cas.StringArray", "uima.cas.String", false);

        LeoTypeSystemDescription ftsd = new LeoTypeSystemDescription();
        try {
            ftsd.addType(regex);
        } catch (Exception e) {
            logger.warn("Exception occurred generating WhitespaceTypeSystem", e);
            throw new RuntimeException(e);
        }//catch
        return ftsd;
    }

    /**
     * Set the feature value on the output annotation provided.  Only works for String features.
     *
     * @param featureName          name of the feature to set
     * @param featureValue         value of the feature to set
     * @param outputAnnotationType output annotation instance
     */
    protected void doSetFeatureValue(String featureName, String featureValue,
                                     Annotation outputAnnotationType) {

        if (!GenericValidator.isBlankOrNull(featureName)) {
            Feature feature = outputAnnotationType.getType().getFeatureByBaseName(featureName);
            if (feature != null) {
                outputAnnotationType.setStringValue(feature, featureValue);
            } else {
                logger.error("Could not set feature (" + featureName + "), it is not a valid feature on the annotation.");
            }
        }
    }

    /**
     * Set the group feature of a regex output annotation.
     *
     * @param groupFeatureName name of the feature to set
     * @param m                pattern matcher whose groups will be added to the StringArray
     * @param outputAnnotation output annotation instance
     * @param cas              CAS for the new StringArray
     */
    protected void doSetGroups(String groupFeatureName, Matcher m, Annotation outputAnnotation, JCas cas) {
        if (m.groupCount() < 1) {
            return;
        }
        if (GenericValidator.isBlankOrNull(groupFeatureName)) {
            return;
        }

        // Add groups.
        Feature groupFeatureNameFeature = outputAnnotation.getType().getFeatureByBaseName(groupFeatureName);
        StringArrayFS sa = new StringArray(cas, m.groupCount());

        for (int i = 1; i <= m.groupCount(); i++) {
            sa.set(i - 1, m.group(i));
        }

        if (groupFeatureNameFeature != null) {
            outputAnnotation.setFeatureValue(groupFeatureNameFeature, sa);
        } else {
            logger.warn("Matched Pattern Feature (" + groupFeatureName + ") is not a valid feature on the annotation. Pattern not set. ");
        }
    }

}//RegexAnnotator class