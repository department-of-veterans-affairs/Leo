package gov.va.vinci.leo.cr;

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
