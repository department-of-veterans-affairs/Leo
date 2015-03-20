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

import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 * Database CollectionReader that pulls the data in batches of a configurable size.  Assumes the query provided returns
 * one record per row of results.
 *
 * User: Thomas Ginter
 * Date: 7/18/14
 * Time: 11:44
 */
public class BatchDatabaseCollectionReader extends DatabaseCollectionReader {
    /**
     * Min record ID.
     */
    protected int minRecordNumber = -1;
    /**
     * Max record ID.
     */
    protected int maxRecordNumber = -1;
    /**
     * Size of each batch to pull.
     */
    protected int batchSize = -1;
    /**
     * Number of random batches to pull.
     */
    protected int randomBatches = 0;
    /**
     * Query template that will be filled in with min and max values for each batch .
     */
    protected String baseQuery = null;
    /**
     * Total number of batches available to be executed in the table.
     */
    protected int totalNumberOfBatches = 0;
    /**
     * Current batch number we are executing.
     */
    protected int currentBatch = 0;
    /**
     * True if we are pulling random batches.
     */
    protected boolean isRandom = false;
    /**
     * Store the batch numbers already visited when executing random batches.
     */
    protected HashSet<Integer> usedBatches = null;
    /**
     * Generates the random batch number if random batches are indicated.
     */
    protected Random random = new Random(System.currentTimeMillis());

    /**
     * Default constructor used during UIMA initialization.
     */
    public BatchDatabaseCollectionReader() { /** Do Nothing **/ }

