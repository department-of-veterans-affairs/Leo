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
import gov.va.vinci.leo.descriptors.LeoDelegate;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
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
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
     * logging object.
     */
    protected static Logger logger = Logger.getLogger(LeoBaseAnnotator.class.getCanonicalName());

    /**
     * The types of annotations to use as "anchors".
     */
    protected String[] inputTypes = null;

    /**
     * The output type that will be created for each matching window.
     */
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
     * Type system description of this annotator.
     */
    protected LeoTypeSystemDescription typeSystemDescription = new LeoTypeSystemDescription();

    /**
     * A map of parameters that is loaded during the initialization of this annotator.
     */
    @Deprecated
    protected Map<ConfigurationParameter, Object> parameters
            = new HashMap<ConfigurationParameter, Object>();


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

    /**
     * Constructor setting the initial values for the number of instances, inputTypes, and outputType parameters.
     *
     * @param numInstances Number of replication instances for this annotator in the pipeline
     * @param outputType Annotation type to be used as output for this annotator
     * @param inputTypes One or more annotation types on which processing will occur
     */
    public LeoBaseAnnotator(int numInstances, String outputType, String...inputTypes) {
        this(outputType, inputTypes);
        this.numInstances = numInstances;
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

    @Override
    /**
     * Initialize this annotator.
     *
     * @param aContext the UimaContext to initialize with.
     *
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
     *
     */
    public void initialize(UimaContext aContext)
            throws ResourceInitializationException {
        try {
            initialize(aContext, this.getAnnotatorParams());
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
    }// initialize method

    /**
     * Initialize this annotator.
     *
     * @param aContext the UimaContext to initialize with.
     * @param params the annotator params to load from the descriptor.
     *
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
     * @param aContext
     * @param params
     * @throws ResourceInitializationException  if any exception occurs during initialization.
     */
    public void initialize(UimaContext aContext, ConfigurationParameter[] params)
            throws ResourceInitializationException {
        super.initialize(aContext);

        if (params != null) {
            for (ConfigurationParameter param : params) {
                if (param.isMandatory()) {
                    /** Check for null value **/
                    if (aContext.getConfigParameterValue(param.getName()) == null) {
                        throw new ResourceInitializationException(new IllegalArgumentException("Required parameter: " + param.getName() + " not set."));
                    }
                    /** Check for empty as well if it is a plain string. */
                    if (    ConfigurationParameter.TYPE_STRING.equals(param.getType()) &&
                            !param.isMultiValued() &&
                            GenericValidator.isBlankOrNull((String) aContext.getConfigParameterValue(param.getName()))) {
                        throw new ResourceInitializationException(new IllegalArgumentException("Required parameter: " + param.getName() + " cannot be blank."));
                    }
                }

                parameters.put(param, aContext.getConfigParameterValue(param.getName()));

                /** Set the parameter value in the class field variable **/
                try {
                    Field field = FieldUtils.getField(this.getClass(), param.getName(), true);
                    if(field != null) {
                        FieldUtils.writeField(field, this, aContext.getConfigParameterValue(param.getName()), true);
                    }
                } catch (IllegalAccessException e) {
                    logger.warn("Could not set field (" + param.getName() + "). Field not found on annotator class to reflectively set.");
                }
            }
        }
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
    public void setNumberOfCASesProcessed(long numberOfCASesProcessed) {
        this.numberOfCASesProcessed = numberOfCASesProcessed;
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

    /**
     * Returns a LeoAEDescriptor based on Param defined in the Annotator either via an Enum
     * that implements AnnotatorParam, or a static class named Param with static AnnotatorParam object.
     * <p/>
     * For Example:
     * <p/>
     * <pre>
     * {@code
     * public enum Param implements AnnotatorParam {
     *      RESOURCE("resource", true, false, "String"),
     *      ...
     * }
     * }</pre>
     *
     * or
     *
     * <pre>
     * {@code
     * public static class Param {
     *      public static ConfigurationParameter PARAMETER_NAME = new ConfigurationParameterImpl("name", "description", "type", isMandatory, isMultivalued, new String[]{});
     * }
     * }</pre>
     *
     * @return  the descriptor for this annotator.
     * @throws Exception if there is an error getting the descriptor.
     */
    public LeoDelegate getDescriptor() throws Exception {
        String annotatorName = (StringUtils.isBlank(name))? this.getClass().getSimpleName() : name;
        LeoAEDescriptor descriptor = new LeoAEDescriptor(annotatorName, this.getClass().getCanonicalName());
        descriptor.setNumberOfInstances(this.numInstances);
        descriptor.setTypeSystemDescription(getLeoTypeSystemDescription());
        //Add and set parameter values based on the map for this object
        Map<ConfigurationParameter, Object> parameterMap = this.getParametersToValuesMap();
        for(ConfigurationParameter parameter : parameterMap.keySet()) {
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
     * Returns a LeoAEDescriptor a static class named Param with static ConfigurationParameter objects.  Descriptor
     * generation is further facilitated by creating a class variable to store the parameter value whose name is the
     * same as the parameter name set in the ConfigurationParameter object.
     * <p/>
     * For Example:
     * <p/>
     * <pre>
     * {@code
     * protected String parameterName = "parameter value";
     *
     * public static class Param {
     *      public static ConfigurationParameter PARAMETER_NAME
     *          = new ConfigurationParameterImpl(
     *              "parameterName", "Description", "String", false, true, new String[] {}
     *          );
     * }
     * }</pre>
     *
     * Parameters from parent classes can be used, including those from the LeoBaseAnnotator, by extending the parent
     * class Params.
     *
     * <p>
     * For Example:
     * </p>
     * <pre>
     * {@code
     * public static class Param extends LeoBaseAnnotator.Param {
     *     ...
     * }
     * }
     * </pre>
     *
     * @return  the descriptor for this annotator.
     * @throws Exception if there is an error getting the descriptor.
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
     * Returns parameters defined in a static class named Param with static ConfigurationParameter objects.
     * <p/>
     * For Example:
     * <p/>
     * <pre>
     * {@code
     * protected String parameterName = "parameter value";
     *
     * public static class Param {
     *      public static ConfigurationParameter PARAMETER_NAME
     *          = new ConfigurationParameterImpl(
     *              "parameterName", "Description", "String", false, true, new String[] {}
     *          );
     * }
     * }</pre>
     *
     * @return  the annotator parameters this annotator can accept.
     * @throws IllegalAccessException  if there is an exception trying to get annotator parameters via reflection.
     */
    protected ConfigurationParameter[] getAnnotatorParams() throws IllegalAccessException {
        ConfigurationParameter params[] = null;

        for (Class c : this.getClass().getDeclaredClasses()) {
            if (c.isEnum() && Arrays.asList(c.getInterfaces()).contains(ConfigurationParameter.class)) {
                params = new ConfigurationParameter[EnumSet.allOf(c).size()];
                int counter = 0;
                for (Object o : EnumSet.allOf(c)) {
                    params[counter] = ((ConfigurationParameter) o);
                    counter++;
                }
                break;
            } else if (c.getCanonicalName().endsWith(".Param")) {
                Field[] fields = c.getFields();
                List<ConfigurationParameter> paramList = new ArrayList<ConfigurationParameter>();

                for (Field f : fields) {
                    if (ConfigurationParameter.class.isAssignableFrom(f.getType())) {
                        paramList.add((ConfigurationParameter) f.get(null));
                    }
                }
                params = paramList.toArray(new ConfigurationParameter[paramList.size()]);
            }
        }
        return params;
    }

    /**
     * Get a map of parameters and their values.  Parameter list is retrieved from the inner Param class. Values are
     * retrieved by matching the name of the ConfigurationParameter to the name of a variable set in the class.
     *
     * @return  a map of parameters and their values that is created during initialization.
     */
    protected Map<ConfigurationParameter, Object> getParametersToValuesMap() {
        Map<ConfigurationParameter, Object> parameterObjectMap = new HashMap<ConfigurationParameter, Object>();
        ConfigurationParameter[] params;
        try {
            params = this.getAnnotatorParams();
            for(ConfigurationParameter parameter : params) {
                try {
                    Field field = FieldUtils.getField(this.getClass(), parameter.getName(), true);
                    parameterObjectMap.put(parameter, FieldUtils.readField(field, this, true));
                } catch (Exception eField) {
                    parameterObjectMap.put(parameter, null);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return parameterObjectMap;
    }

    /**
     * Get a map of parameters and their values.  Parameter list is retrieved from the inner Param class.  Values are
     * retrieved by matching the name of the ConfigurationParameter to the name of a variable set in the class.
     *
     * @return a map of parameters and their values that is created during initialization.
     * @deprecated This method has been replaced with LeoBaseAnnotator#getParametersToValuesMap
     */
    @Deprecated
    public Map<ConfigurationParameter, Object> getParameters() {
        return parameters;
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
    public static class Param {
        /**
         * The type output by this annotator.
         */
        public static ConfigurationParameter OUTPUT_TYPE
                = new ConfigurationParameterImpl(
                    "outputType", "outputType", "String", false, false, new String[] {}
                );
        /**
         * the type of annotation to be searched. Default is uima.tcas.DocumentAnnotation. This is an array of strings,
         * and can contain multiple types.
         */
        public static ConfigurationParameter INPUT_TYPE
                = new ConfigurationParameterImpl(
                    "inputTypes", "inputType", "String", false, true, new String[] {}
                );
    }
}
