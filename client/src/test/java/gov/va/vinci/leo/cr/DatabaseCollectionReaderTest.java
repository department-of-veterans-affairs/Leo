package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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
import gov.va.vinci.leo.listener.DoNothingListener;
import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.db.DataManager;
import org.apache.uima.util.Progress;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 3/31/16.
 */
public class DatabaseCollectionReaderTest {
    protected static DatabaseConnectionInformation dbConnectionInfo = null;
    protected static DataManager dataManager = null;
    protected static final int TEST_CORPUS_SIZE = 10;
    protected static final String query = "SELECT id, note FROM notes";
    protected static final String username = "sa";
    protected static final String password = "";
    protected static Connection connection = null;

    @Before
    public void setup() throws Exception {
        if (connection != null) return;
        dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:BaseDbCollectionReaderTest;sql.enforce_strict_size=true", username, password);
        dataManager = new DataManager(dbConnectionInfo);
        connection = dataManager.getDataSource().getConnection();
        try {
            connection.createStatement().execute("CREATE TABLE PUBLIC.notes (id INTEGER NOT NULL, note VARCHAR(1000), PRIMARY KEY (id))");
            for (int i = 10; i < (TEST_CORPUS_SIZE + 10); i++) {
                connection.createStatement().execute("INSERT INTO PUBLIC.notes (id, note) VALUES (" + i + ", '" + new BigInteger(130, new SecureRandom()).toString(32) + "')");
            }
        } catch (Exception e) {
            if (!e.toString().contains("object name already exists:")) {
                throw e;
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateParamsNoQuery() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, null, "id", "note");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateParamsNoId() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, query, null, "note");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateParamsNoNote() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, query, "ID", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateParamsBadNoteColumn() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, "seled id, note from RYAN", "ID", "NOTE2");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidateParamsBadIdColumn() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, "seled id, note from RYAN", "ID2", "NOTE");
    }

    @Test
    public void testValidateParams() throws IOException {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, "seled id, note from RYAN", "ID", "NOTE");
    }


    @Test
    public void testInitializeWithDCI() throws Exception {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, query, "id", "NOTE");
        assertNotNull(dcr);
        assertEquals(dbConnectionInfo.getDriver(), dcr.getDriver());
        assertEquals(dbConnectionInfo.getUrl(), dcr.getURL());
        assertEquals(dbConnectionInfo.getUsername(), dcr.getUsername());
        assertEquals(dbConnectionInfo.getPassword(), dcr.getPassword());
        assertEquals(query, dcr.getQuery());
        assertEquals("id", dcr.getIdColumn());
        assertEquals("NOTE", dcr.getNoteColumn());
    }

    @Test
    public void testInitializeWithDciDqi() throws Exception {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo,
                new DataQueryInformation(query, "note", "id"));
        assertNotNull(dcr);
        assertEquals(dbConnectionInfo.getDriver(), dcr.getDriver());
        assertEquals(dbConnectionInfo.getUrl(), dcr.getURL());
        assertEquals(dbConnectionInfo.getUsername(), dcr.getUsername());
        assertEquals(dbConnectionInfo.getPassword(), dcr.getPassword());
        assertEquals(query, dcr.getQuery());
        assertEquals("id", dcr.getIdColumn());
        assertEquals("note", dcr.getNoteColumn());
    }

    @Test
    public void testRunWithPropertySetting() throws Exception {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader()
                .setQuery(query)
                .setIdColumn("id")
                .setNoteColumn("note")
                .setDriver(dbConnectionInfo.getDriver())
                .setURL(dbConnectionInfo.getUrl())
                .setUsername(dbConnectionInfo.getUsername())
                .setPassword(dbConnectionInfo.getPassword());
        assertNotNull(dcr);
        assertEquals(dbConnectionInfo.getDriver(), dcr.getDriver());
        assertEquals(dbConnectionInfo.getUrl(), dcr.getURL());
        assertEquals(dbConnectionInfo.getUsername(), dcr.getUsername());
        assertEquals(dbConnectionInfo.getPassword(), dcr.getPassword());
        assertEquals(query, dcr.getQuery());
        assertEquals("id", dcr.getIdColumn());
        assertEquals("note", dcr.getNoteColumn());

        //Create the mock client and run it
        DoNothingListener listener = new DoNothingListener();
        assertNotNull(listener);
        MockClient client = new MockClient();
        assertNotNull(client);
        client.run(dcr, listener);
        //Check that all records were pulled
        assertEquals(TEST_CORPUS_SIZE, listener.getNumReceived());
        Progress progress = dcr.getProgress()[0];
        assertEquals(0, progress.getCompleted());
        assertEquals(0, progress.getTotal());
        assertEquals("row(s)", progress.getUnit());
    }
}