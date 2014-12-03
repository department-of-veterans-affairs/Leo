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

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * DataSource that creates/manages the database connection and returns a reference to the connection object.
 *
 * User: Thomas Ginter
 * Date: 7/17/14
 * Time: 14:08
 */
public class LeoDataSource implements DataSource {

    /**
     * Database connection object.
     */
    protected Connection mConn = null;
    /**
     * JDBC driver class name.
     */
    protected String mDriver   = null;
    /**
     * JDBC database connection URL.
     */
    protected String mUrl      = null;

    /**
     * Initialize the DataSource with the connection object.
     *
     * @param conn connection object to use
     * @throws SQLException if there is an error getting the MetaData from the connection object
     */
    public LeoDataSource(Connection conn) throws SQLException  {
        this.mConn = conn;
        this.mDriver = mConn.getMetaData().getDriverName();
        this.mUrl = mConn.getMetaData().getURL();
    }

    /**
     * Initialize the DataSource with the connection information.
     *
     * @param driver JDBC driver class name
     * @param url JDBC connection URL
     * @param username database user name
     * @param password database user password
     * @throws SQLException if there is an error connecting to the database
     * @throws ClassNotFoundException if the driver class is not in the classpath
     */
    public LeoDataSource(String driver, String url, String username, String password) throws SQLException, ClassNotFoundException {
        connect(driver, url, username, password);
        this.mDriver = driver;
        this.mUrl = url;
    }

    /**
     * Connect to the database.
     *
     * @param driver Database driver class
     * @param url    URL for the connection.
     * @param user   Username. If null, the connection is made without using a username and password.
     * @param pwd    Password if needed.
     * @throws ClassNotFoundException  thrown if the JDBC driver cannot be found.
     * @throws SQLException     thrown if there is an exception with the query or result set.
     */
    protected void connect(String driver, String url, String user, String pwd) throws ClassNotFoundException, SQLException {
        if(DbUtils.loadDriver(driver)) {

            if (!StringUtils.isBlank(user)) {
                this.mConn = DriverManager.getConnection(url, user, pwd);
            } else {
                this.mConn = DriverManager.getConnection(url);
            }
        } else {
            throw new ClassNotFoundException("Driver not found: " + driver);
        }
    }//connect method

    /**
     * <p>Retrieves the log writer for this <code>DataSource</code>
     * object.
     * <p/>
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.DriverManager</code> class.  When a
     * <code>DataSource</code> object is
     * created, the log writer is initially null; in other words, the
     * default is for logging to be disabled.
     *
     * @return the log writer for this data source or null if
     * logging is disabled
     * @throws java.sql.SQLException if a database access error occurs
     * @see #setLogWriter
     * @since 1.4
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    /**
     * <p>Sets the log writer for this <code>DataSource</code>
     * object to the given <code>java.io.PrintWriter</code> object.
     * <p/>
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source-
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.DriverManager</code> class. When a
     * <code>DataSource</code> object is created the log writer is
     * initially null; in other words, the default is for logging to be
     * disabled.
     *
     * @param out the new log writer; to disable logging, set to null
     * @throws java.sql.SQLException if a database access error occurs
     * @see #getLogWriter
     * @since 1.4
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    /**
     * <p>Sets the maximum time in seconds that this data source will wait
     * while attempting to connect to a database.  A value of zero
     * specifies that the timeout is the default system timeout
     * if there is one; otherwise, it specifies that there is no timeout.
     * When a <code>DataSource</code> object is created, the login timeout is
     * initially zero.
     *
     * @param seconds the data source login time limit
     * @throws java.sql.SQLException if a database access error occurs.
     * @see #getLoginTimeout
     * @since 1.4
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    /**
     * Gets the maximum time in seconds that this data source can wait
     * while attempting to connect to a database.  A value of zero
     * means that the timeout is the default system timeout
     * if there is one; otherwise, it means that there is no timeout.
     * When a <code>DataSource</code> object is created, the login timeout is
     * initially zero.
     *
     * @return the data source login time limit
     * @throws java.sql.SQLException if a database access error occurs.
     * @see #setLoginTimeout
     * @since 1.4
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    /**
     * Return the parent Logger of all the Loggers used by this data source. This
     * should be the Logger farthest from the root Logger that is
     * still an ancestor of all of the Loggers used by this data source. Configuring
     * this Logger will affect all of the log messages generated by the data source.
     * In the worst case, this may be the root Logger.
     *
     * @return the parent Logger for this data source
     * @throws java.sql.SQLFeatureNotSupportedException if the data source does not use <code>java.util.logging</code>.
     * @since 1.7
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    /**
     * <p>Attempts to establish a connection with the data source that
     * this <code>DataSource</code> object represents.
     *
     * @return a connection to the data source
     * @throws java.sql.SQLException if a database access error occurs
     */
    @Override
    public Connection getConnection() throws SQLException {
        return mConn;
    }

    /**
     * <p>Attempts to establish a connection with the data source that
     * this <code>DataSource</code> object represents.
     *
     * @param username the database user on whose behalf the connection is
     *                 being made
     * @param password the user's password
     * @return a connection to the data source
     * @throws java.sql.SQLException if a database access error occurs
     * @since 1.4
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            connect(mDriver, mUrl, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return mConn;
    }

    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     * <p/>
     * If the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper
     * and the wrapped object implements the interface then the result is the
     * wrapped object or a proxy for the wrapped object. Otherwise return the
     * the result of calling <code>unwrap</code> recursively on the wrapped object
     * or a proxy for that result. If the receiver is not a
     * wrapper and does not implement the interface, then an <code>SQLException</code> is thrown.
     *
     * @param iface A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the actual implementing object.
     * @throws java.sql.SQLException If no object found that implements the interface
     * @since 1.6
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper
     * for an object that does. Returns false otherwise. If this implements the interface then return true,
     * else if this is a wrapper then return the result of recursively calling <code>isWrapperFor</code> on the wrapped
     * object. If this does not implement the interface and is not a wrapper, return false.
     * This method should be implemented as a low-cost operation compared to <code>unwrap</code> so that
     * callers can use this method to avoid expensive <code>unwrap</code> calls that may fail. If this method
     * returns true then calling <code>unwrap</code> with the same argument should succeed.
     *
     * @param iface a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly wraps an object that does.
     * @throws java.sql.SQLException if an error occurs while determining whether this is a wrapper
     *                               for an object with the given interface.
     * @since 1.6
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
