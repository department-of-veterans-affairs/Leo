package gov.va.vinci.leo.db;

/*
 * #%L
 * Leo Core
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


// TODO - Fix
public class PerformanceTest {

    /**
    public DatabaseConnectionInformation getMicrosoftConnection() {
        return new DatabaseConnectionInformation("com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "jdbc:sqlserver://VHACDWRB01:1433;databasename=ORD_Goodman_201208021D;integratedSecurity=true",
                "", "");

    }

    public DatabaseConnectionInformation getJTDSConnection() {
        return new DatabaseConnectionInformation("net.sourceforge.jtds.jdbc.Driver",
                "jdbc:jtds:sqlserver://VHACDWRB01:1433/ORD_Goodman_201208021D;useNTLMv2=true;domain=vha19",
                "", "");
    }


    public void jTDSTest() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        DatabaseConnectionInformation connectionInformation = getJTDSConnection();
        ChexSimanDataSourceConfiguration oldManSimanDataSourceConfiguration =
                new ChexSimanDataSourceConfiguration(connectionInformation, "SELECT ReportText, TIUDocumentSID FROM [dflt].[tiu_sample2] doc, [validation].[document_xref_example_20130509] xref WHERE doc.TIUDocumentSID= xref.tiu_document_sid and xref.guid = ?", "validation", "_20130509", "[", "]");
        doPerformanceTest(oldManSimanDataSourceConfiguration, "jTDS");
    }


    public void MicrosoftTest() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        DatabaseConnectionInformation connectionInformation = getMicrosoftConnection();
        ChexSimanDataSourceConfiguration oldManSimanDataSourceConfiguration =
                new ChexSimanDataSourceConfiguration(connectionInformation, "SELECT ReportText, TIUDocumentSID FROM [dflt].[tiu_sample2] doc, [validation].[document_xref_example_20130509] xref WHERE doc.TIUDocumentSID= xref.tiu_document_sid and xref.guid = ?", "validation", "_20130509",  "[", "]");
        doPerformanceTest(oldManSimanDataSourceConfiguration, "Microsoft");
    }

    public void doPerformanceTest(ChexSimanDataSourceConfiguration oldManSimanDataSourceConfiguration, String name) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        //ResultSet randomAnnotationGuids = c.prepareStatement(sql).executeQuery();
        OldManSimanDAO dao = new OldManSimanDAO(oldManSimanDataSourceConfiguration);

        for (int i=0; i<5; i++) {
            Long startTime = System.currentTimeMillis();
            dao.getAllDocuments();
            Long endTime = System.currentTimeMillis();

            System.out.println(name + ": Ran for (" + (endTime - startTime) + " ms.)");
        }

        // Load them, checking performance.

    }

    //@Test
    public void testQueryMultiple() throws SQLException {
        DatabaseConnectionInformation connectionInformation = getMicrosoftConnection();
        ChexSimanDataSourceConfiguration oldManSimanDataSourceConfiguration =
                new ChexSimanDataSourceConfiguration(connectionInformation, "SELECT ReportText, TIUDocumentSID FROM [dflt].[tiu_sample2] doc, [validation].[document_xref_example_20130509] xref WHERE doc.TIUDocumentSID= xref.tiu_document_sid and xref.guid = ?", "validation", "_20130509", "[", "]");
        Connection conn = oldManSimanDataSourceConfiguration.getDataSource().getConnection();
        PreparedStatement
                documentSelectAllStatement = conn.prepareStatement(oldManSimanDataSourceConfiguration.getDocumentSelectAllSql());
        PreparedStatement annotationFeatureSelectStatement = conn.prepareStatement(oldManSimanDataSourceConfiguration.getAnnotationsAndFeaturesSelectByDocumentSql());

        System.out.println(oldManSimanDataSourceConfiguration.getDocumentSelectAllSql());
        System.out.println(oldManSimanDataSourceConfiguration.getAnnotationsAndFeaturesSelectByDocumentSql());


        Long startTime = System.currentTimeMillis();
        ResultSet rs = documentSelectAllStatement.executeQuery();
        while (rs.next()) {
            annotationFeatureSelectStatement.setString(1, rs.getString(1));
            annotationFeatureSelectStatement.executeQuery();
        }

        Long end = System.currentTimeMillis();
        System.out.println("Queries took: " + (end - startTime) + "ms.");

    }

    //@Test
    public void testQueryOne() throws SQLException {
        DatabaseConnectionInformation connectionInformation = getMicrosoftConnection();
        ChexSimanDataSourceConfiguration oldManSimanDataSourceConfiguration =
                new ChexSimanDataSourceConfiguration(connectionInformation, "SELECT ReportText, TIUDocumentSID FROM [dflt].[tiu_sample2] doc, [validation].[document_xref_example_20130509] xref WHERE doc.TIUDocumentSID= xref.tiu_document_sid and xref.guid = ?", "validation", "_20130509", "[", "]");
        Connection conn = oldManSimanDataSourceConfiguration.getDataSource().getConnection();
        PreparedStatement
                documentSelectAllStatement = conn.prepareStatement("select " +
                "document.[guid], " +          // 1
                "document.[version], " +           //2
                "document.[document_xref_guid], " +    //3
                "document.[document_xref_table], " +  //4
                "document.[entry_date_time], \n" +     //5
                "a.[guid], " +                            //6
                "a.[version], " +                         //7
                "a.[document_guid], " +                   //8
                "a.[end], " +                             //9
                "a.[start], " +                            //10
                "a.[type], " +                             //11
                "a.[user_id], " +                          //12
                "f.[guid], " +                             //13
                "f.[annotation_guid], " +                  //14
                "f.[feature_index], f.[type], f.[value], f.[version] \n" +
                "from [validation].document_20130509 document, [validation].annotation_20130509 a        LEFT JOIN [validation].feature_20130509 f             ON a.[guid] = f.[annotation_guid]    \n" +
                "where a.[document_guid] = document.[guid] order by document.[guid], a.[guid], f.[guid]");

        Long startTime = System.currentTimeMillis();
        ResultSet resultSet = documentSelectAllStatement.executeQuery();

        String currentDocumentGUID = "";
        String currentAnnotationGUID = "";
        Annotation annotation = null;
        List<Document> docs = new ArrayList<Document>();
        Document doc= null;
        while (resultSet.next()) {
            if (!resultSet.getString(1).equals(currentDocumentGUID))      {
                doc = new Document();
                doc.setGuid(resultSet.getString(1));
                doc.setVersion(resultSet.getTimestamp(2));
                doc.setDocumentXrefGuid(resultSet.getString(3));
                doc.setDocumentXrefTable(resultSet.getString(4));
                doc.setEntryDateTime(resultSet.getDate(5));
                currentDocumentGUID = doc.getGuid();
                docs.add(doc);
            }
            if (!resultSet.getString(6).equals(currentAnnotationGUID))   {
                // Add annotation
                annotation = new Annotation();
                annotation.setGuid(resultSet.getString(6));
                annotation.setVersion(resultSet.getTimestamp(7));
                annotation.setEnd(resultSet.getInt(9));
                annotation.setStart(resultSet.getInt(10));
                annotation.setType(resultSet.getString(11));
                annotation.setUser(resultSet.getLong(12));
                annotation.setDocument(doc);

                doc.getAnnotations().add(annotation);
                currentAnnotationGUID = annotation.getGuid();
            }

            if (resultSet.getString(13) != null) {
                Feature feature = new Feature();
                feature.setGuid(resultSet.getString(13));
                feature.setAnnotation(annotation);
                feature.setFeatureIndex(resultSet.getInt(15));
                feature.setType(resultSet.getString(16));
                feature.setValue(resultSet.getString(17));
                feature.setVersion(resultSet.getTimestamp(18));
                annotation.getFeatures().add(feature);
            }
        }

        Long end = System.currentTimeMillis();
        System.out.println("Query One took: " + (end - startTime) + "ms.");

    }

    @Test
    public void dummyTest() {

    }
     **/
}
