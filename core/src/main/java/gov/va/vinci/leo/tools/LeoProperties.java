/**
 * LeoProps.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo.tools;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Handles loading Properties from the leo.properties file including setting
 * defaults initially and storing values we expect from that properties file.
 *
 * @author thomasginter
 */
public abstract class LeoProperties {
    /**
     * Name and location of the properties file.
     */
    protected String mPropertiesFile = null;

    /**
     * File path to the deployment descriptor for the AS service that will be deployed.
     */
    protected String mDeploymentDescriptorFile = null;

    /**
     * Name of the service for deployment.
     */
    protected String mServiceName = null;

    /**
     * File path to the aggregate descriptor.
     */
    protected String mAggregateDescriptorFile = null;

    /**
     * Broker URL for the remote service.
     */
    protected String mBrokerURL = null;

    /**
     * Endpoint or Input Queue Name for the service.
     */
    protected String mEndpoint = null;

    /**
     * The path to the directory where descriptors will be generated. By default this path is null as descriptors are
     * generated in the tmp directory of the system.
     */
    protected String mDescriptorDirectory = null;

    /**
     * CAS pool size determines the max number of requests that can be outstanding.
     */
    protected int mCasPoolSize = 4;

    /**
     * FS heap size in bytes of each CAS in the pool.
     */
    protected int mFSHeapSize = 2000000;

    /**
     * Timeout period in seconds.  If a CAS does not return within this time period it is considered
     * an error.  Default is wait forever.
     */
    protected int mTimeout = 0;

    /**
     * Initialization timeout period in seconds.  If initialization request does not return within this
     * time it is considered an error.  Default is 60 seconds
     */
    protected int mInitTimeout = 60;

    /**
     * CAS complete timeout in seconds.  Once all CAS requests are complete a collection process complete
     * command is sent.  Default is wait forever
     */
    protected int mCCTimeout = 0;

    /**
     * The base url for the Jam (JMX Analytics Monitoring) server if available.
     */
    protected String mJamServerBaseUrl = null;

    /**
     * If registering with JAM, what is the monitor query interval in seconds. default is 60*60 (1 hour)
     */
    protected int mJamQueryIntervalInSeconds = 60 * 60;

    /**
     * If registering with JAM, should statistics be reset after each gather? Default is true.
     */
    protected boolean mJamResetStatisticsAfterQuery = true;

    /**
     * Should the generated XML descriptors be deleted on program exit?  Default is true.
     */
    protected boolean mDeleteOnExit = true;

    /**
     * Logging object of output.
     */
    public static final Logger LOG = Logger.getLogger(LeoProperties.class.getCanonicalName());

    /**
     * Default value for the Broker URL.
     */
    public static final String BROKERURL_DEFAULT = "tcp://localhost:61616";

    /**
     * Default value for the Endpoint Queue Name, Queue name should be unique for each application.
     */
    public static final String ENDPOINT_DEFAULT = "mySimpleQueueName";

    /**
     * Default value for the Properties File path.
     */
    public static final String PROPERTIES_DEFAULT = "conf/leo.properties";

    /**
     * Load default values for required parameters.
     */
    protected void loadDefaults() {
        this.mPropertiesFile = PROPERTIES_DEFAULT;
        this.mBrokerURL = BROKERURL_DEFAULT;
        this.mEndpoint = ENDPOINT_DEFAULT;
    }//loadDefaults method

    /**
     * Load the properties file specified by the user.
     *
     * @param propertiesFile Path to the properties file to be loaded
     * @throws Exception If unable to load the properties file
     */
    public void loadprops(String propertiesFile) throws Exception {
        if (StringUtils.isBlank(propertiesFile)) {
            LOG.warn("Properties file path required, unable to load properties file: " + propertiesFile);
            return;
        }//if
        this.mPropertiesFile = propertiesFile;
        this.loadprops();
    }//loadprops method, string path input

