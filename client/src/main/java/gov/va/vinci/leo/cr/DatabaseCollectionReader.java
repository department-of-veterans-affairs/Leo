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
import gov.va.vinci.leo.types.CSI;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.util.Progress;

import java.io.IOException;
import java.util.Map;

/**
 * CollectionReader that executes a SQL query one time to pull the needed data from the database.  Assumes one record
 * per row of the ResultSet.
 *
 * User: Thomas Ginter
 * Date: 7/11/14
 * Time: 17:08
 */
public class DatabaseCollectionReader extends BaseDatabaseCollectionReader {
    /**
     * Query to be used to get the data.
     */
    protected String query = null;
    /**
     * Zero based index of the ID column in the query.
     */
    protected String idColumn = null;
    /**
     * Zero based index of the note column in the query.
     */
    protected String noteColumn = null;

    /**
     * Default constructor used during UIMA initialization.
     */
    public DatabaseCollectionReader() { /** Do Nothing **/ }

    /**
     * Initialize with the database connection and query information.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param dataQueryInformation database query for retrieving results, assumes one record per row.
     */
    public DatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation, DataQueryInformation dataQueryInformation) {
        super(databaseConnectionInformation);
        if(dataQueryInformation == null) {
            throw new IllegalArgumentException("DataQueryInformation argument cannot be null!");
        }
        validateParams(dataQueryInformation.getQuery(), dataQueryInformation.getIdColumn(), dataQueryInformation.getNoteColumn());
        this.query     = dataQueryInformation.getQuery();
        this.idColumn = dataQueryInformation.getIdColumn();
        this.noteColumn = dataQueryInformation.getNoteColumn();
    }

    /**
     * Initialize with the database connection and query information.
     *
     * @param databaseConnectionInformation database connection information to use.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     */
    public DatabaseCollectionReader(DatabaseConnectionInformation databaseConnectionInformation, String query, String idColumn, String noteColumn) {
        super(databaseConnectionInformation);
        validateParams(query, idColumn, noteColumn);
        this.query = query;
        this.idColumn = idColumn;
        this.noteColumn = noteColumn;
    }

    /**
     * Initialize with the database connection and query information.
     *
     * @param driver JDBC driver class
     * @param url JDBC connection URL
     * @param username database user name.
     * @param password database user password.
     * @param query SQL query used to retrieve the data, one record per row of results.
     * @param idColumn name of the ID column in the SQL query. Assumes only one column to use as the record ID.
     *                 Additional ID fields are propagated through the row results String array of the CSI annotation.
     * @param noteColumn name of the note column in the SQL query. Assumes only one note column.
     */
    public DatabaseCollectionReader(String driver, String url, String username, String password, String query, String idColumn, String noteColumn) {
        super(driver, url, username, password);
        validateParams(query, idColumn, noteColumn);
        this.query = query;
        this.idColumn = idColumn;
        this.noteColumn = noteColumn;
    }

    /**
     * Validate the parameters for the DatabaseCollectionReader to ensure that none of the required arguments are
     * missing or invalid.
     *
     * @param query SQL query to execute to retrieve the data.
     * @param idColumn Name of the column from which the record ID will be maintained.
     * @param noteColumn Name of the column which contains the note.
     */
    protected void validateParams(String query, String idColumn, String noteColumn) {
        if(StringUtils.isBlank(query)) {
            throw new IllegalArgumentException("Query String cannot be blank!");
        }
        if(StringUtils.isBlank(idColumn)) {
            throw new IllegalArgumentException("ID column cannot be blank!");
        }
        if(StringUtils.isBlank(noteColumn)) {
            throw new IllegalArgumentException("Note column cannot blank!");
        }
        if(!query.toLowerCase().contains(idColumn.toLowerCase())) {
            throw new IllegalArgumentException("ID column, " + idColumn + " was not found in the query! " + query);
        }
        if(!query.toLowerCase().contains(noteColumn.toLowerCase())) {
            throw new IllegalArgumentException("Note column, " + noteColumn + ", was not found in the query! " + query);
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
        this.query     = (String) getConfigParameterValue(Param.QUERY.getName());
        this.idColumn = (String) getConfigParameterValue(Param.ID_COLUMN.getName());
        this.noteColumn = (String) getConfigParameterValue(Param.NOTE_COLUMN.getName());
    }

    /**
     * @param aCAS the CAS to populate with the next document;
     * @throws org.apache.uima.collection.CollectionException if there is a problem getting the next and populating the CAS.
     */
    @Override
    public void getNext(CAS aCAS) throws CollectionException, IOException {
        JCas jCas;
        try {
            jCas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        Object[] row = mRecordList.get(mRowIndex++);
        int idIndex = dataManager.getHandler().getColumnIndex(idColumn);
        int noteIndex = dataManager.getHandler().getColumnIndex(noteColumn);
        jCas.setDocumentText(LeoUtils.filterText((String) row[noteIndex], filters));
        CSI csi = new CSI(jCas);
        csi.setBegin(0);
        csi.setEnd(jCas.getDocumentText().length());
        csi.setID(row[idIndex].toString());
        csi.setLocator(query);
        //Set the row data
        StringArray rowArray = new StringArray(jCas, row.length);
        String item;
        for(int index = 0; index < row.length; index++) {
            if(index == noteIndex) {
                item = null;
            } else {
                item = row[index].toString();
            }
            rowArray.set(index, item);
        }
        csi.setRowData(rowArray);
        csi.addToIndexes();
    }

    /**
     * @return true if and only if there are more elements available from this CollectionReader.
     * @throws java.io.IOException
     * @throws org.apache.uima.collection.CollectionException
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        if(mRowIndex < 0) {
            //Execute the query to get the data from the database
            try {
                getData(query);
                if(dataManager.getHandler().getColumnIndex(idColumn) < 0) {
                    throw new CollectionException(
                            "ID column name, " + idColumn + " was not found in the query: " + query,
                            null);
                }
                if(dataManager.getHandler().getColumnIndex(noteColumn) < 0) {
                    throw new CollectionException(
                            "Note column name, " + noteColumn + " was not found in the query: " + query,
                            null);
                }
            } catch (Exception e) {
                throw new CollectionException(e);
            }
        }
        return (mRowIndex < mRecordList.size());
    }

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
    public Progress[] getProgress() {
        return new Progress[0];
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
        return produceCollectionReader(LeoUtils.getStaticConfigurationParameters(Param.class), parameterValues);
    }

    /**
     * Static inner class for holding parameters information.
     */
    public static class Param extends BaseDatabaseCollectionReader.Param {
        /**
         * SQL Query to be executed.
         */
        public static ConfigurationParameter QUERY =
                new ConfigurationParameterImpl("Query", "SQL Query to be executed",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[] {} );
        /**
         * Column index for the id column in the query (zero based).
         */
        public static ConfigurationParameter ID_COLUMN =
                new ConfigurationParameterImpl("IdIndex", "Column index for the id column in the query (zero based)",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[] {});
        /**
         * Column index for the note column in the query (zero based).
         */
        public static ConfigurationParameter NOTE_COLUMN =
                new ConfigurationParameterImpl("NoteIndex", "Column index for the note column in the query (zero based)",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[]{});
    }
}
