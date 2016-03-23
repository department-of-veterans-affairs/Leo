# Leo Overview

Leo provides a framework and set of classes for more easily using UIMA AS. These include classes for programatically
creating analysis, aggregate, and type descriptors, and well as base classes for different readers and listeners.

# Packages & Classes

* **gov.va.vinci.leo** :
    The top level package that contains Client and Service for creating a UIMA AS Client and UIMA AS Service.
* **gov.va.vinci.leo.client** :
    Client related utilities and base clients. For instance, DatabaseMultiBatchClient allows for getting data from
    a database in database batches and sending it to the UIMA AS service.
* **gov.va.vinci.leo.cr** :
    Collection readers. These are base classes and implementations of collection readers clients use to read data
    to send to UIMA AS.
* **gov.va.vinci.leo.descriptors** :
    Descriptor related classes and factories for building aggregate, analysis engine, collection reader, or type system
     descriptors.
* **gov.va.vinci.leo.gov.va.vinci.leo.listener** :
    Base listeners and example listeners clients use to process CAS objects returned from the UIMA AS service.
* **gov.va.vinci.leo.model** :
    POJO model objects used within Leo.
* **gov.va.vinci.leo.tools** :
    Utility classes and tools used within Leo.
* **gov.va.vinci.leo.types** :
    Types used in Leo.

# Usage

## Scaling with Leo and UIMA AS

Leo and UIMA AS can be scaled in several ways.

### Scaling By Deploying Copies of a Pipeline

An aggregate pipeline can be deployed on UIMA AS to create multiple copies of the pipeline via:

	LeoAEDescriptor myAggregate = new LeoAEDescriptor((List<LeoAEDescriptor>) myAnnotatorList);
	aggregate.setIsAsync(false); // This is key, and sets the server to have a single queue in front of 
				     // each pipeline instance. 
	aggregate.setNumberOfInstances(1); // How many instances (copies) of this pipeline to deploy.
	server.init(aggregate);

This deploys multiple copies of the complete pipeline in a single JVM.

### Scaling By Deploying Copies of Individual Annotators

Primitive annotators can also be scaled, instead of full pipeline copies. For instance, if Annotator A is twice as slow as Annotator B, you may wish to deploy twice as many Annotator A's than B's:

		Server server = null;
		try {
			server = new Server("conf/leo.properties");
			
			//List for holding our annotators. 
			ArrayList<LeoAEDescriptor> annotators = new ArrayList<LeoAEDescriptor>();
			
			//Add annotatorA 
			LeoAEDescriptor annotatorA = new LeoAEDescriptor("my.package.AnnotatorA", true);
			annotatorA.setNumberOfInstances(2);
			annotators.add(annotatorA);

			//Add annotatorB
            LeoAEDescriptor annotatorB = new LeoAEDescriptor("my.package.AnnotatorB", true);
            annotatorB.setNumberOfInstances(1);
            annotators.add(annotatorB);

			// Create an aggregate of the components. 
			LeoAEDescriptor aggregate = new LeoAEDescriptor(annotators);

			// Specifies a queue in front of all of the delegates. 
			aggregate.setIsAsync(true);
			aggregate.setNumberOfInstances(1); 
			
			//Initialize the service
			server.init(aggregate);
		} catch (Exception e) {
			e.printStackTrace();
		}


### Scaling By Multiple Deployments

Finally, in addition/combination with the above, you can launch multiple JVMs running the same Leo Server application. If all server applications use the same broker url, the service is distributed via the broker to the multiple JVMs. Tuning an individual JVM for optimal performance, then deploying multiple instanes of the JVM is an effective way to scale. Note: The JVMs can also be located on different machines of physical networks, as long as they are all pointing to the same broker URL.

## Simple Annotation Schema

Simple Annotation Schema is the reduced database schema for storing annotations. It consists of 3 tables:

* Document
* Annotation
* Feature

It also requires an addition table of your own that contains a GUID and the necessary columns needed to find the
document. For example, your table name might be document\_xref\_simple and contains 2 columns, GUID and TIU\_DOCUMENT\_SID.

## Using the Listener (Writing results to the database)

### Step 1: Create the schema

To create the schema, use **gov.va.vinci.leo.tools.OldManSimanUtils.getCreateTablesSQL(...);**

This will need to be done **once per validation set**, and is best done by adding this to the run method of your client
with a boolean **createSchema** that you can change easily at the class level. You will also likely want to make the
schema and suffix class level variables to they are also easily changed.

#### Creating Schema Example


    DatabaseConnectionInformation dci = new DatabaseConnectionInformation(p.getProperty("client.reader.database.driverClass"),
                                                    p.getProperty("client.reader.database.connectionUrl"),
                                                    p.getProperty("client.reader.database.username"),
                                                    p.getProperty("client.reader.database.password"));
    SimpleAnnotationSchemaDataSourceConfiguration sdc =
                        new SimpleAnnotationSchemaDataSourceConfiguration(dci, "", "validation", "_20130221", "[", "]");
    SimpleAnnotationSchemaDatabaseListener.createAnnotationSchema(sdc,
                                              SimpleAnnotationSchemaUtils.SchemaType.SQL_SERVER);



### Step 2: Implement a gov.va.vinci.leo.listener

To use the gov.va.vinci.leo.listener, you need to extend gov.va.vinci.leo.gov.va.vinci.leo.listener.OldManSimanDatabaseListener and implement

**protected abstract NameValue insertDocumentXref(CAS cas) throws SQLException;**

This should insert a record into your cross reference table and return the table name and GUID of the inserted record back.

An example of this is:

        @Override
        protected NameValue insertDocumentXref(CAS arg0) throws SQLException {
            String tiuDocumentSID = (this.docInfo == null)? "" : this.docInfo.getID();
            String patientSID      = (this.docInfo == null || this.docInfo.getRowData() == null)? "" : this.docInfo.getRowData(1);
            String recordUUID = UUID.randomUUID().toString();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO [ORD_Lafleur_201203013D].[validation].[document_xref_example"+ Constants.TABLE_SUFFIX + "] " +
                       " ( [guid],[version],[patient_sid],[tiu_document_sid] ) VALUES " +
                       " ( ?, ?, ?, ? )");

            ps.setString(1, recordUUID);
            ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            ps.setString(3, patientSID);
            ps.setString(4, tiuDocumentSID);
            ps.execute();

            return new NameValue("[validation].[document_xref_example" + Constants.TABLE_SUFFIX + "]", recordUUID);
        }


#### Using the Listener

    DatabaseConnectionInformation dci = new DatabaseConnectionInformation(p.getProperty("client.reader.database.driverClass"),
                                                    p.getProperty("client.reader.database.connectionUrl"),
                                                    p.getProperty("client.reader.database.username"),
                                                    p.getProperty("client.reader.database.password"));
    BedSimpleAnnotationSchemaListener bedSimpleAnnotationSchemaListener = new BedSimpleAnnotationSchemaListener(
         new SimpleAnnotationSchemaDataSourceConfiguration(dci, "", "validation", "_20130221", "[", "]"),
         new String[] { BedHzValidation.class.getCanonicalName()});

## Using the Reader (Getting results out of the database)

The reader consists of two parts. The first is **gov.va.vinci.leo.tools.SimpeAnnotationSchemaDAO**. This class does
database selects and returns POJO objects. It can be used outside of the UIMA environment.

The second part is the actual UIMA Reader **gov.va.vinci.leo.cr.OldManSimanDatabaseReader**.