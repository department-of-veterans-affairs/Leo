package gov.va.vinci.leo.model;

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


/**
 * Holds information needed to make a connection
 * to a database.
 *
 * @author vhaislcornir
 */
public class DatabaseConnectionInformation {

    /**
     * JDBC Driver for the connection.
     */
    private String driver;

    /**
     * URL for the connection.
     */
    private String url;

    /**
     * Username for the connection.
     */
    private String username;

    /**
     * Password for the connection.
     */
    private String password;

    /**
     * Validation query if validating the connection.
     */
    private String validationQuery = null;


    /**
     * Constructor.
     * @param driver the java driver name (ie "com.mysql.jdbc.Driver")
     * @param url  the jdbc connection url (ie "jdbc:mysql://hostname:port/dbname")
     * @param username the connection username (or null)
     * @param password the connection password (or null)
     */
    public DatabaseConnectionInformation(String driver, String url,
                                         String username, String password) {
        super();
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor.
     * @param driver the java driver name (ie "com.mysql.jdbc.Driver")
     * @param url  the jdbc connection url (ie "jdbc:mysql://hostname:port/dbname")
     * @param username the connection username (or null)
     * @param password the connection password (or null)
     * @param validationQuery a query to run that validation a connection is established. (ie select 1)
     */
    public DatabaseConnectionInformation(String driver, String url,
                                         String username, String password, String validationQuery) {
        this(driver, url, username, password);
        this.validationQuery = validationQuery;
    }

    /**
     * Get database driver classname.
     *
     * @return the database driver classname
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Set database driver classname.
     *
     * @param driver the java driver name (ie "com.mysql.jdbc.Driver")
     */
    public DatabaseConnectionInformation setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    /**
     * Get the connection URL.
     *
     * @return the connection url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the connection URL.
     *
     * @param url the jdbc connection url
     */
    public DatabaseConnectionInformation setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Get the connection username.
     *
     * @return the connection username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the connection username.
     *
     * @param username the connection username
     */
    public DatabaseConnectionInformation setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Get the connection password.
     *
     * @return the connection password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the connection password.
     *
     * @param password the connection password.
     */
    public DatabaseConnectionInformation setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Get the validation query for this connection.
     *
     * @return validation query for this connection.
     */
    public String getValidationQuery() {
        return validationQuery;
    }

    /**
     * Set the validation query for this connection.
     *
     * @param validationQuery the validation query for this connection.
     */
    public DatabaseConnectionInformation setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
        return this;
    }

}
