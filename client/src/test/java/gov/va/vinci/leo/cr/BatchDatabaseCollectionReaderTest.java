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

import gov.va.vinci.leo.MockClient;
import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.listener.DoNothingListener;
import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.db.DataManager;
import gov.va.vinci.leo.types.CSI;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * User: Thomas Ginter
 * Date: 7/28/14
 * Time: 15:33
 */
public class BatchDatabaseCollectionReaderTest {
    protected static DatabaseConnectionInformation dbConnectionInfo = null;
    protected static DataManager dataManager = null;
    protected static final int TEST_CORPUS_SIZE = 10;
    protected static final int MIN_RECORD_NUMBER = 0;
    protected static final int BATCH_SIZE = 2;
    protected static final String query = "SELECT id, note FROM notes WHERE id > {min} AND id < {max} ORDER BY id";
    protected static Connection connection = null;

    @Before
    public void setup() throws Exception{
        if(connection != null) return;
        dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:DbCollectionReaderTest;sql.enforce_strict_size=true", "sa", "");
        dataManager = new DataManager(dbConnectionInfo);
        connection = dataManager.getDataSource().getConnection();
        try {
            connection.createStatement().execute("CREATE TABLE PUBLIC.notes (id INTEGER NOT NULL, note VARCHAR(1000), PRIMARY KEY (id))");
            for(int i = 10; i < (TEST_CORPUS_SIZE+10); i++) {
                connection.createStatement().execute("INSERT INTO PUBLIC.notes (id, note) VALUES (" + i + ", '" + new BigInteger(130, new SecureRandom()).toString(32) + "')");
            }
        } catch(Exception e) {
            if(!e.toString().contains("object name already exists:"))
                System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInitialization() throws Exception {
        BatchDatabaseCollectionReader reader = (BatchDatabaseCollectionReader) new BatchDatabaseCollectionReader(dbConnectionInfo,
                    new DataQueryInformation(query, "note", "id"), MIN_RECORD_NUMBER, TEST_CORPUS_SIZE, BATCH_SIZE)
                    .setRandomBatches(4)
                    .produceCollectionReader();
        assertNotNull(reader);
        assertTrue(query.equals(reader.baseQuery));
        assertTrue(reader.noteColumn.equals("note"));
        assertTrue(reader.idColumn.equals("id"));
        assertTrue(reader.randomBatches == 4);
        assertTrue(reader.minRecordNumber == MIN_RECORD_NUMBER);
        assertTrue(reader.maxRecordNumber == TEST_CORPUS_SIZE);
        assertTrue(reader.batchSize == BATCH_SIZE);
    }

    @Test
    public void testBatchProcessing() throws Exception{
        BatchDatabaseCollectionReader reader = (BatchDatabaseCollectionReader) new BatchDatabaseCollectionReader(dbConnectionInfo,
                        new DataQueryInformation(query, "note", "id"), MIN_RECORD_NUMBER, TEST_CORPUS_SIZE, BATCH_SIZE)
                        .produceCollectionReader();
        assertNotNull(reader);
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition()
                             .getAnalysisEngineDescription()
        );
        CAS mockCas;
        FSIterator<AnnotationFS> csiIterator;
        CSI csi;
        //Iterate through the collection
        for(int i = 10; i < (TEST_CORPUS_SIZE+10); i++) {
            mockCas = ae.newCAS();
            if(reader.hasNext()) {
                reader.getNext(mockCas);
                csiIterator = mockCas.getAnnotationIndex(mockCas.getTypeSystem().getType(CSI.class.getCanonicalName())).iterator();
                assertTrue(csiIterator.hasNext());
                csi = (CSI) csiIterator.next();
                assertEquals("Expected : " + i + ", but got: " + csi.getID(), csi.getID(), "" + i);
            }
        }
    }

    @Test
    public void testRandomBatchProcessing() throws Exception {
        BatchDatabaseCollectionReader reader = new BatchDatabaseCollectionReader(
                dbConnectionInfo,
                new DataQueryInformation(query, "note", "id"), 0, 1, 0)
                .setMinRecordNumber(MIN_RECORD_NUMBER)
                .setBatchSize(BATCH_SIZE)
                .setMaxRecordNumber(TEST_CORPUS_SIZE)
                .setRandomBatches(2);
        assertNotNull(reader);
        assertTrue(query.equals(reader.baseQuery));
        assertTrue(reader.noteColumn.equals("note"));
        assertTrue(reader.idColumn.equals("id"));
        assertEquals(2, reader.getRandomBatches());
        assertEquals(MIN_RECORD_NUMBER, reader.getMinRecordNumber());
        assertEquals(TEST_CORPUS_SIZE, reader.getMaxRecordNumber());
        assertEquals(BATCH_SIZE, reader.getBatchSize());
        //Init the listener and mock client
        DoNothingListener listener = new DoNothingListener();
        MockClient client = new MockClient(listener);
        assertNotNull(client);
        client.run(reader);
    }
}
