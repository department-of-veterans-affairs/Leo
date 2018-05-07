
**The Leo framework** - The [VINCI](http://www.hsrd.research.va.gov/for_researchers/vinci/)-developed Natural Language Processing(NLP) infrastructure, is a set of services and libraries that facilitate the rapid creation and deployment of Apache UIMA-AS annotators focused on NLP. Leo, named for the Spanish word meaning *“I read”*, was first built to support scalable deployment of NLP pipelines (VINCI now has more than 2 billion clinical text notes available). It extends the open-source Apache Unstructured Information Management Architecture (UIMA). 

UIMA provides a common workflow and basic functionalities with which to develop and integrate reusable components for NLP and information extraction. Leo eliminates the maintenance required by UIMA descriptor files and generates pipelines based on parameters and type definitions provided programmatically. By providing a solid infrastructure, Leo changes the focus of NLP researchers to algorithm development. The UIMA-AS underpinning allows Leo to manage the scale needed for real-time processing. It provides remote configuration tools for enabling automatic system optimization. With its developer utilities, functionality can be added and seamlessly integrated with existing NLP services. Leo enables users to programmatically generate primitive and aggregate UIMA analysis engine descriptors and deployment descriptors.

After the success of using Leo for being able to process millions of notes for a single project, Leo was further developed to include reusable, configurable components for finding keywords and phrases, using regular expressions and patterns, building complex rules for identifying and associating annotations within a document, mapping strings of text found in notes to standard terminologies, and developing feature vectors for training and validating ML components. Leo supports the whole workflow of annotation, iterative NLP development, and validation by seamlessly connecting UIMA functionality with annotation and validation tools and ML libraries. By using a community-developed data model and standard API, we were also able to take existing, open-source components and incorporate them into Leo. An upcoming publication will describe how Leo enabled the Apache Clinical Text Analysis and Knowledge Extraction System [cTAKES](https://ctakes.apache.org), a commonly used open-source information extraction NLP pipeline, to scale from one document at a time to a number only constrained by hardware resources. In addition, Leo has integrated the National Library of Medicine’s [MetaMap system](http://metamap.nlm.nih.gov), the [ConText algorithm](http://orbit.nlm.nih.gov/resource/context), and many parsing and syntax modules available in open-source that can be made available as part of this prototype.

Leo provides factories, base annotators, base readers, base listeners, and other utilities for UIMA AS, so that all UIMA AS components can be instantiated, configured, and deployed programmatically.

**Some of the features of Leo are:**

*  Rapid Natural Language Processing (NLP) Development
*  Programmatically Generate:
    *  UIMA Descriptors
    *  Parameter and Type information for Analysis Engine (AE)
    *  Aggregate AE for a Service
    *  Configuration and Service Launch
    *  Standard and Custom CollectionReaders
*  Plug and Play Algorithm Modules
*  Launch Services and Clients in UIMA-AS

**UIMA** – [Unstructured Information Management Architecture](http://uima.apache.org/index.html ) – is a powerful and flexible software system designed to analyze large volumes of unstructured information. In order to accommodate all the available functionality, configuration and deployment of systems based on UIMA AS require a set of descriptor files in XML format. Creating and maintaining these descriptor files manually can be complicated and time consuming.   [Apache uimaFit](http://uima.apache.org/uimafit.html) is a library that enables programmatic instantiation of UIMA components.  

**UIMA AS** – [UIMA Asynchronous Scaleout]( http://uima.apache.org/doc-uimaas-what.html ) - is substantially different from UIMA and provides a range of additional functionality enabling scalable processing. While UIMA components can be run within UIMA AS with no descriptor changes, additional descriptor files are required to deploy systems based on UIMA AS.  

Questions? Comments? Contact us at vinciservices@va.gov
