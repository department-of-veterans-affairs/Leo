package gov.va.vinci.leo.cr;

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.tools.db.DataManager;
import gov.va.vinci.leo.tools.db.LeoArrayListHandler;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ryancornia on 1/20/15.
 */
public class SQLServerPagedDatabaseCollectionReaderTest {


    @Test
    public void testHasNext() throws Exception {
        MockSQLServerPagedDatabaseCollectionReader cr = (MockSQLServerPagedDatabaseCollectionReader) new MockSQLServerPagedDatabaseCollectionReader("com.mock", "url", "user", "password", "select id, note from doc order by id",
                "id", "note", 3).produceCollectionReader();


        DataManager dm = mock(DataManager.class);
        LeoArrayListHandler handler = mock(LeoArrayListHandler.class);


        List<Object[]> mockResults = new ArrayList<Object[]>();

        mockResults.add(new Object[] { new Integer(1), "abc", new Integer(1), });
        mockResults.add(new Object[] { new Integer(2), "def", new Integer(2), });
        mockResults.add(new Object[] { new Integer(3), "ghi", new Integer(3), });

        /** First Batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 0 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(mockResults);

        /** Second batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 3 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(mockResults);

        /** Last batch, return an empty batch. **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 6 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(new ArrayList<Object[]>());

        when(handler.getColumnIndex("id")).thenReturn(0);
        when(handler.getColumnIndex("note")).thenReturn(1);

        when(dm.getHandler()).thenReturn(handler);

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(SampleService
                .simpleServiceDefinition()
                .getAnalysisEngineDescription());
        CAS mockCas = ae.newCAS();
        cr.setDataManager(dm);

        assert(cr.hasNext());

        /** Batch 1 **/
        cr.getNext(ae.newCAS());
        assert(cr.hasNext());

        cr.getNext(ae.newCAS());
        assert(cr.hasNext());

        cr.getNext(ae.newCAS());
        assert(cr.hasNext());

        /** Batch 2 **/
        cr.getNext(ae.newCAS());
        assert(cr.hasNext());

        cr.getNext(ae.newCAS());
        assert(cr.hasNext());

        cr.getNext(ae.newCAS());

        /** Empty batch **/
        assert(!cr.hasNext());
    }


}
