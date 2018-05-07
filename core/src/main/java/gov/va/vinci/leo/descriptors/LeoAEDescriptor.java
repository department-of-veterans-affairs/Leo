/**
 *
 */
package gov.va.vinci.leo.descriptors;

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

import gov.va.vinci.leo.model.NameValue;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.FlowConstraints;
import org.apache.uima.analysis_engine.metadata.impl.AnalysisEngineMetaData_impl;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.*;
import org.apache.uima.resource.metadata.impl.ConfigurationParameterDeclarations_impl;
import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Allows the user to easily modify and create an AnalysisEngineDescriptor object without
 * needing to directly access the UIMA API.
 *
 * @author thomasginter
 */
public class LeoAEDescriptor implements LeoDelegate {
    /**
     * AnalysisEngineDescription reference object.
     */
    protected AnalysisEngineDescription mAEDescriptor = null;

    /**
     * Delegate AnalysisEngineDescriptors for aggregates only.
     */
    protected ArrayList<LeoDelegate> mDelegates = null;

    /**
     * Names of the delegate engines for aggregates only.
     */
    protected ArrayList<String> mDelegateNames = null;

    /**
     * Flow constraints for aggregates not using a fixed flow.
     */
    protected FlowConstraints mFlowConstraints = null;

    /**
     * For an aggregate engine deployment will this aggregate be executed asynchronously,
     * if false (default) then the engine will be executed and scaled as a unit,
     * if true then the delegate engines will each be scaled individually.
     */
    protected boolean mIsAsync = false;

    /**
     * The number of instances of this Engine to execute in parellel on the service.
     * Aggregate engines with async=false must be set to 1 for the number of instances.
     */
    protected int mNumberOfInstances = DEFAULT_NUMBER_INSTANCES;

    /**
     * The locator for this descriptor.
     */
    protected URI mDescriptorLocator = null;

    /**
     * If TRUE then delete the generated descriptor file once the program exits, defaults to TRUE.
     */
    protected boolean deleteOnExit = true;

    /**
     * Logging object of output.
     */
    public static final Logger LOG = Logger.getLogger(LeoAEDescriptor.class.getName());

    /**
     * Default number of replication instances for this AnalysisEngine.
     */
    public static final int DEFAULT_NUMBER_INSTANCES = 1;

    /**
     * Default constructor.  Just gets a blank AnalysisEngineDescriptor object from the factory.
     */
    public LeoAEDescriptor()  {
        AnalysisEngineDescription_impl d = new AnalysisEngineDescription_impl();
        d.setPrimitive(true);
        setAEDescriptor(d);
    }//default constructor

    /**
     * Constructor that creates a blank AnalysisEngineDescriptor object with the name provided. Also
     * sets the implementation class name to the implementation_class string provided.
     *
     * @param name                 the name of the AnalysisEngine, Leo will make sure it is unique.
     * @param implementation_class the fully qualified name of the Annotator Class.
     */
    public LeoAEDescriptor(String name, String implementation_class) {
        setAEDescriptor(AnalysisEngineFactory.generateAED(name, implementation_class));
    }//constructor (name, implementation_class)

    /**
     * Create a new description object from the descriptor file provided.
     *
     * @param descriptor  path or name of a descriptor file used to generate the AED
     * @param byName      if true then try to import by name otherwise import by location
     * @param paramValues Optional, list of parameters names and values to set.  Creates the
     *                    parameter if it does not already exist.
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public LeoAEDescriptor(String descriptor, boolean byName, NameValue... paramValues) throws IOException, InvalidXMLException {
        this(AnalysisEngineFactory.generateAED(descriptor, byName));
        if (paramValues != null) {
            for (NameValue fpv : paramValues) {
                try {
                    this.setParameterSetting(fpv.getName(), fpv.getValue());
                } catch (Exception e) {
                    //setParameterSetting throws an exception if parameter does not exist
                    LOG.warn("Exception thrown when setting parameter: " +
                            fpv.name + ", value: " + fpv.getValue().toString());
                }//catch
            }//for
        }//if
    }//constructor with string and boolean input

    /**
     * Create a new LeoAEDescriptor object that represents an aggregate from a list of
     * LeoAEDescriptor primitives.
     *
     * @param primitives List of primitives to be included in the aggregate
     */
    public LeoAEDescriptor(List<LeoAEDescriptor> primitives) {
        this();

        for (LeoAEDescriptor a : primitives) {
            this.addDelegate(a);
        }
    }//constructor LeoAEDescriptor list input

    /**
     * Use an existing AnalysisEngineDescription object to create a new LeoAEDescriptor.
     *
     * @param aed AnalysisEngineDescription object used to store the metadata for this descriptor
     */
    public LeoAEDescriptor(AnalysisEngineDescription aed) {
        setAEDescriptor(aed);
    }//constructor with AnalysisEngineDescription input

