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

import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.StreamUtils;
import gov.va.vinci.leo.tools.XMLSerializer;
import org.apache.log4j.Logger;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Store the metadata for a Deployment Descriptor.  Allow parameters to be modified as well as support
 * serializing the descriptor to XML.
 *
 * @author thomasginter
 */
public class LeoDeployDescriptor {
    /**
     * Name of this DeploymentDescriptor.
     */
    private String mName = "defaultLeoDeployDescriptor" + LeoUtils.getTimestampDateDotTime();

    /**
     * Description of this Deployment Descriptor if provided.
     */
    private String mDescription = null;

    /**
     * Version of the xslt used for this deployment descriptor.
     */
    private String mVersion = "1.0";

    /**
     * Protocol for deployment, just jms for now.
     */
    private String mProtocol = "jms";

    /**
     * Provider for deployment, just activemq for now.
     */
    private String mProvider = "activemq";

    /**
     * Number of CASes for each CAS pool.  Must have at least one, will match largest scaleout number.
     */
    private int mCasPoolSize = 1;

    /**
     * Initial heap size used for service processing of queues, default 2,000,000.
     */
    private int initialFsHeapSize = 2000000;

    /**
     * The broker URL where this service activemq deployment will live, defaults to tcp://localhost:61616 .
     */
    private String mBrokerURL = "tcp://localhost:61616";

    /**
     * The queue name used for initial communications with the service.
     */
    private String mEndpoint = "defaultLeoQueue" + LeoUtils.getTimestampDateDotTime();

    /**
     * Prefetch setting for reply queues.
     */
    private int prefetch = 0; //default is 0

    /**
     * Logger.
     */
    static Logger log = Logger.getLogger(LeoDeployDescriptor.class.getCanonicalName());

    /**
     * Top Descriptor file for the primitive or aggregate that will be used in the service.
     */
    private LeoAEDescriptor mTopDescriptor = null;

    /**
     * Constructor to start with top descriptor by at filename.
     *
     * @param filename path to import AnalysisEngineDescriptor by name or location
     * @param byName   if true import filename by name in classpath
     *                 if false import filename by location
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public LeoDeployDescriptor(String filename, boolean byName) throws IOException, InvalidXMLException {
        this.setTopDescriptor(filename, byName);
    }//constructor with filename and byName flag inputs

    /**
     * Constructor that sets top descriptor initially.
     *
     * @param faed set the Top Descriptor to the LeoAEDescriptor provided
     */
    public LeoDeployDescriptor(LeoAEDescriptor faed) {
        this.setTopDescriptor(faed);
    }//constructor with LeoAEDescriptor input

    /**
     * Validate that the essential service data has been set.
     *
     * @throws Exception  if any of the required data for this descriptor is missing.
     */
    private void validateData() throws Exception {
        if (mName == null || mName.equals("")) throw new Exception("Deployment name must be provided");
        if (mEndpoint == null || mEndpoint.equals("")) throw new Exception("Endpoint Queue must be set");
        if (mBrokerURL == null || mBrokerURL.equals("")) throw new Exception("Broker URL must be set");
        if (mTopDescriptor == null) throw new Exception("Top Descriptor must be set");
    }//validateData method

    /**
     * Set the name of the Deployment.
     *
     * @param name the name of the deployment
     */
    public LeoDeployDescriptor setName(String name) {
        this.mName = name;
        return this;
    }//setName method

    /**
     * Return the name of this Deployment.
     *
     * @return the name of this deployment
     */
    public String getName() {
        return mName;
    }//getName method

    /**
     * Set the description for this deployment descriptor.
     *
     * @param description the description of this deployment
     */
    public LeoDeployDescriptor setDescription(String description) {
        this.mDescription = description;
        return this;
    }//setDescription method

    /**
     * Get the description of this deployment descriptor.
     *
     * @return the description
     */
    public String getDescription() {
        return mDescription;
    }//getDescription method

