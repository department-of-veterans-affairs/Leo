package gov.va.vinci.leo.tools.db;

/*
 * #%L
 * Leo Core
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
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Handle the database connection, query, and result set.  Returns one row at a time in
 * the form of an ArrayList<String> via  the getRow method.
 *
 * @author thomasginter
 */
public class DataManager extends QueryRunner {

    /**
     * Query results handler.
     */
    protected LeoArrayListHandler handler = new LeoArrayListHandler();

    /**
     * Constructor.
     * @param driver   the jdbc driver class.
     * @param url      the jdbc connection url.
     * @param user     the connection username.
     * @param pwd      the connection password.
     * @throws ClassNotFoundException  thrown if the JDBC driver cannot be found.
     * @throws IllegalAccessException  thrown if there is an access exception making the JDBC driver connection.
     * @throws InstantiationException  thrown if the JDBC driver cannot be instantiated.
     * @throws SQLException     thrown if there is an exception with the query or result set.
     */
    public DataManager(String driver, String url, String user, String pwd) throws ClassNotFoundException, SQLException {
        super( new LeoDataSource(driver, url, user, pwd));

        if (StringUtils.isBlank(driver))
            throw new IllegalArgumentException("Missing required parameter driver!");
        if (StringUtils.isBlank(url))
            throw new IllegalArgumentException("Missing required parameter URL!");
    }

    /**
     * Constructor with database connection information.
     * @param databaseConnectionInformation  the connection information object.
     * @throws ClassNotFoundException  thrown if the JDBC driver cannot be found.
     * @throws IllegalAccessException  thrown if there is an access exception making the JDBC driver connection.
     * @throws InstantiationException  thrown if the JDBC driver cannot be instantiated.
     * @throws SQLException     thrown if there is an exception with the query or result set.
     */
    public DataManager(DatabaseConnectionInformation databaseConnectionInformation) throws ClassNotFoundException, SQLException {
        this(databaseConnectionInformation.getDriver(),
                databaseConnectionInformation.getUrl(),
                databaseConnectionInformation.getUsername(),
                databaseConnectionInformation.getPassword());
    }

    /**
     * Initialize the DataManager with a DataSource object.
     *
     * @param dataSource the data source to use for the query.
     */
    public DataManager(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Execute a SQL query against the database and capture the ResultSet.
     *
     * @param sql   the sql to execute.
     * @return List of Object[], each array represents one row of results.
     * @throws java.sql.SQLException if a SQL error occurs.
     */
    public List<Object[]> query(String sql) throws SQLException {
        return this.query(sql, handler);
    }//query method

    /**
     * Execute a SQL query against the database and capture the ResultSet.
     *
     * @param sql   the sql to execute.
     * @param params the parameters to pass to the sql query.
     * @throws java.sql.SQLException if a SQL error occurs.
     * @return List of Object[], each array represents one row of results.
     */
    public List<Object[]> query(String sql, Object...params) throws SQLException {
        return this.query(sql, handler, params);
    }//query method

    /**
     * Return the MetaData from the ResultSet of the query that was last executed.
     *
     * @return the ResultSetMetaData from the last query.
     */
    public ResultSetMetaData getResultSetMetaData() {
        return handler.getResultSetMetaData();
    }

    /**
     * Return a reference to the LeoArrayListHandler which manages query execution and results.
     *
     * @return reference to the LeoArrayListHandler
     */
    public LeoArrayListHandler getHandler() {
        return handler;
    }

}//DataManager class