    /**
     * Initialize the reader using the database connection and query information provided, including the size of each batch.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param dataQueryInformation database query for retrieving results, assumes one record per row.
     * @param minRecordNumber starting row number for record retrieval.
     * @param maxRecordNumber ending row number for record retrieval.
     * @param batchSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     */
    public BatchDatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation,
                                         DataQueryInformation dataQueryInformation,
                                         int minRecordNumber,
                                         int maxRecordNumber,
                                         int batchSize) {
        this(databaseConnectionInformation, dataQueryInformation, minRecordNumber, maxRecordNumber, batchSize, 0);
    }

    /**
     * Initialize the reader using the database connection and query information provided, including the size of each
     * batch and optionally the number of random batches to execute.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param dataQueryInformation database query for retrieving results, assumes one record per row.
     * @param minRecordNumber starting row number for record retrieval.
     * @param maxRecordNumber ending row number for record retrieval.
     * @param batchSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     * @param randomBatches number of random batches to execute, rather then accessing the data sequentially.  Setting
     *                      this parameter to a value less than one will cause the reader to produce no random batches.
     */
    public BatchDatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation,
                                         DataQueryInformation dataQueryInformation,
                                         int minRecordNumber,
                                         int maxRecordNumber,
                                         int batchSize,
                                         int randomBatches) {
        this(databaseConnectionInformation.getDriver(), databaseConnectionInformation.getUrl(),
                databaseConnectionInformation.getUsername(), databaseConnectionInformation.getPassword(),
                dataQueryInformation.getQuery(), dataQueryInformation.getIdColumn(),
                dataQueryInformation.getNoteColumn(), minRecordNumber, maxRecordNumber, batchSize, randomBatches);
    }

    /**
     * Initialize the reader using the provided connection and query information, including the size of each batch.
     *
     * @param driver JDBC driver class
     * @param url JDBC connection URL
     * @param username database user name.
     * @param password database user password.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     * @param minRecordNumber starting row number for record retrieval.
     * @param maxRecordNumber ending row number for record retrieval.
     * @param batchSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     */
    public BatchDatabaseCollectionReader(String driver, String url, String username, String password, String query,
                                         String idColumn, String noteColumn,
                                         int minRecordNumber, int maxRecordNumber, int batchSize) {
        this(driver, url, username, password, query, idColumn, noteColumn, minRecordNumber, maxRecordNumber, batchSize, 0);
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
     * @param minRecordNumber starting row number for record retrieval.
     * @param maxRecordNumber ending row number for record retrieval.
     * @param batchSize size of each batch to retrieve at a time.  Optimal size will vary with the database environment.
     * @param randomBatches number of random batches to execute, rather then accessing the data sequentially.  Setting
     *                      this parameter to a value less than one will cause the reader to produce no random batches.
     */
    public BatchDatabaseCollectionReader(String driver, String url, String username, String password, String query,
                                         String idColumn, String noteColumn,
                                         int minRecordNumber, int maxRecordNumber, int batchSize, int randomBatches) {
        super(driver, url, username, password, query, idColumn, noteColumn);
        validateParams(minRecordNumber, maxRecordNumber, batchSize);
        this.minRecordNumber = minRecordNumber;
        this.maxRecordNumber = maxRecordNumber;
        this.batchSize = batchSize;
        this.randomBatches = randomBatches;
        this.baseQuery = this.query;
    }

    /**
     * Validate the parameters that will initialize this object.
     *
     * @param minRecordNumber Minimum record ID where processing will start
     * @param maxRecordNumber Maximum record ID where processing will start
     * @param batchSize Size of each batch to be executed
     */
    protected void validateParams(int minRecordNumber, int maxRecordNumber, int batchSize) {
        if(minRecordNumber < 0) {
            throw new IllegalArgumentException("Minimum record ID must be 0 or greater!");
        }
        if(maxRecordNumber < 0) {
            throw new IllegalArgumentException("Max record ID must be 0 or greater!");
        }
        if(batchSize < 0) {
            throw new IllegalArgumentException("Batch size must be 0 or greater!");
        }
    }

    /**
     * This method is called during initialization. Subclasses should override it to perform one-time startup logic.
     *
     * @throws org.apache.uima.resource.ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        this.minRecordNumber = (Integer) getConfigParameterValue(Param.MINIMUM_RECORD_NUMBER.getName());
        this.maxRecordNumber = (Integer) getConfigParameterValue(Param.MAXIMUM_RECORD_NUMBER.getName());
        this.batchSize = (Integer) getConfigParameterValue(Param.BATCH_SIZE.getName());
        if(getConfigParameterValue(Param.RANDOM_BATCHES.getName()) != null) {
            this.randomBatches = (Integer) getConfigParameterValue(Param.RANDOM_BATCHES.getName());
        }
        //Calculate the total number of batches
        int length = (this.maxRecordNumber - this.minRecordNumber);
        if(length < 0) {
            totalNumberOfBatches = 0;
        } else if(length == 0) {
            totalNumberOfBatches = 1;
        } else {
            totalNumberOfBatches = (int) Math.ceil((double) length / (double) batchSize);
        }
        this.baseQuery = this.query;
        if(randomBatches > 0) isRandom = true;
    }

    /**
     * @return true if and only if there are more elements available from this CollectionReader.
     * @throws org.apache.uima.collection.CollectionException
     */
    @Override
    public boolean hasNext() throws CollectionException {
        //if the current row set is empty or the index is still -1 then setup the query for the next set
        while(mRecordList == null || !super.hasNext()) {
            //Setup the next batch query if there is one
            if(isRandom && randomBatches > 0) { //Setup another random batch
                this.query = getQuery(getNextRandomBatchNumber());
                randomBatches--;
            } else if(currentBatch < totalNumberOfBatches) { //Setup another inline batch
                this.query = getQuery(currentBatch);
                currentBatch++;
            } else {    //No more batches or batch data to process, return false
                return false;
            }
            getData(query);
        }

        return true;
    }

    /**
     * Get the next unused random batch number to pull.
     *
     * @return random batch number
     */
    protected int getNextRandomBatchNumber() {
        int randomBatch = random.nextInt(totalNumberOfBatches);
        //make sure we have not picked this batch before
        while(usedBatches.contains(new Integer(randomBatch))){
            if(randomBatch < totalNumberOfBatches - 1) {
                randomBatch++;
            } else {
                randomBatch = random.nextInt(totalNumberOfBatches);
            }
        }
        usedBatches.add(randomBatch);
        return randomBatch;
    }

    /**
     * Does a string substitution on the query, replacing {min} and {max} with the appropriate
     * values for this batch.
     *
     * @param batch the batch number being queried.
     * @return the SQL statement with min/max replaced with appropriate values for this batch.
     */
    protected String getQuery(int batch) {
        int startRecord = (batch * batchSize) + minRecordNumber -1;
        int endRecord = startRecord + batchSize + 1;
        if (endRecord > maxRecordNumber) {
            endRecord = maxRecordNumber + 1;
        }
        return this.baseQuery.replaceAll("\\{min\\}", "" + startRecord).replaceAll("\\{max\\}", "" + endRecord);
    }

    /**
     * Generate the UIMA Collection reader with resources.
     *
     * @return a uima collection reader.
     * @throws org.apache.uima.resource.ResourceInitializationException
     */
    @Override
    public CollectionReader produceCollectionReader() throws ResourceInitializationException {
        Map<String, Object> parameterValues = produceBaseDatabaseCollectionReaderParams();
        parameterValues.put(Param.QUERY.getName(), query);
        parameterValues.put(Param.ID_COLUMN.getName(), idColumn);
        parameterValues.put(Param.NOTE_COLUMN.getName(), noteColumn);
        parameterValues.put(Param.MINIMUM_RECORD_NUMBER.getName(), minRecordNumber);
        parameterValues.put(Param.MAXIMUM_RECORD_NUMBER.getName(), maxRecordNumber);
        parameterValues.put(Param.BATCH_SIZE.getName(), batchSize);
        parameterValues.put(Param.RANDOM_BATCHES.getName(), randomBatches);
        return produceCollectionReader(LeoUtils.getStaticConfigurationParameters(Param.class), parameterValues);
    }

    /**
     * Static inner class for holding parameters information.
     */
    public static class Param extends DatabaseCollectionReader.Param {
        /**
         * Minimum record ID.
         */
        public static ConfigurationParameter MINIMUM_RECORD_NUMBER =
                new ConfigurationParameterImpl("MinimumRecordNumber", "Minimum record id",
                        ConfigurationParameter.TYPE_INTEGER, true, false, new String[] {} );
        /**
         * Maximum record ID.
         */
        public static ConfigurationParameter MAXIMUM_RECORD_NUMBER =
                new ConfigurationParameterImpl("MaximumRecordNumber", "Maximum record id",
                        ConfigurationParameter.TYPE_INTEGER, true, false, new String[] {} );
        /**
         * Number of records to retrieve in each batch.
         */
        public static ConfigurationParameter BATCH_SIZE =
                new ConfigurationParameterImpl("BatchSize", "Number of records to retrieve in each batch",
                        ConfigurationParameter.TYPE_INTEGER, true, false, new String[] {} );
        /**
         * Number of random batches to pull from the total set.
         */
        public static ConfigurationParameter RANDOM_BATCHES =
                new ConfigurationParameterImpl("RandomBatches", "Number of random batches to pull from the total set",
                        ConfigurationParameter.TYPE_INTEGER, false, false, new String[] {});
    }
}
