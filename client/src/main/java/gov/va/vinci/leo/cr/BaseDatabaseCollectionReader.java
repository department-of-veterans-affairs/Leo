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

import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.db.DataManager;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.util.Progress;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
    @LeoConfigurationParameter(mandatory = true)
    protected String driver = null;

    /**
     * Connection URL for the database to be accessed.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected String url = null;

    /**
     * Username for this database.
     */
    @LeoConfigurationParameter
    protected String username = null;

    /**
     * Password for this database.
     */
    @LeoConfigurationParameter
    protected String password = null;

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
        this.driver = databaseConnectionInformation.getDriver();
        this.url = databaseConnectionInformation.getUrl();
        this.password = databaseConnectionInformation.getPassword();
        this.username = databaseConnectionInformation.getUsername();
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
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Get the Driver text string.
     * 
     * @return driver string
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Set the driver name for this reader.
     * 
     * @param driver driver string
     * @return reference to this reader instance
     */
    public <T> T setDriver(String driver) {
        this.driver = driver;
        return (T) this;
    }

    /**
     * Database connection URL.
     * 
     * @return connection URL String
     */
    public String getURL() {
        return url;
    }

    /**
     * Set the driver URL.
     * 
     * @param url driver URL.
     * @return reference to this reader instance
     */
    public <T> T setURL(String url) {
        this.url = url;
        return (T) this;
    }

    /**
     * Return the database username.
     * 
     * @return database username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the database username.
     * 
     * @param username database username
     * @return reference to this reader instance
     */
    public <T> T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    /**
     * Get the database password.
     * 
     * @return database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the database password.
     * 
     * @param password database password
     * @return reference to this reader instance
     */
    public <T> T setPassword(String password) {
        this.password = password;
        return (T) this;
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
        return new DataManager(driver, url, username, password);
    }

}
