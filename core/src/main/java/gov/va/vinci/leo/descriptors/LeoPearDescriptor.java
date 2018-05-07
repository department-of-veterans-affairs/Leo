package gov.va.vinci.leo.descriptors;

import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.PearSpecifier;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.impl.Parameter_impl;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

import javax.xml.transform.sax.TransformerHandler;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by tginter on 7/6/17.
 */
public class LeoPearDescriptor implements LeoDelegate {

    protected PearSpecifier resourceSpecifier;

    protected URI descriptorLocator;

    public LeoPearDescriptor(String pearDescPath) throws IOException, InvalidXMLException {
        this(new File(pearDescPath));
    }

    public LeoPearDescriptor(File pearDescFile) throws IOException, InvalidXMLException {
        this(pearDescFile.toURI());
    }


    public LeoPearDescriptor(URI pearDescFile) throws IOException, InvalidXMLException {
        descriptorLocator = pearDescFile;
        resourceSpecifier = (PearSpecifier) ResourceSpecifierFactory.createResourceSpecifier(pearDescFile.toURL());
    }

    @Override
    public ResourceSpecifier getResourceSpecifier() {
        return resourceSpecifier;
    }

    @Override
    public LeoPearDescriptor setDescriptorLocator(URI descriptorLocator) {
        /** The descriptor locator can only be set at creation **/
        return this;
    }

    @Override
    public String getDescriptorLocator() {
        return descriptorLocator.getPath();
    }

    @Override
    public String getName() {
        Parameter param = LeoUtils.getParameter("componentName", resourceSpecifier.getParameters());
        return (param == null)? null : param.getValue();
    }

    @Override
    public LeoPearDescriptor setName(String name) throws IOException {
        Parameter param = LeoUtils.getParameter("componentName", resourceSpecifier.getParameters());
        if(param != null) {
            param.setValue(name);
        } else {
            Parameter[] parameters = LeoUtils.addParameter(
                    new Parameter_impl("componentName", name),
                    resourceSpecifier.getParameters());
            resourceSpecifier.setParameters(parameters);
        }
        return this;
    }

    @Override
    public boolean isDeleteOnExit() {
        return false;
    }

    @Override
    public LeoPearDescriptor setIsDeleteOnExit(boolean deleteOnExit) {
        /** We will not delete an installed Pear component descriptor **/
        return this;
    }

    @Override
    public void toXML() throws Exception {
        /** We will not be overriding the installed pear descriptor **/
    }

    @Override
    public void toXML(String filename) throws Exception {
        /** Writing the pear component discriptor to a new location would invalidate the descriptor **/
    }

    @Override
    public LeoPearDescriptor analysisEngineSection(TransformerHandler thd, boolean isTopDescriptor) throws SAXException {
        return this;
    }
}
