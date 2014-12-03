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

import gov.va.vinci.leo.SampleService;
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
    protected DatabaseConnectionInformation dbConnectionInfo = null;
    protected DataManager dataManager = null;
    protected static final int TEST_CORPUS_SIZE = 100;
    protected static final String query = "SELECT id, note FROM notes WHERE id > {min} AND id < {max} ORDER BY id";

    @Before
    public void setup() throws Exception{
        dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:DbCollectionReaderTest;sql.enforce_strict_size=true", "sa", "");
        dataManager = new DataManager(dbConnectionInfo);
        Connection conn = dataManager.getDataSource().getConnection();
        try {
            conn.createStatement().execute("CREATE TABLE PUBLIC.notes (id INTEGER NOT NULL, note VARCHAR(1000), PRIMARY KEY (id))");
            for(int i = 0; i < TEST_CORPUS_SIZE; i++) {
                conn.createStatement().execute("INSERT INTO PUBLIC.notes (id, note) VALUES (" + i + ", '" + new BigInteger(130, new SecureRandom()).toString(32) + "')");
            }
        } catch(Exception e) {
            if(!e.toString().contains("object name already exists:"))
                System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInitialization() throws Exception {
        BatchDatabaseCollectionReader reader =
                (BatchDatabaseCollectionReader) new BatchDatabaseCollectionReader(
                        dbConnectionInfo,
                        new DataQueryInformation(query, "note", "id"),
                        0,
                        TEST_CORPUS_SIZE,
                        10,
                        4)
                        .produceCollectionReader();
        assertNotNull(reader);
        assertTrue(query.equals(reader.baseQuery));
        assertTrue(reader.noteColumn.equals("note"));
        assertTrue(reader.idColumn.equals("id"));
        assertTrue(reader.randomBatches == 4);
        assertTrue(reader.minRecordNumber == 0);
        assertTrue(reader.maxRecordNumber == TEST_CORPUS_SIZE);
        assertTrue(reader.batchSize == 10);
    }

    @Test
    public void testBatchProcessing() throws Exception{
        BatchDatabaseCollectionReader reader =
                (BatchDatabaseCollectionReader) new BatchDatabaseCollectionReader(
                        dbConnectionInfo,
                        new DataQueryInformation(query, "note", "id"),
                        0,
                        TEST_CORPUS_SIZE,
                        10)
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
        for(int i = 0; i < TEST_CORPUS_SIZE; i++) {
            mockCas = ae.newCAS();
            if(reader.hasNext())
                reader.getNext(mockCas);
            csiIterator = mockCas.getAnnotationIndex(mockCas.getTypeSystem().getType(CSI.class.getCanonicalName())).iterator();
            assertTrue(csiIterator.hasNext());
            csi = (CSI) csiIterator.next();
            assertEquals("Expected : " + i + ", but got: " + csi.getID(), csi.getID(), "" + i);
        }
    }
}