    /**
     * Load the properties from the properties file that will be used for execution.
     * @throws java.lang.Exception if the properties file cannot be found or loaded.
     */
    protected void loadprops() throws Exception {
        LOG.info("Loading initial vars");

        //Init the properties file object
        if (this.mPropertiesFile == null) this.mPropertiesFile = PROPERTIES_DEFAULT;
        File propsFile;
        try {
            propsFile = new File(this.mPropertiesFile);
            //If there is no properties file then return, there is nothing to load.
            if (propsFile == null || !propsFile.exists() || propsFile.isDirectory()) {
                LOG.info("No Properties to load: " + this.mPropertiesFile);
                return;
            }//if
        } catch (Exception e) {
            //Will sometimes throw a NullPointerException if this.mPropertiesFile is not set
            return;
        }//catch

        Properties properties = new Properties();
        FileInputStream propsFileInputStream = new FileInputStream(propsFile);
        try {
            properties.load(propsFileInputStream);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            propsFileInputStream.close();
        }

        //Load the deployment descriptor
        String dd = properties.getProperty("deploymentDescriptor");
        if (StringUtils.isNotBlank(dd)) {
            File fileDD = new File(dd);
            if (!fileDD.exists() || !fileDD.isFile()) {
                throw new Exception("Deployment descriptor: " + dd + " is either a directory or does not exist");
            }//if file issue
            this.mDeploymentDescriptorFile = dd;
        }//if dd.isEmpty or null
        LOG.info("mDeploymentDescriptorFile=" + this.mDeploymentDescriptorFile);

        //Load the brokerURL
        if (properties.getProperty("brokerURL") != null) {
            this.mBrokerURL = properties.getProperty("brokerURL");
        }//if
        LOG.info("mBrokerURL=" + this.mBrokerURL);

        //Load the endpoint
        if (properties.getProperty("endpoint") != null) {
            this.mEndpoint = properties.getProperty("endpoint");
        }//if
        LOG.info("mEndpoint=" + this.mEndpoint);

        //Load the directory where descriptors should be generated
        if(properties.getProperty("descriptorPath") != null) {
            this.mDescriptorDirectory = properties.getProperty("descriptorPath");
        }
        LOG.info("descriptorPath=" + this.mDescriptorDirectory);

        //Load jam variables
        mJamServerBaseUrl = properties.getProperty("jamServerBaseUrl");
        LOG.info("mJamServerBaseUrl=" + this.mJamServerBaseUrl);

        if (properties.getProperty("jamQueryIntervalInSeconds") != null) {
            try {
                mJamQueryIntervalInSeconds = Integer.parseInt(properties.getProperty("jamQueryIntervalInSeconds"));
            } catch (Exception e) {
                LOG.warn("Could not parse jamQueryIntervalInSeconds value. It should be an int, but was " + properties.getProperty("jamQueryIntervalInSeconds"));
            }
        }
        if (properties.getProperty("jamResetStatisticsAfterQuery") != null) {
            try {
                mJamResetStatisticsAfterQuery = Boolean.parseBoolean(properties.getProperty("jamResetStatisticsAfterQuery"));
            } catch (Exception e) {
                LOG.warn("Could not parse jamResetStatisticsAfterQuery value. It should be a boolean, but was " + properties.getProperty("jamResetStatisticsAfterQuery"));
            }
        }

        //Get the service name
        if (properties.getProperty("serviceName") != null) {
            this.mServiceName = properties.getProperty("serviceName");
        }//if
        LOG.info("this.mServiceName=" + this.mServiceName);

        //Set the casPoolSize
        if (properties.getProperty("casPoolSize") != null) {
            try {
                this.mCasPoolSize = Integer.parseInt(properties.getProperty("casPoolSize"));
            } catch (Exception e) {
                LOG.warn("Could not parse the casPoolSize " + properties.getProperty("casPoolSize"));
            }
        }//if
        LOG.info("this.mCasPoolSize=" + this.mCasPoolSize);

        //Set the fsHeapSize
        if (properties.getProperty("fsHeapSize") != null) {
            try {
                this.mFSHeapSize = Integer.parseInt(properties.getProperty("fsHeapSize"));
            } catch (Exception e) {
                LOG.warn("Could not parse the fsHeapSize " + properties.getProperty("fsHeapSize"));
            }
        }//if
        LOG.info("this.mFSHeapSize=" + this.mFSHeapSize);

        //Set the timeout value
        if (properties.getProperty("timeout") != null) {
            try {
                this.mTimeout = Integer.parseInt(properties.getProperty("timeout"));
            } catch (Exception e) {
                LOG.warn("Could not parse the timeout " + properties.getProperty("timeout"));
            }
        }//if
        LOG.info("this.mTimeout=" + this.mTimeout);

        //Set the timeout value
        if (properties.getProperty("initTimeout") != null) {
            try {
                this.mInitTimeout = Integer.parseInt(properties.getProperty("initTimeout"));
            } catch (Exception e) {
                LOG.warn("Could not parse the initTimeout " + properties.getProperty("initTimeout"));
            }//catch
        }//if
        LOG.info("this.mInitTimeout=" + this.mInitTimeout);

        //Set the timeout value
        if (properties.getProperty("ccTimeout") != null) {
            try {
                this.mCCTimeout = Integer.parseInt(properties.getProperty("ccTimeout"));
            } catch (Exception e) {
                LOG.warn("Could not parse the ccTimeout " + properties.getProperty("ccTimeout"));
            }
        }//if
        LOG.info("this.mCCTimeout=" + this.mCCTimeout);

        if(properties.getProperty("deleteOnExit") != null) {
            try {
                this.mDeleteOnExit = Boolean.parseBoolean(properties.getProperty("deleteOnExit"));
            } catch (Exception e) {
                LOG.warn("Could not parse the deleteOnExit property as a boolean: " + properties.getProperty("deleteOnExit"));
            }
        }

        LOG.info("Load Complete");
    }//loadprops method

