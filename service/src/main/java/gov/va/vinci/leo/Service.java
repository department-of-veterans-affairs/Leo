/**
 *
 */
package gov.va.vinci.leo;

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

import gov.va.vinci.leo.ae.LeoAnnotator;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoDelegate;
import gov.va.vinci.leo.descriptors.LeoDeployDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.JamService;
import gov.va.vinci.leo.tools.LeoProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;

import java.io.File;
import java.net.InetAddress;
import java.util.*;

/**
 * This object will deploy a UIMA AS server from the list of primitive descriptor files
 * provided by the caller.  Any modules that will be executed must be in the classpath
 * of the Service process, remote modules being the exception once those are implemented.
 * Optionally the user can edit the standard properties file to modify the default settings
 * for the UIMA pipeline execution.
 *
 * @author thomasginter
 */
public class Service extends LeoProperties {
    /**
     * Logger for this class.
     */
    public static final Logger LOGGER = Logger.getLogger(Service.class.getCanonicalName());

    /**
     * Application execution map used primarily by the client.
     */
    protected Map<String, Object> mAppCtx = null;

    /**
     * UIMA Asynchronous Engine object used to deploy the server and later to
     * init and run the client side of the service also.
     */
    protected UimaAsynchronousEngine mUAEngine = null;

    /**
     * Default constructor loads the application execution variables from the properties file.
     * If variables are not provided then default values are used where they can be.
     *
     * @throws Exception if properties file cannot be read or if data is invalid
     */
    public Service() throws Exception {
        this.loadDefaults();
    }//Default constructor

    /**
     * Constructor which first sets a new properties file then calls the default constructor
     * to load the properties.
     *
     * @param propertiesFile path to a properties file to be used
     * @throws java.lang.Exception if there is a problem loading the properties.
     */
    public Service(String propertiesFile) throws Exception {
        this();
        this.loadprops(propertiesFile);
    }//Constructor with popertiesFile input

    /**
     * Get the aggregate descriptor file path.
     *
     * @return the mAggregateDescriptorFile
     */
    public String getAggregateDescriptorFile() {
        return this.mAggregateDescriptorFile;
    }//getAggregateDescriptorFile method