    /**
     * Assign the AnalysisEngineDescription model for this LeoAEDescriptor object.  For aggregates
     * we also get the delegates and assign them within each map.
     *
     * @param aed AnalysisEngineDescription object
     */
    protected void setAEDescriptor(AnalysisEngineDescription aed) {
        if (aed == null) {
            throw new IllegalArgumentException("AnalysisEngineDescription cannot be null. ");
        }

        //Set the name
        String name = aed.getMetaData().getName();
        if (StringUtils.isBlank(name)) {
            name = (aed.isPrimitive()) ? "leoPrimitive" : "leoAggregate";
        }//if

        //if primitive just set the mAEDescriptor and locator
        if (aed.isPrimitive()) {
            mAEDescriptor = aed;
            this.setName(name);
        } else {
            //For aggregate engines we will create a new LeoAEDescriptor for each delegate engine
            //then add it to the map in the order it is executed.
            try {
                FlowConstraints fcs = aed.getAnalysisEngineMetaData().getFlowConstraints();
                Map<String, ResourceSpecifier> delegates = aed.getDelegateAnalysisEngineSpecifiers();
                String[] flow;

                if (fcs != null && FixedFlow.FLOW_CONSTRAINTS_TYPE.equals(fcs.getFlowConstraintsType())) {
                    flow = ((FixedFlow) fcs).getFixedFlow();
                } else {
                    mFlowConstraints = fcs;
                    flow = delegates.keySet().toArray(new String[delegates.keySet().size()]);
                }//else
                if (mDelegates == null) mDelegates = new ArrayList<LeoDelegate>();
                if (mDelegateNames == null) mDelegateNames = new ArrayList<String>();
                for (String id : flow) {
                    ResourceSpecifier spec = delegates.get(id);
                    if(spec instanceof AnalysisEngineDescription) {
                        mDelegates.add(new LeoAEDescriptor((AnalysisEngineDescription)spec));
                    } else if(spec instanceof CustomResourceSpecifier) {
                        mDelegates.add(new LeoRemoteAEDescriptor((CustomResourceSpecifier)spec));
                    } else {
                        throw new RuntimeException("Unknown Delegate Type: " + spec.getClass().getCanonicalName());
                    }
                    mDelegateNames.add(id);
                }//for
                //Create new Aggregate from list of faed's
                mAEDescriptor = AggregateEngineFactory.createAggregateDescription(mDelegates, name);
                this.setName(name);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }//catch
        }//else
    }//setAEDescriptor method

    /**
     * Return an AnalysisEngineDescription object reference for this model.  If this is an aggregate
     * engine then combine the delegates into a new descriptor in case delegate params have been set.
     *
     * @return AnalysisEngineDescription object
     */
    protected AnalysisEngineDescription getAEDescriptor() {
        //Just return the descriptor reference if this is a primitive engine
        if (mAEDescriptor.isPrimitive()) return mAEDescriptor;

        //Get a list of resource specifiers and return an aggregate engine based on those
        //Creates a reference to the descriptors themselves rather than imports
        List<ResourceSpecifier> delegates = new ArrayList<ResourceSpecifier>();
        for(LeoDelegate ld : mDelegates) {
            delegates.add(ld.getResourceSpecifier());
        }

        return AggregateEngineFactory.createAggregateDescription(this.getName(), delegates);
    }//getAEDescriptor method

    /**
     * Add a delegate analysis engine descriptor to this engine using the descriptor provided.  Optionally the user can
     * also set parameter values in the delegate.
     *
     * @param descriptor  Name or path to the delegate descriptor to be added.
     * @param byName      If true import this descriptor by name, else assume the descriptor param is a path
     * @param paramValues Optional, parameter values to be set in the delegate descriptor
     * @return this Leo AE Descriptor object.
     * @throws java.io.IOException if the descriptor cannot be read.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public LeoAEDescriptor addDelegate(String descriptor, boolean byName, NameValue... paramValues) throws IOException, InvalidXMLException {
        LeoAEDescriptor faed = new LeoAEDescriptor(descriptor, byName, paramValues);
        return this.addDelegate(faed);
    }//addDelegate String descriptor method

    /**
     * Add the provided LeoAEDescriptor as a delegate to this AnalysisEngine.
     *
     * @param delegate Delegate to be added.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addDelegate(LeoDelegate delegate)  {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate parameter cannot be null: delegate == null");
        }//if
        //Create the delegates ArrayList if not set yet
        if (mDelegates == null) {
            mDelegates = new ArrayList<LeoDelegate>();
        }//if
        //Create the delegate names ArrayList if not set yet
        if (mDelegateNames == null) {
            mDelegateNames = new ArrayList<String>();
        }//if
        //Add this delegate to the delegate lists and regenerate the aggregate
        mAEDescriptor.setPrimitive(false);
        mDelegates.add(delegate);
        mDelegateNames.add(delegate.getName());
        AnalysisEngineDescription aed = this.getAEDescriptor();
        if (aed == null) {
            //Generate our own new one if we could not get one from getAEDescriptor
            aed = AggregateEngineFactory.createAggregateDescription(mDelegates, this.getName());
        }//if
        this.mAEDescriptor = aed;
        return this;
    }//addDelegate LeoAEDescriptor method

    /**
     * Get a LeoAEDescriptor for a delegate engine with the name provided.
     *
     * @param name Annotator delegate name
     * @return Delegate LeoAEDescriptor with name or null if not found
     */
    public LeoDelegate getDelegate(String name) {
        if (mDelegates == null) return null;
        if (mDelegateNames == null) return null;
        //Lookup the name in the names array for the index position
        int index = mDelegateNames.indexOf(name);
        if (index < 0) return null; //name not found
        LeoDelegate faed;
        try {
            faed = mDelegates.get(index);
        } catch (Exception e) {
            faed = null;
        }//catch
        return faed;
    }//getDelegate method

