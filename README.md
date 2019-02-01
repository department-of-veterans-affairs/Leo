# Leo Overview

Leo provides a framework and set of classes for more easily using UIMA AS. These include classes for programatically
creating analysis, aggregate, and type descriptors, and well as base classes for different readers and listeners.

If you use Leo, please cite:

Cornia R, Patterson OV, Ginter T, Duvall SL. Rapid NLP Development with Leo. In: AMIA Annu Symp Proc. Washington, DC, USA; 2014:1356.
Can be retrieved from: 
https://knowledge.amia.org/56638-amia-1.1540970/t-005-1.1543914/f-005-1.1543915/a-288-1.1544807/an-288-1.1544808?qr=1


# Packages & Classes

* **gov.va.vinci.leo** :
    The top level package that contains Client and Service for creating a UIMA AS Client and UIMA AS Service.
* **gov.va.vinci.leo.ae** :
    Annotation engine classes, including the LeoBaseAnnotator and classes used to import third party annotators and for 
    including remote services as annotation engines in the pipeline.
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

Leo and UIMA-AS can be scaled in several ways.

### Scaling By Deploying Copies of a Pipeline

An aggregate pipeline can be deployed on UIMA AS to create multiple copies of the pipeline via:

	Service service = new Service();
	service.deploy(myTypeSystemDescription, numInstances, myAnnotator1, myAnnotator2, myAnnotator3);

This deploys multiple copies of the complete pipeline in a single JVM.

### Scaling By Deploying Copies of Individual Annotators

Primitive annotators can also be scaled, instead of full pipeline copies. For instance, if Annotator A is twice as slow as Annotator B, you may wish to deploy twice as many Annotator A's than B's:

        Service service = new Service();
        
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
        
        //Initialize the service
        service.deploy(aggregate);

### Scaling By Multiple Deployments

Finally, in addition/combination with the above, you can launch multiple JVMs running the same Leo Server application. If all server applications use the same broker url, the service is distributed via the broker to the multiple JVMs. Tuning an individual JVM for optimal performance, then deploying multiple instanes of the JVM is an effective way to scale. Note: The JVMs can also be located on different machines of physical networks, as long as they are all pointing to the same broker URL.

# More Information

For more information about deploying clients and services using Leo see the user guide in the documentation.
