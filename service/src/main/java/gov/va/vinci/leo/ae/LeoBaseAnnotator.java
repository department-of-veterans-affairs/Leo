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

import gov.va.vinci.leo.descriptors.*;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Base annotator class for holding functions that all annotators might take
 * advantage of, such as loading resource files, etc.
 * <p/>
 * Note: This base class assumes a string[] parameter of input types and a
 * string output type in the uima parameters to know what input types to use and
 * what output type to create.
 */
public abstract class LeoBaseAnnotator extends JCasAnnotator_ImplBase implements LeoAnnotator {

    /**
     * Logger object for messaging.
     */
    private static Logger  logger = Logger.getLogger(LeoBaseAnnotator.class.getCanonicalName());

    /**
     * The types of annotations to use as "anchors".
     */
    @LeoConfigurationParameter
    protected String[] inputTypes = null;

    /**
     * The output type that will be created for each matching window.
     */
    @LeoConfigurationParameter
    protected String outputType = null;

    /**
     * Counter for the number of CASes this annotator has processed.
     */
    protected long numberOfCASesProcessed = 0;

    /**
     * Number of replication instances for this annotator.
     */
    protected int numInstances = LeoAEDescriptor.DEFAULT_NUMBER_INSTANCES;

    /**
     * Optional, Name of the annotator to be set in the descriptor.
     */
    protected String name = null;

    /**
     * Type system for this annotator, defaults to an empty type system.
     */
    protected LeoTypeSystemDescription typeSystemDescription = new LeoTypeSystemDescription();

    /**
     * Use this setting to filter a CAS for processing.
     */
    protected String[] typesFilter = null;

    /**
     * If true then only a CAS with one of the types filter annotations will be processed otherwise only a CAS
     * without one of the types will be passed on for processing. Defaults to true.
     */
    protected boolean includeFilter = true;

    /**
     * Default constructor for initialization by the UIMA Framework.
     */
    public LeoBaseAnnotator() { /** Do Nothing Here **/ }

    /**
     * Constructor setting the initial values for the inputTypes and outputType parameters.
     *
     * @param outputType Annotation type to be used as output for this annotator
     * @param inputTypes One or more annotation types on which processing will occur
     */
    public LeoBaseAnnotator(String outputType, String...inputTypes) {
        this.outputType = outputType;
        this.inputTypes = inputTypes;
    }

    public String[] getInputTypes() {
        return inputTypes;
    }

    public <T extends LeoBaseAnnotator> T setInputTypes(String[] inputTypes) {
        this.inputTypes = inputTypes;
        return (T) this;
    }

    public String getOutputType() {
        return outputType;
    }

    public <T extends LeoBaseAnnotator> T setOutputType(String outputType) {
        this.outputType = outputType;
        return (T) this;
    }

    /**
     * Number of instances of this annotator in the pipeline.  Used during asynchronous processing only.
     *
     * @return Number of instances
     */
    public int getNumInstances() {
        return numInstances;
    }

    /**
     * Set the number of instances of this annotator in the pipeline.  Used during asynchronous processing.
     *
     * @param numInstances Number of replication instances for this annotator in the pipeline
     * @param <T> delegate object reference for extending classes to support the builder pattern.
     * @return reference to this object instance
     */
    public <T extends LeoBaseAnnotator> T setNumInstances(int numInstances) {
        this.numInstances = numInstances;
        return (T) this;
    }

    /**
     * Get the name of this annotator.
     *
     * @return String representation of the annotator name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this annotator.
     *
     * @param name String representation of the annotator name to set
     * @param <T> delegate object reference for extending classes to support the builder pattern.
     * @return reference to this object instance
     */
    public <T extends LeoBaseAnnotator> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * Return the types filter array of type names.  Null if not set.
     *
     * @return list of type names to filter on
     */
    public String[] getTypesFilter() {
        return typesFilter;
    }

    /**
     * Set one or more types to filter on.
     *
     * @param typesFilterNames One or more type names to filter on
     * @return reference to this annotation instance
     */
    public <T extends LeoBaseAnnotator> T setTypesFilter(String...typesFilterNames) {
        this.typesFilter = typesFilterNames;
        return (T) this;
    }

    /**
     * Return the value of the include filter flag.
     *
     * @return true if includeFilter is true, false otherwise
     */
    public boolean isIncludeFilter() {
        return includeFilter;
    }

    /**
     * Set the includeFilter flag value.  If true then a CAS is processes only when one of the typesFilter named types
     * are found, false otherwise.
     *
     * @param includeFilter filter value to set
     * @return reference to this annotation instance
     */
    public <T extends LeoBaseAnnotator> T setIncludeFilter(boolean includeFilter) {
        this.includeFilter = includeFilter;
        return (T) this;
    }

