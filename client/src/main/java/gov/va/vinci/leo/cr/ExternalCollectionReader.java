package gov.va.vinci.leo.cr;

/*
 * #%L
 * Leo Client
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

import gov.va.vinci.leo.descriptors.ResourceSpecifierFactory;
import org.apache.uima.UIMAFramework;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Import a third party CollectionReader from a descriptor or an already initialized CollectionReader object.
 */
public class ExternalCollectionReader implements LeoCollectionReaderInterface {

    /**
     * Reference to the external CollectionReader object that will do the real work of getting the data.
     */
    protected CollectionReader reader = null;

    /**
     * Initialize with a pre-created CollectionReader object.
     *
     * @param reader CollectionReader to set.
     */
    public ExternalCollectionReader(CollectionReader_ImplBase reader) {
        this.reader = reader;
    }

    /**
     * Set the reader from the path to the descriptor.  The path should be preceded by file: or classpath: to distinguish
     * between explicit file system paths and classpath resources.
     *
     * @param collectionReaderDescriptorPath Path to the CollectionReader descriptor.
     * @throws ResourceInitializationException if there is an error reading the descriptor.
     * @throws IOException if there is an error in the XML of the descriptor.
     * @throws InvalidXMLException if the CollectionReader cannot be initialized by the framework.
     */
    public ExternalCollectionReader(String collectionReaderDescriptorPath) throws ResourceInitializationException, IOException, InvalidXMLException {
        this(new File(collectionReaderDescriptorPath));
    }

    /**
     * Set the reader from the CollectionReader referenced in the File object specified.
     *
     * @param collectionReaderDescriptor CollectionReader descriptor to use.
     * @throws IOException if there is an error reading the descriptor.
     * @throws InvalidXMLException if there is an error in the XML of the descriptor.
     * @throws ResourceInitializationException if the CollectionReader cannot be initialized by the framework.
     */
    public ExternalCollectionReader(File collectionReaderDescriptor) throws IOException, InvalidXMLException, ResourceInitializationException {
        setCollectionReader(collectionReaderDescriptor);
    }

    /**
     * Set the CollectionReader class field from the CollectionReader descriptor file.
     *
     * @param collectionReaderDescriptor CollectionReader descriptor file.
     * @throws ResourceInitializationException if there is an error reading the descriptor.
     * @throws IOException if there is an error in the XML of the descriptor.
     * @throws InvalidXMLException if the CollectionReader cannot be initialized by the framework.
     */
    private void setCollectionReader(File collectionReaderDescriptor) throws IOException, InvalidXMLException, ResourceInitializationException {
        CollectionReaderDescription collectionReaderDescription = (CollectionReaderDescription) ResourceSpecifierFactory
                .createResourceSpecifier(collectionReaderDescriptor);
        reader = UIMAFramework.produceCollectionReader(collectionReaderDescription);
    }

    /**
     * Generate the UIMA Collection reader with resources.
     *
     * @return  a uima collection reader.
     * @throws ResourceInitializationException if there is an error initializing the reader.
     */
    @Override
    public CollectionReader produceCollectionReader() throws ResourceInitializationException {
        return reader;
    }
}
