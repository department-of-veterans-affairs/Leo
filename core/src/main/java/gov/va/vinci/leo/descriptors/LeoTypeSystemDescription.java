package gov.va.vinci.leo.descriptors;

/*
 * #%L
 * Leo
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


import gov.va.vinci.leo.tools.AutoCompile;
//import gov.va.vinci.leo.tools.ESAPIValidationType;
//import gov.va.vinci.leo.tools.ESAPIValidator;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;
import org.apache.uima.tools.jcasgen.Jg;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a type system description gov.va.vinci.leo.model that is used for import and creation of type objects.
 *
 * @author prafulla
 * @author Thomas Ginter
 */
public class LeoTypeSystemDescription {
    /**
     * The underlying TypeSystemDescriptor gov.va.vinci.leo.model for this TypeSystem.
     */
    private TypeSystemDescription myTypeSystemDescription = null;

    /**
     * The locator for the xml representation of this TypeSystemDescription.
     */
    private String mDescriptorLocator;

    /**
     * Logging object of output.
     */
    public static final Logger log = Logger.getLogger(LeoTypeSystemDescription.class.getName());

    /**
     * Create an initial, empty TypeSystemDescription. Optionally a list of TypeDescription objects
     * can also be provided which will be added to the new TypeSystemDescription.
     *
     * @param types Optional, TypeDescription objects to be added to the TypeSystem
     */
    public LeoTypeSystemDescription(TypeDescription... types) {
        this(TypeSystemFactory.generateTypeSystemDescription());
        //Add the types if the parameters are provided
        for (TypeDescription td : types) {
            this.addType(td);
        }//for

    }//default constructor

