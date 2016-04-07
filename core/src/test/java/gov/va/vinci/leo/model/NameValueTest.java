package gov.va.vinci.leo.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/7/16.
 */
public class NameValueTest {

    @Test
    public void testGettersAndSetters() throws Exception {
        NameValue nv = new NameValue("name", "value")
                .setName("myName")
                .setValue("myValue");
        assertEquals("myName", nv.getName());
        assertEquals("myValue", nv.getValue().toString());
    }
}
