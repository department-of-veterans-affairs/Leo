package gov.va.vinci.leo.tools.db;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.sql.*;
import java.util.List;

/**
 * Helper class for paging through SQL Server records. This uses the built-in offset/fetch next functionality
 * that is available in SQL Server 2008+
 *
 * Note: This closes the connection after getting a page, and re-opens it before getting the next page.
 *
 */
public class SQLServerPagedQuery {
    private String driver;
    private String url;
    private String username;
    private String password;
    private String query;
    private Integer pageSize;
    ArrayListHandler resultSetHandler = new ArrayListHandler();


    /**
     * Constructor
     * @param driver    the SQL Server jdbc driver class name
     * @param url       the SQL Server connection URL.
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     * @throws ClassNotFoundException
     */
    public SQLServerPagedQuery(String driver, String url, String query, Integer pageSize) {
        this(driver, url, null, null, query , pageSize);
    }

    /**
     * Constructor
     * @param driver    the SQL Server jdbc driver class name
     * @param url       the SQL Server connection URL.
     * @param username  the username to use for the connection.
     * @param password  the password to use for the connection.
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     * @throws ClassNotFoundException
     */
    public SQLServerPagedQuery(String driver, String url, String username, String password, String query, Integer pageSize) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.query = query;
        this.pageSize = pageSize;
    }


    /**
     * Get the query needed to get a specific page of records from the database.
     * @param pageNumber which page to get. Page index starts at 0.
     * @return The SQL Server query to get the page of records.
     * @throws SQLException
     */
    public String getPageQuery(Integer pageNumber){
        int offsetRows = pageSize * pageNumber;
        return query + "\n\t\tOFFSET " + offsetRows + " ROWS\n" +
                  "\t\tFETCH NEXT " + pageSize + " ROWS ONLY;";
    }

    /**
     * Get a specific page of records from the database.
     * @param pageNumber which page to get. Page index starts at 0.
     * @return a list with each element being an array of objects representing a single row.
     * @throws SQLException
     */
    public List<Object[]> getPage(Integer pageNumber) throws SQLException, ClassNotFoundException {

        Class.forName(driver);
        QueryRunner run = new QueryRunner();
        Connection conn = getConnection(); // open a connection
        int offsetRows = pageSize * pageNumber;

        try{
            List<Object[]> result = run.query(
                    conn, query + "\n\t\tOFFSET " + offsetRows + " ROWS\n" +
                            "\t\tFETCH NEXT " + pageSize + " ROWS ONLY;", resultSetHandler);
            return result;
        } finally {
            // Use this helper method so we don't have to check for null
            DbUtils.close(conn);
        }
    }

    protected Connection getConnection() throws SQLException {
        if (username != null || password  != null) {
            return DriverManager.getConnection(url, username, password);
        } else {
            return DriverManager.getConnection(url);
        }
    }
}
