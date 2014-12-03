package gov.va.vinci.leo.integration;

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

import gov.va.vinci.leo.listener.BaseDatabaseListener;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.cas.CAS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Thomas Ginter
 * Date: 11/5/13
 * Time: 12:48 PM
 */
public class IntegrationClientDbListener  extends IntegrationClient{

    public void run(Services service) throws Exception {

        super.run(service);
    }

    protected UimaAsBaseCallbackListener[] getListeners() {
        try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DatabaseConnectionInformation databaseConnectionInformation = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:aname", "sa", "");

        TestDatabaseListener listener = new TestDatabaseListener(databaseConnectionInformation, "insert into TEST_TABLE values (?, ?, ?);", 1, false);
        listener.setExitOnError(true);

        UimaAsBaseCallbackListener[] list = new UimaAsBaseCallbackListener[1];
        list[0] = listener;
        return list;
    }

    public static void main(String[] args) {
        try {
            new IntegrationClientDbListener().run(Services.HEART_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected class TestDatabaseListener extends BaseDatabaseListener {

        public TestDatabaseListener(DatabaseConnectionInformation databaseConnectionInformation, String preparedStatementSQL, int batchSize, boolean validateConnectionEachBatch) {
            super(databaseConnectionInformation, preparedStatementSQL, batchSize, validateConnectionEachBatch);
        }

        public List<Object[]> getRows(CAS aCas) {
            System.out.println("get rows called.");
            List<Object[]> rows = new ArrayList<Object[]>();
            rows.add(new Object[]{"LONG FIELD TEST 1234567890", "LONG FIELD TEST 1234567890", "LONG FIELD TEST 1234567890"});
            return rows;
        }
    }
}
