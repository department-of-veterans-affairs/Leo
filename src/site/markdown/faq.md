### Frequently asked questions


**I have a UIMA analysis engine. Can I use it with Leo?**
 
Yes, you can add it to your pipeline in Leo, as well as any other analysis engine used by UIMA. The Leo team has also built a number of engines already included in Leo that van be found in the [Components and User Guide](components.html) page.

**Do you have a plugin that sets up the initial directory structure?**

 No, however if you use the mvn command ```mvn eclipse:eclipse ``` the entire project will be set up for you in eclipse.  However, the structure can be outlined like so:

* src/main - This folder contains the primary code and resources that will be distributed in the JAR file that is generated.
* src/main/java - Java code is here
* src/main/resources - Resource files, like regex pattern files, that the code will need to function properly
* src/test - This folder contains the test code and resource that is never distributed in any JAR or release
* src/test/java - Test code, typically JUnit code that tests the application automatically
* src/test/resources - Resources specific to needs of the testing code
* pom.xml - At the root of the project this file tells maven what your dependancies are and how to build your code.  Maven assumes the directory structure above.
* .gitignore - This file is useful when using a GIT source repository in order to prevent project metadata files from being checked into the source tree.



**I already have a Maven repository , how do I set up my project from there?**

To use our maven repository, you need to update your settings.xml (in
~/.m2/) to point to our repository as a mirror. See the [download](download.html) page for more details.

Additionally, if using Eclipse, you need to set the M2_REPO variable in
Eclipse->Preferences->Java->Build Path->Classpath Variables.
It should point to your .m2/repository directory, for example
/Users/vhaslcpatteo/.m2/repository

**Where can I access the sources for Leo standard readers and listeners?**

Complete descriptions of all Leo components can be found in their respective javadocs [here](http://decipher.chpc.utah.edu/sites/gov.va.vinci.leo/leo-client/2016.05.0/leo-client/apidocs/).

If you use Eclipse and include leo as a dependency in your pom file, you can automatically download and link source code using maven:

mvn eclipse:eclipse -DdownloadSources=true

**Why Leo?**

The Goal of Leo is to remove the requirement of building the complicated and long xml descriptors files either manually or with a wizard.  Within the Leo pipeline, all descriptor files are built automatically and seamlessly to the user.  If for any reason you need the actual XML descriptor files, they are created in a temp folder. Each run of the Service creates its set of descriptor files with unique names. If you want to get the path to the xml descriptor files for any specific run, you can add this line

System.out.println( service.getAggregateDescriptorFile());

And it will send a full path to the standard output. The individual descriptor files for each of the AEs will be in the same directory. 

**I think I found a bug; where do I report it?**

Bugs, errors and any other problem associated with Leo can be reported to Vinci Services at vinciservices@va.gov