    /**
     * Create a deployment descriptor and initialize the mAppCtx and mUAEngine objects then call
     * deploy to deploy the service.
     *
     * @param primitives ArrayList<String> of primitive Analysis Engine descriptors
     * @param byName if true, it is assumed the list of primitives exists in the classpath. If false, full
     *          paths to the primitives is required.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(ArrayList<String> primitives, boolean byName) throws Exception {
        ArrayList<LeoAEDescriptor> annotators = new ArrayList<LeoAEDescriptor>();
        for (String delegate : primitives) {
            annotators.add(new LeoAEDescriptor(delegate, byName));
        }//for

        deploy(annotators);
    }//init(ArrayList<String> primitives) method

    /**
     * Initialize a service pipeline by first generating an aggregate descriptor from a list of
     * LeoAEDescriptor objects.
     *
     * @param primitives a list of primitive components to deploy as a service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(List<LeoAEDescriptor> primitives) throws Exception {
        //Create Aggregate Engine Descriptor
        LeoAEDescriptor aggregate = new LeoAEDescriptor(primitives);

        deploy(aggregate);
    }//init(List<LeoAEDescriptor>)

    /**
     * Initialize a pipeline by generating the aggregate descriptor file from the list of LeoAnnotator objects then
     * deploy it in the UIMA-AS framework.
     *
     * @param annotators one or more annotators to deploy as a pipeline in the service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(LeoAnnotator...annotators) throws Exception {
        deploy(new LeoTypeSystemDescription(), annotators);
    }

    /**
     * Initialize a pipeline by generating the aggregate descriptor file from the list of LeoAnnotator objects then
     * deploy it in the UIMA-AS framework.
     *
     * @param leoTypeSystemDescription the type system to add to each delegate.
     * @param annotators one or more annotators to deploy as a pipeline in the service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(LeoTypeSystemDescription leoTypeSystemDescription, LeoAnnotator...annotators) throws Exception {
        deploy(leoTypeSystemDescription, LeoAEDescriptor.DEFAULT_NUMBER_INSTANCES, annotators);
    }

    /**
     * Initialize a pipeline by generating the aggregate descriptor file from the list of LeoAnnotator objects then
     * deploy it in the UIMA-AS framework.
     *
     * @param leoTypeSystemDescription the type system to add to each delegate.
     * @param numInstances number of pipeline instances to deploy.
     * @param annotators one or more annotators to deploy as a pipeline in the service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(LeoTypeSystemDescription leoTypeSystemDescription, int numInstances, LeoAnnotator...annotators) throws Exception {
        deploy(Arrays.asList(annotators), numInstances, leoTypeSystemDescription);
    }

    /**
     * Initialize a pipeline by generating the aggregate descriptor file from the list of LeoAnnotator objects then
     * deploy it in the UIMA-AS framework.
     *
     * @param annotators list of annotators to deploy as a pipeline in the service.
     * @param numInstances number of pipeline instances to deploy.
     * @param leoTypeSystemDescription the type system to add to each delegate.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(List<LeoAnnotator> annotators, int numInstances, LeoTypeSystemDescription leoTypeSystemDescription) throws Exception {
        if(annotators == null) {
            throw new IllegalArgumentException("List of Annotators for the Service cannot be NULL!");
        }
        if(leoTypeSystemDescription == null) {
            leoTypeSystemDescription = new LeoTypeSystemDescription();
        }
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        //Grab the default type systems from the annotators.
        for(LeoAnnotator annotator : annotators) {
            if(annotator instanceof  LeoBaseAnnotator) {
                leoTypeSystemDescription.addTypeSystemDescription(((LeoBaseAnnotator) annotator).getLeoTypeSystemDescription());
            }
        }
        //Set the type system in each annotator and add it as a delegate.
        for(LeoAnnotator annotator : annotators) {
            LeoDelegate descriptor = annotator.getDescriptor();
            if(descriptor instanceof LeoAEDescriptor) ((LeoAEDescriptor) descriptor).addTypeSystemDescription(leoTypeSystemDescription);
            aggregate.addDelegate(descriptor);
        }
        aggregate.setNumberOfInstances(numInstances);
        deploy(aggregate);
    }

    /**
     * Use the AnalysisEngineDescription as input for the deployment descriptor that is generated.
     *
     * @param aggregate Aggregate AnalysisEngineDescriptor to be used in the UIMA AS service
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(AnalysisEngineDescription aggregate) throws Exception {
        //Create the LeoAEDescriptor representation of this aggregate
        LeoAEDescriptor faed = new LeoAEDescriptor(aggregate);

        //Call Init for remaining initialization
        deploy(faed);
    }//init(AnalysisEngineDescrption aggregate)

    /**
     * Initialize the Deployment Descriptor for the service using the provided
     * LeoAEDescriptor object as the Top Descriptor.
     *
     * @param faed  the descriptor to be used for this service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(LeoAEDescriptor faed) throws Exception {
        if (faed == null) throw new Exception("Top descriptor required for initialization");
        LeoDeployDescriptor fdd = new LeoDeployDescriptor(faed);
        if (mServiceName != null) fdd.setName(mServiceName);
        if (mEndpoint != null) fdd.setEndpoint(mEndpoint);
        if (mBrokerURL != null) fdd.setBrokerURL(mBrokerURL);
        //Only reset the CAS pool size if the user designates something larger than the max instances in the service.
        if(mCasPoolSize > fdd.getCasPoolSize()) {
            fdd.setCasPoolSize(mCasPoolSize);
        }
        fdd.setInitialFsHeapSize(mFSHeapSize);

        deploy(fdd);
    }//init(LeoAEDescriptor) method

    /**
     * Initialize the service using the provided LeoDeployDescriptor object as the basis of
     * the deployment information gov.va.vinci.leo.model.
     *
     * @param fdd  the descriptor to be used for this service.
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy(LeoDeployDescriptor fdd) throws Exception {
        if (fdd == null) throw new Exception("Deploy descriptor required for initialization");
        //Serialize the Top Descriptor
        LeoAEDescriptor topDescriptor = fdd.getTopDescriptor();
        //Set the descriptor directory if specified
        if(StringUtils.isNotBlank(this.mDescriptorDirectory)) {
            topDescriptor.setDescriptorLocator(new File(this.mDescriptorDirectory).toURI());
        }
        //Mark the delete on exit flag
        topDescriptor.setIsDeleteOnExit(mDeleteOnExit);
        //Serialize the xml descriptors for the Aggregate engine
        topDescriptor.toXML((fdd.getTopDescriptor().isPrimitive()) ? "leoPrimitive" : "leoAggregate");
        LOG.info("AggregateDescriptor: " + fdd.getTopDescriptor().getDescriptorLocator());
        this.mAggregateDescriptorFile = fdd.getTopDescriptor().getDescriptorLocator();

        //Serialize the Deploy Descriptor
        File deployFile = null;
        if(StringUtils.isNotBlank(this.mDescriptorDirectory)) {
            deployFile = File.createTempFile("leoDeployment", ".xml", new File(this.mDescriptorDirectory));
        } else {
            deployFile = File.createTempFile("leoDeployment", ".xml");
        }
        if(mDeleteOnExit) { deployFile.deleteOnExit(); }
        mDeploymentDescriptorFile = deployFile.getAbsolutePath();
        LOG.info(mDeploymentDescriptorFile);
        fdd.toXML(mDeploymentDescriptorFile);

        //Perform remaining initialization
        deploy();
    }//init(LeoDeployDescriptor) method

    /**
     * Initialize the mAppCtx and mUAEngine objects then deploy the service.  Requires that a
     * deployment descriptor file has already been set.
     *
     * @throws Exception if any error occurs during deployment.
     */
    public void deploy() throws Exception {
        //Initialize the execution map
        mAppCtx = new HashMap<String, Object>();

        mAppCtx.put(UimaAsynchronousEngine.DD2SpringXsltFilePath, System.getenv("UIMA_HOME") + "/bin/dd2spring.xsl");
        mAppCtx.put(UimaAsynchronousEngine.SaxonClasspath, "file:" + System.getenv("UIMA_HOME") + "/saxon/saxon8.jar");
        LOG.info("UimaAsynchronousEngine.DD2SpringXsltFilePath="
                + mAppCtx.get(UimaAsynchronousEngine.DD2SpringXsltFilePath));
        LOG.info("UimaAsynchronousEngine.SaxonClasspath="
                + mAppCtx.get(UimaAsynchronousEngine.SaxonClasspath));

        //Create the Asynchronous Engine
        if (mUAEngine == null)
            mUAEngine = new BaseUIMAAsynchronousEngine_impl();

        //Deploy the "service"
        if (mDeploymentDescriptorFile == null) {
            throw new Exception("No Deployment Descriptor File for initializing the Service");
        }//if mDeploymentDescriptorFile == null

        mUAEngine.deploy(mDeploymentDescriptorFile, mAppCtx);
        LOG.info("'Deployed' the AS service and containers");
        registerWithJam();
    }//init method


