package gov.va.vinci.leo.descriptors;

/*
 * #%L
 * Leo Core
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
import org.apache.uima.aae.jms_adapter.JmsAnalysisEngineServiceAdapter;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;

/**
 * Generate a CustomResourceSpecifier that describes a remote service to be included in the pipeline.
 *
 * User: Thomas Ginter
 * Date: 10/31/13
 * Time: 12:33 PM
 */
public class RemoteEngineFactory {

    /**
     * Create a CustomResourceSpecifier that represents a remote AE service.
     *
     * @return CustomResourceSpecifier object representing remote service
     */
    public static CustomResourceSpecifier generateRemoteAEDescriptor() {
        CustomResourceSpecifier_impl crs = new CustomResourceSpecifier_impl();
        crs.setResourceClassName(JmsAnalysisEngineServiceAdapter.class.getCanonicalName());
        return crs;
    }

    /**
     * Create a CustomResourceSpecifier from a descriptor xml file.
     *
     * @param descriptor
     *      String path to the descriptor file
     * @param byName
     *      if true then import the descriptor file by name using a URL
     * @return CustomResourceSpecifier object
     * @throws org.apache.uima.util.InvalidXMLException if there is an error in the descriptor xml
     * @throws java.io.IOException if there is an error reading the descriptor file
     */
    public static CustomResourceSpecifier generateRemoteAEDescriptor(String descriptor, boolean byName) throws InvalidXMLException, IOException {
        ResourceSpecifier spec;
        if(byName) {
            spec = ResourceSpecifierFactory.createResourceSpecifier(LeoUtils.createURL(descriptor));
        } else {
            spec = ResourceSpecifierFactory.createResourceSpecifier(descriptor);
        }
        if(spec instanceof CustomResourceSpecifier || ((CustomResourceSpecifier)spec).getResourceClassName()
            .equals(JmsAnalysisEngineServiceAdapter.class.getCanonicalName()))  {
            return (CustomResourceSpecifier) spec;
        } else {
            return null;
        }
    }
}
