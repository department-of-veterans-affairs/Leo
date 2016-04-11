package gov.va.vinci.leo.cr;

import gov.va.vinci.leo.MockClient;
import gov.va.vinci.leo.listener.DoNothingListener;
import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.db.DataManager;
import org.apache.uima.util.Progress;
import org.junit.Before;
import org.junit.Test;

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
            if (!e.toString().contains("object name already exists:"))
                System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInitializeWithDCI() throws Exception {
        DatabaseCollectionReader dcr = new DatabaseCollectionReader(dbConnectionInfo, query, "id", "note");
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