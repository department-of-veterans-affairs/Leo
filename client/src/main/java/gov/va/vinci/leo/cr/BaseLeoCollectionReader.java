/**
 *
 */
package gov.va.vinci.leo.cr;

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

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.TextFilter;
import gov.va.vinci.leo.tools.XmlFilter;
import gov.va.vinci.leo.types.TypeLibrarian;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.Constants;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.collection.impl.CollectionReaderDescription_impl;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.OperationalProperties;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;
import org.apache.uima.resource.metadata.impl.OperationalProperties_impl;

import java.io.IOException;
import java.util.*;

/**
 * Provides the CollectionReader interface object to the UIMA framework for implementations of SubReaders.
 * This interface class allows us to provide a common data information structure propagated throughout
 * every CAS object it produces.
 *
 * @author thomasginter
 */
public abstract class BaseLeoCollectionReader extends CollectionReader_ImplBase implements LeoCollectionReaderInterface {


    /**
     * List of filters to be applied to the text before adding to the CAS as document text.
     */
    protected List<TextFilter> filters = null;

    /**
     * Log file handler.
     */
    private final static Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    /**
     * This method is called during initialization, and does nothing by default. Subclasses should
     * override it to perform one-time startup logic.
     *
     * @throws org.apache.uima.resource.ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        String[] text_filters = (String[]) getConfigParameterValue(Param.TEXT_FILTERS.getName());
        this.addFilters(new XmlFilter());
        try {
            this.addFilterList(text_filters);
        } catch (ClassNotFoundException e) {
            throw new ResourceInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new ResourceInitializationException(e);
        } catch (InstantiationException e) {
            throw new ResourceInitializationException(e);
        }
    }

    /**
     * @param aCAS the CAS to populate with the next document;
     *
     * @throws org.apache.uima.collection.CollectionException if there is a problem getting the next and populating the CAS.
     */
    @Override
    public abstract void getNext(CAS aCAS) throws  CollectionException, IOException;


    /**
     * @return true if and only if there are more elements availble from this CollectionReader.
     * @throws java.io.IOException
     * @throws org.apache.uima.collection.CollectionException
     */
    @Override
    public abstract boolean hasNext() throws IOException, CollectionException;

    /**
     * Add the list of filters classes from a String list.  Typically this list is coming from the
     * Configuration Parameter input.
     *
     * @param filterList List of class names, each of which is a TextFilter
     * @throws  java.lang.ClassNotFoundException if one of the filter classes cannot be found
     * @throws java.lang.IllegalAccessException if the filter class cannot be accessed
     * @throws java.lang.InstantiationException if the filter class cannot be instantiated.
     */
    public void addFilterList(String[] filterList) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (filterList == null) {
            return;
        }
        for (String filterName : filterList) {
            if (StringUtils.isBlank(filterName))
                continue;
            Class<?> subclass;
            TextFilter tf = null;
            subclass = (Class<?>) Class.forName(filterName.trim());
            tf = (TextFilter) subclass.newInstance();
            this.addFilters(tf);

        }//for
    }//addFilterlist method

    /**
     * Add one or more text filters to the list of filters to be applied to the
     * document text before adding to the CAS.
     *
     * @param textFilters One or more filters to be added to the list
     */
    public void addFilters(TextFilter... textFilters) {
        if (textFilters == null) return;
        if (filters == null) filters = new ArrayList<TextFilter>();
        //Add each filters to the list
        filters.addAll(Arrays.asList(textFilters));
    }//setFilters method

    /**
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
     * @throws  java.io.IOException  if the underlying reader throws an IOException closing.
     */
    public void close() throws IOException
    {

    }

    /**
     * Returns a CollectionReader which has been initialized by the framework using the parameter settings provided.
     *
     * @param configurationParameters set of ConfigurationParameters to add to the descriptor.
     * @param parameterValues Map of parameter names to values to set in the descriptor.
     * @return initialized CollectionReader object.
     * @throws ResourceInitializationException if there is an error initializing the CollectionReader.
     */
    protected CollectionReader produceCollectionReader( Set<ConfigurationParameter> configurationParameters, Map<String, ?> parameterValues) throws ResourceInitializationException {
        //creating a blank CollectionReaderDescription object
        CollectionReaderDescription crDesc = new CollectionReaderDescription_impl();
        //setting the meta-data for the SuperReaderDescription
        crDesc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
        String implementationName= this.getClass().getCanonicalName();
        crDesc.setImplementationName(implementationName);
        ProcessingResourceMetaData md = crDesc.getCollectionReaderMetaData();
        md.setName(this.getClass().getCanonicalName());
        md.setDescription("Descriptor for " + implementationName + " collection reader");
        md.setVersion("1.0");

        LeoTypeSystemDescription desc = new LeoTypeSystemDescription(TypeLibrarian.getCSITypeSystemDescription());
        md.setTypeSystem(desc.getTypeSystemDescription());

        md.getConfigurationParameterDeclarations().setConfigurationParameters(configurationParameters.toArray(new ConfigurationParameter[configurationParameters.size()]));

        //setting the OperationalProperties
        OperationalProperties opProps = new OperationalProperties_impl();
        opProps.setModifiesCas(true);
        opProps.setMultipleDeploymentAllowed(false);
        opProps.setOutputsNewCASes(true);

        md.setOperationalProperties(opProps);
        ConfigurationParameterSettings confSettings = crDesc.getMetaData().getConfigurationParameterSettings();

        for (String key : parameterValues.keySet()) {
            confSettings.setParameterValue(key, parameterValues.get(key));

        }

        //resetting the parameters in the description
        crDesc.getMetaData().getConfigurationParameterSettings().setParameterSettings(confSettings.getParameterSettings());
        return UIMAFramework.produceCollectionReader(crDesc);       //creating a CollectionReader object with the new parameters set.
    }

    /**
     * Static inner class for holding parameter information.
     */
    public static class Param {
        /**
         * Text filters to apply to reader text.
         */
        public static ConfigurationParameter TEXT_FILTERS =
                new ConfigurationParameterImpl("TextFilters", "Text filters to apply to the text",
                        ConfigurationParameter.TYPE_STRING, false, true, new String[]{});
    }

}//SuperReader class
