/**
 *
 */
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
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Factory for create UIMA resources.
 * <br/><br/>
 * (type descriptors, aggregate descriptors, analysis engine descriptors, etc..)
 *
 * @author thomasginter
 */
public class ResourceSpecifierFactory {
    /**
     * Create a ResourceSpecifier from an XMLInputSource (UIMA Object) and return the Specifier.
     *
     * @param xis XMLInputSource that is parsed to form the Specifier
     * @return ResourceSpecifier which is the parent interface for nearly all UIMA Descriptors
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    private static ResourceSpecifier createResourceSpecifier(XMLInputSource xis) throws InvalidXMLException {
        //Create the ResourceSpecifier
        return UIMAFramework.getXMLParser().parseResourceSpecifier(xis);
    }//createResourceSpecifier method


    /**
     * Create a ResourceSpecifier from a URL which points to a Descriptor File.
     *
     * @param descURL The descriptor URL to be parsed into a ResourceSpecifier
     * @return a ResourceSpecifier object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static ResourceSpecifier createResourceSpecifier(URL descURL) throws InvalidXMLException, IOException {
        return createResourceSpecifier(new XMLInputSource(descURL));
    }//createResourceSpecifier method

    /**
     * Create a ResourceSpecifier from the path to a Descriptor File.
     *
     * @param descPath String path to a descriptor file.
     * @return a ResourceSpecifier object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static ResourceSpecifier createResourceSpecifier(String descPath) throws IOException, InvalidXMLException {
        return createResourceSpecifier(new XMLInputSource(descPath));
    }//createResourceSpecifier method

    /**
     * Return a ResourceSpecifier from a File object provided.
     *
     * @param descFile Descriptor File object
     * @return a ResourceSpecifier object
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static ResourceSpecifier createResourceSpecifier(File descFile) throws IOException, InvalidXMLException {
        return createResourceSpecifier(new XMLInputSource(descFile));
    }//createResourceSpecifier method
}//ResourceSpecifierFactory class
