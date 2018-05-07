package gov.va.vinci.leo.tools.db;

/*
 * #%L
 * Leo Core
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

import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 4/7/16.
 */
public class LeoDataSourceTest {

    protected static Connection connection = null;

    @Before
    public void setup() throws Exception {
        if(connection != null) return;
        DatabaseConnectionInformation dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:JdbcScriptRunnerTest;sql.enforce_strict_size=true", "sa", "");
        connection = new DataManager(dbConnectionInfo).getDataSource().getConnection();
    }

    @Test
    public void testLeoDataSource() throws Exception {
        LeoDataSource dataSource = new LeoDataSource(connection);
        assertNotNull(dataSource);
        PrintWriter pw = new PrintWriter(new StringWriter());
        assertNotNull(pw);
        dataSource.setLogWriter(pw);
        assertEquals(null, dataSource.getLogWriter());
        dataSource.setLoginTimeout(25);
        assertEquals(0, dataSource.getLoginTimeout());
        assertEquals(null, dataSource.getParentLogger());
    }
}
