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

import gov.va.vinci.leo.descriptors.LeoDelegate;
import gov.va.vinci.leo.descriptors.LeoRemoteAEDescriptor;
import gov.va.vinci.leo.tools.LeoUtils;

/**
 * Create an annotator that sends each CAS received to a remote service.  Remote annotators cannot have more than one
 * instance per asynchronous pipeline so the number of instances cannot be set.
 *
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 12:55
 */
public class LeoRemoteAnnotator implements LeoAnnotator {

    /**
     * Descriptor representing the remote annotator.
     */
    protected LeoRemoteAEDescriptor descriptor = null;

    /**
     * Create a new LeoRemoteAnnotator with the brokerURL and endPoint variables specified.
     *
     * @param brokerURL ActiveMQ broker URL for remote UIMA-AS service
     * @param endPoint Input queue name to which the remote service responds
     */
    public LeoRemoteAnnotator(String brokerURL, String endPoint) {
        this.descriptor = new LeoRemoteAEDescriptor(brokerURL, endPoint);
        this.descriptor.setName(endPoint + "_remote_" + LeoUtils.getUUID());
    }

    /**
     * Create a new LeoRemoteAnnotator with the brokerURL, endPoint, and annotator name specified.
     *
     * @param brokerURL ActiveMQ broker URL for remote UIMA-AS service
     * @param endPoint Input queue name to which the remote service responds
     * @param name Name of the annotator in the pipeline
     */
    public LeoRemoteAnnotator(String brokerURL, String endPoint, String name) {
        this.descriptor = new LeoRemoteAEDescriptor(brokerURL, endPoint);
        this.descriptor.setName(name);
    }

    /**
     * Get the name of this annotator.
     *
     * @return String representation of the annotator name
     */
    public String getName() {
        return descriptor.getName();
    }

    /**
     * Set the name of this annotator.
     *
     * @param name String representation of the annotator name to set
     * @return reference to this LeoRemoteAnnotator object
     */
    public <T extends LeoRemoteAnnotator> T setName(String name) {

        descriptor.setName(name);
        return (T) this;
    }

    /**
     * Return the Descriptor for this AnalysisEngine or RemoteAnalysisEngine.
     *
     * @return Descriptor object.
     * @throws Exception If there is an error generating the descriptor.
     */
    @Override
    public LeoDelegate getDescriptor() throws Exception {
        return descriptor;
    }
}
