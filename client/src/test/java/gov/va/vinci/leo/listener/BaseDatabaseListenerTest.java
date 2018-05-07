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

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.types.CSI;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class BaseDatabaseListenerTest {

    String dbName = RandomStringUtils.randomAlphabetic(5);

    DatabaseConnectionInformation dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + dbName + "2112testdb;sql.enforce_strict_size=true", "sa", "");

    DatabaseConnectionInformation dbConnectionInfoWithValidation = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + dbName + "2112testdb;sql.enforce_strict_size=true", "sa", "", "select count(*) from dummy_table");

    CAS cas = null;
    String rootDirectory = "";

    Connection conn = null;

    /**
     * Setup an in-memory db to test against with a simple schema.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                SampleService.simpleServiceDefinition().getAnalysisEngineDescription()
        );
        cas = ae.newCAS();
        cas.setDocumentText("a b c");
        CSI csi = new CSI(cas.getJCas());
        csi.setID("1");
        csi.setBegin(0);
        csi.setEnd(5);
        csi.addToIndexes();
        ae.process(cas);
        Class.forName(dbConnectionInfo.getDriver()).newInstance();

        conn = DriverManager.getConnection(dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(), dbConnectionInfo.getPassword());
        conn.createStatement().execute("CREATE TABLE DUMMY_TABLE ( col1 varchar(100), col2 varchar(100), col3 varchar(100))");
    }

    @Test
    public void oneRecordSimpleTest() throws Exception {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);
        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);
        listener.collectionProcessComplete(null);

        ResultSet rs = conn.createStatement().executeQuery("select * from dummy_table");
        assertTrue(rs.next());
        assertEquals(rs.getString(1), "a");
    }

    @Test
    public void batchFlushingTest() throws Exception {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);
        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        /** Partial batch, should have only persisted 2 of the 3. **/
        ResultSet rs = conn.createStatement().executeQuery("select count(*) from dummy_table");
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 2);

        listener.collectionProcessComplete(null);
        /** Now the third one should be there. */
        rs = conn.createStatement().executeQuery("select count(*) from dummy_table");
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 3);
    }

    @Test
    public void validationTest() throws Exception {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfoWithValidation, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(true);
        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        listener.onBeforeMessageSend(new MockUimaASProcessStatus());
        listener.entityProcessComplete(cas, null);

        /** Partial batch, should have only persisted 2 of the 3. **/
        ResultSet rs = conn.createStatement().executeQuery("select count(*) from dummy_table");
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 2);

        listener.collectionProcessComplete(null);
        /** Now the third one should be there. */
        rs = conn.createStatement().executeQuery("select count(*) from dummy_table");
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 3);
    }


    @Test(expected = IllegalArgumentException.class)
    public void badConstructor1Test() {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(-1)
                .setValidateConnectionEachBatch(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void badConstructor2Test() {
        TestDatabaseListener listener = new TestDatabaseListener(null, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(1)
                .setValidateConnectionEachBatch(false);
    }

    @Test(expected = RuntimeException.class)
    public void collectionProcessingCompleteExceptionTest() {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);

        listener.onBeforeMessageSend(null);
        listener.entityProcessComplete(cas, null);

        listener.ps = null;
        listener.collectionProcessComplete(null);
    }

    @Test(expected = ClassNotFoundException.class)
    public void badValidateConnectionTest() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        TestDatabaseListener listener = new TestDatabaseListener(new DatabaseConnectionInformation("org.hsqldb.jdbcdfsgfdg Driver", "jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true", "", ""),
                "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);
        listener.validateConnection();
    }

    @Test(expected = SQLException.class)
    public void badValidateConnection2Test() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        TestDatabaseListener listener = new TestDatabaseListener(
                new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true", "", "", "select * from DUMMY_TABLE"),
                "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)"
        )
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);
        listener.validateConnection();
    }

    @Test
    public void testCreateTable() throws Exception {
        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)")
                .setBatchSize(2)
                .setValidateConnectionEachBatch(false);
        assertNotNull(listener);
        HashMap<String, String> colMap = new HashMap<String, String>(2);
        colMap.put("id", "integer");
        colMap.put("note", "varchar(1000)");
        listener.createTable(false, "PUBLIC", "create_test_table", colMap);
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM create_test_table");
        assertNotNull(rs);
        int numCols = rs.getMetaData().getColumnCount();
        assertEquals(2, numCols);
        assertEquals(2, listener.getBatchSize());
    }

    @Test
    public void testFieldList() {
        List<DatabaseField> fields = new ArrayList<>();

        fields.add(new DatabaseField("f1", "varchar(10)"));
        fields.add(new DatabaseField("f2", "datetime"));
        fields.add(new DatabaseField("number1", "bigdecimal(20)"));

        TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "mydb", "myTable", fields);
        assertEquals("INSERT INTO mydb.myTable ( f1, f2, number1 ) VALUES (  ?, ?, ? ) ;", listener.preparedStatementSQL);

        listener = new TestDatabaseListener(dbConnectionInfo, null, "myTable", fields);
        assertEquals("INSERT INTO myTable ( f1, f2, number1 ) VALUES (  ?, ?, ? ) ;", listener.preparedStatementSQL);

    }

    public class TestDatabaseListener extends BaseDatabaseListener {

        public TestDatabaseListener(DatabaseConnectionInformation databaseConnectionInformation, String databaseName, String databaseTable,  List<DatabaseField> fieldList) {
            super(databaseConnectionInformation, databaseName, databaseTable, fieldList);
        }

        public TestDatabaseListener(
                DatabaseConnectionInformation databaseConnectionInformation, String preparedStatementSQL) {
            super(databaseConnectionInformation, preparedStatementSQL);
        }

        @Override
        protected List<Object[]> getRows(CAS aCas) {
            List<Object[]> results = new ArrayList<Object[]>();
            results.add(new Object[]{"a", "b", "c"});
            return results;
        }

    }


}
