package gov.va.vinci.leo.tools.db;

/**
 * Helper class for paging through SQL Server records. This uses the built-in offset/fetch next functionality
 * that is available in SQL Server 2008+
 *
 * Note: This closes the connection after getting a page, and re-opens it before getting the next page.
 *
 */
public class SQLServerPagedQuery {
    private String query;
    private Integer pageSize;

    /**
     * Constructor
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     */
    public SQLServerPagedQuery(String query, Integer pageSize) {
        this.query = query;
        this.pageSize = pageSize;
    }


    /**
     * Get the query needed to get a specific page of records from the database.
     * @param pageNumber which page to get. Page index starts at 0.
     * @return The SQL Server query to get the page of records.
     */
    public String getPageQuery(Integer pageNumber){
        int offsetRows = pageSize * pageNumber;
        return query + "\n\t\tOFFSET " + offsetRows + " ROWS\n" +
                  "\t\tFETCH NEXT " + pageSize + " ROWS ONLY;";
    }

}
