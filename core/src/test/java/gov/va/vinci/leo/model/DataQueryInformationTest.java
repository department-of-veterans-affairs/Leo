package gov.va.vinci.leo.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/7/16.
 */
public class DataQueryInformationTest {

    @Test
    public void testGettersAndSetters() throws Exception {
        DataQueryInformation dqi = new DataQueryInformation("query", "noteColumn", "idColumn")
                .setIdColumn("myID")
                .setNoteColumn("myNotes")
                .setQuery("SELECT 1");
        assertEquals("SELECT 1", dqi.getQuery());
        assertEquals("myNotes", dqi.getNoteColumn());
        assertEquals("myID", dqi.getIdColumn());
    }
}