    /**
     * Get the cas pool size for this deployment.
     *
     * @return the size of the CAS pool
     */
    public int getCasPoolSize() {
        return mCasPoolSize;
    }//getCasPoolSize method

    /**
     * Set the cas pool size for this deployment.
     *
     * @param casPoolSize the size of the CAS pool to set
     */
    public LeoDeployDescriptor setCasPoolSize(int casPoolSize) {
        this.mCasPoolSize = casPoolSize;
        return this;
    }//setCasPoolSize method

    /**
     * get the initial fs heap size for this deployment.
     *
     * @return the initialFsHeapSize
     */
    public int getInitialFsHeapSize() {
        return initialFsHeapSize;
    }//getInitialFsHeapSize method

    /**
     * set the initial fs heap size for this deployment.
     *
     * @param initialFsHeapSize the initialFsHeapSize to set
     */
    public LeoDeployDescriptor setInitialFsHeapSize(int initialFsHeapSize) {
        this.initialFsHeapSize = initialFsHeapSize;
        return this;
    }//setInitialFsHeapSize method

    /**
     * get the broker url for this deployment descriptor.
     *
     * @return the BrokerURL for the queue services
     */
    public String getBrokerURL() {
        return mBrokerURL;
    }//getBrokerURL method

    /**
     * Set the broker url for this deployment descriptor.
     *
     * @param brokerURL the BrokerURL for the queue services to set
     */
    public LeoDeployDescriptor setBrokerURL(String brokerURL) {
        this.mBrokerURL = brokerURL;
        return this;
    }//setBrokerURL method

    /**
     * Get the endpoint for this deployment descriptor.
     *
     * @return the mEndpoint
     */
    public String getEndpoint() {
        return mEndpoint;
    }//getEndpoint method

    /**
     * Set the endpoint for this deployment descriptor.
     *
     * @param endpoint the Endpoint queue name to set
     */
    public LeoDeployDescriptor setEndpoint(String endpoint) {
        this.mEndpoint = endpoint;
        return this;
    }//setEndpoint method

    /**
     * Get a reference to the top descriptor for this service.
     *
     * @return LeoAEDescriptor object representing the top descriptor
     */
    public LeoAEDescriptor getTopDescriptor() {
        return this.mTopDescriptor;
    }//getTopDescriptor method

    /**
     * Set the top descriptor for this deployment by importing the descriptor by name or value.
     *
     * @param filename descriptor to import by name or location
     * @param byName   if true import filename by name in classpath
     *                 if false import filename by location
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public LeoDeployDescriptor setTopDescriptor(String filename, boolean byName) throws IOException, InvalidXMLException {
        this.setTopDescriptor(new LeoAEDescriptor(filename, byName));
        return this;
    }//setTopDescriptor method filename input

    /**
     * Set the top descriptor for this deployment to the LeoAEDescriptor object provided.
     *
     * @param faed the LeoAEDescriptor object to set as the top Descriptor
     */
    public LeoDeployDescriptor setTopDescriptor(LeoAEDescriptor faed) {
        if (faed != null) this.mTopDescriptor = faed;
        this.mCasPoolSize = this.mTopDescriptor.getMaxNumberOfInstances();
        return this;
    }//setTopDescriptor method LeoAEDescriptor input

	/**
	 * Serialize this Deployment Descriptor to a file at the name and path provided.
	 *
	 * @param filename File name and path where the xml file will be serialized
	 * @throws Exception  if the file cannot be accessed or the xml not written.
	 */
	public void toXML(String filename) throws Exception 
	{
		FileOutputStream fos =null;
		try
		{
			fos = new FileOutputStream(filename);
			toXML(fos);
		}
		finally
		{
			if ( fos!=null ) fos.close();	
		}

	}//toXML filename input param method

