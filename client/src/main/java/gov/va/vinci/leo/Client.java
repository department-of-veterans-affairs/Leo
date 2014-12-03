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

import gov.va.vinci.leo.cr.LeoCollectionReaderInterface;
import gov.va.vinci.leo.listener.BaseListener;
import gov.va.vinci.leo.tools.LeoProperties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Initialize and execute a pipeline via the client API for UIMA AS which has been
 * created via the Service object.  Utilizes the same Engine created and provided by
 * the Service.
 *
 * @author thomasginter
 */
public class Client extends LeoProperties {

    /**
     * UIMA Asynchronous Engine object used to deploy the server and later to
     * init and run the client side of the service also.
     */
    protected LeoEngine mUAEngine = null;

    /**
     * Application execution map used primarily by the client.
     */
    protected Map<String, Object> mAppCtx = new HashMap<String, Object>();

    /**
     * Holds performance report data from the engine after processing is complete.
     */
    protected String performanceReport = null;

    /**
     * Flag to let us know if a UAB Listener has been added.
     */
    protected boolean isUAListenerAdded = false;

    /**
     * Logger object.
     */
    protected final static Logger LOG = Logger.getLogger(Client.class);


    /**
     * Collection reader used for reading documents in and sending them to the service.
     */
    protected LeoCollectionReaderInterface collectionReader = null;


    /**
     * Default Constructor, will try to load properties from file located at
     * conf/leo.properties.
     *
     * @param uaListeners Listeners that will catch Service callback events
     */
    public Client(UimaAsBaseCallbackListener... uaListeners) {
        this.mUAEngine = new LeoEngine();

        this.loadDefaults();
        if (uaListeners != null) {
            for (UimaAsBaseCallbackListener uab : uaListeners) {
                this.addUABListener(uab);
            }//for
        }//if
    }//default constructor

    /**
     * Constructor with properties file input for client execution context.
     *
     * @param propertiesFile  the full path to the properties file for client properties.
     * @param uaListeners    Listeners that will catch Service callback events
     * @throws Exception  if there is an error loading properties.
     */
    public Client(String propertiesFile, UimaAsBaseCallbackListener... uaListeners) throws Exception {
        this(uaListeners);
        this.loadprops(propertiesFile);
    }//Constructor with properties file for initialization

    /**
     * Constructor with properties file input for client execution context, a collection reader for input, and option listeners.
     *
     * @param propertiesFile   the full path to the properties file for client properties.
     * @param collectionReader the input collection reader for this client.
     * @param uaListeners      Listeners that will catch Service callback events
     * @throws Exception if there is an error loading properties.
     */
    public Client(String propertiesFile, LeoCollectionReaderInterface collectionReader, UimaAsBaseCallbackListener... uaListeners) throws Exception {
        this(propertiesFile, uaListeners);
        this.collectionReader = collectionReader;
    }//Constructor with properties file for initialization

    /**
     * Set the collection reader for this client.
     *
     * @param reader the collection reader for this client.
     */
    public void setLeoCollectionReader(LeoCollectionReaderInterface reader) {
        this.collectionReader = reader;
    }

    /**
     * Get the collection reader for this client.
     *
     * @return the collection reader for this client.
     */
    public LeoCollectionReaderInterface getLeoCollectionReader() {
        return collectionReader;
    }


    /**
     * Set a reference to the UABCallbackListener object for handling service events.
     *
     * @param uaListener adds a callback gov.va.vinci.leo.listener for this client.
     */
    public void addUABListener(UimaAsBaseCallbackListener uaListener) {
        //Nothing to add if null given as gov.va.vinci.leo.listener
        if (uaListener == null)
            return;
        //Init the engine if not already done
        if (this.mUAEngine == null)
            this.mUAEngine = new LeoEngine();
        this.mUAEngine.addStatusCallbackListener(uaListener);
        this.isUAListenerAdded = true;
    }//setUABListener method

