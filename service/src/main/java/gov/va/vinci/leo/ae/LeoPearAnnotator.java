package gov.va.vinci.leo.ae;

import gov.va.vinci.leo.descriptors.LeoDelegate;
import gov.va.vinci.leo.descriptors.LeoPearDescriptor;
import org.apache.uima.util.InvalidXMLException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;

/**
 * Created by tginter on 7/7/17.
 */
public class LeoPearAnnotator implements LeoAnnotator {

    protected LeoPearDescriptor descriptor = null;

    public LeoPearAnnotator(@Nonnull URI pearDescFile) throws IOException, InvalidXMLException {
        this.descriptor = new LeoPearDescriptor(pearDescFile);
    }

    public LeoPearAnnotator(@Nonnull LeoPearDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public LeoDelegate getDescriptor() throws Exception {
        return descriptor;
    }

    public LeoPearAnnotator setDescriptor(@Nonnull LeoPearDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public String getName() {
        return descriptor.getName();
    }

    public LeoPearAnnotator setName(String name) throws IOException {
        descriptor.setName(name);
        return this;
    }
}