    /**
     * Get the set of delegate engines for this aggregate if it exists.
     *
     * @return LeoAEDescriptor array of delegate engines or null if no delegates are found
     */
    public LeoDelegate[] getDelegates() {
        if (mDelegates == null) {
            return null;
        }
        return mDelegates.toArray(new LeoDelegate[mDelegates.size()]);
    }//getDelegates method

    /**
     * Return the names of the delegates found in aggregate descriptors.  If no names
     * have been set then returns null.
     *
     * @return list of delegate names or null if no names are found.
     */
    public List<String> getDelegateNames() {
        return mDelegateNames;
    }//getDelegateNames method

    /**
     * Generate the AnalysisEngineDescription object from the descriptor file and set it as the internal gov.va.vinci.leo.model
     * for this LeoAEDescriptor.
     *
     * @param descriptor file used to generate the AnalysisEngineDescription object
     * @param byName     if true import the descriptor by name otherwise import by location
     * @return this Leo AE Descriptor object.
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public LeoAEDescriptor setAnalysisEngineDescription(String descriptor, boolean byName) throws IOException, InvalidXMLException {
        setAEDescriptor(AnalysisEngineFactory.generateAED(descriptor, byName));
        return this;
    }//setAnalysisEngineDescription method

    /**
     * Set the AnalysisEngineDescription object that will be the core gov.va.vinci.leo.model for this LeoAEDescriptor.
     *
     * @param aed The AnalysisEngineDescription object
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setAnalysisEngineDescription(AnalysisEngineDescription aed) {
        setAEDescriptor(aed);
        return this;
    }//seAnalysisEngineDescription method

    /**
     * Get a reference to the core AnalysisEngineDescription object that this LeoAEDescriptor uses.
     *
     * @return an AnalysisEngineDescription object
     */
    public AnalysisEngineDescription getAnalysisEngineDescription() {
        return getAEDescriptor();
    }//getAnalysisEngineDescription method

    /**
     * Set the parameter with the name to the value.
     *
     * @param name  String representing the parameter to be set
     * @param value Object that is the value of this parameter
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setParameterSetting(String name, Object value)  {
        ConfigurationParameter cp = mAEDescriptor.getAnalysisEngineMetaData()
                .getConfigurationParameterDeclarations().getConfigurationParameter(null, name);
        if (cp != null) {
            mAEDescriptor.getAnalysisEngineMetaData()
                    .getConfigurationParameterSettings().setParameterValue(name, value);
        } else {
            throw new IllegalArgumentException("Parameter '" + name + "' not found in descriptor.");
        }//else

        return this;
    }//setParameterSetting method

    /**
     * Set a parameter from a NameValue object.  Set create to true if this method
     * should create a parameter if it does not exist.
     *
     * @param fpv Parameter name and value to be set
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setParameterSetting(NameValue fpv)  {
        return this.setParameterSetting(fpv.name, fpv.getValue());
    }//setParameterSetting method

    /**
     * Add a new parameter.  If a parameter already exists with the same name
     * then the optional value is set.
     *
     * @param paramName     Name of the parameter to be created
     * @param isMandatory   If True then this parameter is required, false otherwise
     * @param isMultiValued If True then this parameter is multi-valued (an Array), false otherwise
     * @param paramType     The data type of the parameter, i.e. String, Boolean, etc.
     * @return this Leo AE Descriptor object.
     * @throws Exception Thrown if the parameter cannot be set with the optional value or an required argument is missing
     */
    public LeoAEDescriptor addParameterSetting(String paramName, boolean isMandatory, boolean isMultiValued, String paramType) throws Exception {
        return addParameterSetting(paramName, isMandatory, isMultiValued, paramType, null);
    }//addParameterSetting method

