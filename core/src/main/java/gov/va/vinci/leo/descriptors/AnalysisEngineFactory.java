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

import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;

/**
 * Generate AnalysisEngineDescription objects with factory methods.
 *
 * @author thomasginter
 */
public class AnalysisEngineFactory {

    /**
     * Create an AnalysisEngineFactory object from a string descriptor.
     *
     * @param descriptor String path to an analysis engine descriptor file
     * @param byName     if true then import the descriptor file by name
     * @return AnalysisEngineDescriptor file
     * @throws java.io.IOException if the descriptor cannot created.
     * @throws org.apache.uima.util.InvalidXMLException if there is a problem creating the xml.
     */
    public static AnalysisEngineDescription generateAED(String descriptor, boolean byName) throws IOException, InvalidXMLException {
        ResourceCreationSpecifier spec;
        if (byName) {
            spec = (ResourceCreationSpecifier) ResourceSpecifierFactory
                    .createResourceSpecifier(LeoUtils.createURL(descriptor));
        } else {
            spec = (ResourceCreationSpecifier) ResourceSpecifierFactory
                    .createResourceSpecifier(descriptor);
        }//else
        if (spec instanceof AnalysisEngineDescription)
            return (AnalysisEngineDescription) spec;
        else
            return null;
    }//generateAED method

    /**
     * Create a blank AnalysisEngineDescription object.
     *
     * @return AnalysisEngineDescription object representing a primitive.
     */
    public static AnalysisEngineDescription generateAED()  {
        AnalysisEngineDescription_impl d = new AnalysisEngineDescription_impl();
        d.setPrimitive(true);
        d.getMetaData().setName("leoPrimitive");
        return d;
    }//generateAED() method

    /**
     * Create an AnalysisEngineDescription object with the name provided.  Sets the
     * implementation class name to the implementation_class.
     *
     * @param name                 the name of the AnalysisEngine, should be unique within an aggregate
     * @param implementation_class the fully qualified name of the AnnotatorClass used in this annotation engine.
     * @return  An AnalysisEngineDescription object with the name provided.  Sets the
     * implementation class name to the implementation_class.
     */
    public static AnalysisEngineDescription generateAED(String name, String implementation_class) {
        AnalysisEngineDescription_impl d = new AnalysisEngineDescription_impl();
        d.setPrimitive(true);
        d.getMetaData().setName(name);
        d.setImplementationName(implementation_class);
        return d;
    }//generateAED(String name, String implementation_class)

}//AnalysisEngineFactory class
