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
    private Integer firstOffset = 0;
    private Integer maxOffset = 0;



    /**
     * Constructor
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     */
    public SQLServerPagedQuery(String query, Integer pageSize) {
        this (query, pageSize, 0);
    }

    /**
     * Constructor
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     * @param firstOffset the first row to start with. Generally this will be 0.
     */
    public SQLServerPagedQuery(String query, Integer pageSize, Integer firstOffset) {
        this(query, pageSize, firstOffset, -1);
    }


    /**
     * Constructor
     * @param query     the query to be paged, must include an order by.
     * @param pageSize  the page size.
     * @param firstOffset the first row to start with. Generally this will be 0.
     * @param maxOffset the last row to get.
     */
    public SQLServerPagedQuery(String query, Integer pageSize, Integer firstOffset, Integer maxOffset) {
        this.query = query;
        this.pageSize = pageSize;
        if (firstOffset != null) {
            this.firstOffset = firstOffset;
        }
        if (maxOffset != null) {
            this.maxOffset = maxOffset;
        }
    }


    /**
     * Get the query needed to get a specific page of records from the database.
     * @param pageNumber which page to get. Page index starts at 0.
     * @return The SQL Server query to get the page of records. If no more records can be
     * selected (maxOffset > offset), then null is returned.
     */
    public String getPageQuery(Integer pageNumber){
        Integer offsetRows = (pageSize * pageNumber) + firstOffset;

        if (maxOffset > -1) {
            if (offsetRows > maxOffset) {
                return null;
            }

            if (offsetRows + pageSize > maxOffset) {
                String result =  query + "\n\t\tOFFSET " + offsetRows + " ROWS\n" +
                        "\t\tFETCH NEXT " + ((offsetRows + pageSize) - maxOffset + 1) + " ROWS ONLY;";
                return result;
            }
        }

        return query + "\n\t\tOFFSET " + offsetRows + " ROWS\n" +
                "\t\tFETCH NEXT " + pageSize + " ROWS ONLY;";
    }

}
