package gov.va.vinci.leo.ae;

import gov.va.vinci.leo.descriptors.LeoDelegate;

/**
 * Interface for Leo pipeline annotator objects.
 *
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 09:40
 */
public interface LeoAnnotator {

    /**
     * Return the Descriptor for this AnalysisEngine or RemoteAnalysisEngine.
     *
     * @return Descriptor object.
     * @throws Exception If there is an error generating the descriptor.
     */
    public LeoDelegate getDescriptor() throws Exception;
}
