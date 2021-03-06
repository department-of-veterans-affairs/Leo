package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import gov.va.vinci.leo.descriptors.ConfigurationParameterUtils;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.types.TypeLibrarian;
import org.apache.commons.io.FileUtils;
import org.apache.uima.Constants;
import org.apache.uima.UIMAFramework;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.impl.CollectionReaderDescription_impl;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.OperationalProperties;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;
import org.apache.uima.resource.metadata.impl.OperationalProperties_impl;

import java.io.File;
import java.util.Map;

/**
 * This is a file reader that generates a descriptor file for testing importing external readers from descriptors.
 *
 * User: Thomas Ginter
 * Date: 10/31/14
 * Time: 13:03
 */
public class ExternalFileCollectionReader extends FileCollectionReader {

    File descriptor = null;

    /**
     * Default constructor used during UIMA initialization
     */
    public ExternalFileCollectionReader() {
    }

    /**
     * Constructor that takes an input directory path and recurse flag as inputs.
     *
     * @param inputDirectory Path to the input directory to be read.
     * @param recurse        If true then recursively search sub-directories for files to process.
     */
    public ExternalFileCollectionReader(File descriptor, File inputDirectory, boolean recurse) {
        super(inputDirectory, recurse);
        this.descriptor = descriptor;
    }

    @Override
    public CollectionReader produceCollectionReader() throws ResourceInitializationException {
        CollectionReaderDescription crDesc = new CollectionReaderDescription_impl();

        //Set the initial metadata for the descriptor
        crDesc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
        crDesc.setImplementationName(this.getClass().getCanonicalName());
        ProcessingResourceMetaData md = crDesc.getCollectionReaderMetaData();
        md.setName(this.getClass().getCanonicalName());
        md.setDescription("Descriptor for " + this.getClass().getCanonicalName() + " collection reader");
        md.setVersion("1.0");
        md.setTypeSystem(new LeoTypeSystemDescription(TypeLibrarian.getCSITypeSystemDescription()).getTypeSystemDescription());

        //setting the OperationalProperties
        OperationalProperties opProps = new OperationalProperties_impl();
        opProps.setModifiesCas(true);
        opProps.setMultipleDeploymentAllowed(false);
        opProps.setOutputsNewCASes(true);
        md.setOperationalProperties(opProps);

        //Get the configuration parameters map
        Map<ConfigurationParameterImpl, ?> configMap = ConfigurationParameterUtils.getParamsToValuesMap(this);

        //Add the configuration parameter settings
        md.getConfigurationParameterDeclarations().setConfigurationParameters(configMap.keySet().toArray(new ConfigurationParameter[configMap.size()]));

        //Set the parameter values
        ConfigurationParameterSettings confSettings = crDesc.getMetaData().getConfigurationParameterSettings();
        for(ConfigurationParameterImpl parameter : configMap.keySet()) {
            if(configMap.get(parameter) != null)
                confSettings.setParameterValue(parameter.getName(), configMap.get(parameter));
        }
        crDesc.getMetaData().getConfigurationParameterSettings().setParameterSettings(confSettings.getParameterSettings());

        //Write the descriptor out to a file
        if(descriptor != null) {
            try {
                crDesc.toXML(FileUtils.openOutputStream(descriptor));
            } catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }

        //Return the generated CollectionReader
        return UIMAFramework.produceCollectionReader(crDesc);
    }

}