    /**
     * Add a new parameter and optionally set an initial value.  If a parameter already exists with the same name
     * then the optional value is set.
     *
     * @param paramName     Name of the parameter to be created
     * @param isMandatory   If True then this parameter is required, false otherwise
     * @param isMultiValued If True then this parameter is multi-valued (an Array), false otherwise
     * @param paramType     The data type of the parameter, i.e. String, Boolean, etc.
     * @param initValue     Optional, the initial value to set for this parameter
     * @return this Leo AE Descriptor object.
     * @throws Exception Thrown if the parameter cannot be set with the optional value or an required argument is missing
     */
    public LeoAEDescriptor addParameterSetting(String paramName, boolean isMandatory, boolean isMultiValued, String paramType, Object initValue) throws Exception {
        //Check to see if the name or paramType are null
        if (paramName == null || paramName.isEmpty())
        {
            throw new IllegalArgumentException("Parameter Name is required: name is null or empty");
        }
        validParamType(paramType);

        //Check for existing parameter value
        ConfigurationParameter cp = mAEDescriptor.getAnalysisEngineMetaData()
                .getConfigurationParameterDeclarations().getConfigurationParameter(null, paramName);
        if (cp == null) {
            //Add this parameter
            cp = new ConfigurationParameter_impl();
            cp.setName(paramName);
            cp.setMandatory(isMandatory);
            cp.setMultiValued(isMultiValued);
            cp.setType(paramType);
            this.addConfigurationParameter(cp);
        }
        //Set the initial value if it is not null
        if (initValue != null)
            this.setParameterSetting(paramName, initValue);
        return this;
    }//addParameterSetting method

    /**
     * Test the Parameter Type string to make sure it is not null or empty and it is one of the defined type
     * constants defined in the ConfigurationParameter interface.
     *
     * @param paramType The parameter Type string to be validated
     * @throws Exception Thrown if the type string is empty or null or is not one of the type
     *                   constants defined in the ConfigurationParameter interface.
     */
    protected void validParamType(String paramType) throws Exception {
        if (paramType == null || paramType.isEmpty())
            throw new Exception("Parameter Type is required: paramType is null or empty");
        if (!ConfigurationParameter.TYPE_BOOLEAN.equals(paramType) &&
                !ConfigurationParameter.TYPE_FLOAT.equals(paramType) &&
                !ConfigurationParameter.TYPE_INTEGER.equals(paramType) &&
                !ConfigurationParameter.TYPE_STRING.equals(paramType))
            throw new Exception("Parameter Type must be one of the constant definitions in the ConfigurationParemeter interface.");
    }//isValidParamType method

    /**
     * Adds a configuration parameter to the metadata for this AnalysisEngineDescriptor.
     *
     * @param aConfigurationParameter UIMA ConfigurationParameter to add
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addConfigurationParameter(ConfigurationParameter aConfigurationParameter) {
        ConfigurationParameterDeclarations params = ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData())
                .getConfigurationParameterDeclarations();
        if (params == null) {
            params = new ConfigurationParameterDeclarations_impl();
        }//if

        params.addConfigurationParameter(aConfigurationParameter);
        return this;
    }//addConfigurationParameter method

    /**
     * Return the value to which the parameter has been set.
     *
     * @param paramName The name of the parameter whose value will be returned
     * @return Parameter value or null if the value cannot be retrieved.
     */
    public Object getParameterValue(String paramName) {
        if (paramName == null || paramName.isEmpty()) return null;
        return mAEDescriptor.getAnalysisEngineMetaData().getConfigurationParameterSettings().getParameterValue(paramName);
    }//getParameterValue method

    /**
     * Add a new type to the TypeSystem with the Name and SuperType name provided.
     *
     * @param aTypeName        Name of the new Type to be added to the TypeSystem
     * @param aTypeDescription Description of the new Type to be added
     * @param aSuperTypeName   Name of the SuperType from which this type will derive
     * @return this Leo AE Descriptor object.
     * @throws Exception If the required parameters, aTypeName or aSuperTypeName are missing.
     */
    public LeoAEDescriptor addType(String aTypeName, String aTypeDescription, String aSuperTypeName) throws Exception {
        //If we are missing the type name or the super type then throw an Exception
        if (StringUtils.isBlank(aTypeName) || StringUtils.isBlank(aSuperTypeName)) {
            throw new Exception("TypeName and SuperTypeName are required parameters, Name: " + aTypeName + ", SuperType: " + aSuperTypeName);
        }//if

        return this.addType(new TypeDescription_impl(aTypeName, aTypeDescription, aSuperTypeName));
    }//addType method with inputs typename,typedescription,supertypename and boolean value for

