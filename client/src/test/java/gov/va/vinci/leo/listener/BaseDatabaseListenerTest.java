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

import gov.va.vinci.leo.descriptors.AnalysisEngineFactory;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseDatabaseListenerTest {

	String dbName = RandomStringUtils.randomAlphabetic(5);

	DatabaseConnectionInformation dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + dbName + "2112testdb;sql.enforce_strict_size=true", "sa", "");
	
	DatabaseConnectionInformation dbConnectionInfoWithValidation = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + dbName + "2112testdb;sql.enforce_strict_size=true", "sa", "", "select count(*) from dummy_table");
	
	Connection conn = null;
	CAS cas = null;
    String rootDirectory = "";


    /**
	 * Setup an in-memory db to test against with a simple schema.
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }

        Class.forName(dbConnectionInfo.getDriver()).newInstance();
		
		conn = DriverManager.getConnection(dbConnectionInfo.getUrl(), dbConnectionInfo.getUsername(), dbConnectionInfo.getPassword());
		conn.createStatement().execute("CREATE TABLE DUMMY_TABLE ( col1 varchar(100), col2 varchar(100), col3 varchar(100))");
		AnalysisEngineDescription aed = AnalysisEngineFactory.generateAED(rootDirectory + "src/test/resources/desc/gov/va/vinci/leo/ae/WhitespaceTokenizerDescriptor.xml", false);
		
		cas = CasCreationUtils.createCas(aed);
	}
	
	@Test
	public void oneRecordSimpleTest() throws Exception {
		TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 2, false);		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		listener.collectionProcessComplete(null);
		
		ResultSet rs = conn.createStatement().executeQuery("select * from dummy_table");
		assertTrue(rs.next());
		assertEquals(rs.getString(1), "a");
	}

	@Test
	public void batchFlushingTest() throws Exception {
		TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 2, false);		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
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
		TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfoWithValidation, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 2, true);		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
		listener.onBeforeMessageSend(new MockUimaASProcessStatus());
		listener.entityProcessComplete(cas,  null);
		
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
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void badConstructor1Test() {
		TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", -1, false);
	}

	@Test(expected=IllegalArgumentException.class)
	public void badConstructor2Test() {
		TestDatabaseListener listener = new TestDatabaseListener(null, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 1, false);
	}
	
	@Test(expected=RuntimeException.class)
	public void collectionProcessingCompleteExceptionTest() {
		TestDatabaseListener listener = new TestDatabaseListener(dbConnectionInfo, "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 2, false);		
		
		listener.onBeforeMessageSend(null);
		listener.entityProcessComplete(cas,  null);
		
		listener.ps = null;
		listener.collectionProcessComplete(null);
	}

	@Test(expected=ClassNotFoundException.class)
	public void badValidateConnectionTest() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		TestDatabaseListener listener = new TestDatabaseListener(new DatabaseConnectionInformation("org.hsqldb.jdbcdfsgfdg Driver", "jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true", "", ""), "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 1, false);
		listener.validateConnection();
	}

	@Test(expected=SQLException.class)
	public void badValidateConnection2Test() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		TestDatabaseListener listener = new TestDatabaseListener(new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true", "", "", "select * from DUMMY_TABLE"), "INSERT INTO DUMMY_TABLE (col1, col2, col3) values (?, ?, ?)", 1, false);
		listener.validateConnection();
	}
	
	public class TestDatabaseListener extends BaseDatabaseListener {

		public TestDatabaseListener(
				DatabaseConnectionInformation databaseConnectionInformation,
				String preparedStatementSQL, int validateConnectionNumberOfCAS,
				boolean validateAfterEachBatch) {
			super(databaseConnectionInformation, preparedStatementSQL,
					validateConnectionNumberOfCAS, validateAfterEachBatch);
		}

		@Override
		protected List<Object[]> getRows(CAS aCas) {
			List<Object[]> results = new ArrayList<Object[]>();
			results.add(new Object[] {"a", "b", "c"});
			return results;
		}
		
	}


}
