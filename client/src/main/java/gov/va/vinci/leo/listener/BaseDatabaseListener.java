package gov.va.vinci.leo.listener;

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

import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for inserting data into a database as a UIMA AS Listener
 * <p/>
 * To use this class, you must extend it, and implement List<Object> getRows(CAS). This is what
 * returns the data for each CAS.
 *
 * @author vhaislcornir
 */
public abstract class BaseDatabaseListener extends BaseListener {
    /**
     * The database connection.
     */
    protected Connection conn = null;
    /**
     * The prepared statement for inserting a record.
     */
    protected PreparedStatement ps = null;
    /**
     *   The database connection information.
     */
    protected DatabaseConnectionInformation databaseConnectionInformation = null;
    /**
     * If true, the connection is validated before committing each batch. If false, it is not.
     */
    protected boolean validateConnectionEachBatch = false;
    /**
     * The insert statement sql.
     */
    protected String preparedStatementSQL;
    /**
     * Batch size before doing a commit.
     */
    protected int batchSize = 1;
    /**
     * List for holding a batch until batchSize is reached and the batch is committed.
     */
    protected List<Object[]> batch = new ArrayList<Object[]>();
    /**
     * Logging object of output.
     */
    protected Logger LOG = Logger.getLogger(this.getClass());

    /**
     * Base gov.va.vinci.leo.listener.
     *
     * @param databaseConnectionInformation information on the database to connect to
     * @param preparedStatementSQL          the prepared statement to use for inserts. For example: insert into myTable ( col1, col2, col2) values (?, ?, ?)
     * @param batchSize                     the size of batches to do in each insert. Typically 1000 is a good batch size.
     * @param validateConnectionEachBatch   if true, the databaseConnectionInformation.validationQuery will be run before each batch in addition to making sure the connection
     *                                      is not null.
     */
    public BaseDatabaseListener(DatabaseConnectionInformation databaseConnectionInformation, String preparedStatementSQL, int batchSize, boolean validateConnectionEachBatch) {
        if (batchSize < 1) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }
        if (databaseConnectionInformation == null) {
            throw new IllegalArgumentException("Database Connection Information cannot be null.");
        }
        this.databaseConnectionInformation = databaseConnectionInformation;
        this.validateConnectionEachBatch = validateConnectionEachBatch;
        this.preparedStatementSQL = preparedStatementSQL;
        this.batchSize = batchSize;
    }

    /**
     * Abstract method: You must implement this method.
     *
     * @param aCas the currently processed CAS
     * @return a list of object[]. Each entry in the list is a row to be inserted in the database, and each
     *         element in the object array is the column. This needs to match up with the prepared statement specified in the constructor.
     */
    protected abstract List<Object[]> getRows(CAS aCas);

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS, org.apache.uima.collection.EntityProcessStatus)
     * @param aCas      the CAS containing the processed entity and the analysis results
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        try {
            super.entityProcessComplete(aCas, aStatus);

            // See if we need to validate the connection.
            if (conn == null) {
                validateConnection();
            }

            batch.addAll(getRows(aCas));

            // Batch full, process it!
            if (batch.size() >= this.batchSize) {
                processBatch();
            }
        } catch (Exception e) {
            LOG.error("Error validating the connection or executing the batch", e);
            //If exitOnError is set then kill the client to prevent doing damage to the database
            if(exitOnError)
                System.exit(1);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when a batch is ready to be send to the database. It connects to the database (if needed),
     * created the prepared statement batch, and executes it.
     *
     * @throws ClassNotFoundException  thrown if the JDBC driver cannot be found.
     * @throws IllegalAccessException    thrown if any of the reflection calls throw an exception.
     * @throws SQLException       thrown if  there is an SQL Exception accessing the database.
     * @throws InstantiationException  thrown if the JDBC driver cannot be instantiated.
     */
    protected synchronized void processBatch() throws SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        if (this.validateConnectionEachBatch) {
            validateConnection();
        }
        LOG.info("Dumping DB Batch of (" + batchSize + ") after processing " + numReceived + " records");
        StopWatch clock = new StopWatch();
        clock.start();
        for (Object[] row : batch) {
            int i = 1;
            for (Object o : row) {
                ps.setObject(i, o);
                i++;
            }
            ps.addBatch();
        }
        try {
            ps.executeBatch();
        } catch(SQLException e) {
            LOG.error("Error executing the batch!", e);
            LOG.debug("BATCH DATA:\n\n" + batch.toString()); //Could be a LOT of output so just print if logging is set to debug
            throw e;
        } finally {
            //Always clear the batch to avoid re-executing SQL with errors
            batch.clear();
        }
        clock.stop();
        LOG.info("Finished dumping DB batch: " + clock.toString());
    }

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        try {
            super.collectionProcessComplete(aStatus);

            // Run any partial batch.
            processBatch();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }//collectionProcessComplete method

    /**
     * Method to validate the connection is not null, and if specified, run the validation query against the database.
     * Any connection or query errors are thrown as exceptions.
     *
     * @throws ClassNotFoundException  thrown if the JDBC driver cannot be found.
     * @throws IllegalAccessException    thrown if any of the reflection calls throw an exception.
     * @throws SQLException       thrown if  there is an SQL Exception accessing the database.
     * @throws InstantiationException  thrown if the JDBC driver cannot be instantiated.
     */
    protected void validateConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (conn == null) {
            Class.forName(databaseConnectionInformation.getDriver()).newInstance();
            conn = DriverManager.getConnection(databaseConnectionInformation.getUrl(), databaseConnectionInformation.getUsername(), databaseConnectionInformation.getPassword());
        }

        if (databaseConnectionInformation.getValidationQuery() != null) {
            ResultSet rs = conn.createStatement().executeQuery(databaseConnectionInformation.getValidationQuery());
            if (!rs.next()) {
                throw new SQLException("Validation query failed on connection.");
            }
        }

        ps = conn.prepareStatement(preparedStatementSQL);
    }

    /**
     * Creates the table structure.
     *
     * @param createStatement  The sql create statement.
     * @param dropFirst        If drop, the table is dropped before creation.
     * @param tableName        The resulting table name.
     * @throws java.lang.ClassNotFoundException if the driver class is not in the classpath.
     * @throws java.sql.SQLException if there is an error executing the SQL to drop or create the table.
     * @throws java.lang.InstantiationException if there is an error creating the connection
     * @throws java.lang.IllegalAccessException if there is an error executing the query due to insufficient rights or the database cannot be reached.
     */
    public void createTable(String createStatement, boolean dropFirst, String tableName) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        validateConnection();

        if(dropFirst && StringUtils.isNotBlank(tableName)) {
            conn.createStatement().execute("DROP TABLE " + tableName);
        }//if
        conn.createStatement().execute(createStatement);
    }

    /**
     * Creates the table structure.
     * @param dropFirst        If drop, the table is dropped before creation.
     * @param dbsName          The database name (needed by some databases for the create statement)
     * @param tableName        The resulting table name
     * @param fieldList        A map of column names (key) and types (value) for example "id", "varchar(20)"
     * @throws java.lang.ClassNotFoundException if the driver class is not in the classpath.
     * @throws java.sql.SQLException if there is an error executing the SQL to drop or create the table.
     * @throws java.lang.InstantiationException if there is an error creating the connection
     * @throws java.lang.IllegalAccessException if there is an error executing the query due to insufficient rights or the database cannot be reached.
     */
    public void createTable(   boolean dropFirst,
                               String dbsName,
                               String tableName,
                               Map<String, String> fieldList
                           ) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String createStatement = createCreateStatement(dbsName, tableName, fieldList);
        createTable(createStatement, dropFirst, tableName);
    }//createTable method


    /**
     * Protected method to build a create table statement.
     * @param dbsName       the database name if needed.
     * @param tableName     the table name.
     * @param fieldList     A map of column names (key) and types (value) for example "id", "varchar(20)"
     * @return              The sql create statement.
     */
    protected String createCreateStatement(String dbsName, String tableName, Map<String, String> fieldList) {
        String statement = "CREATE TABLE " + dbsName + "." + tableName + " ( ";
        for (String column : fieldList.keySet())
            statement = statement + column + " " + fieldList.get(column) + ", ";
        statement = statement.substring(0, (statement.length() - 2)) + " ) ;";
        return statement;
    }
}