    /**
     * Add an Annotation Type to the TypeSystem.  Return a reference pointer to this LeoAEDescriptor object.
     *
     * @param td TypeDescription to add to the TypeSystem
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addType(TypeDescription td) {
        //If the TypeDescription is null just return, nothing to add
        if (td == null) {
            return this;
        }//if

        //Create the LeoTypeSystemDescription and add the new type
        LeoTypeSystemDescription ftsd = (mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem() == null) ?
                new LeoTypeSystemDescription() :
                new LeoTypeSystemDescription(mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem());
        ftsd.addType(td);
        mAEDescriptor.getAnalysisEngineMetaData().setTypeSystem(ftsd.getTypeSystemDescription());
        return this;
    }//addType method

    /**
     * Append the TypeSystem contained in a LeoTypeSystemDescription object to the TypeSystem contained in
     * this Analysis Engine.
     *
     * @param ftsd LeoTypeSystemDescription to add to this TypeSystem.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addTypeSystemDescription(LeoTypeSystemDescription ftsd) {
        //Just return if null
        if (ftsd == null) {
            return this;
        }
        return this.addTypeSystemDescription(ftsd.getTypeSystemDescription());
    }//addTypeSystemDescription method LeoTypeSystemDescription input

    /**
     * Append an existing TypeSystem to the existing TypeSystem contained in this Analysis Engine.
     *
     * @param tsd TypeSystemDescription to append to the existing one.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addTypeSystemDescription(TypeSystemDescription tsd) {
        //Just return if there is nothing to add
        if (tsd == null) {
            return this;
        }

        //Get a LeoTypeSystemDescription from the existing TypeSystem, blank one if no TypeSystem exists yet
        LeoTypeSystemDescription ftsd = (mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem() == null) ?
                new LeoTypeSystemDescription() :
                new LeoTypeSystemDescription(mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem());

        //Append the new tsd to the existing one.
        ftsd.addTypeSystemDescription(tsd);
        mAEDescriptor.getAnalysisEngineMetaData().setTypeSystem(ftsd.getTypeSystemDescription());

        return this;
    }//addTypeSystemDescription method

    /**
     * This method sets the type system description for this analysis engine to the TypeSystem provided.
     *
     * @param ftsd LeoTypeSystemDescription to be used in this analysis engine
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setTypeSystemDescription(LeoTypeSystemDescription ftsd) {
        return this.setTypeSystemDescription(ftsd.getTypeSystemDescription());
    }//setTypeDescription LeoTypeSystemDescription input

    /**
     * Set the type system description for this analysis engine to the TypeSystem provided.
     *
     * @param tsd TypeSystemDescription to be used for this analysis engine
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setTypeSystemDescription(TypeSystemDescription tsd) {
        if (tsd == null) {
            return this;
        }

        mAEDescriptor.getAnalysisEngineMetaData().setTypeSystem(tsd);
        return this;
    }//setTypeDescription method TypeSystemDescription input

    /**
     * A simple method for getting the hold of current Type System.
     * @return  the type system for this descriptor.
     */
    public LeoTypeSystemDescription getTypeSystemDescription() {
        return new LeoTypeSystemDescription(mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem());
    }//getTypeSystemDescription method

    /**
     * Generate the java source files for the TypeSystem of this AnalysisEngine, then compile them.
     *
     * @param srcDirectory Directory where the java source files will be generated into
     * @param binDirectory Directory path where the .class files will be generated
     * @throws Exception If we are unable to generate the java files or there is a compilation error
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor genTypeSystemJava(String srcDirectory, String binDirectory) throws Exception {
        LeoTypeSystemDescription ftsd = new LeoTypeSystemDescription(mAEDescriptor.getAnalysisEngineMetaData().getTypeSystem());
        ftsd.jCasGen(srcDirectory, binDirectory);
        return this;
    }//genTypeSystemJava method

    /**
     * @return boolean async flag for aggregate engines
     *         if true then delegates are scaled individually
     *         if false (default) then aggregate is scaled as a whole
     */
    public boolean isAsync() {
        return mIsAsync;
    }//isAsync method

    /**
     * Set the async flag for an aggregate deployment only, see the
     * isAsync method for more information about flag usage. This method also
     * changes the name to "aggregate" if async is set to true.
     *
     * @param async If this is a primitive, async will be set to false, otherwise it will be set to async.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setIsAsync(boolean async)  {
        mIsAsync = (this.isAggregate()) && async;
        if (mIsAsync) {
            this.setName("leoAggregate");
        }
        return this;
    }//setIsAsync method

    /**
     * @return true if this is an aggregate engine or false if primitive
     */
    public boolean isAggregate() {
        return (mDelegates != null);
    }//isAggregate method

    /**
     * @return the NumberOfInstances to scale this deployment into
     */
    public int getNumberOfInstances() {
        return mNumberOfInstances;
    }//getNumberOfInstances method

    /**
     * @param numberOfInstances the NumberOfInstances to scale this engine
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setNumberOfInstances(int numberOfInstances) {
        mNumberOfInstances = numberOfInstances;
        return this;
    }//setNumberOfInstances method

    /**
     * @return the max number of instances for this engine and any delegates
     */
    public int getMaxNumberOfInstances() {
        int max = mNumberOfInstances;
        if (this.isAggregate() && mDelegates != null) {
            for (LeoDelegate faed : this.mDelegates) {
                if(faed instanceof LeoAEDescriptor) {
                    int dMax = ((LeoAEDescriptor)faed).getMaxNumberOfInstances();
                    if (dMax > max) max = dMax;
                }
            }//for
        }//if
        return max;
    }//getMaxNumberOfInstances method

    /**
     * Set the location where the descriptor should be generated.  If a file name is specified it will be ignored and the
     * parent directory path will be used instead.  This method should never be called directly by the user as settings
     * are propagated from the service object and will be overridden.  Descriptor location can be set in the service
     * object using <code>Service.setDescriptorDirectory("my/desc/dir")</code>.
     *
     * @param mDescriptorLocator URI path where the descriptors should be located
     * @return reference to this LeoAEDescriptor object instance for builder pattern
     */
    public LeoAEDescriptor setDescriptorLocator(URI mDescriptorLocator) {
        this.mDescriptorLocator = mDescriptorLocator;
        return this;
    }