    /**
     * The deployment descriptor file path and name.
     *
     * @return the DeploymentDescriptorFile
     */
    public String getDeploymentDescriptorFile() {
        return mDeploymentDescriptorFile;
    }

    /**
     * Set the existing Deployment Descriptor file to use.
     *
     * @param mDeploymentDescriptorFile the DeploymentDescriptorFile to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setDeploymentDescriptorFile(String mDeploymentDescriptorFile) {
        this.mDeploymentDescriptorFile = mDeploymentDescriptorFile;
        return (T) this;
    }

    /**
     * Get the service name.
     *
     * @return the ServiceName
     */
    public String getServiceName() {
        return mServiceName;
    }

    /**
     * Optional, name of the service to deploy.
     *
     * @param mServiceName the ServiceName to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setServiceName(String mServiceName) {
        this.mServiceName = mServiceName;
        return (T) this;
    }

    /**
     * Get the broker url.
     *
     * @return the BrokerURL
     */
    public String getBrokerURL() {
        return mBrokerURL;
    }

    /**
     * set the broker url.
     *
     * @param mBrokerURL the BrokerURL to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setBrokerURL(String mBrokerURL) {
        this.mBrokerURL = mBrokerURL;
        return (T) this;
    }

    /**
     * get the endpoint.
     *
     * @return the Endpoint
     */
    public String getEndpoint() {
        return mEndpoint;
    }

    /**
     * Set the "InputQueueName" for this service.
     *
     * @param mEndpoint the Endpoint to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setEndpoint(String mEndpoint) {
        this.mEndpoint = mEndpoint;
        return (T) this;
    }

    /**
     * Get the input queue name.
     *
     * @return the InputQueueName
     */
    public String getInputQueueName() {
        return this.getEndpoint();
    }

    /**
     * Set the "InputQueueName" for this service.
     *
     * @param mInputQueueName  the input queue name for this service.
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setInputQueueName(String mInputQueueName) {
        this.setEndpoint(mInputQueueName);
        return (T) this;
    }

    /**
     * get the cas pool size.
     *
     * @return the mCasPoolSize
     */
    public int getCasPoolSize() {
        return mCasPoolSize;
    }

    /**
     * Set the size of the CAS pool.  This figure is auto-increased by Leo to match
     * the largest number of replicated objects in the service definition.
     *
     * @param mCasPoolSize the CasPoolSize to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setCasPoolSize(int mCasPoolSize) {
        this.mCasPoolSize = mCasPoolSize;
        return (T) this;
    }

    /**
     * Get FS Heap Size.
     *
     * @return the FSHeapSize
     */
    public int getFSHeapSize() {
        return mFSHeapSize;
    }

    /**
     * Size of the FS heap in bytes of each CAS in the pool.
     *
     * @param mFSHeapSize the FSHeapSize to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setFSHeapSize(int mFSHeapSize) {
        this.mFSHeapSize = mFSHeapSize;
        return (T) this;
    }

    /**
     * Set timeout.
     *
     * @return the Timeout
     */
    public int getTimeout() {
        return mTimeout;
    }

