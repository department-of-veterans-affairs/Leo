package gov.va.vinci.leo.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/6/16.
 */
public class DatabaseConnectionInformationTest {

    @Test
    public void testGettersSetters() throws Exception {
        DatabaseConnectionInformation dci
                = new DatabaseConnectionInformation("driver", "url", "username", "password", "validationQuery")
                    .setDriver("com.driver.happy")
                    .setUrl("jdbc:url")
                    .setPassword("mypwd")
                    .setUsername("myuser")
                    .setValidationQuery("valQuery");
        assertEquals("com.driver.happy", dci.getDriver());
        assertEquals("jdbc:url", dci.getUrl());
        assertEquals("mypwd", dci.getPassword());
        assertEquals("myuser", dci.getUsername());
        assertEquals("valQuery", dci.getValidationQuery());
    }
}