    /**
     * @return Descriptor locator for the AnalysisEngineDescriptor xml file
     *         represented by this LeoAEDescriptor object or null if the locator is not set.
     */
    public String getDescriptorLocator() {
        if(mDescriptorLocator != null) {
            return mDescriptorLocator.getPath();
        } else {
            return null;
        }
    }//getDescriptorLocator method

    /**
     * Return a reference to the internal ResourceSpecifier that is the backend model for the delegate.  AnalysisEngines
     * will have a ResourceSpecifier that is an instance of AnalysisEngineDescription whereas remote delegates have a
     * ResourceSpecifier that is a CustomResourceSpecifier.
     *
     * @return ResourceSpecifier object for this delegate
     */
    @Override
    public ResourceSpecifier getResourceSpecifier() {
        return (ResourceSpecifier) this.getAnalysisEngineDescription();
    }

    /**
     * Write the descriptor out to a temp XML file with the name provided as filename.
     *
     * @param filename the filename to write out to. Note: the name is used in creating a temp file with the .xml
     *                 suffix, so name should just be the root filename with a path or extension.
     * @throws Exception  if the xml cannot be written out to the specified file
     */
    public void toXML(String filename) throws Exception {
        if (StringUtils.isBlank(filename))
            throw new IllegalArgumentException("Filename must be included for XML output call");
        if (this.mAEDescriptor == null) throw new Exception("No Descriptor for XML output");
        //Setup the file reference where the xml will be generated
        File xmlFile = null;
        if(mDescriptorLocator != null && StringUtils.isNotBlank(mDescriptorLocator.getPath())) {
            File locator = new File(mDescriptorLocator.getPath());
            File tmpDir = (locator.isDirectory())? new File(locator.getPath()) : new File(locator.getParent());
            xmlFile = File.createTempFile(filename, ".xml", tmpDir);
        } else {
            xmlFile = File.createTempFile(filename, ".xml");
        }
        mAEDescriptor.getAnalysisEngineMetaData().setName(filename);
        mDescriptorLocator = xmlFile.toURI();
        this.toXML();
    }//toXML method filename input

    /**
     * Write the descriptor file out based on the locator information in the object.
     * If no locator information has been set then we generate a temp file.
     *

     * @throws java.io.IOException if the file cannot be written to
     * @throws org.xml.sax.SAXException if there is a problem creating the xml
     * @throws org.apache.uima.util.InvalidXMLException if the xml is invalid.
     * @throws java.net.URISyntaxException if the path specified is invalid.
     */
    public void toXML() throws IOException, SAXException, InvalidXMLException, URISyntaxException {
        /**
         * For aggregate descriptors generate each delegate xml then regenerate an
         * aggregate gov.va.vinci.leo.model based on the locations for the delegates.
         */
        if (!mAEDescriptor.isPrimitive()) {
            //Generate each delegate descriptor
            for (LeoDelegate delegate : mDelegates) {
                try {
                    if (delegate.getName() == null) {
                        delegate.setName("leoDelegate");
                    }
                    if(mDescriptorLocator != null) { delegate.setDescriptorLocator(mDescriptorLocator); }
                    delegate.setIsDeleteOnExit(this.deleteOnExit);
                    delegate.toXML(delegate.getName());
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                }
            }//for

            //Generate the aggregate from the newly generated delegates
            this.mAEDescriptor = AggregateEngineFactory.createAggregateDescription(mDelegates, this.getName());
        }//if

        //Create the output file handle base on the locator info
        File outFile = new File(mDescriptorLocator);
        if(deleteOnExit) { outFile.deleteOnExit(); }
        mDescriptorLocator = outFile.toURI();

		FileOutputStream fos=null;
		try
		{
			fos = new FileOutputStream(outFile);
			mAEDescriptor.toXML(fos, true);			
		}
		finally
		{
			if ( fos!=null ) fos.close();
		}
	}

    /**
     * Generate the XML for the analysisEngine section of the deployment descriptor.
     *
     * @param thd    the xml transform handler.
     * @param isTopDescriptor   true if this is a top level descriptor, else false.
     * @throws SAXException  if there is no descriptor to output
     * @return this Leo AE Descriptor
     */

