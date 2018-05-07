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
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLSyntaxErrorException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomasginter on 4/7/16.
 */
public class JdbcScriptRunnerTest {

    public static String scriptText
            = "-- Comment to be ignored\n" +
            "CREATE TABLE PUBLIC.notes (id INTEGER NOT NULL, note VARCHAR(1000), PRIMARY KEY (id));\n" +
            "INSERT INTO PUBLIC.notes (id, note) VALUES (1, '" + new BigInteger(130, new SecureRandom()).toString(32) + "');\n" +
            "INSERT INTO PUBLIC.notes (id, note) VALUES (2, '" + new BigInteger(130, new SecureRandom()).toString(32) + "');\n" +
            "INSERT INTO PUBLIC.notes (id, note) VALUES (3, '" + new BigInteger(130, new SecureRandom()).toString(32) + "');\n" +
            "SELECT id, note FROM notes;";

    protected StringWriter writer = new StringWriter();
    protected static Connection connection = null;

    @Before
    public void setup() throws Exception {
        writer = new StringWriter();
        if (connection != null) return;
        DatabaseConnectionInformation dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:JdbcScriptRunnerTest;sql.enforce_strict_size=true", "sa", "");
        connection = new DataManager(dbConnectionInfo).getDataSource().getConnection();
    }

    @Test
    public void testRunScript() throws Exception {
        JdbcScriptRunner runner = new JdbcScriptRunner(connection, true, true)
                .setErrorLogWriter(new PrintWriter(writer))
                .setLogWriter(new PrintWriter(writer));
        assertNotNull(runner);
        StringReader reader = new StringReader(scriptText);
        assertNotNull(reader);
        runner.runScript(reader);
        String text = writer.toString();
        assertTrue(StringUtils.isNotBlank(text));
        assertTrue(text.contains("CREATE TABLE PUBLIC.notes"));
        assertTrue(text.contains("INSERT INTO PUBLIC.notes (id, note) VALUES (1"));
        assertTrue(text.contains("SELECT id, note FROM notes"));
    }

    @Test(expected = SQLSyntaxErrorException.class)
    public void testRunBadScript() throws Exception {
        JdbcScriptRunner runner = new JdbcScriptRunner(connection, true, true)
                .setErrorLogWriter(new PrintWriter(writer))
                .setLogWriter(new PrintWriter(writer));
        assertNotNull(runner);
        StringReader reader = new StringReader("SOME BAD SQL SCRIPT;");
        assertNotNull(reader);
        runner.runScript(reader);
    }
}
