package gov.va.vinci.leo.cr;

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

import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Defines the interface for the SuperReader which primarily defines abstract
 * methods that exist in CollectionReader implementation objects.
 *
 * @author thomasginter
 */
public interface LeoCollectionReaderInterface {

    /**
     * Generate the UIMA CollectionReader with resources.
     *
     * @return  a UIMA CollectionReader.
     * @throws ResourceInitializationException if there is an error initializing the CollectionReader.
     */
    public abstract CollectionReader produceCollectionReader() throws ResourceInitializationException;

}//SuperReaderInterface interface