    public LeoAEDescriptor analysisEngineSection(TransformerHandler thd, boolean isTopDescriptor) throws SAXException {
        //validate we have something to output
        if (this.mAEDescriptor == null) throw new MissingResourceException("No Descriptor available for XML output", AnalysisEngineDescription.class.getCanonicalName(), "mAEDescriptor");

        AttributesImpl atts = new AttributesImpl();
        //<analysisEngine>
        if (!isTopDescriptor)
            atts.addAttribute("", "", "key", "CDATA", mAEDescriptor.getMetaData().getName());
        if (mIsAsync) {
            atts.addAttribute("", "", "async", "CDATA", "true");
        } else {
            atts.addAttribute("", "", "async", "CDATA", "false");
        }//else

        atts.addAttribute("", "", "async", "CDATA", Boolean.toString(mIsAsync));
        thd.startElement("", "", "analysisEngine", atts);
        atts.clear();

        //<scaleout .../>
        atts.addAttribute("", "", "numberOfInstances", "CDATA", Integer.toString(mNumberOfInstances));
        thd.startElement("", "", "scaleout", atts);
        thd.endElement("", "", "scaleout");
        atts.clear();

        //Handle delegate tags for aggregate engines
        if (this.isAggregate() && mIsAsync) {
            //<delegates>
            thd.startElement("", "", "delegates", atts);

            //output all the delegate engines
            for (LeoDelegate delegate : mDelegates) {
                delegate.analysisEngineSection(thd, false);
            }//for

            //</delegates>
            thd.endElement("", "", "delegates");
        }//if


        //<asyncPrimitiveErrorConfiguration> or <asyncAggregateErrorConfiguration>
        if (isTopDescriptor)
            thd.startElement("", "", "asyncPrimitiveErrorConfiguration", atts);
        else
            thd.startElement("", "", "asyncAggregateErrorConfiguration", atts);

        //<getMetadataErrors .../>
        if (!isTopDescriptor) {
            atts.addAttribute("", "", "maxRetries", "CDATA", "0");
            atts.addAttribute("", "", "timeout", "CDATA", "0");
            atts.addAttribute("", "", "errorAction", "CDATA", "terminate");
            thd.startElement("", "", "getMetadataErrors", atts);
            thd.endElement("", "", "getMetadataErrors");
            atts.clear();
        }//if

        //<processCasErrors .../>
        atts.addAttribute("", "", "thresholdCount", "CDATA", "0");
        atts.addAttribute("", "", "thresholdWindow", "CDATA", "0");
        atts.addAttribute("", "", "thresholdAction", "CDATA", "terminate");
        thd.startElement("", "", "processCasErrors", atts);
        thd.endElement("", "", "processCasErrors");
        atts.clear();

        //<collectionProcessCompleteErrors .../>
        atts.addAttribute("", "", "timeout", "CDATA", "0");
        atts.addAttribute("", "", "additionalErrorAction", "CDATA", "terminate");
        thd.startElement("", "", "collectionProcessCompleteErrors", atts);
        thd.endElement("", "", "collectionProcessCompleteErrors");
        atts.clear();

        //</asyncPrimitiveErrorConfiguration> or <asyncAggregateErrorConfiguration>
        if (isTopDescriptor)
            thd.endElement("", "", "asyncPrimitiveErrorConfiguration");
        else
            thd.endElement("", "", "asyncAggregateErrorConfiguration");

        //</analysisEngine>
        thd.endElement("", "", "analysisEngine");
        return this;
    }//analysisEngineSection method

    /**
     * Retrieves whether the AnalysisEngine is primitive (consisting of one annotator), as opposed to aggregate (containing multiple delegate AnalysisEngines).
     *
     * @return true if this is a primitive, else false.
     */
    public boolean isPrimitive() {
        return mAEDescriptor.isPrimitive();
    }

    /**
     * Sets the name of this ResourceCreationSpecifier's implementation. This must be a fully qualified Java class name.
     *
     * @param s the implementation name of the Annotator
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setImplementationName(String s) {
        if (this.isPrimitive()) mAEDescriptor.setImplementationName(s);
        return this;
    }//setImplementationName method

    /**
     * Retrieves the name of this ResourceCreationSpecifier's implementation. This must be a fully qualified Java class name.
     *
     * @return the implementation name of the CasConsumer
     */
    public String getImplementationName() {
        return mAEDescriptor.getImplementationName();
    }

    /**
     * Sets the description of this Resource.
     *
     * @param desc the description of this Resource
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setDescription(String desc) {
        mAEDescriptor.getMetaData().setDescription(desc);
        return this;
    }

    /**
     * Gets the description of this Resource.
     *
     * @return the description of this Resource
     */
    public String getDescription() {
        return mAEDescriptor.getMetaData().getDescription();
    }

    /**
     * Sets the name of this Resource.
     *
     * @param name the name of this Resource
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setName(String name) {
        String finalName = name + "2112" + LeoUtils.getUUID();
        mAEDescriptor.getMetaData().setName(finalName);
        return this;
    }//setName method

    /**
     * Gets the name of this Resource.
     *
     * @return the name of this Resource
     */
    public String getName() {
        if(mAEDescriptor != null && StringUtils.isBlank(mAEDescriptor.getMetaData().getName())) {
            if(mAEDescriptor.isPrimitive()) {
                this.setName("leoPrimitive");
            } else {
                this.setName("leoAggregate");
            }
        }
        return (mAEDescriptor == null) ? null : mAEDescriptor.getMetaData().getName();
    }//getName method

    /**
     * Sets the version number of this Resource.
     *
     * @param version he version number of this Resource, as a String
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setVersion(String version) {
        mAEDescriptor.getMetaData().setVersion(version);
        return this;
    }

    /**
     * Sets the version number of this Resource.
     *
     * @return the version number of this Resource, as a String
     */
    public String getVersion() {
        return mAEDescriptor.getMetaData().getVersion();
    }

