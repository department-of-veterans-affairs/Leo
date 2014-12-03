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


import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Allows creating Metadata object and access methods to parse TypeSystemDescription XMLs.
 *
 * @author Prafulla
 */
public class MetaDataObjectFactory {

    /**
     * Create a MetaDataObject from an XMLInputSource (UIMA Object) and return the Specifier.
     *
     * @param xis XMLInputSource that is parsed to form the Specifier
     * @return MetaDataObject which is the parent interface for TypeSystemDescriptors
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    private static MetaDataObject createMetaDataObject(XMLInputSource xis) throws InvalidXMLException {

        MetaDataObject mdo;
        XMLParser parser = UIMAFramework.getXMLParser();
        //mdo = parser.parseResourceSpecifier(xis);
        mdo = parser.parseTypeSystemDescription(xis);
        return mdo;
    }//createMetaDataObject

    /**
     * Create a TypeSystemDescription from a URL which points to a Descriptor File.
     *
     * @param descURL The descriptor URL to be parsed into a MetaDataObject
     * @return a MetaDataObject object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static MetaDataObject createMetaDataObject(URL descURL) throws IOException, InvalidXMLException {
        return createMetaDataObject(new XMLInputSource(descURL));
    }//createMetaDataObject method

    /**
     * Create a MetaDataObject from the path to a Descriptor File.
     *
     * @param descPath String path to a descriptor file.
     * @return a MetaDataObject object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static MetaDataObject createMetaDataObject(String descPath) throws IOException, InvalidXMLException {
        return createMetaDataObject(new XMLInputSource(descPath));
    }//createMetaDataObject method

    /**
     * Return a ResourceSpecifier from a File object provided.
     *
     * @param descFile Descriptor File object
     * @return a ResourceSpecifier object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static MetaDataObject createMetaDataObject(File descFile) throws IOException, InvalidXMLException {
        return createMetaDataObject(new XMLInputSource(descFile));
    }//createResourceSpecifier method

}