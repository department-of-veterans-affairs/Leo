package gov.va.vinci.leo.listener;

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

import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ryancornia
 */
public class BaseDatabaseInsertErrorTest {
    CAS cas = null;

    /**
     * Setup an in-memory db to test against with a simple schema.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(new WhitespaceTokenizer().getLeoAEDescriptor()
                .setParameterSetting("tokenOutputType", Token.class.getCanonicalName())
                .setParameterSetting("wordOutputType", WordToken.class.getCanonicalName())
                .setParameterSetting("tokenOutputTypeFeature", 3)
                .setTypeSystemDescription(new WhitespaceTokenizer().getLeoTypeSystemDescription())
                .getAnalysisEngineDescription());
        cas = CasCreationUtils.createCas(ae.getAnalysisEngineMetaData());

    }

    /**
     * Making sure that if there is an sql error, it is propogated as a runtime exception the gov.va.vinci.leo.listener can propogate.
     *
     * @throws SQLException
     */
    @Test(expected = RuntimeException.class)
    public void testException() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
        DatabaseConnectionInformation databaseConnectionInformation = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:aname", "sa", "");

        TestDatabaseListener listener = new TestDatabaseListener(databaseConnectionInformation, "insert into TEST_TABLE values (?, ?, ?);", 1, false);

        listener.entityProcessComplete(cas, null);
    }


    protected class TestDatabaseListener extends BaseDatabaseListener {

        public TestDatabaseListener(DatabaseConnectionInformation databaseConnectionInformation, String preparedStatementSQL, int batchSize, boolean validateConnectionEachBatch) {
            super(databaseConnectionInformation, preparedStatementSQL, batchSize, validateConnectionEachBatch);
        }

        public List<Object[]> getRows(CAS aCas) {
            List<Object[]> rows = new ArrayList<Object[]>();
            rows.add(new Object[]{"LONG FIELD TEST 1234567890", "LONG FIELD TEST 1234567890", "LONG FIELD TEST 1234567890"});
            return rows;
        }
    }
}
