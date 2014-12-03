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

import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Handles executing the query and processing the result set.
 *
 * User: Thomas Ginter
 * Date: 7/17/14
 * Time: 13:39
 */
public class LeoArrayListHandler extends ArrayListHandler {

    /**
     * Metadata from the ResultSet passed into the handle method.
     */
    protected ResultSetMetaData mResultSetMetaData = null;
    /**
     * Map of column name to index location in the result set.
     */
    protected HashMap<String, Integer> mColumnNames = null;

    /**
     * Whole <code>ResultSet</code> handler. It produce <code>List</code> as
     * result. To convert individual rows into Java objects it uses
     * <code>handleRow(ResultSet)</code> method.
     *
     * @param rs <code>ResultSet</code> to process.
     * @return a list of all rows in the result set
     * @throws java.sql.SQLException error occurs
     * @see #handleRow(java.sql.ResultSet)
     */
    @Override
    public List<Object[]> handle(ResultSet rs) throws SQLException {
        //Stash the metadata and column names
        mResultSetMetaData = rs.getMetaData();
        mColumnNames = new HashMap<String, Integer>(mResultSetMetaData.getColumnCount());
        for(int i = 0; i < mResultSetMetaData.getColumnCount(); i++) {
            mColumnNames.put(mResultSetMetaData.getColumnName(i+1).toLowerCase(), i);
        }
        //Return the list of Object[] representing the result set
        return super.handle(rs);
    }

    /**
     * Return the ResultSetMetaData from the last query handled.
     *
     * @return ResultSetMetaData object
     */
    public ResultSetMetaData getResultSetMetaData() {
        return mResultSetMetaData;
    }

    /**
     * Get the set of column names from the last query handled.
     *
     * @return Set<String> of column names
     */
    public Set<String> getColumnNames() {
        return (mColumnNames == null)? null : mColumnNames.keySet();
    }

    /**
     * Checks the column names from the last executed query, returns true if the name is found.
     *
     * @param name Column name to check for
     * @return true if found, false if not found or the column names have not been set
     */
    public boolean hasColumn(String name) {
        return !(StringUtils.isBlank(name) || mColumnNames == null) && mColumnNames.containsKey(name);
    }

    /**
     * Get the index of the column name provided from the last query handled.
     *
     * @param name Column name whose index will be retrieved
     * @return index of the column or -1 if not found or the list is empty
     */
    public int getColumnIndex(String name) {
        if(StringUtils.isBlank(name) || mColumnNames == null || !mColumnNames.containsKey(name.toLowerCase()))
            return -1;
        return mColumnNames.get(name);
    }
}
