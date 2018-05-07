/**
 *
 */
package gov.va.vinci.leo.listener;

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


import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.AnnotationViewerMain;

import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;


/**
 * Store processed CAS objects in XMI files then launch the AnnotationViewer once collection
 * processing is complete.
 *
 * @author thomasginter
 */
public class SimpleXmiListener extends BaseListener {

    /**
     * Flag to launch annotation viewer when processing is complete.
     */
    protected boolean launchAnnotationViewer = false;

    /**
     * Path to the type system descriptor or aggregate descriptor file for the service.
     */
    protected LeoTypeSystemDescription typeSystemDescriptor = null;

    /**
     * File pointer to the serialized type description.
     */
    protected File typeDescriptionFile = null;

    /**
     * Name of the type system description which this listener will serialize if the typeSystemDescriptor is set.
     */
    public static final String TYPE_DESCRIPTION_NAME = "type_system_desc.xml";

    /**
     * Logging object of output.
     */
    public static final Logger log = Logger.getLogger(SimpleXmiListener.class.getName());

    /**
     * Constructor with output directory input.
     *
     * @param outputDir             the directory to write the final xmi files to.
     */
    public SimpleXmiListener(File outputDir) {
        mOutputDir = outputDir;
    }//Constructor with String input param

    /**
     * Get the type system or aggregate descriptor File reference that this listener will set in the viewer if launched.
     *
     * @return leo type system description
     */
    public LeoTypeSystemDescription getTypeSystemDescriptor() {
        return typeSystemDescriptor;
    }

    /**
     * Set the type system descriptor File reference that this listener will set in the viewer if launched.
     *
     * @param typeSystemDescriptor type system descriptor file reference
     * @return reference to this listener instance
     */
    public SimpleXmiListener setTypeSystemDescriptor(File typeSystemDescriptor) throws Exception {
        this.typeSystemDescriptor = new LeoTypeSystemDescription(typeSystemDescriptor.getAbsolutePath(), false);
        return this;
    }

    /**
     * Set the type system description this listener will serialize into the output directory.
     *
     * @param typeSystemDescriptor type system descriptor to set
     * @return reference to this listener instance
     */
    public SimpleXmiListener setTypeSystemDescriptor(LeoTypeSystemDescription typeSystemDescriptor) {
        this.typeSystemDescriptor = typeSystemDescriptor;
        return this;
    }

    /**
     * True if the annotation viewer should be launched after processing is complete, else false.
     * @return     True if the annotation viewer should be launched after processing is complete, else false.
     */
    public boolean isLaunchAnnotationViewer() {
        return launchAnnotationViewer;
    }

    /**
     * Set the value of launch annotation viewer. If true, when collectionProcessComplete is finished,
     * the annotation view will be launched.
     *
     * @param launchAnnotationViewer   True if the annotation viewer should be launched after processing is complete, else false.
     * @return reference to this listener instance
     */
    public SimpleXmiListener setLaunchAnnotationViewer(boolean launchAnnotationViewer) {
        this.launchAnnotationViewer = launchAnnotationViewer;
        return this;
    }

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS, org.apache.uima.collection.EntityProcessStatus)
     * @param aCas      the CAS containing the processed entity and the analysis results
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);

        //Get the original file name
        String doc = null;
        JCas jcas;

        /**
         * If we have an annotation filter, check to see if this document needs processed.
         */
        if (getAnnotationTypeFilter().length > 0 && !this.hasFilteredAnnotation(aCas)) {
            return;
        }
        try {
            jcas = aCas.getJCas();
            doc = this.getReferenceLocation(jcas);
        } catch (CASException e1) {
            /**calculate the name another way**/
            log.error(e1.getMessage());
        }

        //Check for an exception
        if (aStatus != null && aStatus.isException()) {
            log.error("Exceptions thrown on getMeta call to remote service...");
            List<Exception> exceptions = aStatus.getExceptions();
            StringBuilder errors = new StringBuilder();
            for (Exception exception : exceptions) {
                log.error(exception.getMessage(), exception);
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                exception.printStackTrace(printWriter);
                errors.append(exception.toString() + "\n---\n");
                errors.append(result.toString() + "\n\n");
            }//for

            // Write exception out to file.
            BufferedWriter s = null;
            try {
                File errorOutFile = new File(mOutputDir, doc + ".err");
                s = new BufferedWriter(new FileWriter(errorOutFile));
                s.write(errors.toString());
            } catch (Exception e) {
                log.error("Write error: " + e);
            } finally {
                try {
                    s.close();
                } catch (Exception e) {
                    log.warn("Could not close stream: " + e);
                }
            }
            return;
        }//if aStatus != null && isException

        //Serialize the file if not done already
        if(typeSystemDescriptor != null && typeDescriptionFile == null) {
            typeDescriptionFile = new File(mOutputDir, TYPE_DESCRIPTION_NAME);
            try {
                typeSystemDescriptor.toXML(typeDescriptionFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("Error writing out the Type system XML", e);
            }
        }

        if (doc == null || doc.equals("")) {
            String casID = ((UimaASProcessStatus) aStatus).getCasReferenceId();
            doc = casID + ".xmi";
        } else {
            doc += ".xmi";
        }

        FileOutputStream xmiOut = null;
        try {
            File xmiOutFile = new File(mOutputDir, doc);
            xmiOut = new FileOutputStream(xmiOutFile);
            XmiCasSerializer.serialize(aCas, xmiOut);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                xmiOut.close();
            } catch (Exception e) {
                log.warn("Could not close file: " + e);
            }
        }

    }//entityProcessComplete method

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus);

        if (launchAnnotationViewer) {
            launchAnnotationViewer();
        }
    }//collectionProcessComplete method

    /**
     * Launch the UIMA annotation viewer.
     */
    protected void launchAnnotationViewer() {
        Preferences prefs = Preferences.userRoot().node("org/apache/uima/tools/AnnotationViewer");
        if (typeDescriptionFile != null) {
            prefs.put("taeDescriptorFile", typeDescriptionFile.getAbsolutePath());
        }//if typeSystemDescriptor != null
        if (mOutputDir != null) {
            try {
                prefs.put("inDir", mOutputDir.getCanonicalPath());
            } catch (IOException e) {
                log.error("Could not get output directory canonical path to set for annotation viewer.");
            }
        }//if mOutputDir != null
        AnnotationViewerMain avm = new AnnotationViewerMain();
        avm.setBounds(0, 0, 1000, 225);
        avm.setVisible(true);
    }

}//XmiUABListener class