    /**
     * Call the appropriate JAM web services to register this service with jam.
     */
    protected void registerWithJam() {
        if (mJamServerBaseUrl == null) {
            LOG.warn("JAM Server Base URL is null, NOT trying to register with JAM.");
            return;
        }

        // First, see if this server is running JMX by seeing if a port is specified.
        String port = System.getProperty("com.sun.management.jmxremote.port");

        if (port == null) {
            LOG.warn("com.sun.management.jmxremote.port is null, NOT trying to register with JAM.");
            return;
        }


        int intPort = -1;
        try {
            intPort = Integer.parseInt(port);
        } catch (Exception e) {
            LOG.warn("Could not register with Jam, invalid JMX port specified via com.management.jmxremote.port value " + port);
        }
        final int finalPort = intPort;

        // Next, register/add host.
        final JamService jamService = new JamService(this.mJamServerBaseUrl);
        try {
            if (!jamService.doesServiceQueueExist(mEndpoint, this.mBrokerURL)) {
                // register this queuename
                jamService.registerServiceQueue(mEndpoint, this.mBrokerURL, mJamQueryIntervalInSeconds, mJamResetStatisticsAfterQuery, true);
            }

            // Add this server.
            jamService.addServerToServiceQueue(mEndpoint, this.mBrokerURL, InetAddress.getLocalHost().getHostAddress(), intPort);

        } catch (Exception e) {
            LOG.error("Could not register with JAM server. Error:" + e);
        }

        // Add shutdown hook to de-register this ip/port with the service when done.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOG.info("Un-registering this service with JAM.");
                try {
                    jamService.removeServerFromServiceQueue(mEndpoint, mBrokerURL, InetAddress.getLocalHost().getHostAddress(), finalPort);
                } catch (Exception e) {
                    LOG.warn(e.toString());
                }
            }
        });
    }//registerWithJam method

}//Service class