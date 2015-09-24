package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
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

import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.db.DataManager;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.util.Progress;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves rows from a database and returns them one at a time for extending readers to handle. Assumes there is one
 * record per row of results.
 *
 * User: Thomas Ginter
 * Date: 7/11/14
 * Time: 15:00
 */
public abstract class BaseDatabaseCollectionReader extends BaseLeoCollectionReader {
    /**
     * Driver to be used for the database connection.
     */
    protected String mDriver = null;

    /**
     * Connection URL for the database to be accessed.
     */
    protected String mURL = null;

    /**
     * Username for this database.
     */
    protected String mUsername = null;

    /**
     * Password for this database.
     */
    protected String mPassword = null;

    /**
     * Index of the current analyte being processed, -1 if the query has not yet been executed.
     */
    protected int mRowIndex = -1;

    /**
     * List of Records that were returned by the query.
     */
    protected List<Object[]> mRecordList = null;

    /**
     * DataManager that performs the query and returns the results.  Metadata is stored in the Handler object which is
     * cached in the DataManager after a query has been performed.
     */
    protected DataManager dataManager = null;

    /**
     * Default constructor used during UIMA initialization.
     */
    public BaseDatabaseCollectionReader() {

    }

    /**
     * Initialize the connection information from the DatabaseConnectionInformation object.
     *
     * @param databaseConnectionInformation database connection information to set.
     */
    public BaseDatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation) {
        if(databaseConnectionInformation == null) {
            throw new IllegalArgumentException("DatabaseConnectionInformation parameter cannot be null!");
        }
        this.mDriver   = databaseConnectionInformation.getDriver();
        this.mURL      = databaseConnectionInformation.getUrl();
        this.mPassword = databaseConnectionInformation.getPassword();
        this.mUsername = databaseConnectionInformation.getUsername();
    }

    /**
     * Initialize the connection information to the parameters provided.
     *
     * @param driver JDBC driver to use.
     * @param url JDBC url for the connection.
     * @param username username for database access.
     * @param password password of the database access user.
     */
    public BaseDatabaseCollectionReader(String driver, String url, String username, String password) {
        this.mDriver = driver;
        this.mURL = url;
        this.mUsername = username;
        this.mPassword = password;
    }

    /**
     * This method is called during initialization. Subclasses should override it to perform one-time startup logic.
     *
     * @throws org.apache.uima.resource.ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        mDriver = (String) getConfigParameterValue(Param.DRIVER.getName());
        mURL = (String) getConfigParameterValue(Param.URL.getName());
        mUsername = (String) getConfigParameterValue(Param.USERNAME.getName());
        mPassword = (String) getConfigParameterValue(Param.PASSWORD.getName());
    }

    /**
     * Log file handler.
     */
    private final static Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

    /**
     * @param aCAS the CAS to populate with the next document;
     * @throws org.apache.uima.collection.CollectionException if there is a problem getting the next and populating the CAS.
     */
    @Override
    public abstract void getNext(CAS aCAS) throws CollectionException, IOException;

    /**
     * @return true if and only if there are more elements availble from this CollectionReader.
     * @throws java.io.IOException
     * @throws org.apache.uima.collection.CollectionException
     */
    @Override
    public abstract boolean hasNext() throws IOException, CollectionException;

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p/>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public abstract Progress[] getProgress();

    /**
     * Executes the query and saves the results internally to a <code>List of Object[]</code> to be retrieved when
     * <code>getNext()</code> is called.
     *
     * @param query the sql query to run.
     * @throws ClassNotFoundException if the driver cannot be found
     * @throws java.sql.SQLException if there is an error executing the query
     * @throws org.apache.uima.collection.CollectionException if there is an error setting the data.
     */
    protected void getData(String query) throws SQLException, ClassNotFoundException, CollectionException {
        StopWatch timer = new StopWatch();
        dataManager = getDataManager();
        LOG.info("Query: " + query);
        timer.start();
        mRecordList = dataManager.query(query);
        mRowIndex = 0;
        timer.stop();
        LOG.info("Query and result parsing returned " + mRecordList.size() + " rows and took: " + timer);
    }

    /**
     * Get the data manager to run queries with.
     * @return the data manager to run queries with.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected DataManager getDataManager() throws SQLException, ClassNotFoundException {
        return new DataManager(mDriver, mURL, mUsername, mPassword);
    }

    /**
     * Sets columns to "".
     * @param rows  The rows to process.
     * @param skipColumns  The columns in each row to set to "". Zero based.
     * @return   the rows with specified columns set to "".
     */
    protected static ArrayList<String> removeUnneededColumns(ArrayList<String> rows, int... skipColumns) {
        // See of this one needs skipped.
        for (int toSkip : skipColumns) {
            rows.set(toSkip, "");
        }
        return rows;
    }

    /**
     * Create the base set of parameters for DatabaseCollectionReaders to use.
     *
     * @return Map of parameter names and values.
     * @throws org.apache.uima.resource.ResourceInitializationException if there is an error initializing the reader.
     */
    protected Map<String, Object> produceBaseDatabaseCollectionReaderParams() throws ResourceInitializationException {
        Map<String, Object> parameterValues = new HashMap<String, Object>();
        parameterValues.put(Param.DRIVER.getName(),   mDriver);
        parameterValues.put(Param.URL.getName(),      mURL);
        parameterValues.put(Param.USERNAME.getName(), mUsername);
        parameterValues.put(Param.PASSWORD.getName(), mPassword);
        return parameterValues;
    }

    /**
     * Static inner class for parameter definitions.
     */
    public static class Param extends BaseLeoCollectionReader.Param {
        /**
         * JDBC Driver to use for the connection.
         */
        public static ConfigurationParameter DRIVER =
                new ConfigurationParameterImpl("Driver", "JDBC Driver String",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[] {} );
        /**
         * Database connection url (ie "jdbc:mysql://hostname:port/dbname").
         */
        public static ConfigurationParameter URL =
                new ConfigurationParameterImpl("URL", "Database connection URL (ie 'jdbc:mysql://hostname:port/dbname'",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[] {});
        /**
         * Database username if required.
         */
        public static ConfigurationParameter USERNAME =
                new ConfigurationParameterImpl("Username", "Optional, Username for the database connection authentication",
                        ConfigurationParameter.TYPE_STRING, false, false, new String[] {});
        /**
         * Database password if required.
         */
        public static ConfigurationParameter PASSWORD =
                new ConfigurationParameterImpl("Password", "Optional, Password for the database connection authentication",
                        ConfigurationParameter.TYPE_STRING, false, false, new String[] {});
    }
}
