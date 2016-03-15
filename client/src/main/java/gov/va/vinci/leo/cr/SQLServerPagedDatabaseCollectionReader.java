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
import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.db.SQLServerPagedQuery;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.util.Map;

/**
 * Database CollectionReader that pulls the data in pages of a configurable size from SQL Server. This uses the
 * SQL Server OFFSET functionality available in SQL Server 2008+.
 *
 * Assumes the query provided returns one record per row of results. Query must include an order by to work
 * with offset (SQL Server requirement)
 *
 * Notes:
 *    firstOffset can be set to start from any record besides the first record.
 *    maxOffset The maximum record offset to get. If (currentBatch * pageSize) + firstOffset > [this value], then
 *              the collection reader is finished. If -1, no max is set.
 *
 */
public class SQLServerPagedDatabaseCollectionReader extends DatabaseCollectionReader {

    /**
     * Size of each batch to pull.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected int pageSize = -1;

    /**
     * the first row to start with. Generally this will be 0.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected int firstOffset=0;

    /**
     * The maximum record offset to get. If (currentBatch * pageSize) + firstOffset > [this value], then
     * the collection reader is finished. If -1, no max is set.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected int maxOffset;

    /**
     * Current batch number we are executing.
     */
    protected int currentBatch = 0;

    /**
     * The SQLServerPageQuery that creates the query for the specific page of records needed at any given time.
     */
    protected SQLServerPagedQuery sqlServerPagedQuery;

    /**
     * Default constructor used during UIMA initialization.
     */
    public SQLServerPagedDatabaseCollectionReader() { /** Do Nothing **/ }

    /**
     * Initialize the reader using the database connection and query information provided, including the size of each
     * batch and optionally the number of random batches to execute.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param dataQueryInformation database query for retrieving results, assumes one record per row.
     * @param pageSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     */
    public SQLServerPagedDatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation,
                                                  DataQueryInformation dataQueryInformation,
                                                  int pageSize) {
        this(databaseConnectionInformation.getDriver(), databaseConnectionInformation.getUrl(),
                databaseConnectionInformation.getUsername(), databaseConnectionInformation.getPassword(),
                dataQueryInformation.getQuery(), dataQueryInformation.getIdColumn(),
                dataQueryInformation.getNoteColumn(),  pageSize, 0);
    }

    /**
     * Initialize the reader using the database connection and query information provided, including the size of each
     * batch and optionally the number of random batches to execute.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param dataQueryInformation database query for retrieving results, assumes one record per row.
     * @param pageSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     * @param firstOffset the first row to start with. Generally this will be 0.
     */
    public SQLServerPagedDatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation,
                                                  DataQueryInformation dataQueryInformation,
                                                  int pageSize, int firstOffset) {
        this(databaseConnectionInformation.getDriver(), databaseConnectionInformation.getUrl(),
                databaseConnectionInformation.getUsername(), databaseConnectionInformation.getPassword(),
                dataQueryInformation.getQuery(), dataQueryInformation.getIdColumn(),
                dataQueryInformation.getNoteColumn(),  pageSize, firstOffset);
    }

    /**
     * Initialize the reader using the provided connection and query information, including the size of each batch and
     * optionally the number of random batches to execute.
     *
     * @param driver JDBC driver class
     * @param url JDBC connection URL
     * @param username database user name.
     * @param password database user password.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     * @param pageSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     */
    public SQLServerPagedDatabaseCollectionReader(String driver, String url, String username, String password, String query,
                                                  String idColumn, String noteColumn,int pageSize ) {
        this(driver, url, username, password, query, idColumn, noteColumn, pageSize, 0);
    }

    /**
     * Initialize the reader using the provided connection and query information, including the size of each batch and
     * optionally the number of random batches to execute.
     *
     * @param driver JDBC driver class
     * @param url JDBC connection URL
     * @param username database user name.
     * @param password database user password.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     * @param pageSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     * @param firstOffset the first row to start with. Generally this will be 0.
     */
    public SQLServerPagedDatabaseCollectionReader(String driver, String url, String username, String password, String query,
                                                  String idColumn, String noteColumn,int pageSize, int firstOffset ) {
        this(driver, url, username, password, query, idColumn, noteColumn, pageSize, firstOffset, -1);
    }

    /**
     * Initialize the reader using the provided connection and query information, including the size of each batch and
     * optionally the number of random batches to execute.
     *
     * @param driver JDBC driver class
     * @param url JDBC connection URL
     * @param username database user name.
     * @param password database user password.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     * @param pageSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     * @param firstOffset the first row to start with. Generally this will be 0.
     */
    public SQLServerPagedDatabaseCollectionReader(String driver, String url, String username, String password, String query,
                                                  String idColumn, String noteColumn,int pageSize, int firstOffset, int maxOffset ) {
        super(driver, url, username, password, query, idColumn, noteColumn);
        if(pageSize < 1) {
            throw new IllegalArgumentException("Page size must be 1 or greater!");
        }
        this.pageSize = pageSize;
        this.firstOffset = firstOffset;
        this.maxOffset = maxOffset;
    }

    /**
     * Get the page size.
     *
     * @return page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set the page size.
     *
     * @param pageSize page size
     * @return reference to this reader instance
     */
    public SQLServerPagedDatabaseCollectionReader setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Get the first row offset.
     *
     * @return first offset
     */
    public int getFirstOffset() {
        return firstOffset;
    }

    /**
     * Set the first row offset.
     *
     * @param firstOffset first row offset
     * @return reference to this reader instance
     */
    public SQLServerPagedDatabaseCollectionReader setFirstOffset(int firstOffset) {
        this.firstOffset = firstOffset;
        return this;
    }

    /**
     * Get the max offset.
     *
     * @return max offset
     */
    public int getMaxOffset() {
        return maxOffset;
    }

    /**
     * Set the max offset.
     *
     * @param maxOffset max offset
     * @return reference to this reader instance
     */
    public SQLServerPagedDatabaseCollectionReader setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
        return this;
    }

    /**
     * This method is called during initialization. Subclasses should override it to perform one-time startup logic.
     *
     * @throws org.apache.uima.resource.ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();

        sqlServerPagedQuery= new SQLServerPagedQuery(query, pageSize, firstOffset, maxOffset);
    }

    /**
     * @return true if and only if there are more elements available from this CollectionReader.
     * @throws org.apache.uima.collection.CollectionException
     */
    @Override
    public boolean hasNext() throws CollectionException {
        //if the current row set is empty or the index is still -1 then setup the query for the next set
        if(mRowIndex < 0 || mRecordList == null || mRowIndex == mRecordList.size()) {
            this.query = sqlServerPagedQuery.getPageQuery(currentBatch);
            if (this.query == null) {
                return false;
            }
            currentBatch++;
            mRowIndex = -1;
        }
        return super.hasNext();
    }

}
