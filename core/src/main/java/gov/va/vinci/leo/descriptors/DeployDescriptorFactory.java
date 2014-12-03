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

import gov.va.vinci.leo.tools.XMLSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Map;

/**
 * Generate a Deployment Descriptor. Accepts a fileName, name for deployment desc, description, and a descriptor Map containing:
 * <ul>
 * <li>Name</li>
 * <li>description (optional)</li>
 * <li>Endpoint</li>
 * <li>brokerURL</li>
 * <li>topDescriptor (full or relative path)</li>
 * <li>numberOfCASes (Optional, CAS Pool Size)</li>
 * <li>initialFsHeapSize (Optional)</li>
 * <li>numberOfInstances (Optional, Number of top AE's to deploy)</li>
 * </ul>
 *
 * @author thomasginter
 */
public class DeployDescriptorFactory {
    /**
     * Serializer object that manages creating the appropriate transformer and handler
     * objects for serializing the XML using SAX and JAXP.
     */
    private XMLSerializer xmls = null;

    /**
     * TransformerHandler that does the serializing to a file.
     */
    private TransformerHandler thd = null;

    /**
     * Descriptor Map. Can contain:
     * <ul>
     * <li>name</li>
     * <li>description (optional)</li>
     * <li>endpoint</li>
     * <li>brokerURL</li>
     * <li>topDescriptor (full or relative path)</li>
     * <li>numberOfCASes (Optional, CAS Pool Size)</li>
     * <li>initialFsHeapSize (Optional)</li>
     * <li>numberOfInstances (Optional, Number of top AE's to deploy)</li>
     * </ul>
     */
    private Map<String, String> mDescMap = null;

    /**
     * Constructor for DeployDescriptorFactory to serialize a deploymentDescriptor based on the
     * settings contained in the descriptorMap provided.
     *
     * @param fileName the filename to output to.
     * @param descMap  map containing key/values for the parameters to this descriptor.
     * <ul>
     *      <li>Name</li>
     *      <li>description (optional)</li>
     *      <li>Endpoint</li>
     *      <li>brokerURL</li>
     *      <li>topDescriptor (full or relative path)</li>
     *      <li>numberOfCASes (Optional, CAS Pool Size)</li>
     *      <li>initialFsHeapSize (Optional)</li>
     *      <li>numberOfInstances (Optional, Number of top AE's to deploy)</li>
     * </ul>
     *
     * @throws Exception if file cannot be created or name is invalid or if required data
     *                   is not provided.
     */
    public DeployDescriptorFactory(String fileName, Map<String, String> descMap) throws Exception {
        xmls = new XMLSerializer(fileName);
        validateMap(descMap);
        mDescMap = descMap;
        thd = xmls.getTHandler();
    }//default constructor

    /**
     * Validate that the descriptorMap contains all the required data.
     *
     * @param descMap  map containing key/values for the parameters to this descriptor.
     * <ul>
     *      <li>Name</li>
     *      <li>description (optional)</li>
     *      <li>Endpoint</li>
     *      <li>brokerURL</li>
     *      <li>topDescriptor (full or relative path)</li>
     *      <li>numberOfCASes (Optional, CAS Pool Size)</li>
     *      <li>initialFsHeapSize (Optional)</li>
     *      <li>numberOfInstances (Optional, Number of top AE's to deploy)</li>
     * </ul>
     * @throws Exception   if any required fields are missing.
     */
    private void validateMap(Map<String, String> descMap) throws Exception {
        if (descMap == null) throw new Exception("Descriptor Map cannot be NULL");
        String exception = "Missing map elements:";
        int numMissing = 0;

        if (!descMap.containsKey("name")) {
            numMissing++;
            exception += " Name";
        }//if

        if (!descMap.containsKey("endpoint")) {
            numMissing++;
            if (numMissing > 1) exception += ",";
            exception += "endpoint";
        }//if

        if (!descMap.containsKey("brokerURL")) {
            numMissing++;
            if (numMissing > 1) exception += ",";
            exception += "brokerURL";
        }//if

        if (!descMap.containsKey("topDescriptor")) {
            numMissing++;
            if (numMissing > 1) exception += ",";
            exception += "topDescriptor";
        }//if

        if (numMissing > 0) throw new Exception(exception);
    }//validate Map

    /**
     * Create a deployment descriptor file from the map data.
     *
     * @throws org.xml.sax.SAXException if there is an error creating the XML document.
     */
    public void serialize() throws SAXException {
        //start the document
        start();

        //start the deployment and service sections
        deployment();

        //end the document
        end();
    }//serialize method