    /**
     * Validate the required data for client execution.
     *
     * @throws Exception if any data required for the client is missing.
     */
    public void validateData() throws Exception {
        String msg = "Missing required data for client initialization: ";
        boolean isMissing = false;

        if (this.mUAEngine == null) {
            msg += "Uninitialized Uima AS Engine";
            isMissing = true;
        }//if

        if ((this.mAppCtx == null ||
                !this.mAppCtx.containsKey(UimaAsynchronousEngine.ServerUri)) &&
                this.mBrokerURL == null) {
            msg += (isMissing) ? ", BrokerURL" : "BrokerURL";
            isMissing = true;
        }//if brokerURL is missing

        if ((this.mAppCtx == null ||
                !this.mAppCtx.containsKey(UimaAsynchronousEngine.ENDPOINT)) &&
                this.mEndpoint == null) {
            msg += (isMissing) ? ", Queue Name" : "Queue Name";
            isMissing = true;
        }//if missing endpoint (queue name)

        if (!this.isUAListenerAdded) {
            isMissing = true;
            msg += (isMissing) ? ", UABListener not registered" : "UABListener not registered";
        }//if

        if (isMissing)
            throw new Exception(msg);
    }//validateData method

    /**
     * Initialize the client engine and application context.
     *
     * @param uabs Listeners that will catch Service callback events
     * @throws Exception if any data required for the client is missing.
     */
    protected void init(UimaAsBaseCallbackListener... uabs) throws Exception {
        if (uabs != null) {
            for (UimaAsBaseCallbackListener uab : uabs) {
                this.addUABListener(uab);
            }//for
        }//if
        validateData();

        //Add Broker URL
        if (this.mBrokerURL != null)
            mAppCtx.put(UimaAsynchronousEngine.ServerUri, this.mBrokerURL);
        //Add endpoint
        if (this.mEndpoint != null)
            mAppCtx.put(UimaAsynchronousEngine.ENDPOINT, this.mEndpoint);
        //Add timeouts in milliseconds
        mAppCtx.put(UimaAsynchronousEngine.Timeout, mCCTimeout * 1000);
        mAppCtx.put(UimaAsynchronousEngine.GetMetaTimeout, mInitTimeout * 1000);
        mAppCtx.put(UimaAsynchronousEngine.CpcTimeout, mCCTimeout * 1000);
        //Add Cas Pool Size
        mAppCtx.put(UimaAsynchronousEngine.CasPoolSize, mCasPoolSize);
        //Add FS heap size
        mAppCtx.put(UIMAFramework.CAS_INITIAL_HEAP_SIZE, Integer.valueOf(mFSHeapSize / 4).toString());
    }//init method


    /**
     * Run with the collection reader that is already set in the client.
     *
     * @param uabs List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    public void run(UimaAsBaseCallbackListener... uabs) throws Exception {
        if (collectionReader == null) {
            throw new IllegalStateException("Client does not have a collection reader set.");
        }
        run(this.collectionReader, uabs);
    }

    /**
     * Execute the AS pipeline using the LeoCollectionReaderInterface object.
     *
     * @param collectionReader LeoCollectionReaderInterface that will produce CASes for the pipeline
     * @param uabs             List of Listeners that will catch callback events from the service
     * @throws Exception  if any error occurs during processing
     */
    public void run(LeoCollectionReaderInterface collectionReader, UimaAsBaseCallbackListener... uabs) throws Exception {
        this.init(uabs);
        CollectionReader reader = collectionReader.produceCollectionReader();
        mUAEngine.setCollectionReader(reader);
        mUAEngine.initialize(mAppCtx);


        LOG.info("SerializationStrategy: " + mUAEngine.getSerialFormat());

        /**
         * Catch exceptions that are thrown during processing, output the exception to the log,
         * then make sure the engine is stopped.
         */
        try {
            mUAEngine.process();

            // Insure all CAS have come back. (Work around a bug in UIMA 2.3.1)
            int count = 0;
            boolean finished = false;
            while (!finished && count < 10) {
                boolean listenersComplete = true;

                /** Check all listeners. **/
                for (Object listener : mUAEngine.getListeners()) {
                    if (listener instanceof BaseListener) {
                        BaseListener baseListener = (BaseListener) listener;
                        if (baseListener.getNumSent() != baseListener.getNumReceived()) {
                            listenersComplete = false;
                        }
                    } else {
                        LOG.warn("Listener " + listener.getClass().getCanonicalName() + " does not inherit from BaseListener, so the number sent vs. received cannot be verified.");
                    }
                }

                /** see if the are all done, or sleep for 2 seconds and try again. **/
                if (!listenersComplete) {
                    LOG.warn("Have not recieved the number of CAS back that were sent for all listeners. Sleeping for 2 seconds. (" + (count + 1) + " / 10 tries)");
                    if (count == 10) {
                        LOG.warn("Did not recieve the number of CAS back that were sent for all listeners.");
                    }
                    Thread.sleep(2000);
                } else {
                    LOG.info("All listeners have recieved all CAS they sent.");
                    finished = true;
                }
                count++;
            }
        } catch (Exception e) {
            LOG.error("Exception thrown during content processing:\n"
                    + ExceptionUtils.getMessage(e) + "\n"
                    + ExceptionUtils.getFullStackTrace(e));
            throw e;
        } finally {

            mUAEngine.stop();
        }
    }//run method with collectionReaderDescriptor path


