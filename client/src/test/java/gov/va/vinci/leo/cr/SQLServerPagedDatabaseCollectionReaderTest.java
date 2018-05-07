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

import gov.va.vinci.leo.SampleService;
import gov.va.vinci.leo.model.DataQueryInformation;
import gov.va.vinci.leo.model.DatabaseConnectionInformation;
import gov.va.vinci.leo.tools.db.DataManager;
import gov.va.vinci.leo.tools.db.LeoArrayListHandler;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ryancornia on 1/20/15.
 */
public class SQLServerPagedDatabaseCollectionReaderTest {

    @Test
    public void testInitializeDciDqi() throws Exception {
        String driver = "com.mock", url = "url", username = "username", password = "password",
               query = "select * from notes order by id", idColumn = "id", noteColumn = "note";
        int pageSize = 10, firstOffset = 0;
        //Create reader using first constructor
        SQLServerPagedDatabaseCollectionReader reader = new SQLServerPagedDatabaseCollectionReader(
            new DatabaseConnectionInformation(driver, url, username, password),
            new DataQueryInformation(query, noteColumn, idColumn),
            pageSize
        );
        assertNotNull(reader);
        assertEquals(driver, reader.getDriver());
        assertEquals(url, reader.getURL());
        assertEquals(username, reader.getUsername());
        assertEquals(password, reader.getPassword());
        assertEquals(query, reader.getQuery());
        assertEquals(idColumn, reader.getIdColumn());
        assertEquals(noteColumn, reader.getNoteColumn());
        assertEquals(pageSize, reader.getPageSize());
        assertEquals(0, reader.getFirstOffset());

        //Create reader using second constructor
        reader = new SQLServerPagedDatabaseCollectionReader(
                new DatabaseConnectionInformation(driver, url, username, password),
                new DataQueryInformation(query, noteColumn, idColumn),
                pageSize, firstOffset
        );
        assertNotNull(reader);
        assertEquals(driver, reader.getDriver());
        assertEquals(url, reader.getURL());
        assertEquals(username, reader.getUsername());
        assertEquals(password, reader.getPassword());
        assertEquals(query, reader.getQuery());
        assertEquals(idColumn, reader.getIdColumn());
        assertEquals(noteColumn, reader.getNoteColumn());
        assertEquals(pageSize, reader.getPageSize());
        assertEquals(firstOffset, reader.getFirstOffset());

        //Check getters and setters
        reader.setFirstOffset(100);
        reader.setMaxOffset(1000);
        reader.setPageSize(200);
        assertEquals(100, reader.getFirstOffset());
        assertEquals(1000, reader.getMaxOffset());
        assertEquals(200, reader.getPageSize());
    }

    @Test
    public void testHasNext() throws Exception {
        MockSQLServerPagedDatabaseCollectionReader cr = (MockSQLServerPagedDatabaseCollectionReader) new MockSQLServerPagedDatabaseCollectionReader("com.mock", "url", "username", "password", "select id, note from doc order by id",
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

        boolean hasNext = cr.hasNext();

        assert(hasNext);

        /** Batch 1 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        /** Batch 2 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());

        /** Empty batch **/
        hasNext = cr.hasNext();
        assert(!hasNext);
    }



    @Test
    public void testHasNextWithStartOffset() throws Exception {
        MockSQLServerPagedDatabaseCollectionReader cr = (MockSQLServerPagedDatabaseCollectionReader) new MockSQLServerPagedDatabaseCollectionReader("com.mock", "url", "username", "password", "select id, note from doc order by id",
                "id", "note", 3, 2).produceCollectionReader();


        DataManager dm = mock(DataManager.class);
        LeoArrayListHandler handler = mock(LeoArrayListHandler.class);


        List<Object[]> mockResults = new ArrayList<Object[]>();

        mockResults.add(new Object[] { new Integer(1), "abc", new Integer(1), });
        mockResults.add(new Object[] { new Integer(2), "def", new Integer(2), });
        mockResults.add(new Object[] { new Integer(3), "ghi", new Integer(3), });

        /** First Batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 2 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(mockResults);

        /** Second batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 5 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(mockResults);

        /** Last batch, return an empty batch. **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 8 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(new ArrayList<Object[]>());

        when(handler.getColumnIndex("id")).thenReturn(0);
        when(handler.getColumnIndex("note")).thenReturn(1);

        when(dm.getHandler()).thenReturn(handler);

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(SampleService
                .simpleServiceDefinition()
                .getAnalysisEngineDescription());
        CAS mockCas = ae.newCAS();
        cr.setDataManager(dm);

        boolean hasNext = cr.hasNext();

        assert(hasNext);

        /** Batch 1 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        /** Batch 2 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());

        /** Empty batch **/
        hasNext = cr.hasNext();
        assert(!hasNext);
    }



    @Test
    public void testHasNextWithStartOffsetMaxOffset() throws Exception {
        MockSQLServerPagedDatabaseCollectionReader cr = (MockSQLServerPagedDatabaseCollectionReader) new MockSQLServerPagedDatabaseCollectionReader("com.mock", "url", "username", "password", "select id, note from doc order by id",
                "id", "note", 3, 2, 7).produceCollectionReader();


        DataManager dm = mock(DataManager.class);
        LeoArrayListHandler handler = mock(LeoArrayListHandler.class);


        List<Object[]> mockResults = new ArrayList<Object[]>();

        mockResults.add(new Object[] { new Integer(1), "abc", new Integer(1), });
        mockResults.add(new Object[] { new Integer(2), "def", new Integer(2), });
        mockResults.add(new Object[] { new Integer(3), "ghi", new Integer(3), });


        List<Object[]> mockResults2 = new ArrayList<Object[]>();

        mockResults2.add(new Object[] { new Integer(1), "abc", new Integer(1), });
        mockResults2.add(new Object[] { new Integer(2), "def", new Integer(2), });

        /** First Batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 2 ROWS\n" +
                "\t\tFETCH NEXT 3 ROWS ONLY;")).thenReturn(mockResults);

        /** Second batch **/
        when(dm.query("select id, note from doc order by id\n" +
                "\t\tOFFSET 5 ROWS\n" +
                "\t\tFETCH NEXT 2 ROWS ONLY;")).thenReturn(mockResults2);

        when(handler.getColumnIndex("id")).thenReturn(0);
        when(handler.getColumnIndex("note")).thenReturn(1);

        when(dm.getHandler()).thenReturn(handler);

        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(SampleService
                .simpleServiceDefinition()
                .getAnalysisEngineDescription());
        CAS mockCas = ae.newCAS();
        cr.setDataManager(dm);

        boolean hasNext = cr.hasNext();

        assert(hasNext);

        /** Batch 1 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        /** Batch 2 **/
        cr.getNext(ae.newCAS());
        hasNext = cr.hasNext();
        assert(hasNext);

        cr.getNext(ae.newCAS());

        /** Empty batch **/
        hasNext = cr.hasNext();
        assert(!hasNext);
    }
}