    /**
     * Set the initial underlying TypeSystemDescription to the object reference provided.
     *
     * @param td TypeSystemDescription object to be added
     */
    public LeoTypeSystemDescription(TypeSystemDescription td) {
        setTypeSystemDescription(td);
        if (myTypeSystemDescription!=null && StringUtils.isBlank(myTypeSystemDescription.getName())) 
        {
            myTypeSystemDescription.setName("leoTypeDescription_" + LeoUtils.getUUID());
        }//if

        // Set a default file if one is not already set.
        try {
            if (this.getDescriptorLocator() == null) {
                this.setDescriptorLocator("leoTypeDescription_" + LeoUtils.getUUID());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }//constructor with TypeSystemDescription input

    /**
     * Import the TypeSystemDescription from the descriptor provided either by name or by path.
     *
     * @param descriptor Path or name of the descriptor to be imported
     * @param byName     If true then import the descriptor by name, otherwise import by the path provided.
     * @throws Exception If the descriptor cannot be imported or the xml is invalid
     */
    public LeoTypeSystemDescription(String descriptor, boolean byName) throws Exception {
        this(TypeSystemFactory.generateTypeSystemDescription(descriptor, byName));
    }//Constructor with descriptor file and import flag inputs

    /**
     * Create a new TypeSystem then add a type object with the name (aType), description,
     * and name of the super type.
     *
     * @param aType          Name of the initial Annotation Type initially being created
     * @param aDescription   Description of the Annotation Type initially being created
     * @param aSupertypeName Name of the supertype from which this initial Annotation Type inherits
     */
    public LeoTypeSystemDescription(String aType, String aDescription, String aSupertypeName) {
        this(TypeSystemFactory.generateTypeSystemDescription());
        if ( myTypeSystemDescription!=null ) myTypeSystemDescription.addType(aType, aDescription, aSupertypeName);
    }//constructor for creating type description from scratch

    /**
     * Sets the TypeSystemDescription to parameter if parameter is not null.
     *
     * @param td A TypeSystemDescription object
     */
    private void setTypeSystemDescription(TypeSystemDescription td) {
        if (td != null) {
            myTypeSystemDescription = (TypeSystemDescription) td.clone();
        }
    }//setTypeSystemDescriptor method

    /**
     * Return a reference to the underlying UIMA TypeSystemDescription.
     *
     * @return  the underlying UIMA TypeSystemDescription.
     */
    public TypeSystemDescription getTypeSystemDescription() {
        return this.myTypeSystemDescription;
    }//getTypeSystemDescriptor method

    /**
     * Append the TypeSystem provided to the existing Type System.
     *
     * @param aTypeSystemDescription TypeSystemDescription to be added to this one.
     * @return Reference pointer to this LeoTypeSystemDescription object, for the builder pattern
     */
    public LeoTypeSystemDescription addTypeSystemDescription(TypeSystemDescription aTypeSystemDescription) {
        //If the input type system is null just return
        if (aTypeSystemDescription == null) {
            return this;
        }

        return this.addTypeSystemDescription(new LeoTypeSystemDescription(aTypeSystemDescription));
    }//addTypeSystemDescription method TypeSystemDescription input

    /**
     * Add the TypeSystem provided to the existing Type System.
     *
     * @param aLeoTypeSystemDescription TypeSystemDescription to be added to this one.
     * @return Reference pointer to this LeoTypeSystemDescription object, for the builder pattern.
     */
    public LeoTypeSystemDescription addTypeSystemDescription(LeoTypeSystemDescription aLeoTypeSystemDescription) {
        //Just return in the input type is null
        if (aLeoTypeSystemDescription == null) {
            return this;
        }

        //Append the types to the existing type system
        for (TypeDescription td : aLeoTypeSystemDescription.getTypes()) {
            this.addType(td);
        }//for

        return this;
    }//addTypeSystemDescription method LeoTypeSystemDescription inpu

    /**
     * Add an Annotation Type to the TypeSystem with the name and supertype name provided.  If a type
     * with the same name already exist then the type will be ignored.
     *
     * @param aName        Name of the Annotation Type to create and add.
     * @param aDescription Description of the Annotation Type that will be added
     * @param aSuperType   Name of the Super Type that this Annotation Type will extend
     * @return Reference pointer to this LeoTypeSystemDescription object, used in the builder pattern
     * @throws Exception If either the name or the super type name are not provided
     */
    public LeoTypeSystemDescription addType(String aName, String aDescription, String aSuperType) throws Exception {
        //If the name or supertype are not provided then throw an exception
        if (StringUtils.isBlank(aName) || StringUtils.isBlank(aSuperType)) {
            throw new Exception("Cannot add the type, Name and SuperType are both required, Name: " +
                    aName + ", SuperType: " + aSuperType);
        }//if

        return this.addType(new TypeDescription_impl(aName, aDescription, aSuperType));
    }//addType method

    /**
     * Add an Annotation Type to the TypeSystem from the TypeDescription provided.  If a type with
     * the same name already exists then the type will not be added.
     *
     * @param aTypeDescription The type to be added
     * @return Reference pointer to this LeoTypeSystemDescription object, used in the builder pattern
     */
    public LeoTypeSystemDescription addType(TypeDescription aTypeDescription) {
        if (aTypeDescription == null) {
            return this;
        }//if

        if (this.myTypeSystemDescription == null) {
            this.myTypeSystemDescription = TypeSystemFactory.generateTypeSystemDescription();
        }

        if (this.myTypeSystemDescription.getType(aTypeDescription.getName()) != null) {
            return this;
        }//if

        //Add the type and associated features to the TypeSystem
        this.myTypeSystemDescription.addType(aTypeDescription.getName(), aTypeDescription.getDescription(), aTypeDescription.getSupertypeName());
        TypeDescription newTd = this.myTypeSystemDescription.getType(aTypeDescription.getName());
        for (FeatureDescription fd : aTypeDescription.getFeatures()) {
            newTd.addFeature(fd.getName(), fd.getDescription(), fd.getRangeTypeName(), fd.getElementType(), fd.getMultipleReferencesAllowed());
        }//for

        return this;
    }//setTypeDescription method

    /**
     * Return a TypeDescription object with the name provided.  If that type is not found or there
     * are no types in the TypeSystem then null is returned.
     *
     * @param aTypeName Name of the type to be found
     * @return the TypeDescription or null if no such description is found
     */
    public TypeDescription getType(String aTypeName) {
        if (this.myTypeSystemDescription == null)
            return null;

        return this.myTypeSystemDescription.getType(aTypeName);
    }//getType with String input

    /**
     * Return the collection of Types in this TypeSystem, if there is no TypeSystem set then returns null.
     *
     * @return the collection of Types in this TypeSystem
     */
    public TypeDescription[] getTypes() {
        if (this.myTypeSystemDescription == null)
            return null;

        return this.myTypeSystemDescription.getTypes();
    }//get

    /**
     * Set the path to the temp XML file for this TypeSystemDescription.
     *
     * @param name Base name of the file to be created. This base name is then
     *             created as a temp file with a .xml ending. For instance, basename of "test" may become
     *             c:\temp\test.xml
     *             <p/>
     *             Use getDescriptorLocator() after this call to get the full path.
     * @throws Exception If the temp file cannot be created on the system
     */
    private void setDescriptorLocator(String name) throws Exception {
        File xmlFile = File.createTempFile(name, ".xml");
        mDescriptorLocator = xmlFile.getAbsolutePath();
    }//setDescriptorLocator method

    /**
     * Return the path to the temp XML file for this TypeSystemDescription,
     * null if no path has been generated.
     *
     * @return Path to the XML for this TypeSystemDescription or null if not generated
     */
    public String getDescriptorLocator() {
        return (StringUtils.isBlank(mDescriptorLocator)) ? null : mDescriptorLocator.trim();
    }//getDescriptorLocator method

    /**
     * Serialize the TypeSystemDescription to xml using the path and filename provided.
     *
     * @param filename Path and name of the file to serialize the XML into
     * @throws Exception If unable to serialize to the file or if the XML if invalid
     */
    public void toXML(String filename) throws Exception {
//    	filename = ESAPIValidator.validateStringInput(filename, ESAPIValidationType.PATH_MANIPULATION);
    	//If the path is null throw error.
        if (StringUtils.isBlank(filename)) {
            throw new Exception("Filename cannot be blank.");
        }//if

        if (this.myTypeSystemDescription == null) throw new Exception("No Descriptor available for XML output");

        FileOutputStream fis = null;
        try
        {
        	
        	fis=new FileOutputStream(new File(filename));
        	this.myTypeSystemDescription.toXML(fis);
        }
        finally
        {
        	if ( fis!=null ) fis.close();
        }		
    }

    /**
     * Generate the .java files for the types in this TypeSystem then compile them into byte code
     * to the bin directory specified.
     *
     * @param srcDirectory Path to the root source directory where the .java files will be generated, including package names
     * @param binDirectory Bin directory where .class files will be generated.
     * @throws Exception If there is no type system or there is an error generating the .java files
     */
    public void jCasGen(String srcDirectory, String binDirectory) throws Exception {
        //If there is no type system throw an Exception
        if (this.myTypeSystemDescription == null) {
            throw new Exception("Cannot generate a null TypeSystem");
        }//if

        //Throw and exception if we are missing one of our parameters
        if (StringUtils.isBlank(srcDirectory) || StringUtils.isBlank(binDirectory)) {
            throw new Exception("Both the source directory and bin directory are required arguments");
        }//if

        //Generate the TypeSystemDescription xml
        this.toXML(this.mDescriptorLocator);

        //Get the classpath
        String classpath = System.getProperty("java.class.path");

        //Create an instance of the Jg class and call the "main" method to generate the java files
        String[] args = {"-jcasgeninput", this.mDescriptorLocator.trim(), "-jcasgenoutput", srcDirectory, "=jcasgenclasspath", classpath};
        Jg jcasGen = new Jg();
        int ret = jcasGen.main1(args);
        if (ret == -1) {
            //Error generating the java files
            throw new Exception("Error occurred generating the Java source files");
        }//if

        List<String> fileNames = new ArrayList<String>();
        for (TypeDescription td : getTypes()) {
            String name = td.getName().substring(td.getName().lastIndexOf('.') + 1, td.getName().length());
            fileNames.add(name + ".java");
            fileNames.add(name + "_Type.java");
        }//for


        NameFileFilter fnf = new NameFileFilter(fileNames);
        List<File> javaSrcFiles = LeoUtils.listFiles(new File(srcDirectory), fnf, true);
        if (javaSrcFiles == null) {
            log.warn("No Java Source files found to compile in jCasGen");
            return;
        }//if

        //Iterate through the list of java files and compile them all
        try {
            AutoCompile.compileFiles(javaSrcFiles.toArray(new File[0]), binDirectory);
        } catch (Exception e) {
            log.warn("Exception Thrown compiling files in test", e);
        }
    }//generateTypeSystemJava

}//LeoTypeSystemDescription class
