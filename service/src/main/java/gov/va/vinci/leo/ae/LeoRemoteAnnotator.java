package gov.va.vinci.leo.ae;

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
     */
    public void setName(String name) {
        descriptor.setName(name);
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
