
# Using Leo

Natural Language Processing systems are typically implemented as pipelines, such that multiple processing steps are performed sequentially. UIMA AS Client-Service architecture allows developing highly scalable systems by separating each individual step and allowing replicating those steps, thus, increasing total system throughput. UIMA AS architecture uses queues to connect Client and Service through Broker.

Basic functionality of Leo can be accessed using two objects known as Leo-service and Leo-client.  Leo-service provides the Core Server functionality for launching UIMA-AS services.  Leo-client provides the client functionality including a reader for sending data to the service and a listener interface for receiving processed data back from the service.

In order to run a system build using Leo, Apache ActiveMQ broker has to be launched prior to deploying the service.

Defining your NLP system service as a class separate from the client class would allow you to leverage UIMA AS scalability functionality more efficiently.


## Prerequisites

* [UIMA-AS 2.6.0 or higher](http://uima.apache.org/)
* Java 1.6 or higher
* Java Development environment (for example [Eclipse](http://eclipse.org) or [IntelliJ](https://www.jetbrains.com/idea/download/))
* [Maven](http://maven.apache.org/)

## Getting Leo with Maven

Leo is distributed via [maven](http://maven.apache.org/) to insure dependencies are met. It is located in the decipher.chpc.utah.edu maven repository. To add Leo to your project:

1.) Add decipher.chpc.utah.edu to your pom.

```xml
<project ...>
  <repositories>
     <repository>
	 <id>decipher</id>
	 <url>http://decipher.chpc.utah.edu/nexus/content/groups/public</url>
     </repository>
  </repositories>
  ...
</project>
```

2.) Include the appropriate Leo or tool dependency that you're using.

Leo:

For A complete leo and all of its components, use the Leo-Base Pom:

```xml
<dependency>
    <groupId>gov.va.vinci.leo</groupId>
    <artifactId>leo-base</artifactId>
    <version>2016.05.2</version>
    <type>pom</type>
</dependency>
```

Leo-client:
```xml
   <dependency>
       <groupId>gov.va.vinci</groupId>
       <artifactId>leo</artifactId>
       <version>2016.05.2</version>
   </dependency>
```

Leo-service:
```xml
   <dependency>
       <groupId>gov.va.vinci</groupId>
       <artifactId>leo</artifactId>
       <version>2016.05.2</version>
   </dependency>
```

Leo-Core contains the descriptors and tools used with Leo projects.

```xml
   <dependency>
       <groupId>gov.va.vinci</groupId>
       <artifactId>leo</artifactId>
       <version>2016.05.2</version>
   </dependency>
```

Below are the dependencies of annotators that are most likely going to be required in many projects.  Descriptions of each can be found in Annotator section of the [User guide](userguide.html).

Whitespace Tokenizer:
```xml
<dependency>
    <groupId>gov.va.vinci</groupId>
    <artifactId>leo-whitespace-tokenizer</artifactId>
    <version>LATEST</version>
</dependency>
```

Regex:
```xml
<dependency>
    <groupId>gov.va.vinci</groupId>
    <artifactId>leo-regex</artifactId>
    <version>LATEST</version>
</dependency>
```

Annotation Librarian:
```xml
<dependency>
    <groupId>gov.va.vinci</groupId>
    <artifactId>leo-annotation-librarian</artifactId>
    <version>LATEST</version>
</dependency>
```

AnnotationPattern:
```xml
<dependency>
  <groupId>gov.va.vinci</groupId>
  <artifactId>leo-annotation-pattern</artifactId>
  <version>LATEST</version>
</dependency>
```

Context:
```xml
<dependency>
  <groupId>gov.va.vinci</groupId>
  <artifactId>leo-context</artifactId>
  <version>LATEST</version>
</dependency>
```

Siman:
```xml
<dependency>
  <groupId>gov.va.vinci</groupId>
  <artifactId>leo-siman</artifactId>
  <version>LATEST</version>
</dependency>
```

