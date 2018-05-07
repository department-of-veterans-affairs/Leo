package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
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

import gov.va.vinci.leo.tools.db.DataManager;

import java.sql.SQLException;


public class MockSQLServerPagedDatabaseCollectionReader extends SQLServerPagedDatabaseCollectionReader {
    DataManager dm;

    public MockSQLServerPagedDatabaseCollectionReader() {

    }

    public MockSQLServerPagedDatabaseCollectionReader(String s, String url, String user, String password, String s1, String id, String note, int i) {
        super(s, url, user, password, s1, id, note, i);
    }


    public MockSQLServerPagedDatabaseCollectionReader(String s, String url, String user, String password, String s1, String id, String note, int i, int offset) {
        super(s, url, user, password, s1, id, note, i, offset);
    }

    public MockSQLServerPagedDatabaseCollectionReader(String s, String url, String user, String password, String s1, String id, String note, int i, int offset, int maxOffset) {
        super(s, url, user, password, s1, id, note, i, offset, maxOffset);
    }

    public void setDataManager(DataManager dm) {
        this.dm = dm;
    }

    @Override
    protected DataManager getDataManager() throws SQLException, ClassNotFoundException {
        return dm;
    }
}
