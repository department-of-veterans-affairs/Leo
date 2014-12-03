package gov.va.vinci.leo.cr;

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.TextFilter;
import gov.va.vinci.leo.types.TypeLibrarian;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
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
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
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
     * @param filterList
     */
    public ExternalFileCollectionReader(File descriptor, File inputDirectory, boolean recurse, TextFilter... filterList) {
        super(inputDirectory, recurse, filterList);
        this.descriptor = descriptor;
    }

    /**
     * Constructor that takes an input directory path and recurse flag as inputs.
     *
     * @param inputDirectory Path to the input directory to be read.
     * @param recurse        If true then recursively search sub-directories for files to process.
     * @param fileExtension  The file extension to limit to, ie = ".txt" or ".doc"
     * @param filterList
     */
    public ExternalFileCollectionReader(File descriptor, File inputDirectory, boolean recurse, SuffixFileFilter fileExtension, TextFilter... filterList) {
        super(inputDirectory, recurse, fileExtension, filterList);
        this.descriptor = descriptor;
    }

    /**
     * Constructor that takes an input directory path and recurse flag as inputs.
     *
     * @param inputDirectory Path to the input directory to be read.
     * @param encoding
     * @param recurse        If true then recursively search sub-directories for files to process.
     * @param fileExtension  The file extension to limit to, ie = ".txt" or ".doc"
     * @param filterList
     */
    public ExternalFileCollectionReader(File descriptor, File inputDirectory, String encoding, boolean recurse, SuffixFileFilter fileExtension, TextFilter... filterList) {
        super(inputDirectory, encoding, recurse, fileExtension, filterList);
        this.descriptor = descriptor;
    }

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
        try {
            crDesc.toXML(FileUtils.openOutputStream(descriptor));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UIMAFramework.produceCollectionReader(crDesc);       //creating a CollectionReader object with the new parameters set.
    }
}