    /**
     * Serialize the deployment descriptor to xml using the output stream provided.
     *
     * @param os Output stream used for serializing the xml file
     * @throws Exception  if the file cannot be accessed or the xml not written.
     */
    public void toXML(OutputStream os) throws Exception {
        //Validate the data
        this.validateData();
        //Create the serializer and associated handler for writing the xml file
        XMLSerializer xmls = new XMLSerializer(os);
        TransformerHandler thd = xmls.getTHandler();
        AttributesImpl atts = new AttributesImpl();

        //start
        thd.startDocument();

        //<analysisEngineDeploymentDescription ...>
        atts.addAttribute("", "", "xmlns", "CDATA", "http://uima.apache.org/resourceSpecifier");
        thd.startElement("", "", "analysisEngineDeploymentDescription", atts);
        atts.clear();

        //<name .../>
        thd.startElement("", "", "name", atts);
        thd.characters(mName.toCharArray(), 0, mName.length());
        thd.endElement("", "", "name");

        //<description .../>
        if (mDescription == null) mDescription = "";
        thd.startElement("", "", "description", atts);
        thd.characters(mDescription.toCharArray(), 0, mDescription.length());
        thd.endElement("", "", "description");

        //<version .../>
        thd.startElement("", "", "version", atts);
        thd.characters(mVersion.toCharArray(), 0, mVersion.length());
        thd.endElement("", "", "version");

        //<vendor/>
        thd.startElement("", "", "vendor", atts);
        thd.endElement("", "", "vendor");

        //deployment section
        this.deploymentSectionXML(thd);

		//</analysisEngineDeploymentDescription>
		thd.endElement("", "", "analysisEngineDeploymentDescription");
		atts.clear();
		thd.endDocument();
		os.flush();
		StreamUtils.safeClose(os);

	}//toXML OutputStream input param method

    /**
     * Serialize the deployment section.
     *
     * @param thd TransformerHandler that writes the xml out
     * @throws Exception   if there is no descriptor to output
     */
    protected void deploymentSectionXML(TransformerHandler thd) throws Exception {
        AttributesImpl atts = new AttributesImpl();

        //<deployment protocol= provider=>
        atts.addAttribute("", "", "protocol", "CDATA", mProtocol);
        atts.addAttribute("", "", "provider", "CDATA", mProvider);
        thd.startElement("", "", "deployment", atts);
        atts.clear();

        //<caspool .../>
        atts.addAttribute("", "", "numberOfCASes", "CDATA", Integer.toString(mCasPoolSize));
        atts.addAttribute("", "", "initialFsHeapSize", "CDATA", Integer.toString(this.initialFsHeapSize));
        thd.startElement("", "", "casPool", atts);
        thd.endElement("", "", "casPool");
        atts.clear();

        //services section
        this.serviceSectionXML(thd);

        //</deployment>
        thd.endElement("", "", "deployment");
        atts.clear();
    }//deploymentSectionXML method

    /**
     * Serialize the service section.
     *
     * @param thd TransformerHandler that writes the xml out
     * @throws Exception  if there is no descriptor to output
     */
    protected void serviceSectionXML(TransformerHandler thd) throws Exception {
        AttributesImpl atts = new AttributesImpl();

        //<service>
        thd.startElement("", "", "service", atts);

        //<inputQueue ... />
        atts.addAttribute("", "", "endpoint", "CDATA", mEndpoint);
        atts.addAttribute("", "", "brokerURL", "CDATA", mBrokerURL);
        atts.addAttribute("", "", "prefetch", "CDATA", Integer.toString(prefetch));
        thd.startElement("", "", "inputQueue", atts);
        thd.endElement("", "", "inputQueue");
        atts.clear();

        //<topDescriptor>
        thd.startElement("", "", "topDescriptor", atts);

        //<import .../>
        atts.addAttribute("", "", "location", "CDATA", mTopDescriptor.getDescriptorLocator());
        thd.startElement("", "", "import", atts);
        thd.endElement("", "", "import");

        //</topDescriptor>
        thd.endElement("", "", "topDescriptor");

        //analysisEngine section
        mTopDescriptor.analysisEngineSection(thd, true);

        //</service>
        thd.endElement("", "", "service");
        atts.clear();

    }//servicesSectionXML method

}//LeoDeployDescriptor class