    /**
     * Timeout period in seconds.  If a CAS does not return within this period it is considered
     * an error.  Default is wait forever.
     *
     * @param mTimeout the Timeout to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
        return (T) this;
    }

    /**
     * Get the init timeout.
     *
     * @return the InitTimeout
     */
    public int getInitTimeout() {
        return mInitTimeout;
    }

    /**
     * Initialization timeout period in seconds. If initialization request does not return within this
     * time it is considered an error. Default is 60 seconds.
     *
     * @param mInitTimeout the InitTimeout to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setInitTimeout(int mInitTimeout) {
        this.mInitTimeout = mInitTimeout;
        return (T) this;
    }

    /**
     * Get the CCTimeout.
     *
     * @return the CCTimeout
     */
    public int getCCTimeout() {
        return mCCTimeout;
    }

    /**
     * CAS complete timeout in seconds.  Once all CAS requests are complete a collection process complete
     * event is thrown.  Default is wait forever.
     *
     * @param mCCTimeout the CCTimeout to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setCCTimeout(int mCCTimeout) {
        this.mCCTimeout = mCCTimeout;
        return (T) this;
    }

    /**
     * Get the Jam Server Base URL.
     *
     * @return the JamServerBaseUrl
     */
    public String getJamServerBaseUrl() {
        return mJamServerBaseUrl;
    }

    /**
     * The base URL for the JAM (JMX Analytics Monitoring) server if available.
     *
     * @param mJamServerBaseUrl the JamServerBaseUrl to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setJamServerBaseUrl(String mJamServerBaseUrl) {
        this.mJamServerBaseUrl = mJamServerBaseUrl;
        return (T) this;
    }

    /**
     * Get the JAM Query Interval in seconds.
     *
     * @return the JamQueryIntervalInSeconds
     */
    public int getJamQueryIntervalInSeconds() {
        return mJamQueryIntervalInSeconds;
    }

    /**
     * If registering with JAM, the monitor query interval in seconds.  Default is 60*60 (1 hour)
     *
     * @param mJamQueryIntervalInSeconds the JamQueryIntervalInSeconds to set
     * @return reference to this instance
     */
    public <T extends LeoProperties> T setJamQueryIntervalInSeconds(int mJamQueryIntervalInSeconds) {
        this.mJamQueryIntervalInSeconds = mJamQueryIntervalInSeconds;
        return (T) this;
    }

    /**
     * Reset statistics after jam query.
     *
     * @return the JamResetStatisticsAfterQuery
     */
    public boolean isJamResetStatisticsAfterQuery() {
        return mJamResetStatisticsAfterQuery;
    }

    /**
     * If registering with JAM, should statistics be reset after each gather?  Default is true.
     *
     * @param mJamResetStatisticsAfterQuery the JamResetStatisticsAfterQuery flag to set
     */
    public <T extends LeoProperties> T setJamResetStatisticsAfterQuery(boolean mJamResetStatisticsAfterQuery) {
        this.mJamResetStatisticsAfterQuery = mJamResetStatisticsAfterQuery;
        return (T) this;
    }

    /**
     * TRUE if generated descriptors will be deleted when the program exits, FALSE otherwise.
     *
     * @return TRUE if descriptors will be deleted
     */
    public boolean isDeleteOnExit() {
        return mDeleteOnExit;
    }

    /**
     * Set the flag to TRUE if generated descriptors should be deleted on program exit.  Default is TRUE.
     *
     * @param mDeleteOnExit true if the descriptors should auto delete.
     */
    public <T extends LeoProperties> T setDeleteOnExit(boolean mDeleteOnExit) {
        this.mDeleteOnExit = mDeleteOnExit;
        return (T) this;
    }

    /**
     * Get the path to the directory where descriptors will be generated.
     *
     * @return String representation of the descriptor directory
     */
    public String getDescriptorDirectory() {
        return mDescriptorDirectory;
    }

    /**
     * Set the directory path where generated descriptors will be generated.
     *
     * @param mDescriptorDirectory String representation of the descriptor directory.
     */
    public <T extends LeoProperties> T setDescriptorDirectory(String mDescriptorDirectory) {
        this.mDescriptorDirectory = mDescriptorDirectory;
        return (T) this;
    }
}//LeoProps Class