    /**
     * Start creating the deployment descriptor.
     * @throws SAXException if there is a SAX parser error.
     */
    private void start() throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        thd.startDocument();
        atts.addAttribute("", "", "xmlns", "CDATA", "http://uima.apache.org/resourceSpecifier");
        thd.startElement("", "", "analysisEngineDeploymentDescription", atts);
        atts.clear();
        thd.startElement("", "", "name", atts);
        String name = (String) mDescMap.get("name");
        thd.characters(name.toCharArray(), 0, name.length());
        thd.endElement("", "", "name");
        String desc = (mDescMap.containsKey("description")) ? (String) mDescMap.get("description") : "";
        thd.startElement("", "", "description", atts);
        thd.characters(desc.toCharArray(), 0, desc.length());
        thd.endElement("", "", "description");
        thd.startElement("", "", "version", atts);
        thd.endElement("", "", "version");
        thd.startElement("", "", "vendor", atts);
        thd.endElement("", "", "vendor");
    }//start method

    /**
     * Write out the deployment descriptor.
     * @throws SAXException if there is a SAX parser error.
     */
    private void deployment() throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        //start deployment tag
        atts.addAttribute("", "", "protocol", "CDATA", "jms");
        atts.addAttribute("", "", "provider", "CDATA", "activemq");
        thd.startElement("", "", "deployment", atts);
        atts.clear();

        //Write the casPool tag
        String numberOfCASes = (mDescMap.containsKey("numberOfCASes")) ? (String) mDescMap.get("numberOfCASes") : "1";
        String initialFsHeapSize = (mDescMap.containsKey("initialFsHeapSize")) ? (String) mDescMap.get("initialFsHeapSize") : "2000000";
        atts.addAttribute("", "", "numberOfCASes", "CDATA", numberOfCASes);
        atts.addAttribute("", "", "initialFsHeapSize", "CDATA", initialFsHeapSize);
        thd.startElement("", "", "casPool", atts);
        thd.endElement("", "", "casPool");
        atts.clear();

        //start service section
        thd.startElement("", "", "service", atts);

        //create the inputQueue tag
        atts.addAttribute("", "", "endpoint", "CDATA", (String) mDescMap.get("endpoint"));
        atts.addAttribute("", "", "brokerURL", "CDATA", (String) mDescMap.get("brokerURL"));
        atts.addAttribute("", "", "prefetch", "CDATA", "0");
        thd.startElement("", "", "inputQueue", atts);
        thd.endElement("", "", "inputQueue");
        atts.clear();

        //topDescriptor tag
        thd.startElement("", "", "topDescriptor", atts);
        atts.addAttribute("", "", "location", "CDATA", (String) mDescMap.get("topDescriptor"));
        thd.startElement("", "", "import", atts);
        thd.endElement("", "", "import");
        thd.endElement("", "", "topDescriptor");

        //analysisEngine section
        analysisEngine();

        //end service section
        thd.endElement("", "", "service");

        //end deployment tag
        thd.endElement("", "", "deployment");
    }//deployment method

    /**
     * Write the AnalysisEngine section.
     * @throws SAXException if there is a SAX parser error.
     */
    private void analysisEngine() throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        //start analysisEngine section
        atts.addAttribute("", "", "async", "CDATA", "false");
        thd.startElement("", "", "analysisEngine", atts);
        atts.clear();

        //write scaleout tag
        String numberOfInstances = (mDescMap.containsKey("numberOfInstances")) ? (String) mDescMap.get("numberOfInstances") : "1";
        atts.addAttribute("", "", "numberOfInstances", "CDATA", numberOfInstances);
        thd.startElement("", "", "scaleout", atts);
        thd.endElement("", "", "scaleout");
        atts.clear();

        //write asyncPrimitiveErrorConfiguration section
        thd.startElement("", "", "asyncPrimitiveErrorConfiguration", atts);
        //processCasErrors tag
        atts.addAttribute("", "", "thresholdCount", "CDATA", "0");
        atts.addAttribute("", "", "thresholdWindow", "CDATA", "0");
        atts.addAttribute("", "", "thresholdAction", "CDATA", "terminate");
        thd.startElement("", "", "processCasErrors", atts);
        thd.endElement("", "", "processCasErrors");
        atts.clear();
        //collectionProcessCompleteErrors tag
        atts.addAttribute("", "", "timeout", "CDATA", "0");
        atts.addAttribute("", "", "additionalErrorAction", "CDATA", "terminate");
        thd.startElement("", "", "collectionProcessCompleteErrors", atts);
        thd.endElement("", "", "collectionProcessCompleteErrors");
        atts.clear();
        thd.endElement("", "", "asyncPrimitiveErrorConfiguration");

        //end analysisEngine section
        thd.endElement("", "", "analysisEngine");
    }//analysisEngine method

    /**
     * End the document.
     * @throws SAXException if there is a SAX parser error.
     */
    private void end() throws SAXException {
        thd.endElement("", "", "analysisEngineDeploymentDescription");
        thd.endDocument();
    }//end method

}//DeployDescriptorFactory class
