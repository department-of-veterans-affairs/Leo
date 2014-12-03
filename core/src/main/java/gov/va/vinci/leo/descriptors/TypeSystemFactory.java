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


import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl;

/**
 * Generate TypeSystemDescription objects from imported descriptors or as new objects
 * without arguments.
 *
 * @author Prafulla
 * @author Thomas Ginter
 */
public class TypeSystemFactory {

    /**
     * Create an TypeFactory object from a string descriptor.
     *
     * @param descriptor String path of TypeSystemDescriptor file
     * @param byName     If this attribute is 'true' then we just import the descriptor file by it's name
     * @return TypeSystemDescription object
     * @throws Exception If the descriptor cannot be found or the xml is invalid
     */
    public static TypeSystemDescription generateTypeSystemDescription(String descriptor, boolean byName) throws Exception {

        MetaDataObject mdo;
        // if called by Name then create a URL od the given descriptor
        if (byName) {

            mdo = MetaDataObjectFactory.createMetaDataObject(LeoUtils.createURL(descriptor));
        } else // else directly access the MetaDataObject
        {
            mdo = MetaDataObjectFactory.createMetaDataObject(descriptor);
        }

        return ((TypeSystemDescription) mdo);
    }//generateTypeSystemDescription method

    /**
     * Creates a Default TypeSystemDescription object and returns it.
     *
     * @return TypeSystemDescription object
     */
    public static TypeSystemDescription generateTypeSystemDescription() {
        return new TypeSystemDescription_impl();
    }//generateTypeSystemDescription method

}//TypeSystemFactory class
