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

import org.apache.uima.resource.ResourceSpecifier;
import org.xml.sax.SAXException;

import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.net.URI;

/**
 * Common API interface for Remote and Local Analysis Engine Delegates.
 *
 * User: Thomas Ginter
 * Date: 10/30/13
 * Time: 1:29 PM
 */
public interface LeoDelegate {

    /**
     * Return a reference to the internal ResourceSpecifier that is the backend model for the delegate.  AnalysisEngines
     * will have a ResourceSpecifier that is an instance of AnalysisEngineDescription whereas remote delegates have a
     * ResourceSpecifier that is a CustomResourceSpecifier.
     *
     * @return ResourceSpecifier object for this delegate
     */
    public ResourceSpecifier getResourceSpecifier();

    /**
     * Set the location where the descriptor should be generated.  If a file name is specified it will be ignored and the
     * parent directory path will be used instead.
     *
     * @param descriptorLocator URI path where the descriptors should be located
     * @param <T> reference to this LeoAEDescriptor object instance for builder pattern
     * @return reference to the delegate type whose name is set
     */
    public <T extends LeoDelegate> T setDescriptorLocator(URI descriptorLocator);

    /**
     * @return Descriptor locator for the AnalysisEngineDescriptor xml file
     *         represented by this LeoAEDescriptor object or null if the locator is not set.
     */
    public String getDescriptorLocator();

    /**
     * Get the name of this delegate.
     *
     * @return String representing the name of this delegate
     */
    public String getName();

    /**
     * Set the name of this delegate, return a reference to the delegate type object in each implementation.
     *
     * @param name String name of the delegate to set
     * @param <T> delegate return type
     * @return reference to the delegate type whose name is set
     * @throws java.io.IOException if there is an error setting the name of the delegate descriptor
     */
    public <T extends LeoDelegate> T setName(String name) throws IOException;

    /**
     * Returns TRUE if the generated descriptors will be deleted when the program exits. Default is TRUE.
     *
     * @return TRUE if descriptors will be deleted
     */
    public boolean isDeleteOnExit();

    /**
     * Set the flag to TRUE if generated descriptors should be deleted when the program exits, FALSE to persist them.
     *
     * @param <T> Generic type template for extending classes.
     * @param deleteOnExit true if the descriptors should auto delete
     * @return reference to the delegate type instance whose flag was set
     */
    public <T extends LeoDelegate> T setIsDeleteOnExit(boolean deleteOnExit);

    /**
     * Write the descriptor file out based on the locator information in the object.  If there is no locator information
     * then a temp file is generated and the location stored.
     *
     * @throws Exception if there is an error serializing the XML
     */
    public void toXML() throws Exception;

    /**
     * Write the descriptor out to a temp XML file with the name provided as the base filename.
     *
     * @param filename
     *      Base filename for the XML file
     * @throws Exception if there is an error serializing the XML
     */
    public void toXML(String filename) throws Exception;

    /**
     * Produce the Analysis Engine section for the Deployment Descriptor XML.
     *
     * @param <T> Generic type template for extending classes.
     * @param thd TransformerHandler for SAX XML output
     * @param isTopDescriptor if true then this is the top descriptor in the remote deployment
     * @return Reference to the delegate type object from which the analysisEngineSection was generated
     * @throws SAXException If there is an error serializing the XML
     */
    public <T extends LeoDelegate> T analysisEngineSection(TransformerHandler thd, boolean isTopDescriptor) throws SAXException;
}
