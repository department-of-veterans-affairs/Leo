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

import org.xml.sax.SAXException;

/**
 * Interface that defines common methods for the implementation of a writer that
 * serializes deployment descriptors into files.
 *
 * @author thomasginter
 */
public interface DeployDescriptorWriter {
    /**
     * Serialize the deployment descriptor to a file.
     *
     * @throws org.xml.sax.SAXException if there is an error writting the XML.
     */
    public void serialize() throws SAXException;
}//DeployDescriptorWriter interface
