package gov.va.vinci.leo.descriptors;

/*
 * #%L
 * Leo Core
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
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.impl.Parameter_impl;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import javax.xml.transform.sax.TransformerHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.MissingResourceException;

/**
 * Create and modify remote service analysis engine descriptors in order to include remote services in an aggregate.
 *
 * User: Thomas Ginter
 * Date: 10/30/13
 * Time: 2:51 PM
 */
public class LeoRemoteAEDescriptor implements LeoDelegate {
    /**
     * Location of the XML representation of this descriptor.
     */
    protected URI mDescriptorLocator = null;
    /**
     * Resource specifier reference object.
     */
    protected CustomResourceSpecifier mRemoteDescriptor = null;
    /**
     * Parameter object where the name is stored.
     */
    protected Parameter name = null;
    /**
     * If TRUE then delete the generated descriptor file once the program exits, defaults to TRUE.
     */
    protected boolean deleteOnExit = true;

    /**
     * Default constructor creates a blank remote service descriptor.
     *
     * @throws Exception
     */
    public LeoRemoteAEDescriptor() {
        mRemoteDescriptor = RemoteEngineFactory.generateRemoteAEDescriptor();
    }

    /**
     * Initialize with the CustomResourceSpecifier that represents a remote service.
     *
     * @param crs CustomResourceSpecifier object representing the remote service.
     */
    public LeoRemoteAEDescriptor(CustomResourceSpecifier crs) {
        if(crs == null) {
            throw new IllegalArgumentException("Missing required CustomResourceSpecifieer parameter!");
        }
        if(!crs.getResourceClassName().endsWith("JmsAnalysisEngineServiceAdapter")) {
            throw new IllegalArgumentException("Only JmsAnalysisEngineServiceAdapter CustomResourceSpecifier descriptors permitted for LeoRemoteAEDescriptor");
        }
        mRemoteDescriptor = crs;
    }

    /**
     * Import an XML descriptor and use it as the model.
     *
     * @param descriptor  String path to the XML descriptor file
     * @param byName      if true then import the descriptor by name rather than a literal path
     * @param paramValues Optional parameter values to set after initializing
     * @throws IOException if there is an error reading the file at the descriptor path
     * @throws InvalidXMLException if there is an error parsing the xml of the descriptor
     */
    public LeoRemoteAEDescriptor(String descriptor, boolean byName, NameValue... paramValues) throws IOException, InvalidXMLException {
        this(RemoteEngineFactory.generateRemoteAEDescriptor(descriptor, byName));
        if(paramValues != null) {
            for(NameValue nv : paramValues) {
                this.setParameterSetting(nv);
            }
        }
    }

    /**
     * Create a remote descriptor with the BrokerURL and Endpoint parameters completed.
     *
     * @param brokerURL broker URL to connect.
     * @param endPoint name of the input queue.
     */
    public LeoRemoteAEDescriptor(String brokerURL, String endPoint) {
        this();
        this.addParameterSetting(Param.BROKER_URL.getName(), brokerURL);
        this.addParameterSetting(Param.ENDPOINT.getName(), endPoint);
    }

    /**
     * @return reference to the CustomResourceSpecifier object representing this remote service
     */
    public CustomResourceSpecifier getRemoteDescriptor() {
        return this.mRemoteDescriptor;
    }

    /**
     * Return a reference to the internal ResourceSpecifier that is the backend model for the delegate.  AnalysisEngines
     * will have a ResourceSpecifier that is an instance of AnalysisEngineDescription whereas remote delegates have a
     * ResourceSpecifier that is a CustomResourceSpecifier.
     *
     * @return ResourceSpecifier object for this delegate
     */
    @Override
    public ResourceSpecifier getResourceSpecifier() {
        return this.getRemoteDescriptor();
    }

    /**
     * Set the location where the descriptor should be generated.  If a file name is specified it will be ignored and the
     * parent directory path will be used instead.
     *
     * @param descriptorLocator URI path where the descriptors should be located
     * @return reference to this LeoRemoteAEDescriptor instance
     */
    @Override
    public LeoRemoteAEDescriptor setDescriptorLocator(URI descriptorLocator) {
        this.mDescriptorLocator = descriptorLocator;
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
    }

