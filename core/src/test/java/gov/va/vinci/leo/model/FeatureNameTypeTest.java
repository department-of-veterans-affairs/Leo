package gov.va.vinci.leo.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/7/16.
 */
public class FeatureNameTypeTest {

    @Test
    public void testGettersAndSetters() throws Exception {
        FeatureNameType fnt = new FeatureNameType("name", "type")
                .setName("myName")
                .setType("myType");
        assertEquals("myName", fnt.getName());
        assertEquals("myType", fnt.getType());
    }
}