    /**
     * Initialize this annotator.
     *
     * @param aContext the UimaContext to initialize with.
     *
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
     *
     */
    @Override
    public void initialize(UimaContext aContext)
            throws ResourceInitializationException {
        super.initialize(aContext);
        ConfigurationParameterUtils.initParameterValues(this, aContext);
    }// initialize method

    /**
     * Gets a resource as an input stream.
     * <p/>
     * Note: Resource paths are specified via file: and classpath: to determine
     * if the resource should be looked up in the file system or in the
     * classpath. If file: or classpath: is not specified, file path is assumed.
     *
     * @param resourcePath the path to the resource to get the inputstream for.
     * @return the input stream for the resource.
     * @throws java.io.IOException if the input stream for the resource throws an exception.
     */
    public InputStream getResourceAsInputStream(String resourcePath) throws IOException {
        InputStream stream = null;

        if (resourcePath == null) {
            throw new IllegalArgumentException("Resource path cannot be null.");
        }

        if (resourcePath.startsWith("classpath:")) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            stream = cl.getResourceAsStream(resourcePath.substring(10));
            if (stream == null) {
                throw new IllegalArgumentException("Resource: " + resourcePath
                        + " not found in classpath.");
            }
            return stream;
        } else {
            String file;
            if (resourcePath.startsWith("file:")) {
                file = resourcePath.substring(5);
            } else {
                file = resourcePath;
            }

            File f = new File(file);

            if (!f.exists()) {
                throw new IllegalArgumentException(
                        "Resource: "
                                + resourcePath
                                + " not found using file path resolution. Current directory is "
                                + new File(".").getCanonicalPath());
            }

            return new FileInputStream(f);
        }

    }


    /**
     * Given a cas and type, return all of the annotations in the cas for that type.
     * <p/>
     * Small helper method to make extending annotators more readable.
     *
     * @param aJCas the case
     * @param type  the type to get, ie gov.va.vinci.Token
     * @return a list of annotations in the case matching that type. If none match, this is an empty iterator.
     */
    protected FSIterator<Annotation> getAnnotationListForType(JCas aJCas, String type) {
        return aJCas.getAnnotationIndex(aJCas.getTypeSystem().getType(type)).iterator();
    }


    /**
     * Create an output annotation, and add it to the cas.
     *
     * @param outputType The classname of the type. ie- gov.va.vinci.Token
     * @param cas        The cas to add the output annotation to.
     * @param begin      the start index of the annotation
     * @param end        the end index of the annotation
     * @return   the created annotation.
     * @throws AnalysisEngineProcessException If any exception occurs when getting and instantiating the
     *                                        output annotation type, this exception is thrown with the real exception inside of it. (analysisEngineProcessException.getCause());
     */
    protected Annotation addOutputAnnotation(String outputType, JCas cas,
                                             int begin, int end) throws AnalysisEngineProcessException {
        return this.addOutputAnnotation(outputType, cas, begin, end, null);
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
    protected Annotation addOutputAnnotation(String outputType, JCas cas,
                                             int begin, int end, Map<String, Object> featureNameValues) throws AnalysisEngineProcessException {
        try {
            // Get an instance of the outputType class
            Class<?> outputTypeClass = Class.forName(outputType);
            Constructor<?> con1 = outputTypeClass.getConstructor(JCas.class);
            Annotation outputAnnotation = (Annotation) con1.newInstance(cas);
            // Set the window annotation range
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
                    } else {
                        throw new AnalysisEngineProcessException(
                                new RuntimeException("Unknown feature type in map for feature name: " + featureName
                                        + " value: " + featureNameValues.get(featureName)));
                    }
                }
            }
            return outputAnnotation;
        } catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }


    /**
     * Note: Resource paths are specified via file: and classpath: to determine
     * if the resource should be looked up in the file system or in the
     * classpath. If file: or classpath: is not specified, file path is assumed.
     * <p/>
     * The resource is found, and the contents of the resource returned as a
     * string.
     *
     * @param resourcePath the path to the resource to get
     * @return the resource loaded into a string
     * @throws IOException if the input stream for the resource throws an exception.
     */
    public String getResourceFileAsString(String resourcePath)
            throws IOException {
        StringWriter writer = new StringWriter();
        InputStream stream = getResourceAsInputStream(resourcePath);
        IOUtils.copy(stream, writer);
        return writer.toString();
    }


    /**
     * Get the number of CASes this annotator has processed.
     *
     * @return the number of CASes processed.
     */
    public long getNumberOfCASesProcessed() {
        return numberOfCASesProcessed;
    }

    /**
     * Set the number of CASes this annotator has processed.
     *
     * @param numberOfCASesProcessed  the number of documents this annotator has processed.
     */
    public <T extends LeoBaseAnnotator> T setNumberOfCASesProcessed(long numberOfCASesProcessed) {
        this.numberOfCASesProcessed = numberOfCASesProcessed;
        return (T) this;
    }

    /**
     * Called once by the UIMA Framework for each document being analyzed (each
     * CAS instance). Acts on the parameters given by <initialize> method. Main
     * method to implement the annotator logic. In the base class, this simply
     * increments to numberOfCASesProcessed
     *
     * @param aJCas the CAS to process
     * @throws org.apache.uima.analysis_engine.AnalysisEngineProcessException if an exception occurs during processing.
     *
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
     *
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        numberOfCASesProcessed++;
    }

    //TODO Add abstract processing method for filtering.  Discuss this in the architecture meeting.

    /**
     * If typesFilter is not set then return true.  If there is a typesFilter and includeFilter is true then return true
     * only if one of the typesFilter types have been found and false if not.  If includeFilter is false then return true
     * when all of the types in the filter list have not been found or true otherwise.
     *
     * @param jCas CAS to check for filtered types
     * @return true if the filter is met, false otherwise
     * @throws AnalysisEngineProcessException if one of the type names in the tyepsFilter is not found in the type system
     */
    protected boolean hasFilteredAnnotation(JCas jCas) throws AnalysisEngineProcessException {
        if(typesFilter == null)
            return true;
        for(String typeName : typesFilter) {
            Type type = jCas.getTypeSystem().getType(typeName);
            if(type == null)
                throw new AnalysisEngineProcessException("Type " + typeName + " was not found in the CAS type system.", null);
            if(jCas.getAnnotationIndex(type).size() > 0)
                return includeFilter;
        }
        return !includeFilter;
    }

    /**
     * Generate a descriptor from this annotator object.  Parameters are grabbed from class variables and values with
     * LeoConfigurationParameter annotations.
     *
     * @return descriptor for this annotator
     * @throws Exception if there is an error getting the descriptor
     */
    public LeoDelegate getDescriptor() throws Exception {
        String annotatorName = (StringUtils.isBlank(name))? this.getClass().getSimpleName() : name;
        LeoAEDescriptor descriptor = new LeoAEDescriptor(annotatorName, this.getClass().getCanonicalName());
        descriptor.setNumberOfInstances(this.numInstances);
        descriptor.setTypeSystemDescription(getLeoTypeSystemDescription());
        //Add and set parameter values based on the map for this object
        Map<ConfigurationParameterImpl, ?> parameterMap = ConfigurationParameterUtils.getParamsToValuesMap(this);
        for(ConfigurationParameterImpl parameter : parameterMap.keySet()) {
            descriptor.addParameterSetting(
                    parameter.getName(),
                    parameter.isMandatory(),
                    parameter.isMultiValued(),
                    parameter.getType(),
                    parameterMap.get(parameter)
            );
        }
        return descriptor;
    }

    /**
     * Generate a descriptor from this annotator object.  Parameters are grabbed from class variables with
     * LeoConfigurationParameter annotations.
     *
     * @return  the descriptor for this annotator
     * @throws Exception if there is an error getting the descriptor
     */
    public LeoAEDescriptor getLeoAEDescriptor() throws Exception {
        return (LeoAEDescriptor) this.getDescriptor();
    }

    /**
     * Return the type system description for this annotator.  Extending methods can add or set the variable
     * <code>typeSystemDescription</code> to set a default type system description unique to that annotator.
     *
     * @return the default type system for this annotator.
     */
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return typeSystemDescription;
    }

    /**
     * Add the type system provided to the type system of the annotator.
     *
     * @param typeSystemDescription LeoTypeSystemDescription to add.
     * @param <T> delegate object reference for extending classes to support the builder pattern.
     * @return Reference to this object instance, supports the builder pattern.
     */
    public <T extends LeoBaseAnnotator> T addLeoTypeSystemDescription(LeoTypeSystemDescription typeSystemDescription) {
        this.typeSystemDescription.addTypeSystemDescription(typeSystemDescription);
        return (T) this;
    }

    /**
     * Set the type system for this annotator.  WARN: Replaces the existing type system without preserving it.
     *
     * @param typeSystemDescription LeoTypeSystemDescription to use.
     * @param <T> delegate object reference for extending classes to support the builder pattern.
     * @return Reference to this object instance, supports the builder pattern.
     */
    public <T extends LeoBaseAnnotator> T setLeoTypeSystemDescription(LeoTypeSystemDescription typeSystemDescription) {
        this.typeSystemDescription = typeSystemDescription;
        return (T) this;
    }

    /**
     * A set of default parameters for the base annotation.
     *
     * Extending annotators may use persist parameters from a parent class by extending the inner Param from the parent as in:
     * <code>public static class Param extends LeoBaseAnnotator.param {}</code>
     *
     * Each field represents an annotation parameter and utilizes the UIMA ConfigurationParameter as in the declaration:
     * <p>
     * <code>public static ConfigurationParameter PARAMETER_NAME = new ConfigurationParameterImpl("name", "description", "type", isMandatory, isMultivalued, new String[]{});</code>
     */
    @Deprecated
    public static class Param {
        /** Parameters for this class are now defined using annotations **/
    }

}