    /**
     * Get the name of this delegate or null if no name has been set.
     *
     * @return String representing the name of this delegate
     */
    @Override
    public String getName() {
        if(name == null) {
            name = this.getParameterSetting("componentName");   //Try getting the name from the descriptor
            if(name == null) {   //No name was set
                return null;
            }
        }
        return name.getValue();
    }

    /**
     * Set the name of this remote service, return a reference to the delegate type object in each implementation.
     *
     * @param name String name of the remote service
     * @return reference to the delegate type whose name is set
     */
    @Override
    public LeoRemoteAEDescriptor setName(String name) {
        if(mRemoteDescriptor == null) {
            throw new MissingResourceException("Error, cannot set name: Missing Remote Descriptor", CustomResourceSpecifier.class.getCanonicalName(), "mRemoteDescriptor");
        }
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name to set cannot be blank!");
        }
        name = name + "2112" + LeoUtils.getUUID();
        if(this.name == null) {
            this.addParameterSetting("componentName", name);
        } else {
            this.name.setValue(name);
            this.setParameterSetting(this.name);
        }
        return this;
    }

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
     *
     * @param deleteOnExit
     * @return reference to the delegate type instance whose flag was set
     */
    @Override
    public LeoRemoteAEDescriptor setIsDeleteOnExit(boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
        return this;
    }

    /**
     * Set the remote parameter setting.  Parameter names can be found in the gov.va.vinci.leo.LeoRemoteAEDescriptor.Param
     * class.
     *
     * @param name  name of the parameter to set
     * @param value String value to which the parameter will be set
     * @return Reference to the current LeoRemoteAEDescriptor to support the builder pattern
     */
    public LeoRemoteAEDescriptor setParameterSetting(String name, String value) {
        Parameter p = getParameterSetting(name);
        if(p == null) {
            throw new IllegalArgumentException("Parameter " + name + " not found in the descriptor!");
        }
        p.setValue(value);

        return this;
    }

    /**
     * Set the remote service parameter from the NameValue pair information provided.
     *
     * @param nameValue NameValue object used to store the parameter name and value
     * @return Reference to the current LeoRemoteAEDescriptor to support the builder pattern
     */
    public LeoRemoteAEDescriptor setParameterSetting(NameValue nameValue) {
        return this.setParameterSetting(nameValue.getName(), nameValue.getValue().toString());
    }

    /**
     * Set the remote service parameter from the Parameter name and value information.
     *
     * @param p Parameter object storing the name and value strings to be set
     * @return Reference to the current LeoRemoteAEDescriptor to support the builder pattern
     */
    public LeoRemoteAEDescriptor setParameterSetting(Parameter p) {
        return this.setParameterSetting(p.getName(), p.getValue());
    }

    /**
     * Add a new parameter to the descriptor.  If the parameter name already exists in the descriptor then
     * the existing parameter is set to the value provided.
     *
     * @param name  name of the parameter to add to the descriptor
     * @param value string value of the new parameter
     * @return Reference to the current LeoRemoteAEDescriptor to support the builder pattern
     */
    public LeoRemoteAEDescriptor addParameterSetting(String name, String value) {
        Parameter p = getParameterSetting(name);
        if(p == null) {
            p = new Parameter_impl(name, value);
            Parameter[] plist = mRemoteDescriptor.getParameters();
            Parameter[] nplist = new Parameter[plist.length + 1];
            System.arraycopy(plist, 0, nplist, 0, plist.length);
            nplist[nplist.length - 1] = p;
            mRemoteDescriptor.setParameters(nplist);
        } else {
            p.setValue(value);
        }
        if(name.equals("componentName")) {
            this.name = p;
        }
        return this;
    }

    /**
     * Get the Parameter object from the descriptor with the name provided or null if the
     * parameter is not found in the descriptor.
     *
     * @param name string name of the parameter to retrieve
     * @return reference to the Parameter object with the name provided, null if not found or the name is blank
     */
    protected Parameter getParameterSetting(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        Parameter[] plist = mRemoteDescriptor.getParameters();
        for(Parameter p : plist) {
            if(p.getName().equals(name))
                return p;
        }
        return null;
    }

    /**
     * Get the value of a parameter in the descriptor, returns null if the name is not found in the parameter list.
     *
     * @param name name of the parameter whose value will be returned
     * @return String representation of the value of the parameter
     */
    protected String getParameterValue(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        Parameter p = this.getParameterSetting(name);
        if(p == null) {
            return null;
        }
        return p.getValue();
    }

    @Override
    public void toXML() throws Exception {
        File outFile = (mDescriptorLocator == null) ? File.createTempFile("LeoRemoteAEDescriptor", ".xml")
                            : new File(mDescriptorLocator);
        if(deleteOnExit) { outFile.deleteOnExit(); }
        mDescriptorLocator = outFile.toURI();
        FileOutputStream fos=null;
        try
        {
        	fos = new FileOutputStream(outFile);
        	mRemoteDescriptor.toXML(fos);
        }
        finally
        {
        	if ( fos!=null ) fos.close();
        }
        	
    }

    @Override
    public void toXML(String filename) throws Exception {
        if(StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("Filename is a required parameter to generate the XML file");
        }
        File xmlFile = null;
        if(mDescriptorLocator != null && StringUtils.isNotBlank(mDescriptorLocator.getPath())) {
            File locator = new File(mDescriptorLocator.getPath());
            File tmpDir = (locator.isDirectory())? new File(locator.getPath()) : new File(locator.getParent());
            xmlFile = File.createTempFile(filename, ".xml", tmpDir);
        } else {
            xmlFile = File.createTempFile(filename, ".xml");
        }
        mDescriptorLocator = xmlFile.toURI();
        this.toXML();
    }

    /**
     * Produce the Analysis Engine section for the Deployment Descriptor XML.
     *
     * @param thd             TransformerHandler for SAX XML output
     * @param isTopDescriptor if true then this is the top descriptor in the remote deployment
     * @return Reference to the delegate type object from which the analysisEngineSection was generated
     * @throws org.xml.sax.SAXException If there is an error serializing the XML
     */
    @Override
    public LeoRemoteAEDescriptor analysisEngineSection(TransformerHandler thd, boolean isTopDescriptor) throws SAXException {
        return this;
    }

    /**
     * Default params for remote service descriptors.  The Params are as follows:
     *
     * <pre>
     * LeoRemoteAEDescriptor.Param.BROKER_URL - Required;
     * LeoRemoteAEDescriptor.Param.ENDPOINT - Required;
     * LeoRemoteAEDescriptor.Param.TIMEOUT - Optional;
     * LeoRemoteAEDescriptor.Param.GET_META_TIMEOUT - Optional;
     * LeoRemoteAEDescriptor.Param.CPC_TIMEOUT - Optional;
     * </pre>
     */
    public static class Param {
        /**
         * Broker URL where the remote service is being managed.
         */
        public static ConfigurationParameter BROKER_URL       = new ConfigurationParameterImpl("brokerURL", "Broker Url", "String", true, false, new String[] {});
        /**
         * Input Queue for the remote service.
         */
        public static ConfigurationParameter ENDPOINT       = new ConfigurationParameterImpl("endpoint", "endpoint", "String", true, false, new String[] {});
        /**
         * Process CAS timeout in ms.  Default = no timeout.
         */
        public static ConfigurationParameter TIMEOUT       = new ConfigurationParameterImpl("timeout", "timeout", "String", false, false, new String[] {});
        /**
         * Initialize timeout in ms. Default = 60 seconds.
         */
        public static ConfigurationParameter GET_META_TIMEOUT       = new ConfigurationParameterImpl("getmetatimeout", "getmetatimeout", "String", false, false, new String[] {});
        /**
         * Collection Process Complete timeout. Default = no timeout.
         */
        public static ConfigurationParameter CPC_TIMEOUT       = new ConfigurationParameterImpl("cpctimeout", "cpctimeout", "String", false, false, new String[] {});
    }
}