    /**
     * Sets whether this component will modify the CAS.
     *
     * @param aModifiesCas true if this component modifies the CAS, false if it does not.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setModifiesCas(boolean aModifiesCas) {
        ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().setModifiesCas(aModifiesCas);
        return this;
    }

    /**
     * Gets whether this component will modify the CAS.
     *
     * @return true if this component modifies the CAS, false if it does not.
     */
    public boolean getModifiesCas() {
        return ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().getModifiesCas();
    }

    /**
     * Sets whether multiple instances of this component can be run in parallel, each receiving a subset of the documents from a collection.
     *
     * @param b true if multiple instances can be run in parallel, false if not
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setMultipleDeploymentAllowed(boolean b) {
        ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().setMultipleDeploymentAllowed(b);
        return this;
    }

    /**
     * Gets whether multiple instances of this component can be run in parallel, each receiving a subset of the documents from a collection.
     *
     * @return true if multiple instances can be run in parallel, false if not
     */
    public boolean isMultipleDeploymentAllowed() {
        return ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().isMultipleDeploymentAllowed();
    }

    /**
     * Sets whether this AnalysisEngine may output new CASes. If this property is set to true, an application can use the
     * AnalysisEngine.processAndOutputNewCASes(CAS) to pass a CAS to this this AnalysisEngine and then step through all of
     * the output CASes that it produces. For example, such an AnalysisEngine could segment a CAS into smaller pieces,
     * emitting each as a separate CAS.
     *
     * @param b true if this component may output new CASes, false if it does not
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor setOutputsNewCASes(boolean b) {
        ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().setOutputsNewCASes(b);
        return this;
    }

    /**
     * Gets whether this AnalysisEngine may output new CASes. If this property is set to true, an application can use the
     * AnalysisEngine.processAndOutputNewCASes(CAS) to pass a CAS to this this AnalysisEngine and then step through all of
     * the output CASes that it produces. For example, such an AnalysisEngine could segment a CAS into smaller pieces,
     * emitting each as a separate CAS.
     *
     * @return true if this component may output new CASes, false if it does not
     */
    public boolean getOutputsNewCASes() {
        return ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getOperationalProperties().getOutputsNewCASes();
    }

    /**
     * Adds either outputs or inputs for the specified capability to this ResultSpecification.
     *
     * @param c capability to add.
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addCapability(Capability c) {
        if (((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getCapabilities() == null) {
            ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).setCapabilities(new Capability[]{c});
        } else {
            Capability[] newCapability = Arrays.copyOf(
                    ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getCapabilities(),
                    ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getCapabilities().length + 1);
            newCapability[newCapability.length - 1] = c;
            ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).setCapabilities(newCapability);
        }
        return this;
    }

    /**
     * Get the capabilities for this Analysis Engine from its metadata.
     *
     * @return the array of capabilities, or an empty array if there are no capabilities.
     */
    public Capability[] getCapabilites() {
        if (((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getCapabilities() == null) {
            return new Capability[0];
        } else {
            return ((AnalysisEngineMetaData_impl) mAEDescriptor.getMetaData()).getCapabilities();
        }
    }

    /**
     * Add the external resources specified by the provided ResourceManagerConfiguration object.
     *
     * @param rmc ResourceManagerConfiguration object with external resources to bind
     * @return this Leo AE Descriptor object.
     */
    public LeoAEDescriptor addResourceConfiguration(ResourceManagerConfiguration rmc) {
        ResourceManagerConfiguration existing_rmc = mAEDescriptor.getResourceManagerConfiguration();
        if (existing_rmc == null) {
            existing_rmc = new ResourceManagerConfiguration_impl();
            mAEDescriptor.setResourceManagerConfiguration(existing_rmc);
        }//if
        ExternalResourceBinding[] erbs = rmc.getExternalResourceBindings();
        ExternalResourceDescription[] erds = rmc.getExternalResources();
        for (int i = 0; i < erbs.length; i++) {
            existing_rmc.addExternalResource(erds[i]);
            existing_rmc.addExternalResourceBinding(erbs[i]);
        }//for
        return this;
    }//addResourceConfiguration

    /**
     * Returns TRUE if the generated descriptors will be deleted when the program exits. Default is TRUE.
     *
     * @return TRUE if descriptors will be deleted
     */
    @Override
    public boolean isDeleteOnExit() {
        return this.deleteOnExit;
    }

    /**
     * Set the flag to TRUE if generated descriptors should be deleted when the program exits, FALSE to persist them.
     * This method should never be called directly by the user.  The delete on exit flag is propagated from the Service
     * object and should be set in the <code>Service.setDeleteOnExit(true)</code> method.
     *
     * @param deleteOnExit
     * @return reference to the delegate type instance whose flag was set
     */
    @Override
    public LeoAEDescriptor setIsDeleteOnExit(boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
        //propagate the setting to delegates if there are delegates
        if(!mAEDescriptor.isPrimitive()) {
            for(LeoDelegate delegate : mDelegates) {
                delegate.setIsDeleteOnExit(deleteOnExit);
            }
        }

        return this;
    }
}//LeoAEDescriptor method
