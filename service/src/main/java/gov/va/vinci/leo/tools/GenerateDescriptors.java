package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Service
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


import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A utility for generating Descriptors and Type Descriptors for an annotator.
 * <p/>
 * If run from the command line, the first argument should be the annotator class name, and the second argument
 * should be the root path to write the descriptors out to.
 * <p/>
 * <strong>Note: Annotator Class must extend LeoBaseAnnotator.</strong>
 */
public class GenerateDescriptors {

    /**
     * Main method for running from the command line.
     * @param args command line arguements.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: GenerateDescriptors <annotator class name> <root directory to write files to>");
            System.exit(1);
        }
        String annotatorClassName = args[0];
        String outputRootPath = args[1];
        try {
            GenerateDescriptors.generate(annotatorClassName, outputRootPath);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(2);
        }
    }

    /**
     * A utility for generating Descriptors and Type Descriptors for an annotator.
     *
     * @param annotatorClassName The annotator class name. The annotator class must extend LeoBaseAnnotator and
     *                           be in the classpath.
     * @param outputRootPath     The root directory to output the descriptors to. Descriptors are written to outputRootPath/annotator package/Annotator(Descriptor/Type).xml
     * @throws Exception if any exception occurs during generation.
     */
    public static void generate(String annotatorClassName, String outputRootPath) throws Exception {
        LeoBaseAnnotator annotator = (LeoBaseAnnotator) Class.forName(annotatorClassName).newInstance();
        String shortName = annotatorClassName;
        if (annotatorClassName.contains(".")) {
            shortName = annotatorClassName.substring(annotatorClassName.lastIndexOf(".") + 1);
        }

        String newOutputPath = outputRootPath;
        if (!outputRootPath.endsWith(File.separator)) {
            newOutputPath += File.separator;
        }

        if (annotatorClassName.contains(".")) {
            newOutputPath += annotatorClassName.substring(0, annotatorClassName.lastIndexOf(".")).replaceAll("\\.", File.separator) + File.separator;
        }

        FileUtils.forceMkdir(new File(newOutputPath));
        annotator.getLeoTypeSystemDescription().toXML(newOutputPath + shortName + "Type.xml");

        LeoAEDescriptor desc = (LeoAEDescriptor) annotator.getDescriptor()
                .setDescriptorLocator(new File(newOutputPath + shortName).toURI());
        desc.toXML();
        String descriptorPath = desc.getDescriptorLocator();
        if (descriptorPath.startsWith("file:")) {
            descriptorPath = descriptorPath.substring(5);
        }

        FileUtils.copyFile(new File(descriptorPath), new File(newOutputPath + shortName + "Descriptor.xml"));
    }
}
