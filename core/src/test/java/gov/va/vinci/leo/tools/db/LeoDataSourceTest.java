package gov.va.vinci.leo.tools.db;

import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 4/7/16.
 */
public class LeoDataSourceTest {

    protected static Connection connection = null;

    @Before
    public void setup() throws Exception {
        if(connection != null) return;
        DatabaseConnectionInformation dbConnectionInfo = new DatabaseConnectionInformation("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:JdbcScriptRunnerTest;sql.enforce_strict_size=true", "sa", "");
        connection = new DataManager(dbConnectionInfo).getDataSource().getConnection();
    }

    @Test
    public void testLeoDataSource() throws Exception {
        LeoDataSource dataSource = new LeoDataSource(connection);
        assertNotNull(dataSource);
        PrintWriter pw = new PrintWriter(new StringWriter());
        assertNotNull(pw);
        dataSource.setLogWriter(pw);
        assertEquals(null, dataSource.getLogWriter());
        dataSource.setLoginTimeout(25);
        assertEquals(0, dataSource.getLoginTimeout());
        assertEquals(null, dataSource.getParentLogger());
    }
}
