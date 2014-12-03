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
 * Holds query information for processing.
 *
 * @author rcornia
 */
public class DataQueryInformation {
    /**
     * The query.
     */
    private String query;

    /**
     * The column index of the note. O based.
     */
    private String noteColumn;

    /**
     * The column index of the id column. O based.
     */
    private String idColumn;

    /**
     * Constructor with all fields required.
     * @param query  the database query, ie select id, note from my_table;
     * @param noteColumn the column name for the note
     * @param idColumn  the column name for the id
     */
    public DataQueryInformation(String query, String noteColumn, String idColumn) {
        super();
        this.query = query;
        this.noteColumn = noteColumn;
        this.idColumn = idColumn;
    }

    /**
     * Get the query.
     * @return the query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the query.
     * @param query  the database query, ie select id, note from my_table;
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Get the note column index, 0 based.
     * @return  the note column index, 0 based.
     */
    public String getNoteColumn() {
        return noteColumn;
    }

    /**
     * Set the note column index, 0 based.
     * @param noteColumn the note column index, 0 based.
     */
    public void setNoteColumn(String noteColumn) {
        this.noteColumn = noteColumn;
    }

    /**
     * Get the id column index, 0 based.
     * @return  the id column index, 0 based.
     */
    public String getIdColumn() {
        return idColumn;
    }

    /**
     * Set the id column index, 0 based.
     * @param idColumn column index, 0 based.
     */
    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

}