    /**
     * Run the pipeline on one document at a time using the document text provided.
     *
     * @param stream        the input stream to read the document from.
     * @param uabs         List of Listeners that will catch callback events from the service
     * @throws Exception  if any error occurs during processing
     */
    public void run(InputStream stream, UimaAsBaseCallbackListener... uabs) throws Exception {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);
        run (writer.toString(), uabs);
    }//run method with document text string as input


    /**
     * Run the pipeline on one document at a time using the document text provided.
     *
     * @param documentText the document text to process.
     * @param uabs         List of Listeners that will catch callback events from the service
     * @throws Exception  if any error occurs during processing
     */
    public void run(String documentText, UimaAsBaseCallbackListener... uabs) throws Exception {
        this.init(uabs);
        LOG.info("Initialize the client");
        mUAEngine.initialize(mAppCtx);
        LOG.info("run on document text");
        CAS cas = mUAEngine.getCAS();
        cas.setDocumentText(documentText);
        mUAEngine.sendCAS(cas);
        mUAEngine.collectionProcessingComplete();
        this.performanceReport = mUAEngine.getPerformanceReport();
        mUAEngine.stop();
    }//run method with document text string as input

    /**
     * Run the pipeline on a single CAS object.
     *
     * @param cas an individual cass to process through the client.
     * @param uabs List of Listeners that will catch callback events from the service
     * @throws Exception   if any error occurs during processing
     */
    public void run(CAS cas, UimaAsBaseCallbackListener... uabs) throws Exception {
        this.init(uabs);
        mUAEngine.initialize(mAppCtx);
        mUAEngine.sendCAS(cas);
        mUAEngine.collectionProcessingComplete();
        this.performanceReport = mUAEngine.getPerformanceReport();
        mUAEngine.stop();
    }//run method with CAS object as input

    /**
     * Get the performance report stored after the last "run" invocation in this client object.
     *
     * @return UAEngine performance report as a String object
     */
    public String getPerformanceReport() {
        return this.performanceReport;
    }//getPerformanceReport method


    /**
     * Calls the service to determine the TypeSystemDescription it is using,
     * and returns it.
     *
     * @return the TypeSystemDescription for the remote service.
     * @throws Exception if there is an exception getting the type system from the engine.
     */
    public TypeSystemDescription getServiceTypeSystemDescription() throws Exception {
        init((UimaAsBaseCallbackListener[]) null);
        UimaAsynchronousEngine engine = new BaseUIMAAsynchronousEngine_impl();
        engine.initialize(mAppCtx);
        return engine.getMetaData().getTypeSystem();
    }

    /**
     * Returns the remote service TypeSystemDescription in xml. Typically for being
     * written out to a file for other tools such as the UIMA annotation viewer to use.
     *
     * @return the type system description in xml format.
     * @throws Exception if there is an exception getting the type system from the engine.
     */
    public String getServiceTypeSystemDescriptionXml() throws Exception {
        StringWriter sw = new StringWriter();
        getServiceTypeSystemDescription().toXML(sw);
        return sw.toString();
    }

    /**
     * @see java.lang.Object#finalize()
     * @throws java.lang.Throwable if any exception is thrown by the engine during the stop() call.
     */
    @Override
    protected void finalize() throws Throwable {

        try {
            if (mUAEngine != null) {
                mUAEngine.stop();
            }//if
        } finally {
            super.finalize();
        }//finally
    }//finalize

    /**
     * Extends the Base engine in order to expose the listeners in a getter.
     */
    public static class LeoEngine extends BaseUIMAAsynchronousEngine_impl {

        /**
         * Return all listeners for this engine.
         * @return a list of listeners for this engine.
         */
        @SuppressWarnings("rawtypes")
        public List getListeners() {
            return this.listeners;
        }
    }


}//Client Class
