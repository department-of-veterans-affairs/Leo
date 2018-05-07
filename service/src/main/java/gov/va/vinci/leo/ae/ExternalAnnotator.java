package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoDelegate;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.model.NameValue;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;

/**
 * This annotator is used to import external implementations of annotators by descriptor or directly from the object binary.
 *
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 10:21
 */
public class ExternalAnnotator implements LeoAnnotator {

    /**
     * External descriptor imported to LeoAnnotator for use in the SimplePipeline representation.
     */
    protected LeoAEDescriptor descriptor = null;

    /**
     * Constructor that creates a blank AnalysisEngineDescriptor object with the name provided. Also
     * sets the implementation class name to the implementation_class string provided.
     *
     * @param name                 the name of the AnalysisEngine, Leo will make sure it is unique.
     * @param implementation_class the fully qualified name of the Annotator Class.
     */
    public ExternalAnnotator(String name, String implementation_class) {
        this.descriptor = new LeoAEDescriptor(name, implementation_class);
    }

    /**
     * Create this annotator by importing an external descriptor.
     *
     * @param descriptor  path or name of a descriptor file used to generate the AED
     * @param byName      if true then try to import by name otherwise import by location
     * @param paramValues Optional, list of parameters names and values to set.  Creates the
     *                    parameter if it does not already exist.
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public ExternalAnnotator(String descriptor, boolean byName, NameValue... paramValues) throws IOException, InvalidXMLException {
        this.descriptor = new LeoAEDescriptor(descriptor, byName, paramValues);
    }

    /**
     * Create this annotator by importing an external descriptor.
     *
     * @param descriptor  path or name of a descriptor file used to generate the AED
     * @param byName      if true then try to import by name otherwise import by location
     * @param numInstances Number of replication instances for this annotator in the pipeline
     * @param paramValues Optional, list of parameters names and values to set.  Creates the
     *                    parameter if it does not already exist.
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public ExternalAnnotator(String descriptor, boolean byName, int numInstances, NameValue... paramValues) throws IOException, InvalidXMLException {
        this(descriptor, byName, paramValues);
        this.setNumInstances(numInstances);
    }

    /**
     * Number of instances of this annotator in the pipeline.  Used during asynchronous processing only.
     *
     * @return Number of instances
     */
    public int getNumInstances() {
        return this.descriptor.getNumberOfInstances();
    }

    /**
     * Set the number of instances of this annotator in the pipeline.  Used during asynchronous processing.
     *
     * @param numInstances Number of replication instances for this annotator in the pipeline
     * @return reference to this ExternalAnnotator instance.
     */
    public ExternalAnnotator setNumInstances(int numInstances) {
        this.descriptor.setNumberOfInstances(numInstances);
        return this;
    }

    /**
     * Get the name of this annotator.
     *
     * @return String representation of the annotator name
     */
    public String getName() {
        return this.descriptor.getName();
    }

    /**
     * Set the name of this annotator.
     *
     * @param name String representation of the annotator name to set
     * @return reference to this ExternalAnnotator instance.
     */
    public ExternalAnnotator setName(String name) {
        this.descriptor.setName(name);
        return this;
    }

    @Override
    public LeoDelegate getDescriptor() throws Exception {
        return descriptor;
    }

    /**
     * Return the type system description for this annotator.
     *
     * @return LeoTypeSystemDescription set in this annotator descriptor
     */
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return descriptor.getTypeSystemDescription();
    }

    /**
     * Add the type system provided to the existing type system of this annotator.
     *
     * @param leoTypeSystemDescription type system to add.
     * @return Reference to this ExternalAnnotator instance.
     */
    public ExternalAnnotator addTypeSystemDescription(LeoTypeSystemDescription leoTypeSystemDescription) {
        descriptor.addTypeSystemDescription(leoTypeSystemDescription);
        return this;
    }
}
